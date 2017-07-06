package galaxy;

import java.awt.Color;

public abstract class Unit extends GameSettings {
   Player owner;
   private double[] coords;
   int numUnits;
   int ticsActive = 0;

   // TODO add game ID 
   private static int id = 0;
   public final int ID = id++;
   public Color debugColor; // Where is this used?

   Unit(Player owner, int numUnits, double ... coords) {
      this.owner = owner;
      this.numUnits = numUnits;
      this.coords = coords;
   }
   
   public static final int getLatestID() {
      return id;
   }
   
   public final boolean equals(Unit u) {
      return u == null ? false : u.ID == ID;
   }
   
   public static final boolean areEqual(Unit u1, Unit u2) {
      return u1 == null || u2 == null ? u1 == u2 : u1.ID == u2.ID;
   }

   public final Player getOwner() {
      return owner;
   }

   public final double[] getCoords() {
      return coords.clone();
   }

   final void setCoords(double ... coords) {
      if (coords.length != DIMENSIONS.length) {
         throw new DimensionMismatchException("Invalid dimensions of coordinates given.");
      }
      this.coords = coords.clone();
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

   public final double distanceTo(double ... otherCoords) {
      double sum = 0;
      for (int i = 0; i < DIMENSIONS.length; i++) {
         sum += Math.pow(coords[i] - otherCoords[i], 2);
      }
      return Math.sqrt(sum);
   }

   public final double distanceTo(Unit u) {
      return distanceTo(u.getCoords());
   }
}






