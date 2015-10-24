package galaxy;

import java.awt.Color;

abstract class Unit {
   Player owner;
   private double[] coords;
   int numUnits;

   Unit(Player owner, int numUnits, double ... coords) {
      this.owner = owner;
      this.numUnits = numUnits;
      this.coords = coords;
   }

   protected void debug(String str) {
      if (Main.debugMode) {
         System.out.println(str);
      }
   }

   Player getOwner() {
      return owner;
   }

   public double[] getCoords() {
      return coords.clone();
   }

   void setCoords(double ... coords) {
      if (coords.length != Main.DIMENSIONS.length) {
         throw new RuntimeException("Invalid dimensions of coordinates given.");
      }
      this.coords = coords.clone();
   }

   public Color getColor() {
      return owner == null ? Color.GRAY : owner.COLOR;
   }

   public int getNumUnits() {
      return numUnits;
   }

   public boolean ownedBy(Player player) {
      return Player.areEqual(owner, player);
   }
   
   public boolean ownedByOpponentOf(Player p) {
      return !isNeutral() && !ownedBy(p);
   }
   
   public boolean isNeutral() {
      return owner == null;
   }

   /**
    * E-Z null protection
    * @param u
    * @param player
    * @return
    */
   public static boolean unitOwnedBy(Unit u, Player p) {
      return u == null ? false : u.ownedBy(p);
   }

   public static boolean unitOwnedByOpponentOf(Unit u, Player p) {
      return u == null ? false : u.ownedByOpponentOf(p);
   }

   public double distanceTo(double ... otherCoords) {
      double sum = 0;
      for (int i = 0; i < Main.DIMENSIONS.length; i++) {
         sum += Math.pow(coords[i] - otherCoords[i], 2);
      }
      return Math.sqrt(sum);
   }

   public double distanceTo(Unit u) {
      return distanceTo(u.getCoords());
   }
}






