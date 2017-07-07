package galaxy;

public final class Planet extends Unit {

    public final int RADIUS, PRODUCTION_TIME;
    private int lifespan = 0;
    private boolean recentlyConquered = false;

    Planet(Player owner, int numUnits, int radius, int prodTime, double... coords) {
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
        return 1. / this.PRODUCTION_TIME;
    }

    /**
     * For visualizer to check if a planet was recently conquered.
     *
     * @return Whether this planet had changed hands since last checked
     */
    boolean checkRecentlyConquered() {
        boolean recent = recentlyConquered;
        recentlyConquered = false;
        return recent;
    }

    Fleet sendFleet(Planet target, int numSent) {
        numSent = Math.min(numSent, numUnits);
        if (numSent > 0) {
            numUnits -= numSent;
            return new Fleet(numSent, owner, target, getCoords());
        } else {
            return null;
        }
    }

    String storeSelf() {
        return "";
    }

    void terminate() {
        numUnits = -1;
        owner = null;
    }
}
