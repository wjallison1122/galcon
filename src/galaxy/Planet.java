package galaxy;

import java.util.LinkedList;
import java.util.Scanner;

public class Planet extends Unit
{
   public static final int MAX_RADIUS = 50;
   public static final int MIN_RADIUS = 12;
   public static final int MAX_NEUTRAL_UNITS = 50;
   public static final int MIN_PRODUCE_TIME = 34;
   public static final int MAX_PRODUCE_TIME = 100;


   public final int RADIUS, PRODUCTION_TIME;
   private int updateCnt = 0;
   private boolean recentlyConquered = false;
   
   private static LinkedList<Planet> planets = new LinkedList<Planet>();

   private Planet(Player owner, int numUnits, int radius, int prodTime, double ... coords) {
      super(owner, numUnits, coords);
      RADIUS = radius;
      PRODUCTION_TIME = prodTime;

      planets.add(this);
   }
   
   static Planet generatePlanetFromString(String str) {
      Scanner s = new Scanner(str);
      
      double[] coords = new double[Main.DIMENSIONS];
      for (int i = 0; i < Main.DIMENSIONS; i++) {
         coords[i] = s.nextDouble();
      }
      
      Planet p = new Planet(Main.getPlayer(s.nextInt()), 
            s.nextInt(), s.nextInt(), s.nextInt(), coords);
      s.close();
      return p;
   }
   
   static Planet generateStartingPlanet(Player owner) {
      return new Planet(owner, 100, MAX_RADIUS, MIN_PRODUCE_TIME, getLocation(MAX_RADIUS));
   }
   
   static Planet generatePlanet() {
      int numUnits = (int) (Math.random() * MAX_NEUTRAL_UNITS);
      int radius = (int) (Math.random() * (MAX_RADIUS - MIN_RADIUS) + MIN_RADIUS);
      int prodTime = (int) ((1 - ((double) radius - MIN_RADIUS) / (MAX_RADIUS - MIN_RADIUS)) * 
            (MAX_PRODUCE_TIME - MIN_PRODUCE_TIME) + MIN_PRODUCE_TIME);
      double[] coords = getLocation(radius);
      return new Planet(null, numUnits, radius, prodTime, coords);
   }
   
   private static double[] getLocation(int radius) {
      double[] coords = new double[Main.DIMENSIONS];
      do {
         for (int i = 0; i < Main.DIMENSIONS; i++) {
            coords[i] = Math.random() * (Main.WIN_WIDTH - radius * 2) + radius;
         }
      } while (checkOverlappingOtherPlanets(radius, coords));
      return coords;
   }

   private static boolean checkOverlappingOtherPlanets(int radius, double ... coords) {
      for(Planet p : planets) {
         if(p.distanceTo(coords) < radius + p.RADIUS + 10) {
            return true;
         }
      }
      return false;
   }

   //********** END PLANET GENERATION CODE **********//


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

   static Planet[] getAllPlanets() {
      Planet[] all = new Planet[planets.size()];
      int i = 0;
      for (Planet p : planets) {
         all[i++] = p;
      }
      return all;
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
      if(f.ownedBy(owner)) {
         numUnits += f.getNumUnits();
      } else {
         numUnits -= f.getNumUnits();
         if(numUnits < 0) {
            recentlyConquered = true; // Variable for visualizer, visualizer resets. 
            owner = f.getOwner();
            numUnits *= -1;
         } else if (numUnits == 0) {
            owner = null;
         }
      }
   }
   
   boolean checkRecentlyConquered() {
      boolean recent = recentlyConquered;
      recentlyConquered = false;
      return recent;
   }

   Fleet sendFleet(Planet target, int numSent) {
      if (numSent > 0) {
         numSent = Math.min(numSent, numUnits);
         numUnits -= numSent;
         return new Fleet(numSent, owner, target, getCoords());
      } else {
         return null;
      }
   }

   static void clear() {
      planets.clear();
   }

   static int getNumUnitsInPlanets(Player p)  {
      int count = 0;
      for(Planet f : planets) {
         if(f.ownedBy(p)) {
            count += f.numUnits;
         }
      }
      return count;
   }
}
