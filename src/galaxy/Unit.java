package galaxy;

import java.awt.Color;

public abstract class Unit extends Coords {
    Player owner;
    int numUnits;

    // TODO add game ID
    private static int id = 0;
    public final int ID = id++;
    public Color debugColor; // Where is this used?

    Unit(Player owner, int numUnits, Coords coords) {
        super(coords.getCoords());
        this.owner = owner;
        this.numUnits = numUnits;
    }

    public static final int getLatestID() {
        return id;
    }

    void update() {
    }

    /**
     * Checks whether a Unit is equal to another Unit.
     * This checks based off their IDs. As such, two otherwise identical Units are
     * actually still two distinct Units.
     *
     * For a null-safe way to check two potentially null Units use areEqual
     *
     * In the current implementation this will be equivalent to just using == but
     * that may not always be the case and it's generally not great practice.
     *
     * @param u The Unit to compare to
     */
    @Override
    public final boolean equals(Object u) {
        return u instanceof Unit ? ((Unit)u).ID == ID : false;
    }

    public static final boolean areEqual(Unit u1, Unit u2) {
        return u1 == null || u2 == null ? u1 == u2 : u1.ID == u2.ID;
    }

    public final Player getOwner() {
        return owner;
    }

    public final Color getColor() {
        return debugMode && debugColor != null ? debugColor : (owner == null ? Color.GRAY : owner.COLOR);
    }

    public final int getNumUnits() {
        return numUnits;
    }

    public final boolean ownedBy(Player player) {
        return Player.areEqual(owner, player);
    }

    public final boolean ownedByOpponentOf(Player p) {
        return !isNeutral() && !ownedBy(p);
    }

    public final boolean isNeutral() {
        return owner == null;
    }

    /**
     * E-Z null protection
     *
     * @param u
     * @param player
     * @return
     */
    public final static boolean unitOwnedBy(Unit u, Player p) {
        return u == null ? false : u.ownedBy(p);
    }

    public final static boolean unitOwnedByOpponentOf(Unit u, Player p) {
        return u == null ? false : u.ownedByOpponentOf(p);
    }

    public final double distanceTo(Unit u) {
        return distanceTo(u.getCoords());
    }
}
