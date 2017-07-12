package galaxy;

public final class Fleet extends Unit {
    public final Planet DESTINATION;
    private Coords velocity;
    private double distanceLeft;

    Fleet(int units, Planet start, Planet destination) {
        super(start.getOwner(), units, start);

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

    public Coords getVelocity() {
        return velocity;
    }

    @Override
    void update() {
        if (hasHit()) {
            error(String.format("Fleet %d being updated after reaching destination", ID));
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
