package galaxy;

public class Fleet extends Unit {
    public final Planet DESTINATION;
    public final Coords VELOCITY;
    private int ticsLeft;

    Fleet(int units, Planet start, Planet destination) {
        super(start.getOwner(), units, start);
        DESTINATION = destination;
        double distanceLeft = distanceTo(DESTINATION);
        VELOCITY = DESTINATION.subtract(this).multiply(GameSettings.FLEET_SPEED / distanceLeft);
        ticsLeft = (int)(distanceLeft / GameSettings.FLEET_SPEED) + 1;
    }

    public boolean targeting(Planet p) {
        return DESTINATION == p;
    }

    public int ticsLeft() {
        return ticsLeft;
    }

    public boolean hasHit() {
        return ticsLeft == 0;
    }

    @Override
    void update() {
        if (hasHit()) {
            System.err.println(String.format("Fleet %d being updated after reaching destination", ID));
            return;
        }

        setCoords(sum(VELOCITY));

        if (--ticsLeft == 0) {
            setCoords(DESTINATION); // TODO is this needed?
            DESTINATION.hitBy(this);
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
