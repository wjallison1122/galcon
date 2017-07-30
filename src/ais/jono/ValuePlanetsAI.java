package ais.jono;

import java.awt.Color;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ais.PlayerWithUtils;
import galaxy.Action;
import galaxy.Fleet;
import galaxy.Planet;

public class ValuePlanetsAI extends PlayerWithUtils {
    private static int MIN_DEFENSE = 15;
    private Planet[] planets;

    public ValuePlanetsAI() {
        super(Color.RED, "Value Planets AI");
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

    public double getValue(Planet p) {
        return (p.ownedByOpponentOf(this) ? 1400.0 : 1000.0) / p.PRODUCTION_TIME / (100 + p.getNumUnits());
    }

    protected Collection<Action> makeTurn(Fleet[] fleets) {
        LinkedList<Action> actions = new LinkedList<Action>();
        List<Planet> myPlanets = getPlanetsOwnedByPlayer(planets, this);
        List<Planet> otherPlanets = getPlanetsNotOwnedByPlayer(planets, this);

        Planet target = null;
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

        int needed = target.getNumUnits() + 20;
        int available = 0;
        for (Planet p : myPlanets) {
            int contribution = p.getNumUnits() - getIncomingFleetCount(p, fleets) - MIN_DEFENSE;

            if (available + contribution > needed) {
                actions.add(makeAction(p, target, needed - available));
                available += contribution;
                break;
            }
            available += contribution;
            actions.add(makeAction(p, target, contribution));
        }

        if (available < needed) {
            actions.clear();
        }

        return actions;
    }
}
