package galaxy;

public class Planet extends Unit {

    public final int RADIUS, PRODUCTION_TIME;
    private int lifespan = 0;
    private boolean recentlyConquered = false;

    Planet(Player owner, int numUnits, int radius, int prodTime, Coords coords) {
        super(owner, numUnits, coords);
        RADIUS = radius;
        PRODUCTION_TIME = prodTime;
    }

    @Override
    void update() {
        if (!ownedBy(null) && lifespan++ % PRODUCTION_TIME == 0) {
            numUnits++;
        }
    }

    public int getLifespan() {
        return lifespan;
    }

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

    Fleet sendFleet(Planet target, int numSent) {
        numSent = Math.min(numSent, numUnits);
        numUnits -= numSent;
        return new Fleet(numSent, this, target);
    }

    @Override
    public String toString() {
        return "";
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
