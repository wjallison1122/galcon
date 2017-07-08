package ais.jono;

import java.awt.Color;
import java.util.List;

import ais.PlayerWithUtils;
import galaxy.Planet;

public class CaptureNearestAI extends PlayerWithUtils {

    public CaptureNearestAI() {
        super(new Color(10, 10, 10), "Grow Empire AI");
    }

    @Override
    protected void newGame() {

    }

    @Override
    protected void turn() {
        List<Planet> myPlanets = getPlanetsOwnedByPlayer(planets, this);
        List<Planet> otherPlanets = getPlanetsNotOwnedByPlayer(planets, this);

        for (Planet p : myPlanets) {
            for (Planet pOther : sortByDistance(otherPlanets, p)) {
                if (p.getNumUnits() - getIncomingFleetCount(p, fleets) > 3 * pOther.getNumUnits()) {
                    int currentCount = getPlayersIncomingFleetCount(pOther, fleets, this);
                    if (currentCount == 0) {
                        addAction(p, pOther, 2 * pOther.getNumUnits() + 1);
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected String storeSelf() {
        return null;
    }

}
