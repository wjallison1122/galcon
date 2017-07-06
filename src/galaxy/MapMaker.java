package galaxy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public abstract class MapMaker extends GameSettings {
   private PlanetMaker[] planets = new PlanetMaker[NUM_PLANETS];
   private HashMap<Player, LinkedList<PlanetMaker>> startingPlanets = new HashMap<Player, LinkedList<PlanetMaker>>();
   private int pi;
   private boolean hasReversed = false;

   protected abstract void makeMap(LinkedList<Player> active);

   protected final PlanetMaker makePlanet(Player owner, int numUnits, int radius, int prodTime, double ... coords) {
      PlanetMaker p = new PlanetMaker(owner, numUnits, radius, prodTime, coords);
      planets[pi++] = p;
      if (owner != null) {
         if (startingPlanets.containsKey(owner)) {
            startingPlanets.get(owner).add(p);
         } else {
            LinkedList<PlanetMaker> starting = new LinkedList<PlanetMaker>();
            starting.add(p);
            startingPlanets.put(owner, starting);
         }
      }
      return p;
   }

   boolean hasRevsered() {
      return hasReversed;
   }

   public final Planet[] getExistingMap() {
      return generateMap();
   }

   public final Planet[] getReversedMap() {
      swapStartingPlanets();
      return generateMap();
   }


   // Made slightly more complex due to the tenet that the engine should support games with more than 2 players, 
   // even if those games are not the intended use case of the AIs
   private void swapStartingPlanets() {
      hasReversed = true;
      HashMap<Player, LinkedList<PlanetMaker>> newStartingSet = new HashMap<Player, LinkedList<PlanetMaker>>();
      Iterator<Player> players = startingPlanets.keySet().iterator();
      Player first = players.next();
      Player p1 = first;
      Player p2;
      while (players.hasNext()) {
         p2 = players.next();
         newStartingSet.put(p2, startingPlanets.get(p1));
         for (PlanetMaker plan : startingPlanets.get(p1)) {
            plan.owner = p2;
         }
         p1 = p2;
      }
      newStartingSet.put(first, startingPlanets.get(p1));
      for (PlanetMaker plan : startingPlanets.get(p1)) {
         plan.owner = first;
      }

      startingPlanets = newStartingSet;
   }

   public final Planet[] getNewMap(LinkedList<Player> active) {
      hasReversed = false;
      pi = 0;
      startingPlanets = new HashMap<Player, LinkedList<PlanetMaker>>();
      makeMap(active);
      if (pi != NUM_PLANETS) {
         throw new RuntimeException("Not enough planets generated.");
      }
      return generateMap();
   }

   private Planet[] generateMap() {
      Planet[] map = new Planet[NUM_PLANETS];
      for (int i = 0; i < map.length; i++) {
         map[i] = planets[i].makePlanet();
      }
      return map;
   }

   protected boolean checkOverlappingPlanets(int radius, double ... coords) {
      for (int i = 0; i < pi; i++) {
         if (planets[i].distanceTo(coords) < radius + planets[i].RADIUS + 10) {
            return true;
         }
      }
      return false;
   }

   @Override
   public String toString() {
      return "";
   }

   class PlanetMaker extends Unit {
      final int RADIUS;
      int prodTime;

      PlanetMaker(Player owner, int numUnits, int radius, int prodTime, double[] coords) {
         super(owner, numUnits, coords);
         RADIUS = radius;
         this.prodTime = prodTime;
      }

      Planet makePlanet() {
         return new Planet(owner, numUnits, RADIUS, prodTime, getCoords());
      }

      @Override
      public String toString() {
         return "";
      }
   }
}
