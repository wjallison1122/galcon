package galaxy;

import java.util.LinkedList;

public abstract class MapMaker extends GameSettings {
   public static final int MAX_RADIUS = Planet.MAX_RADIUS;
   public static final int MIN_RADIUS = Planet.MIN_RADIUS;
   public static final int MAX_NEUTRAL_UNITS = Planet.MAX_NEUTRAL_UNITS;
   public static final int MIN_PRODUCE_TIME = Planet.MIN_PRODUCE_TIME;
   public static final int MAX_PRODUCE_TIME = Planet.MAX_PRODUCE_TIME;
   
   private Planet[] planets = new Planet[NUM_PLANETS];
   private int pi = 0;
   
   
   protected abstract void makeMap(LinkedList<Player> active);
   
   protected final Planet makePlanet(Player owner, int numUnits, int radius, int prodTime, double ... coords) {
      return planets[pi++] = new Planet(owner, numUnits, radius, prodTime, coords);
   }
   
   public final Planet[] getMap(LinkedList<Player> active) {
      makeMap(active);
      return planets;
   }
   
   protected boolean checkOverlappingOtherPlanets(int radius, double ... coords) {
      LinkedList<Planet> plans = new LinkedList<Planet>();
      plans.clone();
      for (int i = 0; i < pi; i++) {
         if (planets[i].distanceTo(coords) < radius + planets[i].RADIUS + 10) {
            return true;
         }
      }
      return false;
   }
}
