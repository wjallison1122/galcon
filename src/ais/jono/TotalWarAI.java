package ais.jono;

import java.awt.Color;
import java.util.List;

import ais.PlayerWithUtils;
import galaxy.Planet;

public class TotalWarAI extends PlayerWithUtils {

    public TotalWarAI() {
        super(Color.WHITE, "Total War AI");
    }

    @Override
    protected void newGame() {
        sentFleet = false;
    }

    private boolean sentFleet = false;

    @Override
    protected void turn() {
        List<Planet> myPlanets = getPlanetsOwnedByPlayer(planets, this);
        List<Planet> otherPlanets = getPlanetsNotOwnedByPlayer(planets, this);
        List<Planet> opponentsPlanets = getOpponentsPlanets(planets, this);

        Planet smallestUnoccupied = otherPlanets.stream()
                .min((a, b) -> Integer.compare(a.getNumUnits(), b.getNumUnits())).get();

        if (!sentFleet) {
            sentFleet = true;
            addAction(myPlanets.get(0), smallestUnoccupied, smallestUnoccupied.getNumUnits() + 1);
        }

        if (opponentsPlanets.size() > 0) {
            Planet target = opponentsPlanets.get(0);
            for (Planet p : myPlanets) {
                addAction(p, target, 1000);
            }
        }
    }
}
