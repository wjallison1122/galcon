package ais.oldais.tyler;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import ais.oldais.LegacyPlayerWithUtils;
import galaxy.Action;
import galaxy.Fleet;
import galaxy.Planet;

public class TylerAI extends LegacyPlayerWithUtils {

    private static final int MAX_UNIT_SPREAD_TO_DETECT_STALEMATE = 10;

    private boolean opponentMadeMove = false;
    private boolean stalemate = false;
    private int turnCount = 0;
    private int maxUnits = 0;
    private int minUnits = 1000;
    private int stalemateTimer = 0;

    private ArrayList<Planet> planets;

    public TylerAI() {
        super(new Color(50, 100, 0), "Tyler AI");
        setHandler(new PlayerHandler() {
            @Override
            public Collection<Action> turn(ArrayList<Fleet> fleets) {
                return makeTurn(fleets);
            }

            @Override
            public void newGame(ArrayList<Planet> newMap) {
                planets = newMap;
            }
        });
    }

    public TylerAI(Color c) {
        super(c, "Tyler AI");
    }

    protected Collection<Action> makeTurn(ArrayList<Fleet> fleets) {
        if (getOpponentsFleets(fleets, this).size() > 0) {
            opponentMadeMove = true;
        }

        int myUnits = getMyUnitCount(fleets, planets, this);
        if (myUnits > maxUnits) {
            maxUnits = myUnits;
            if (maxUnits - minUnits > MAX_UNIT_SPREAD_TO_DETECT_STALEMATE) {
                minUnits = maxUnits - MAX_UNIT_SPREAD_TO_DETECT_STALEMATE;
                stalemateTimer = 0;
                stalemate = false;
            }
        }
        if (myUnits < minUnits) {
            minUnits = myUnits;
            if (maxUnits - minUnits > MAX_UNIT_SPREAD_TO_DETECT_STALEMATE) {
                maxUnits = minUnits + MAX_UNIT_SPREAD_TO_DETECT_STALEMATE;
                stalemateTimer = 0;
                stalemate = false;
            }
        }

        if (stalemateTimer > 500) {
            stalemate = false;
            stalemateTimer = 0;
        } else if (stalemateTimer > 200) {
            stalemate = true;
        }

        LinkedList<Action> actions = new LinkedList<Action>();
        if ((opponentMadeMove || turnCount > 200) && !stalemate) {
            actions = firstAI(fleets);
        }

        turnCount++;
        stalemateTimer++;
        return actions;
    }

    ///////////////////////
    // SORTING //
    ///////////////////////

    private void greedySort(List<Planet> planets) {
        Collections.sort(planets, new Comparator<Planet>() {
            @Override
            public int compare(Planet p1, Planet p2) {
                return planetValue(p2) - planetValue(p1);
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

    // The higher the value, the better the planet is
    private int planetValue(Planet p) {
        // return -p.getNumUnits();

        int value1 = p.PRODUCTION_TIME + p.getNumUnits() * 5;// +
                                                             // p.distanceTo(p1)
                                                             // / 5);
        value1 += p.getColor().equals(Color.GRAY) ? 0 : 2;
        return -value1;
    }

    ///////////////////////
    // AIs //
    ///////////////////////

    private LinkedList<Action> firstAI(ArrayList<Fleet> fleets) {
        LinkedList<Action> actions = new LinkedList<Action>();
        List<Planet> myPlanets = getPlanetsOwnedByPlayer(planets, this);
        List<Planet> otherPlanets = getUnoccupiedPlanets(planets);
        List<Planet> oppPlanets = getOpponentsPlanets(planets, this);

        greedySort(oppPlanets);
        greedySort(otherPlanets);

        int expendableUnits = totalExpendableUnits(myPlanets);

        for (Planet p : oppPlanets) {
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

        // If we still have some extra units that haven't been sent to the
        // opponent,
        // try to capture neutral planets (only ones with small unit count).
        if (expendableUnits > 0) {
            for (Planet p : otherPlanets) {
                if (p.getNumUnits() < expendableUnits / 4) {
                    int myUnitsEnRoute = getPlayersIncomingFleetCount(p, fleets, this);
                    int unitsNeededToCapturePlanet = unitsNeededToCapturePlanet(p, fleets);
                    if (myUnitsEnRoute < unitsNeededToCapturePlanet) {
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
        }
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
    private int unitsNeededToCapturePlanet(Planet p, ArrayList<Fleet> fleets) {
        int myUnits = getPlayersIncomingFleetCount(p, fleets, this);
        int oppUnits = getOpponentsIncomingFleetCount(p, fleets, this);

        int unitsGeneratedByPlanet = distOfFarthestFleet(getFleetsOfPlayer(fleets, this), p) % p.PRODUCTION_TIME + 2;

        if (p.isNeutral()) {
            return (oppUnits + p.getNumUnits()) - myUnits + 1;
        } else {
            return (oppUnits + p.getNumUnits() + unitsGeneratedByPlanet) - myUnits + 1;
        }
    }
}
