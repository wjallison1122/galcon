package ais.oldais.jono;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ais.oldais.LegacyPlayerWithUtils;
import galaxy.Action;
import galaxy.Coords;
import galaxy.Fleet;
import galaxy.Planet;

public class DistanceValueDefenderAI extends LegacyPlayerWithUtils {
    /*
     * defeats aggressively tuned value defender private static final int
     * MIN_AGGRESSIVE_DEFENSE = 50; private static final int
     * MIN_DEFENSIVE_DEFENSE = 2; private static final double
     * BASE_DISTANCE_FACTOR = 1000; private static final double
     * DISTANCE_WEIGHTING = 0.3; private static final double AGGRESSION = 1.2;
     * //
     */

    // * defeats value defender beater
    private static final int MIN_AGGRESSIVE_DEFENSE = 10;
    private static final int MIN_DEFENSIVE_DEFENSE = 0;
    private static final double BASE_DISTANCE_FACTOR = 10;
    private static final double DISTANCE_WEIGHTING = 1;
    private static final double AGGRESSION = 0.8;

    private ArrayList<Planet> planets;

    LinkedList<Action> actions = new LinkedList<Action>();
    // */

    public DistanceValueDefenderAI() {
        this(new Color(40, 0, 0));
    }

    public DistanceValueDefenderAI(Color c) {
        super(c, "Distance Value Defender AI");
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

    public double getValue(Planet p, Coords averageLocation, double variance) {
        double distanceFactor = (variance + BASE_DISTANCE_FACTOR) / averageLocation.distanceTo(p);
        return (p.getColor().equals(Color.GRAY) ? 1.0 : AGGRESSION) * Math.pow(distanceFactor, DISTANCE_WEIGHTING)
                / p.PRODUCTION_TIME / (100 + p.getNumUnits());
    }

    protected Collection<Action> makeTurn(ArrayList<Fleet> fleets) {
        actions = new LinkedList<Action>();
        List<Planet> myPlanets = getPlanetsOwnedByPlayer(planets, this);
        if (myPlanets.size() == 0) {
            return actions;
        }
        List<Planet> otherPlanets = getPlanetsNotOwnedByPlayer(planets, this);

        boolean defending = false;
        Planet target = null;
        int needed = 0;
        for (Planet p : myPlanets) {
            needed = getOpponentsIncomingFleetCount(p, fleets, this) - p.getNumUnits()
                    - getPlayersIncomingFleetCount(p, fleets, this) + MIN_DEFENSIVE_DEFENSE;
            if (needed > 0) {
                target = p;
                defending = true;
                break;
            }
        }

        Coords average = center(myPlanets);
        double variance = variance(myPlanets);

        if (target == null) {
            double best = Double.MIN_VALUE;
            for (Planet p : otherPlanets) {
                double value = getValue(p, average, variance);
                if (value > best) {
                    if (getPlayersIncomingFleetCount(p, fleets, this) == 0) {
                        target = p;
                        best = value;
                    }
                }
            }
            if (target == null) {
                return actions;
            }
            needed = target.getNumUnits() + 20;
        }

        int available = 0;
        for (Planet p : myPlanets) {
            if (p != target) {
                int contribution = p.getNumUnits() - getIncomingFleetCount(p, fleets)
                        - (defending ? MIN_DEFENSIVE_DEFENSE : MIN_AGGRESSIVE_DEFENSE);

                if (available + contribution > needed) {
                    actions.add(makeAction(p, target, needed - available));
                    available += contribution;
                    break;
                }
                available += contribution;
                actions.add(makeAction(p, target, contribution));
            }
        }

        if (available < needed) {
            actions.clear();
        }

        return actions;
    }
}
