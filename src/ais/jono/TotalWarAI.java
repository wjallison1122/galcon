package ais.jono;

import java.awt.Color;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ais.PlayerWithUtils;
import galaxy.Action;
import galaxy.Fleet;
import galaxy.Planet;

public class TotalWarAI extends PlayerWithUtils {

    private Planet[] planets;

    public TotalWarAI() {
        super(Color.WHITE, "Total War AI");
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

    protected void nextGame() {
        sentFleet = false;
    }

    private boolean sentFleet = false;

    protected Collection<Action> makeTurn(Fleet[] fleets) {
        LinkedList<Action> actions = new LinkedList<Action>();
        List<Planet> myPlanets = getPlanetsOwnedByPlayer(planets, this);
        List<Planet> otherPlanets = getPlanetsNotOwnedByPlayer(planets, this);
        List<Planet> opponentsPlanets = getOpponentsPlanets(planets, this);

        Planet smallestUnoccupied = otherPlanets.stream()
                .min((a, b) -> Integer.compare(a.getNumUnits(), b.getNumUnits())).get();

        if (!sentFleet) {
            sentFleet = true;
            actions.add(makeAction(myPlanets.get(0), smallestUnoccupied, smallestUnoccupied.getNumUnits() + 1));
        }

        if (opponentsPlanets.size() > 0) {
            Planet target = opponentsPlanets.get(0);
            for (Planet p : myPlanets) {
                actions.add(makeAction(p, target, 1000));
            }
        }
        return actions;
    }
}
