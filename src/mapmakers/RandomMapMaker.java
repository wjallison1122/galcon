package mapmakers;

import java.util.LinkedList;

import galaxy.MapMaker;
import galaxy.Planet;
import galaxy.Player;

public class RandomMapMaker extends MapMaker {

   @Override
   protected void makeMap(LinkedList<Player> active) {
      for (Player p : active) {
         generateStartingPlanet(p);
      }

      for (int i = active.size(); i < NUM_PLANETS; i++) {
         generateRandomPlanet();
      }
   }
   
   private Planet generateRandomPlanet() {
      int numUnits = (int) (Math.random() * MAX_NEUTRAL_UNITS);
      int radius = (int) (Math.random() * (MAX_RADIUS - MIN_RADIUS) + MIN_RADIUS);
      int prodTime = (int) ((1 - ((double) radius - MIN_RADIUS) / (MAX_RADIUS - MIN_RADIUS)) * 
            (MAX_PRODUCE_TIME - MIN_PRODUCE_TIME) + MIN_PRODUCE_TIME);
      double[] coords = getLocation(radius);
      return makePlanet(null, numUnits, radius, prodTime, coords);
   }
   
   
   private Planet generateStartingPlanet(Player owner) {
      return makePlanet(owner, 100, Planet.MAX_RADIUS, Planet.MIN_PRODUCE_TIME, getLocation(Planet.MAX_RADIUS));
   }

   
   private double[] getLocation(int radius) {
      double[] coords = new double[DIMENSIONS.length];
      do {
         for (int i = 0; i < DIMENSIONS.length; i++) {
            coords[i] = Math.random() * (DIMENSIONS[i] - radius * 2) + radius;
         }
      } while (checkOverlappingOtherPlanets(radius, coords));
      return coords;
   }
}
