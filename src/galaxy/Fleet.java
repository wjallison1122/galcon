package galaxy;

public final class Fleet extends Unit {
    public final Planet DESTINATION;
    private boolean hasHit = false;

    Fleet(int units, Player owner, Planet destination, double... coords) {
        super(owner, units, coords);

        if (destination == null) {
            throw new NullPointerException("Fleet destination was null.");
        }
        DESTINATION = destination;
    }

    public boolean targeting(Planet p) {
        return DESTINATION.equals(p);
    }

    public double distanceLeft() {
        return distanceTo(DESTINATION);
    }

    public boolean hasHit() {
        return hasHit;
    }

    boolean update() {
        if (hasHit) {
            error("Fleet being updated after reaching destination");
            return true;
        }

        double[] targetCoords = DESTINATION.getCoords();
        double[] fleetCoords = getCoords();
        double distance = distanceLeft();

        for (int i = 0; i < DIMENSIONS.length; i++) {
            fleetCoords[i] += (targetCoords[i] - fleetCoords[i]) / distance * FLEET_SPEED;
        }

        setCoords(fleetCoords);

        if (distance - FLEET_SPEED < 0) {
            setCoords(targetCoords);
            hasHit = true;
            DESTINATION.hitBy(this);
            return true;
        }

        return false;
    }

    String storeSelf() {
        return "";
    }
}
