package galaxy;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import ais.basicai.BasicAI;

/**
 * The base class for an AI.
 *
 * There are a couple of critical things:
 *
 * 1. You must have a constructor whose only param is a Color.
 * 2. You must have a constructor that takes no params.
 * 3. You must call setHandler to actually have your Player work.
 *
 * It is recommended to use a this() call in your empty constructor so that your
 * code for the handler only has to be in one of the constructors.
 *
 * An example of this design can be found in {@link BasicAI}.
 */
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

    final Collection<Action> turn(ArrayList<Fleet> fleets) {
        Collection<Action> actions = handler.turn(fleets);
        return actions == null ? new LinkedList<Action>() : actions;
    }

    final void newGame(ArrayList<Planet> newMap) {
        handler.newGame(newMap);
    }

    final void endGame(Player winner) {
        handler.endGame(winner);
    }

    protected final void setHandler(PlayerHandler newHandler) {
        if (handler == null) {
            handler = newHandler;
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
     * Allows for Player functions to be called by Director thru Player but not by other Players
     *
     */
    protected abstract class PlayerHandler {
        /**
         * Has the Player take their turn.
         * @param fleets The current fleets active.
         * @return The Actions the Player wishes to take.
         */
        protected abstract Collection<Action> turn(ArrayList<Fleet> fleets);

        /**
         * Tells the Player a new game is starting.
         * @param planets The planets for the upcoming game.
         */
        protected abstract void newGame(ArrayList<Planet> newMap);

        /**
         * Tells the Player the current game has ended.
         * @param winner The Player that won the game. Null if no winner.
         */
        protected void endGame(Player winner) {
        }
    }
}
