package ais.cody;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import ais.PlayerWithUtils;
import galaxy.Action;
import galaxy.Fleet;
import galaxy.Planet;

public class ValueCapture extends PlayerWithUtils {
    private static final double FUTURE_COEFFICIENT = 2;
    private static final double DISTANCE_COEFFICIENT = .3;
    private double health;
    private Vector heart;
    private int turn = 0;
    Color myColor = Color.RED;

    private Planet[] planets;

    public ValueCapture() {
        this(new Color(255, 255, 255));
        setHandler(new PlayerHandler() {
            @Override
            public Collection<Action> turn(Fleet[] fleets) {
                return makeTurn(fleets);
            }

            @Override
            public void newGame(Planet[] newMap) {
                planets = newMap;
            }
        });
    }

    public ValueCapture(Color c) {
        super(c, "ValueCapture");
    }

    private void calculateHealth() {
        double[] planetPosition;
        Vector planetVector;

        health = 0;
        heart = new Vector(0., 0., 0.);

        for (Planet p : getPlanetsOwnedByPlayer(planets, this)) {
            this.health += p.getNumUnits();

            planetPosition = p.getCoords();
            try {
                planetVector = new Vector(planetPosition[0], planetPosition[1], planetPosition[2]);
            } catch (Exception ex) {
                planetVector = new Vector(planetPosition[0], planetPosition[1], 0);
            }
            planetVector = Vector.scale(planetVector, p.getNumUnits());
            heart = Vector.add(heart, planetVector);
        }

        heart = Vector.scale(heart, 1. / health);
    }

    protected Collection<Action> makeTurn(Fleet[] fleets) {
        LinkedList<Action> actions = new LinkedList<Action>();
        if (turn++ < 10) {
            return actions;
        }

        ArrayList<Planet> myPlanets = new ArrayList<Planet>(getPlanetsOwnedByPlayer(planets, this));
        ArrayList<Planet> opponentsPlanets = new ArrayList<Planet>(getOpponentsPlanets(planets, this));
        ArrayList<Planet> neutralPlanets = new ArrayList<Planet>(getPlanetsNotOwnedByPlayer(planets, this));
        Planet target = null;
        ArrayList<Planet> attackers = new ArrayList<Planet>();
        int[] unitsToSend;
        int i;
        double distance;
        double least;
        double cost;
        double weightedCost;
        double targetCost = 0;
        int strength;
        int totalToSend = 0;
        boolean canAttack;

        neutralPlanets.removeAll(opponentsPlanets);

        calculateHealth();

        // find the best planet to send units to
        least = Double.MAX_VALUE;
        for (Planet to : planets) {
            cost = costOfPlanet(to, fleets);
            weightedCost = cost;
            distance = to.distanceTo(Vector.getCoords(heart));
            weightedCost += DISTANCE_COEFFICIENT * distance;
            weightedCost -= (FUTURE_COEFFICIENT * to.getProductionFrequency());
            if (cost >= 0 && weightedCost < least) {
                target = to;
                least = weightedCost;
            }
        }

        // determine who should send units
        if (target != null && least != Double.MAX_VALUE) {
            targetCost = costOfPlanet(target, fleets);
            for (Planet from : myPlanets) {
                if (planetStrength(from, fleets) < -1) {
                    attackers.add(from);
                }
            }

            if (!attackers.isEmpty()) {
                unitsToSend = new int[attackers.size()];
                canAttack = true;
                while (totalToSend <= targetCost && canAttack) {
                    canAttack = false;
                    i = 0;
                    for (Planet from : attackers) {
                        strength = planetStrength(from, fleets);
                        if (-strength - unitsToSend[i] > 2) {
                            unitsToSend[i]++;
                            totalToSend++;
                            canAttack = true;
                        }
                        i++;
                    }
                }

                if (totalToSend > targetCost) {
                    i = 0;
                    for (Planet from : attackers) {
                        if (unitsToSend[i] > 0) {
                            actions.add(makeAction(from, target, unitsToSend[i]));
                        }
                        i++;
                    }
                }
            }
        }

        return actions;
    }

    private double costOfPlanet(Planet target, Fleet[] fleets) {
        double cost = Double.MAX_VALUE;
        double timeCost;

        timeCost = (target.getProductionFrequency() * (target.distanceTo(Vector.getCoords(heart)) / FLEET_SPEED)) + 1;

        if (target.ownedBy(this)) {
            cost = planetStrength(target, fleets);
        } else if (target.ownedBy(null)) {
            cost = (double)planetStrength(target, fleets) + 1;
        } else {
            cost = (double)planetStrength(target, fleets) + 1 + timeCost;
        }

        return cost;
    }

    private int planetStrength(Planet planet, Fleet[] fleets) {
        int myStrength;
        int enemyStrength;

        if (planet.ownedBy(this)) {
            myStrength = planet.getNumUnits();
            enemyStrength = 0;
        } else {
            myStrength = 0;
            enemyStrength = planet.getNumUnits();
        }

        for (Fleet fleet : fleets) {
            if (fleet.DESTINATION.equals(planet)) {
                if (fleet.ownedBy(this)) {
                    myStrength += fleet.getNumUnits();
                } else {
                    enemyStrength += fleet.getNumUnits();
                }
            }
        }

        return enemyStrength - myStrength;
    }
}
