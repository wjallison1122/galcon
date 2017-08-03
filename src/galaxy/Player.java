package galaxy;

import java.awt.Color;
import java.util.Collection;
import java.util.LinkedList;

public abstract class Player {
    public final Color COLOR;
    public final String NAME;
    private static int currentId = 0;
    public final int ID = currentId++;

    private PlayerHandler handler;

    /**
     * Arbitrary creation of new players does not matter since ID is forced unique
     * and only main-created players can actually get asked for turns.
     */
    protected Player(Color c, String name) {
        COLOR = c;
        NAME = name;
    }

    final Collection<Action> turn(Fleet[] fleets) {
        Collection<Action> actions = handler.turn(fleets);
        return actions == null ? new LinkedList<Action>() : actions;
    }

    final void newGame(Planet[] newMap) {
        handler.newGame(newMap);
    }

    final void endGame(Player winner) {
        handler.endGame(winner);
    }

    protected final void setHandler(PlayerHandler handler) {
        if (this.handler == null) {
            this.handler = handler;
        }
    }

    protected Action makeAction(Planet start, Planet target, int numUnits) {
        return new Action(start, target, numUnits, this);
    }

    @Override
    public String toString() {
        return "" + ID + " ";
    }

    /**
     * Allows for Player functions to be called by Director thru Player but not other Players
     *
     */
    protected abstract class PlayerHandler {
        /**
         * Has the Player take their turn.
         * @param fleets The current fleets active.
         * @return The Actions the Player wishes to take.
         */
        protected abstract Collection<Action> turn(Fleet[] fleets);

        /**
         * Tells the Player a new game is starting.
         * @param planets The planets for the upcoming game.
         */
        protected abstract void newGame(Planet[] newMap);

        /**
         * Tells the Player the current game has ended.
         * @param winner The Player that won the game. Null if no winner.
         */
        protected void endGame(Player winner) {
        }
    }
}
