package ais.jono;

import java.awt.Color;
import java.util.List;

import ais.PlayerWithUtils;
import galaxy.Coords;
import galaxy.Planet;

public class DistanceValueDefenderAI extends PlayerWithUtils {
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
    // */

    public DistanceValueDefenderAI() {
        super(new Color(40, 0, 0), "Distance Value Defender AI");
    }

    public DistanceValueDefenderAI(Color c) {
        super(c, "Distance Value Defender AI");
    }

    public double getValue(Planet p, Coords averageLocation, double variance) {
        double distanceFactor = (variance + BASE_DISTANCE_FACTOR) / averageLocation.distanceTo(p);
        return (p.getColor().equals(Color.GRAY) ? 1.0 : AGGRESSION) * Math.pow(distanceFactor, DISTANCE_WEIGHTING)
                / p.PRODUCTION_TIME / (100 + p.getNumUnits());
    }

    @Override
    protected void turn() {
        List<Planet> myPlanets = getPlanetsOwnedByPlayer(planets, this);
        if (myPlanets.size() == 0) {
            return;
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
                return;
            }
            needed = target.getNumUnits() + 20;
        }

        int available = 0;
        for (Planet p : myPlanets) {
            if (p != target) {
                int contribution = p.getNumUnits() - getIncomingFleetCount(p, fleets)
                        - (defending ? MIN_DEFENSIVE_DEFENSE : MIN_AGGRESSIVE_DEFENSE);

                if (available + contribution > needed) {
                    addAction(p, target, needed - available);
                    available += contribution;
                    break;
                }
                available += contribution;
                addAction(p, target, contribution);
            }
        }

        if (available < needed) {
            clearActions();
        }
    }

    @Override
    protected void newGame() {

    }
}
