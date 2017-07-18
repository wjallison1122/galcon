package ais.basicai;

import java.awt.Color;

import galaxy.Planet;
import galaxy.Player;

public class BasicAI extends Player {

    public BasicAI() {
        super(Color.BLUE, "James");

    }

    @Override
    protected void turn() {
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
            addAction(hitter, hitted, (int)(Math.random() * hitter.getNumUnits() / 4));
        }
    }

    @Override
    protected void newGame() {

    }
}
