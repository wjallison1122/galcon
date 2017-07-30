package ais.tyler;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import ais.PlayerWithUtils;
import galaxy.Action;
import galaxy.Fleet;
import galaxy.Planet;

public class TylerRandomAI extends PlayerWithUtils {

    private Planet[] planets;

    public TylerRandomAI() {
        this(new Color(50, 100, 0));
    }

    public TylerRandomAI(Color c) {
        super(c, "Pseudo Random AI");
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

    protected Collection<Action> makeTurn(Fleet[] fleets) {
        return pseudoRandomAI(fleets);
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

    // The higher the value, the better the planet is
    private int planetValue(Planet p) {
        return -p.getNumUnits();
    }

    ///////////////////////
    // AIs //
    ///////////////////////

    private Collection<Action> pseudoRandomAI(Fleet[] fleets) {
        LinkedList<Action> actions = new LinkedList<Action>();
        List<Planet> myPlanets = getPlanetsOwnedByPlayer(planets, this);
        List<Planet> otherPlanets = getPlanetsNotOwnedByPlayer(planets, this);

        greedySort(otherPlanets);

        for (Planet p : myPlanets) {
            if (p.getNumUnits() > 10) {
                int rand = (int)(Math.random() * 2);
                int index = 0;
                while (rand % 2 != 0) {
                    index++;
                    rand = (int)(Math.random() * 2);
                }
                if (otherPlanets.size() > 0) {
                    actions.add(makeAction(p, otherPlanets.get(index % otherPlanets.size()), 1));
                }
            }
        }

        return actions;
    }

    // private void randomAI() {
    // List<Planet> myPlanets = getPlanetsOwnedByPlayer(planets, this);
    //
    // for (Planet p : myPlanets) {
    // if (p.getNumUnits() > 10) {
    // addAction(p, planets[(int)(Math.random() * planets.length)], 1);
    // }
    // }
    // }
}
