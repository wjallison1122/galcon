package galaxy;

import java.awt.Color;

public abstract class Unit extends Coords {
    Player owner;
    int numUnits;

    private static int id = 0;
    public final int ID = id++;

    Unit(Player owner, int numUnits, Coords coords) {
        super(coords);
        this.owner = owner;
        this.numUnits = numUnits;
    }

    public static final int getLatestID() {
        return id;
    }

    void update() {
    }

    public final Player getOwner() {
        return owner;
    }

    public final Color getColor() {
        return owner == null ? Color.GRAY : owner.COLOR;
    }

    public final int getNumUnits() {
        return numUnits;
    }

    /**
     * Says whether the given player owns this planet.
     * @param player
     * @return
     */
    public final boolean ownedBy(Player player) {
        return owner == player;
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
