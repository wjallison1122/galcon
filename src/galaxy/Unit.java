package galaxy;

import java.awt.Color;

public abstract class Unit {
   Player owner;
   private double[] coords;
   int numUnits;
   public Color debugColor;

   Unit(Player owner, int numUnits, double ... coords) {
      this.owner = owner;
      this.numUnits = numUnits;
      this.coords = coords;
   }

   protected final void debug(String str) {
      if (Main.debugMode) {
         System.out.println(str);
      }
   }

   public final Player getOwner() {
      return owner;
   }

   public final double[] getCoords() {
      return coords.clone();
   }

   final void setCoords(double ... coords) {
      if (coords.length != Main.DIMENSIONS.length) {
         throw new RuntimeException("Invalid dimensions of coordinates given.");
      }
      this.coords = coords.clone();
   }

   @SuppressWarnings("unused")
   public final Color getColor() {
      return Main.debugMode && debugColor != null ? debugColor : (owner == null ? Color.GRAY : owner.COLOR);
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
      for (int i = 0; i < Main.DIMENSIONS.length; i++) {
         sum += Math.pow(coords[i] - otherCoords[i], 2);
      }
      return Math.sqrt(sum);
   }

   public final double distanceTo(Unit u) {
      return distanceTo(u.getCoords());
   }
}






