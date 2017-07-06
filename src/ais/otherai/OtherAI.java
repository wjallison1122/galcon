package ais.otherai;

import galaxy.Planet;
import galaxy.Player;

import java.awt.Color;

public class OtherAI extends Player {

    public OtherAI() {
        super(Color.RED, "Brian");

    }

    void inheritanceTesting() {
        // Player asdf = new BasicAI();
        // String str = asdf.NAME;
        // asdf.doTurn();
        // asdf.nextGame();
        // asdf.newGame();
        // asdf.addAction(null, null, 0);
        // Planet[] ps = asdf.planets;
        // asdf.clearActions();
    }

    @Override
    protected void turn() {
        debug("Making a turn");
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
            debug("Made an action!");
            addAction(hitter, hitted, (int) (Math.random() * hitter.getNumUnits() / 4));
        } else {
            debug("Something..." + (hitter == null) + " ? " + (hitted == null));
        }
    }

    @Override
    protected void newGame() {

    }

    @Override
    protected String storeSelf() {
        return "";
    }
}
