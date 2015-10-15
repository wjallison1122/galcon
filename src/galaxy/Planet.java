//Jan 14 2015

package galaxy;

import java.awt.Graphics;
import java.awt.Image;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.ImageIcon;

enum SymmetryType {
   VERTICAL,
   HORIZONTAL,
   DIAGONAL,
   RADIAL,
}

public class Planet extends Unit
{
   public static final int MAX_RADIUS = 50;
   public static final int MIN_RADIUS = 12;
   public static final int MAX_NEUTRAL_UNITS = 50;
   public static final int MIN_PRODUCE_TIME = 34;
   public static final int MAX_PRODUCE_TIME = 100;


   public final int RADIUS, PRODUCTION_TIME;
   private int updateCnt = 0;
   
   private static LinkedList<Planet> planets = new LinkedList<Planet>();

   private Planet(Coordinates loc, Player owner, int numUnits, int radius, int prodTime) {
      super(owner, loc, numUnits);
      RADIUS = radius;
      PRODUCTION_TIME = prodTime;

      planets.add(this);
   }
   
   static Planet generatePlanetFromString(String str) {
      Scanner s = new Scanner(str);
      Planet p = new Planet(new Coordinates(s.nextInt(), s.nextInt()), Main.getPlayer(s.nextInt()), 
            s.nextInt(), s.nextInt(), s.nextInt());
      s.close();
      return p;
   }
   
   static Planet generateStartingPlanet(Player owner) {
      return new Planet(getLocation(MAX_RADIUS), owner, 100, MAX_RADIUS, MIN_PRODUCE_TIME);
   }
   
   static Planet generatePlanet() {
      int numUnits = (int) (Math.random() * MAX_NEUTRAL_UNITS);
      int radius = (int) (Math.random() * (MAX_RADIUS - MIN_RADIUS) + MIN_RADIUS);
      int prodTime = (int) ((1 - ((double) radius - MIN_RADIUS) / (MAX_RADIUS - MIN_RADIUS)) * 
            (MAX_PRODUCE_TIME - MIN_PRODUCE_TIME) + MIN_PRODUCE_TIME);
      Coordinates loc = getLocation(radius);
      return new Planet(loc, null, numUnits, radius, prodTime);
   }

   private static Coordinates getLocation(int radius) {
      int x, y;
      do {
         x = (int) (Math.random() * (Main.WIN_WIDTH - radius * 2) + radius);
         y = (int) (Math.random() * (Main.WIN_HEIGHT - radius * 2) + Main.TOP_BAR_HEIGHT
               + radius);
      } while (checkOverlappingOtherPlanets(new Coordinates(x, y), radius));
      return new Coordinates(x, y);
   }

   private static boolean checkOverlappingOtherPlanets(Coordinates loc, int radius) {
      for(Planet p : planets) {
         if(loc.distanceTo(p) < radius + p.RADIUS + 10) {
            return true;
         }
      }
      return false;
   }



   static void updateAll() {
      for(Planet p : planets) {
         p.update();
      }
   }

   private void update() {
      if(!ownedBy(null) && updateCnt++ % PRODUCTION_TIME == 0) {
         numUnits++;
      }
   }

   @SuppressWarnings("unchecked")
   static LinkedList<Planet> getAllPlanets() {
      return (LinkedList<Planet>) planets.clone();
   }

   static Player isGameOver() {
      Player winner = Fleet.findWinner();

      if (winner == null) {
         return null;
      }

      for (Planet p : planets) {
         if (!p.ownedBy(null) && !p.ownedBy(winner)) {
            return null;
         }
      }

      return winner; 
   }

   void hitBy(Fleet f) {
      if(ownedBy(f.getOwner())) {
         numUnits = numUnits + f.getNumUnits();
      } else {
         numUnits = numUnits - f.getNumUnits();
         if(numUnits < 0) {
            owner = f.getOwner();
            numUnits *= -1;
            if (USE_EXPLOSIONS) {
//               new Explosion(X, Y, RADIUS);
            }
         }
      }
   }

   Fleet sendFleet(Planet target, int numSent) {
      if (numSent > 0) {
         numSent = Math.min(numSent, numUnits);
         numUnits -= numSent;
         return new Fleet(new Coordinates(coords.getCoords()), numSent, getOwner(), target);
      } else {
         return null;
      }
   }

   static void clear() {
      planets.clear();
   }

   public static int getNumUnitsInPlanets(Player p)  {
      int count = 0;
      for(Planet f : planets) {
         if(f.ownedBy(p)) {
            count += f.numUnits;
         }
      }
      return count;
   }
}
