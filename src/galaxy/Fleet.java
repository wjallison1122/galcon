package galaxy;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

public class Fleet extends Unit
{
   public static final double SPEED = Main.FLEET_SPEED;

   private Planet destination;

   private static LinkedList<Fleet> fleets = new LinkedList<Fleet>();

   Fleet(Coordinates c, int units, Player owner, Planet target) {
      super(owner, c, units);
      destination = target;

      fleets.add(this);
   }
   
   public Planet getDestination() {
      return destination;
   }

   public double distanceLeft() {
      return distanceTo(destination);
   }

   static LinkedList<Fleet> getAllFleets() {
      return fleets;
   }

   private void update() {
//      double a = destination.X - x; //Distance to the planet vertically
//      double b = destination.Y - y;
//      double c = Math.sqrt(a * a + b * b);
      
      double[] targetCoords = destination.getCoords().getCoords();
      double[] newCoords = new double[Coordinates.DIMENSIONS];
      
      
      for (int i = 0; i < Coordinates.DIMENSIONS; i++) {
         newCoords[i] = targetCoords[i];
      }

//      x += (a / c) * SPEED;
//      y += (b / c) * SPEED;

      if(distanceLeft() < 0) {
         destination.hitBy(this);
         fleets.remove(this);
      }
   }

   static void updateAll() {
      for(int i = 0; i < fleets.size(); i++) {
         fleets.get(i).update();
      }
   }

   static void drawAll(Graphics g) {
      for(Fleet f : fleets) {
         f.draw(g);
      }
   }

   private void draw(Graphics g) {
      int arbitraryRadius = 10 + numUnits / 5;
      Color c = getColor();
      g.setColor(c);
//      g.fillOval((int)x - arbitraryRadius, (int)y - arbitraryRadius, arbitraryRadius *2, arbitraryRadius*2);
      g.setFont(FONT);
      g.setColor(invertColor(c));
//      g.drawString("" + numUnits, (int)(x - 8), (int)(y + 5));

   }

   static void clear() {
      fleets.clear();
   }

   static Player findWinner() {
      Player winner = null;
      for (Fleet f : fleets) {
         if (winner == null) {
            winner = f.getOwner();
         } else if (!f.ownedBy(winner)) {
            return null;
         }
      }
      return winner;
   }

   public static int getNumUnitsInFleets(Player p) {
      int count = 0;
      for(Fleet f : fleets) {
         if(f.ownedBy(p)) {
            count += f.getNumUnits();
         }
      }
      return count;
   }
}



