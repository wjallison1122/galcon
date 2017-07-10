package galaxy;

public final class Fleet extends Unit {
    public final Planet DESTINATION;
    private Coords velocity;
    private double distanceLeft;

    Fleet(int units, Player owner, Planet destination, double... coords) {
        super(owner, units, coords);

        if (destination == null) {
            throw new NullPointerException("Fleet destination was null.");
        }
        DESTINATION = destination;

        distanceLeft = distanceTo(DESTINATION);
        velocity = DESTINATION.subtract(this).multiply(FLEET_SPEED / distanceLeft);
    }

    public boolean targeting(Planet p) {
        return DESTINATION.equals(p);
    }

    public double distanceLeft() {
        return distanceLeft;
    }

    public boolean hasHit() {
        return distanceLeft == 0;
    }

    @Override
    void update() {
        if (hasHit()) {
            error("Fleet being updated after reaching destination");
            return;
        }

        distanceLeft -= FLEET_SPEED;
        setCoords(sum(velocity));

        if (distanceLeft < 0) {
            setCoords(DESTINATION); // TODO is this needed?
            distanceLeft = 0;
            DESTINATION.hitBy(this);
        }
    }

    String storeSelf() {
        return "";
    }
}
