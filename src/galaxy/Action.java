package galaxy;

/**
 * Saves information about a Fleet a Player wants to launch.
 *
 * Will stop Players from creating Fleets that are invalid.
 *
 * Giving an invalid number of units will work here but be filtered out later without an error being made.
 */
public class Action {
    private final Planet START;
    private final Planet TARGET;
    private final int NUM_UNITS;

    /**
     * @param start Planet to launch the Fleet from. Will error if null or not owned by current.
     * @param target Planet to launch the Fleet at. Will error if null or same as start.
     * @param numUnits Number of units to send. If less than 1 the Action will be made but the Fleet will be
     *      ignored. If more than the units available in start it will only send as many as available.
     * @param current The Player attempting to make this Action, to check Action validity.
     */
    Action(Planet start, Planet target, int numUnits, Player current) {
        if (start == null) {
            throw new IllegalArgumentException("Attempted to make action to send fleet from null planet.");
        }

        if (target == null) {
            throw new IllegalArgumentException("Attempted to make action to send fleet to null planet.");
        }

        if (!start.ownedBy(current)) {
            throw new IllegalArgumentException("Player attempted to make an action for a planet not owned by them.");
        }

        if (start == target) {
            throw new IllegalArgumentException("Player attempted to send a fleet nowhere.");
        }

        START = start;
        TARGET = target;
        NUM_UNITS = numUnits;
    }

    /**
     * @return The Fleet that this Action designates.
     */
    final Fleet doAction() {
        return START.sendFleet(TARGET, NUM_UNITS);
    }

    @Override
    public final String toString() {
        return START.ID + " " + NUM_UNITS + " " + TARGET.ID;
    }
}
