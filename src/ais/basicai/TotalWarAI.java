package ais.basicai;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ais.oldais.LegacyPlayerWithUtils;
import galaxy.Action;
import galaxy.Fleet;
import galaxy.Planet;

/**
 * At the start of the game sends a fleet to take over the neutral planet with the fewest units.
 * Then sends all units at most productive enemy planet.
 */
public class TotalWarAI extends LegacyPlayerWithUtils {

    private ArrayList<Planet> planets;

    public TotalWarAI() {
        super(Color.WHITE, "Total War AI");
        setHandler(new PlayerHandler() {
            @Override
            public Collection<Action> turn(ArrayList<Fleet> fleets) {
                return makeTurn(fleets);
            }

            @Override
            public void newGame(ArrayList<Planet> newMap) {
                planets = newMap;
                nextGame();
            }
        });
    }

    protected void nextGame() {
        sentFleet = false;
    }

    private boolean sentFleet = false;

    protected Collection<Action> makeTurn(ArrayList<Fleet> fleets) {
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
            Planet target = opponentsPlanets.stream()
                    .min((a, b) -> Integer.compare(a.PRODUCTION_TIME, b.PRODUCTION_TIME)).get();
            for (Planet p : myPlanets) {
                actions.add(makeAction(p, target, 1000));
            }
        }
        return actions;
    }
}
