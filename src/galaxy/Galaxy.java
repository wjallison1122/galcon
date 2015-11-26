package galaxy;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;


final class Galaxy extends GameSettings {
   private Planet[] planets;
   private LinkedList<Fleet> fleets = new LinkedList<Fleet>();

   File writeMap(String fileName) {
      return null;
   }

   int numUnitsOwnedBy(Player p) {
      return numUnitsInPlanets(p) + numUnitsInFleets(p);
   }

   void update() {
      for (Planet p : planets) {
         p.update();
      }

      Iterator<Fleet> fleeterator = fleets.iterator();
      while (fleeterator.hasNext()) {
         if (fleeterator.next().update()) {
            fleeterator.remove();
         }
      }
   }

   Fleet[] getAllFleets() {
      Fleet[] armada = new Fleet[fleets.size()];
      int i = 0;
      for (Fleet f : fleets) {
         armada[i++] = f;
      }
      return armada;
   }

   Planet[] getAllPlanets() {
      return planets.clone();
   }

   Player checkPlanetWinner() {
      Player p = planets[0].getOwner();
      int i = 1;
      while (i < planets.length && !planets[i++].ownedByOpponentOf(p));
      return i == planets.length ? p : null;
   }

   Player checkWinner() {
      Player winner = checkPlanetWinner();
      for (Fleet f : fleets) {
         if (!f.ownedBy(winner)) {
            return null;
         }
      }
      return winner;
   }

   int numUnitsInPlanets(Player p) {
      int count = 0;
      for(Planet f : planets) {
         if(f.ownedBy(p)) {
            count += f.numUnits;
         }
      }
      return count;
   }

   void addFleet(Fleet f) {
      if (f != null) {
         fleets.add(f);
      }
   }

   int numUnitsInFleets(Player p) {
      int count = 0;
      for(Fleet f : fleets) {
         if(f.ownedBy(p)) {
            count += f.getNumUnits();
         }
      }
      return count;
   }

   void nextGame(LinkedList<Player> active, Planet[] newMap) {
      fleets.clear();
      planets = newMap;
   }
}
