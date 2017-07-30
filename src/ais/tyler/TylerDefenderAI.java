package ais.tyler;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import ais.PlayerWithUtils;
import galaxy.Action;
import galaxy.Coords;
import galaxy.Fleet;
import galaxy.Planet;

public class TylerDefenderAI extends PlayerWithUtils {

    private Set<Planet> defendPlanets = new TreeSet<>(
            (Planet p1, Planet p2) -> p1.PRODUCTION_TIME - p2.PRODUCTION_TIME);

    private Planet myStartingPlanet;
    private Planet enemyStartingPlanet;

    private List<Planet> myPlanets;
    private List<Planet> unownedPlanets;
    private List<Planet> enemyPlanets;
    private List<Planet> otherPlanets;

    private int myUnitCount = 0;
    private int enemyUnitCount = 0;

    private Map<Planet, Integer> myUnitsSent;
    private Map<Planet, Integer> myUnitsApproaching;
    private Map<Planet, Integer> enemyUnitsApproaching;

    private double farthestPlanetDistance = 0;

    private boolean firstTurn = true;

    private Planet[] planets;

    LinkedList<Action> actions = new LinkedList<Action>();

    public TylerDefenderAI(Color c) {
        super(c, "Tyler Defender AI");
        setHandler(new PlayerHandler() {
            @Override
            public Collection<Action> turn(Fleet[] fleets) {
                return makeTurn(fleets);
            }

            @Override
            public void newGame(Planet[] newMap) {
                planets = newMap;
                nextGame();
            }
        });
    }

    public TylerDefenderAI() {
        this(new Color(135, 206, 250));
    }

    protected Collection<Action> makeTurn(Fleet[] fleets) {
        actions = new LinkedList<Action>();
        updateVariables(fleets);
        if (firstTurn) {
            firstTurn = false;
            calculateConstants();
            sendInitialFleets();
        }

        defenderAI(fleets);
        return actions;
    }

    private void calculateConstants() {
        calculateFarthestPlanetDistance();
        myStartingPlanet = (myPlanets.size() == 1) ? myPlanets.get(0) : null;
        enemyStartingPlanet = (enemyPlanets.size() == 1) ? enemyPlanets.get(0) : null;
    }

    private void calculateFarthestPlanetDistance() {
        for (int i = 0; i < planets.length; i++) {
            Planet a = planets[i];
            for (int j = i + 1; j < planets.length; j++) {
                Planet b = planets[j];
                double dist = a.distanceTo(b);
                if (dist > farthestPlanetDistance) {
                    farthestPlanetDistance = dist;
                }
            }
        }
    }

    private void sendInitialFleets() {
        for (Planet p : unownedPlanets) {
            if (p.getNumUnits() <= 4 && p.distanceTo(myStartingPlanet) < p.distanceTo(enemyStartingPlanet)) {
                actions.add(makeAction(myStartingPlanet, p, p.getNumUnits() + 1));
            }
        }
    }

    private void updateVariables(Fleet[] fleets) {
        myPlanets = new ArrayList<>();
        unownedPlanets = new ArrayList<>();
        enemyPlanets = new ArrayList<>();
        otherPlanets = new ArrayList<>();
        for (Planet p : planets) {
            if (p.isNeutral()) {
                unownedPlanets.add(p);
                otherPlanets.add(p);
            } else if (p.ownedBy(this)) {
                myPlanets.add(p);
            } else {
                enemyPlanets.add(p);
                otherPlanets.add(p);
            }
        }

        myUnitCount = getMyUnitCount(fleets, planets, this);
        enemyUnitCount = getOpponentUnitCount(fleets, planets, this);

        // Reset the units sent on this turn from each of my planets to 0
        myUnitsSent = new HashMap<>();
        for (Planet p : myPlanets) {
            myUnitsSent.put(p, 0);
        }

        // Reset the units approaching each planet
        myUnitsApproaching = new HashMap<>();
        enemyUnitsApproaching = new HashMap<>();
        for (Planet p : planets) {
            myUnitsApproaching.put(p, getPlayersIncomingFleetCount(p, fleets, this));
            enemyUnitsApproaching.put(p, getOpponentsIncomingFleetCount(p, fleets, this));
        }
    }

    ///////////////////////
    // SORTING //
    ///////////////////////

    private void valueSort(List<Planet> planets, Coords center) {
        Collections.sort(planets, new Comparator<Planet>() {
            @Override
            public int compare(Planet p1, Planet p2) {
                return oppPlanetValue(p2, center) - oppPlanetValue(p1, center);
            }
        });
    }

    private void distSort(List<Planet> planets, Planet p) {
        Collections.sort(planets, new Comparator<Planet>() {
            @Override
            public int compare(Planet p1, Planet p2) {
                return (int)(p1.distanceTo(p) - p2.distanceTo(p));
            }
        });
    }

    // The higher the value, the better the planet is (used for planets not
    // owned by
    // this player).
    private int oppPlanetValue(Planet p, Coords center) {
        int value = 0;

        // scale the production value to 0-1 (1 being least production time).
        double productionValue = -(p.PRODUCTION_TIME - 100) / 66.0;

        // scale the unit value to 0-1 (1 being least # of units).
        // opponent planets can go negative on this value if they have > 50
        // units.
        double unitValue = -(p.getNumUnits() - 50) / 50.0;

        // scale the distance value to 0-1 (1 being least distance).
        double distValue = -(center.distanceTo(p) - farthestPlanetDistance) / farthestPlanetDistance;

        value += productionValue;
        // value += p.isNeutral() ? 0 : 10;
        value += unitValue;
        value += distValue;

        return value;
    }

    ///////////////////////
    // AIs //
    ///////////////////////

    private void defenderAI(Fleet[] fleets) {
        if (myPlanets.size() == 0) {
            return;
        }
        defendPlanets.addAll(myPlanets);

        int totalExpendableUnits = getTotalExpendableUnits();

        // Recapture any planets I have owned (larger ones first)
        for (Planet p : defendPlanets) {
            // if (!p.ownedBy(this) || p.isNeutral()) {
            sortByDistance(myPlanets, p);
            for (Planet myPlanet : myPlanets) {
                int unitsSentThisTurn = myUnitsSent.get(myPlanet);
                int expendableUnits = getExpendableUnits(myPlanet) - unitsSentThisTurn;
                int myUnitsIncoming = myUnitsApproaching.get(p);
                int enemyUnitsIncoming = enemyUnitsApproaching.get(p);
                int unitsOnPlanet = unitsAtPlanetWhenArrive(p, myPlanet) + enemyUnitsIncoming;
                if (totalExpendableUnits > unitsOnPlanet && expendableUnits > 0 && myUnitsIncoming <= unitsOnPlanet
                        && !getCurrentEventualOwner(p, fleets, this).equals(PlanetOwner.PLAYER)) {
                    int unitsToSend = Math.min(unitsOnPlanet - myUnitsIncoming + 1, expendableUnits);
                    actions.add(makeAction(myPlanet, p, unitsToSend));
                    // Update unit counts
                    myUnitsApproaching.put(p, myUnitsIncoming + unitsToSend);
                    myUnitsSent.put(myPlanet, unitsSentThisTurn + unitsToSend);
                }
            }
            // }
        }

        // Conquer enemy planets
        valueSort(otherPlanets, center(myPlanets));
        for (Planet p : otherPlanets) {
            if (!p.ownedBy(this) && !p.isNeutral()) {
                // System.out.println("Planet: " + (p.isNeutral() ? "Neutral" :
                // "Enemy"));
                distSort(myPlanets, p);
                for (Planet myPlanet : myPlanets) {
                    int unitsSentThisTurn = myUnitsSent.get(myPlanet);
                    int expendableUnits = getExpendableUnits(myPlanet) - unitsSentThisTurn;
                    int myUnitsIncoming = myUnitsApproaching.get(p);
                    int enemyUnitsIncoming = enemyUnitsApproaching.get(p);
                    int unitsOnPlanet = unitsAtPlanetWhenArrive(p, myPlanet) + enemyUnitsIncoming;

                    if (totalExpendableUnits > unitsOnPlanet && unitsOnPlanet < myUnitCount / 4 && expendableUnits > 0
                            && myUnitsIncoming <= unitsOnPlanet) {
                        int unitsToSend = Math.min(unitsOnPlanet - myUnitsIncoming + 1, expendableUnits);
                        actions.add(makeAction(myPlanet, p, unitsToSend));
                        // Update unit counts
                        myUnitsApproaching.put(p, myUnitsIncoming + unitsToSend);
                        myUnitsSent.put(myPlanet, unitsSentThisTurn + unitsToSend);
                    }
                }
            }
        }

        int unitsInAir = getMyUnitsInFleets(fleets, this);
        if (myUnitCount - unitsInAir > enemyUnitCount + 50) {
            // Capture small neutral planets.
            int totalUnitsSentThisTurn = 0;
            for (Planet p : otherPlanets) {
                if (p.isNeutral()) {
                    // System.out.println("Planet: " + (p.isNeutral() ?
                    // "Neutral" : "Enemy"));
                    distSort(myPlanets, p);
                    for (Planet myPlanet : myPlanets) {
                        int unitsSentThisTurn = myUnitsSent.get(myPlanet);
                        int expendableUnits = getExpendableUnits(myPlanet) - unitsSentThisTurn;
                        int myUnitsIncoming = myUnitsApproaching.get(p);
                        int enemyUnitsIncoming = enemyUnitsApproaching.get(p);
                        int unitsOnPlanet = unitsAtPlanetWhenArrive(p, myPlanet) + enemyUnitsIncoming;

                        if (totalExpendableUnits > unitsOnPlanet && unitsOnPlanet < myUnitCount / 4
                                && expendableUnits > 0 && myUnitsIncoming < unitsOnPlanet) {
                            int unitsToSend = Math.min(unitsOnPlanet - myUnitsIncoming + 1, expendableUnits);
                            if (unitsToSend < 50 - totalUnitsSentThisTurn) {
                                actions.add(makeAction(myPlanet, p, unitsToSend));
                                // Update unit counts
                                myUnitsApproaching.put(p, myUnitsIncoming + unitsToSend);
                                myUnitsSent.put(myPlanet, unitsSentThisTurn + unitsToSend);
                                totalUnitsSentThisTurn += unitsToSend;
                            }
                        }
                    }
                }
            }
        }
    }

    ///////////////////////
    // OTHER //
    ///////////////////////

    private int getTotalExpendableUnits() {
        int totalExpendableUnits = 0;

        for (Planet p : myPlanets) {
            totalExpendableUnits += getExpendableUnits(p);
        }

        return totalExpendableUnits;
    }

    private int getExpendableUnits(Planet p) {
        return Math.min(Math.max(0, p.getNumUnits() - unitsNeededToProtectPlanet(p)), myUnitCount);
    }

    // Planet radius ranges from 12 - 50
    private int unitsNeededToProtectPlanet(Planet p) {
        return p.RADIUS / 4;
    }

    private int unitsAtPlanetWhenArrive(Planet dest, Planet src) {
        if (dest.isNeutral()) {
            return dest.getNumUnits();
        } else {
            double turns = (src.distanceTo(dest) / FLEET_SPEED);
            int updateCount = dest.getLifespan() % dest.PRODUCTION_TIME;
            int unitsProduced = (int)((turns + updateCount) / dest.PRODUCTION_TIME);
            if (updateCount == 0) {
                unitsProduced++;
            }
            return dest.getNumUnits() + unitsProduced;
        }
    }

    private class PlanetModel {
        int units;
        int prodTime;
        int radius;
        Planet planet;

        private PlanetModel(Planet p) {
            units = p.getNumUnits();
            prodTime = p.PRODUCTION_TIME;
            radius = p.RADIUS;
            planet = p;
        }
    }

    protected void nextGame() {
        defendPlanets = new TreeSet<>((Planet p1, Planet p2) -> p1.PRODUCTION_TIME - p2.PRODUCTION_TIME);
        firstTurn = true;
    }
}
