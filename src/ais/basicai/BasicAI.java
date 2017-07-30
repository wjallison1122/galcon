package ais.basicai;

import java.awt.Color;
import java.util.Collection;
import java.util.LinkedList;

import galaxy.Action;
import galaxy.Fleet;
import galaxy.Planet;
import galaxy.Player;

public class BasicAI extends Player {
    private Planet[] planets;

    public BasicAI() {
        super(Color.BLUE, "James");
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
        LinkedList<Action> actions = new LinkedList<Action>();
        Planet hitter = null;
        Planet hitted = null;

        for (Planet p : planets) {
            if (p.ownedBy(this) && (hitter == null || Math.random() > .5)) {
                hitter = p;
            }

            if (!p.ownedBy(this) && (hitted == null || Math.random() > .8)) {
                hitted = p;
            }
        }

        if (hitter != null && hitted != null) {
            actions.add(makeAction(hitter, hitted, (int)(Math.random() * hitter.getNumUnits() / 4)));
        }
        return actions;
    }
}
