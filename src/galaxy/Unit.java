package galaxy;

import java.awt.Color;

abstract class Unit {
   Player owner;
   double[] coords;
   int numUnits;

   static final boolean USE_EXPLOSIONS = Main.USE_EXPLOSIONS;


   Unit(Player owner, int numUnits, double ... coords) {
      this.owner = owner;
      this.numUnits = numUnits;
      this.coords = coords;
   }

   Player getOwner() {
      return owner;
   }

   public double[] getCoords() {
      return coords.clone();
   }
   
   void setCoords(double ... coords) {
      if (coords.length != Main.DIMENSIONS) {
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
   
   public double distanceTo(double ... otherCoords) {
      double sum = 0;
      for (int i = 0; i < Main.DIMENSIONS; i++) {
         sum += Math.pow(coords[i] - otherCoords[i], 2);
      }
      return Math.sqrt(sum);
   }

   public double distanceTo(Planet p) {
      return distanceTo(p.getCoords());
   }
}






