package ais.human;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import galaxy.Action;
import galaxy.Fleet;
import galaxy.Planet;
import galaxy.Player;

public class MeatSackAI extends Player {

    private List<FutureAction> pendingActions = new ArrayList<>();
    private boolean autoAdvance = false;
    private int turnsToFinish = 0;
    private MeatSackDisplay display;
    private Planet[] planets;

    public class FutureAction {
        Planet source;
        Planet destination;
        int count;

        public FutureAction(Planet source, Planet destination, int count) {
            this.source = source;
            this.destination = destination;
            this.count = count;
        }

        Action build() {
            return makeAction(source, destination, count);
        }
    }

    public MeatSackAI() {
        super(Color.CYAN, "Fleshling");
        setHandler(new PlayerHandler() {
            @Override
            public Collection<Action> turn(Fleet[] fleets) {
                return makeTurn();
            }

            @Override
            public void newGame(Planet[] newMap) {
                planets = newMap;
                nextGame();
            }
        });
        display = new MeatSackDisplay(this);
    }

    public Collection<Action> makeTurn() {
        LinkedList<Action> actions = new LinkedList<Action>();
        display.updateBase();
        pendingActions.forEach(action -> actions.add(action.build()));
        pendingActions.clear();

        if (!autoAdvance) {
            while (turnsToFinish == 0 && !autoAdvance) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException(ex);
                }
            }

        }
        if (turnsToFinish > 0) {
            turnsToFinish--;
        }

        return actions;
    }

    public void finishTurns(int amt) {
        turnsToFinish = amt;
    }

    public Planet[] getPlanets() {
        return planets;
    }

    public void addAction(FutureAction action) {
        pendingActions.add(action);
    }

    // for testing
    void setPlanets(Planet[] planets) {
        this.planets = planets;
    }

    public boolean getAutoAdvance() {
        return autoAdvance;
    }

    public void setAutoAdvance(boolean autoAdvance) {
        this.autoAdvance = autoAdvance;
    }

    protected void nextGame() {
        display.newGame();
    }
}
