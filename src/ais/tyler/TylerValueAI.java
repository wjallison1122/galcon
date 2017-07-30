package ais.tyler;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import ais.PlayerWithUtils;
import galaxy.Action;
import galaxy.Coords;
import galaxy.Fleet;
import galaxy.Planet;

public class TylerValueAI extends PlayerWithUtils {

    // CONSTANTS (only calculated once)
    private boolean firstTurn = true;
    private double farthestPlanetDistance = 0;

    // VARIABLES (update every turn)
    private int oppUnitCount = 0;
    private int myUnitCount = 0;
    private boolean winning = false;

    private boolean opponentMadeMove = false;
    private int turnCount = 0;

    private Planet[] planets;

    public TylerValueAI() {
        this(new Color(50, 100, 0));
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

    public TylerValueAI(Color c) {
        super(c, "Tyler Value AI");
    }

    protected Collection<Action> makeTurn(Fleet[] fleets) {
        if (firstTurn) {
            calculateConstants();
            firstTurn = false;
        }

        updateVariables(fleets);

        if (!opponentMadeMove && getOpponentsFleets(fleets, this).size() > 0) {
            opponentMadeMove = true;
        }

        Collection<Action> actions = new LinkedList<Action>();
        if (opponentMadeMove || turnCount > 200) {
            actions = valueAI(fleets);
        }

        turnCount++;

        return actions;
    }

    private void calculateConstants() {
        calculateFarthestPlanetDistance();
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

    private void updateVariables(Fleet[] fleets) {
        myUnitCount = getMyUnitCount(fleets, planets, this);
        oppUnitCount = getOpponentUnitCount(fleets, planets, this);

        winning = myUnitCount > oppUnitCount;
    }

    ///////////////////////
    // SORTING //
    ///////////////////////

    private void greedySort(List<Planet> planets, Coords center) {
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

        if (winning) {
            // prefer larger planets and not care as much about unit count or
            // range
            // (opponent planets slightly preferred).
            value += productionValue * 5;
            value += p.isNeutral() ? 0 : 0.5;
            value += unitValue;
            value += distValue;
        } else {
            // prefer planets with small unit count and closer range (neutral
            // planets
            // slightly preferred).
            value += productionValue * 0.5;
            value += p.isNeutral() ? 0.5 : 0;
            value += unitValue * 5;
            value += distValue;
        }

        return value;
    }

    ///////////////////////
    // AIs //
    ///////////////////////

    private Collection<Action> valueAI(Fleet[] fleets) {
        LinkedList<Action> actions = new LinkedList<Action>();
        List<Planet> myPlanets = getPlanetsOwnedByPlayer(planets, this);
        List<Planet> unownedPlanets = getUnoccupiedPlanets(planets);
        List<Planet> oppPlanets = getOpponentsPlanets(planets, this);
        List<Planet> otherPlanets = getPlanetsNotOwnedByPlayer(planets, this);

        if (myPlanets.size() == 0) {
            return actions;
        }

        /*
         * Sort all planets based upon size, units on the planet, and distance
         * from center of my planets.
         *
         * size 50 units 0 size 50 units 25 size 25 units 10
         *
         *
         *
         *
         *
         *
         *
         */

        Coords center = center(myPlanets);
        greedySort(otherPlanets, center);

        int expendableUnits = totalExpendableUnits(myPlanets);

        for (Planet p : otherPlanets) {
            distSort(myPlanets, p);
            if (getCurrentEventualOwner(p, fleets, this) != PlanetOwner.PLAYER) {
                int myUnitsEnRoute = getPlayersIncomingFleetCount(p, fleets, this);
                int unitsNeededToCapturePlanet = unitsNeededToCapturePlanet(p, fleets);
                if (expendableUnits > unitsNeededToCapturePlanet - myUnitsEnRoute) {
                    for (Planet myP : myPlanets) {
                        if (myUnitsEnRoute < unitsNeededToCapturePlanet) {
                            int unitsToSend = Math.min(unitsNeededToCapturePlanet, expendableUnits(myP));
                            if (unitsToSend > 0) {
                                actions.add(makeAction(myP, p, unitsToSend));
                                expendableUnits -= unitsToSend;
                                myUnitsEnRoute += unitsToSend;
                            }
                        }
                    }
                }
            }
        }

        //
        // greedySort(oppPlanets);
        // greedySort(otherPlanets);
        //
        // int expendableUnits = totalExpendableUnits(myPlanets);
        //
        // for (Planet p : oppPlanets) {
        // distSort(myPlanets, p);
        // if (getCurrentEventualOwner(p, fleets, this) !=
        // PlanetOwner.PLAYER) {
        // int myUnitsEnRoute = getPlayersIncomingFleetCount(p, fleets,
        // this);
        // int unitsNeededToCapturePlanet = unitsNeededToCapturePlanet(p);
        // if (expendableUnits > unitsNeededToCapturePlanet - myUnitsEnRoute) {
        // for (Planet myP : myPlanets) {
        // if (myUnitsEnRoute < unitsNeededToCapturePlanet) {
        // int unitsToSend = Math.min(unitsNeededToCapturePlanet,
        // expendableUnits(myP));
        // addAction(myP, p, unitsToSend);
        // expendableUnits -= unitsToSend;
        // myUnitsEnRoute += unitsToSend;
        // }
        // }
        // }
        // }
        // }
        //
        // // If we still have some extra units that haven't been sent to the
        // opponent,
        // // try to capture neutral planets (only ones with small unit count).
        // if (expendableUnits > 0) {
        // for (Planet p : otherPlanets) {
        // if (p.getNumUnits() < expendableUnits / 4) {
        // int myUnitsEnRoute = getPlayersIncomingFleetCount(p, fleets,
        // this);
        // int unitsNeededToCapturePlanet = unitsNeededToCapturePlanet(p);
        // if (myUnitsEnRoute < unitsNeededToCapturePlanet) {
        // for (Planet myP : myPlanets) {
        // if (myUnitsEnRoute < unitsNeededToCapturePlanet) {
        // int unitsToSend = Math.min(unitsNeededToCapturePlanet,
        // expendableUnits(myP));
        // addAction(myP, p, unitsToSend);
        // expendableUnits -= unitsToSend;
        // myUnitsEnRoute += unitsToSend;
        // }
        // }
        // }
        // }
        // }
        // }

        return actions;
    }

    ///////////////////////
    // UTILITES //
    ///////////////////////

    private int totalExpendableUnits(List<Planet> myPlanets) {
        int sum = 0;
        for (Planet p : myPlanets) {
            sum += expendableUnits(p);
        }
        return sum;
    }

    private int expendableUnits(Planet p) {
        return Math.max(0, p.getNumUnits() - p.RADIUS / 5);
    }

    // TODO: optimize this method
    private int unitsNeededToCapturePlanet(Planet p, Fleet[] fleets) {
        int myUnits = getPlayersIncomingFleetCount(p, fleets, this);
        int oppUnits = getOpponentsIncomingFleetCount(p, fleets, this);

        int unitsGeneratedByPlanet = (int)(distOfFarthestFleet(getMyFleets(fleets, this), p)) % p.PRODUCTION_TIME + 2;

        if (p.isNeutral()) {
            return (oppUnits + p.getNumUnits()) - myUnits + 15;
        } else {
            return (oppUnits + p.getNumUnits() + unitsGeneratedByPlanet) - myUnits + 15;
        }
    }

    private double distOfFarthestFleet(List<Fleet> fleets, Planet p) {
        double maxDist = 0;
        for (Fleet f : fleets) {
            if (f.targeting(p)) {
                double dist = f.distanceLeft();
                if (dist > maxDist) {
                    maxDist = dist;
                }
            }
        }
        return maxDist;
    }
}
