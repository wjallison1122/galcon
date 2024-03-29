package ais.oldais.jono;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ais.oldais.LegacyPlayerWithUtils;
import galaxy.Action;
import galaxy.Fleet;
import galaxy.Planet;

public class ValueDefenderAI extends LegacyPlayerWithUtils {
    private static int MIN_DEFENSE = 5;
    private ArrayList<Planet> planets;

    public ValueDefenderAI() {
        this(new Color(40, 0, 0));
    }

    public ValueDefenderAI(Color c) {
        super(c, "Value Defender AI");
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

    public double getValue(Planet p) {
        return (p.ownedByOpponentOf(this) ? 1400.0 : 1000.0) / p.PRODUCTION_TIME / (100 + p.getNumUnits());
    }

    protected Collection<Action> makeTurn(ArrayList<Fleet> fleets) {
        LinkedList<Action> actions = new LinkedList<Action>();
        List<Planet> myPlanets = getPlanetsOwnedByPlayer(planets, this);
        List<Planet> otherPlanets = getPlanetsNotOwnedByPlayer(planets, this);

        // boolean defending = false;

        Planet target = null;
        int needed = 0;
        for (Planet p : myPlanets) {
            needed = getOpponentsIncomingFleetCount(p, fleets, this) - p.getNumUnits()
                    - getPlayersIncomingFleetCount(p, fleets, this) + MIN_DEFENSE;
            if (needed > 0) {
                // defending = true;
                target = p;
                break;
            }
        }

        if (target == null) {
            double best = Double.MIN_VALUE;
            for (Planet p : otherPlanets) {
                double value = getValue(p);
                if (value > best) {
                    if (getPlayersIncomingFleetCount(p, fleets, this) == 0) {
                        target = p;
                        best = value;
                    }
                }
            }
            needed = target.getNumUnits() + 20;
        }

        int available = 0;
        for (Planet p : myPlanets) {
            if (p != target) {
                int contribution = p.getNumUnits() - getIncomingFleetCount(p, fleets) - MIN_DEFENSE;

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
