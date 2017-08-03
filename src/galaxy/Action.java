package galaxy;

public class Action {
    private final Planet START;
    private final Planet TARGET;
    private final int NUM_UNITS;

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

        if (numUnits <= 0) {
            throw new IllegalArgumentException("Player attempted to send a fleet with " + numUnits + " units.");
        }

        START = start;
        TARGET = target;
        NUM_UNITS = numUnits;
    }

    final Fleet doAction() {
        return START.sendFleet(TARGET, NUM_UNITS);
    }

    @Override
    public final String toString() {
        return START.ID + " " + NUM_UNITS + " " + TARGET.ID;
    }
}
