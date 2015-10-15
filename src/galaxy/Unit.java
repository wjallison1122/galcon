package galaxy;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

abstract class Unit {
   Player owner;
   Coordinates coords;
   int numUnits;

   static final boolean USE_EXPLOSIONS = Main.USE_EXPLOSIONS;

   static final Font FONT = new Font("Monospaced", Font.BOLD, 18);

   Unit(Player owner, Coordinates coords, int numUnits) {
      this.owner = owner;
      this.coords = coords;
      this.numUnits = numUnits;
   }

   Player getOwner() {
      return owner;
   }

   Coordinates getCoords() {
      return coords;
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

   Color invertColor(Color c) {
      Color newColor = new Color(255 - c.getRed(),
            255 - c.getGreen(),
            255 - c.getBlue());
      return newColor;
   }

   public double distanceTo(Planet p) {
      return coords.distanceTo(p.getCoords());
   }
}

class Coordinates {
   public static final int DIMENSIONS = Main.DIMENSIONS;
   private double[] coords;

   Coordinates(double ... coords) {
      setCoords(coords);
   }
   
   void setCoords(double ... coords) {
      if (coords.length != DIMENSIONS) {
         throw new RuntimeException("Invalid dimensions of coordinates given.");
      }
      this.coords = coords;
   }
   
   double[] getCoords() {
      return coords.clone();
   }
   
   public double distanceTo(Planet p) {
      return distanceTo(p.getCoords());
   }

   public double distanceTo(Coordinates c) {
      double sum = 0;
      for (int i = 0; i < DIMENSIONS; i++) {
         sum += Math.pow(coords[i] - c.coords[i], 2);
      }

      return Math.sqrt(sum);
   }

   void setCoords(ArrayList<Double> coords) {

   }
}
