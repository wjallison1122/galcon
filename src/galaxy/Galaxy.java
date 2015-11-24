package galaxy;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

import mapmakers.RandomMapMaker;


final class Galaxy extends GameSettings {
   
   static Planet[] planets;
   static LinkedList<Fleet> fleets = new LinkedList<Fleet>();
   
   static final void generateRandomMap(LinkedList<Player> active) {
      planets = ((MapMaker)new RandomMapMaker()).getMap(active);
   }

   static File writeMap(String fileName) {
      return null;
   }

   static int numUnitsOwnedBy(Player p) {
      return Planet.getNumUnitsInPlanets(p) + Fleet.getNumUnitsInFleets(p);
   }

   static void update() {
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
   
   public static Fleet[] getAllFleets() {
      Fleet[] armada = new Fleet[fleets.size()];
      int i = 0;
      for (Fleet f : fleets) {
         armada[i++] = f;
      }
      return armada;
   }
   
   static Player checkPlanetIsGameOver() {
      Player p = planets[0].getOwner();
      int i = 0;
      while (i < planets.length && !planets[i++].ownedByOpponentOf(p));
      
      return i == planets.length ? p : null;
   }
   
   static Player checkWinner() {
      Player winner = checkPlanetIsGameOver();
      for (Fleet f : fleets) {
         if (!f.ownedBy(winner)) {
            return null;
         }
      }
      return winner;
   }
   
   static int getNumUnitsInPlanets(Player p) {
      int count = 0;
      for(Planet f : planets) {
         if(f.ownedBy(p)) {
            count += f.numUnits;
         }
      }
      return count;
   }
   
   static void addFleet(Fleet f) {
      fleets.add(f);
   }
   
   static int getNumUnitsInFleets(Player p) {
      int count = 0;
      for(Fleet f : fleets) {
         if(f.ownedBy(p)) {
            count += f.getNumUnits();
         }
      }
      return count;
   }

   static void clear() {
      fleets.clear();
   }
}
