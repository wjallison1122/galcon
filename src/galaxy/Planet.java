package galaxy;

public class Planet extends Unit {
    public final int RADIUS, PRODUCTION_TIME;
    private int lifespan = 0;
    private boolean recentlyConquered = false;

    /**
     * Creates a new Planet.
     * @param owner Who this Planet starts out belonging to.
     * @param numUnits The number of units this Planet starts with.
     * @param radius The radius of this Planet.
     * @param prodTime The number of tics between creating units.
     * @param coords The location of this Planet.
     */
    Planet(Player owner, int numUnits, int radius, int prodTime, Coords coords) {
        super(owner, numUnits, coords);
        RADIUS = radius;
        PRODUCTION_TIME = prodTime;
    }

    /**
     * Adds a unit to this Planet if it has been long enough.
     */
    @Override
    void update() {
        if (!ownedBy(null) && lifespan++ % PRODUCTION_TIME == 0) {
            numUnits++;
        }
    }

    /**
     * Used to determine whether to create a unit on a tic.
     * @return How many tics it has been since the Planet was last conquered.
     */
    public int getLifespan() {
        return lifespan;
    }

    /**
     * Called by a Fleet when it hits this Planet.
     * If it is a friendly Fleet the units are added to this Planet.
     * If it is an enemy Fleet the units are removed.
     * If the units go negative control of the Planet goes to the owner of the attacking Fleet, with leftover units.
     * If the units go to zero the Planet returns to neutral.
     * @param f The Fleet hitting this Planet.
     */
    void hitBy(Fleet f) {
        if (f.ownedBy(owner)) {
            numUnits += f.getNumUnits();
        } else {
            numUnits -= f.getNumUnits();
            if (numUnits < 0) {
                recentlyConquered = true; // Variable for visualizer, visualizer resets planet.
                owner = f.getOwner();
                numUnits *= -1;
                lifespan = 0;
            } else if (numUnits == 0) {
                owner = null;
            }
        }
    }

    /**
     * @return The inverse of production time.
     */
    public double getProductionFrequency() {
        return 1. / PRODUCTION_TIME;
    }

    /**
     * For visualizer to check if a planet was recently conquered.
     *
     * This should ONLY be called by the active visualizer. This is not for Players.
     *
     * @return Whether this planet had changed hands since last checked
     */
    boolean checkRecentlyConquered() {
        return recentlyConquered && !(recentlyConquered = false);
    }

    /**
     * Launches a Fleet at the target planet.
     * Attempting to send negative units will send 0 units.
     * Fleets with less than 1 unit will be ignored.
     * Should only be called from {link {@link Action#doAction()}
     * @param target The Planet to send the Fleet at.
     * @param numSent The number of units to send.
     * @return The Fleet sent.
     */
    Fleet sendFleet(Planet target, int numSent) {
        numSent = Math.min(Math.max(0, numSent), numUnits);
        numUnits -= numSent;
        return new Fleet(numSent, this, target);
    }

    /**
     * Will eventually have all the info needed to write games to file.
     */
    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * Prevents players from trying to keep using this Planet.
     * With owner null and remaining fleets in air not being updated, no fleets
     * can be sent from this planet.
     */
    void terminate() {
        owner = null;
    }
}
