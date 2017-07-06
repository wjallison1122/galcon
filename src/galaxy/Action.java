package galaxy;

public class Action extends GameSettings {
    private final Planet START;
    private final Planet TARGET;
    private final int NUM_UNITS;

    Action(Planet start, Planet target, int numUnits, Player current) {
        if (start == null) {
            throw new InvalidActionException("Attempted to make action to send fleet from null planet.");
        }

        if (target == null) {
            throw new InvalidActionException("Attempted to make action to send fleet to null planet.");
        }

        if (!start.ownedBy(current)) {
            throw new InvalidActionException("Player attempted to make an action for a planet not owned by them.");
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
        return gameTic() + " " + START.ID + " " + NUM_UNITS + " " + TARGET.ID;
    }

    @SuppressWarnings("serial")
    public class InvalidActionException extends RuntimeException {
        public InvalidActionException(String msg) {
            super(msg);
        }
    }
}
