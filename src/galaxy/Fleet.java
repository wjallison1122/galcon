package galaxy;

public class Fleet extends Unit {
    public final Planet DESTINATION;
    public final Coords VELOCITY;
    private int ticsLeft;

    /**
     * Creates a new Fleet. Should only be called from {link {@link Planet#sendFleet(Planet, int)}
     * @param units The number of units in the fleet.
     * @param start The Planet this Fleet starts from.
     * @param destination The Planet this Fleet is targeting.
     */
    Fleet(int units, Planet start, Planet destination) {
        super(start.getOwner(), units, start);
        DESTINATION = destination;
        double distanceLeft = distanceTo(DESTINATION);
        VELOCITY = DESTINATION.subtract(this).multiply(GameSettings.FLEET_SPEED / distanceLeft);
        ticsLeft = (int)(distanceLeft / GameSettings.FLEET_SPEED) + 1;
    }

    /**
     * Determines whether this Fleet is targeting the given Planet
     * @param p The Planet in question
     * @return Whether this Fleet is targeting that Planet
     */
    public boolean targeting(Planet p) {
        return DESTINATION == p;
    }

    /**
     * @return The number of game tics until the Fleet hits its target.
     */
    public int ticsLeft() {
        return ticsLeft;
    }

    /**
     * @return Whether the Fleet has hit its target yet.
     */
    public boolean hasHit() {
        return ticsLeft == 0;
    }

    /**
     * Moves the Fleet. Hits the target Planet if the Fleet has finished its travel.
     */
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

    /**
     * Will eventually have all the info needed to write games to file.
     */
    @Override
    public String toString() {
        // TODO Make this meaningful
        return super.toString();
    }
}
