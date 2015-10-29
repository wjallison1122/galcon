package galaxy;

import java.io.File;
import java.util.LinkedList;

enum SymmetryType {
   VERTICAL,
   HORIZONTAL,
   DIAGONAL,
   RADIAL,
}

final class Galaxy {
   private static void debug(String str) {
      if (Main.debugMode) {
         System.out.println(str);
      }
   }

   static void generateRandomMap(LinkedList<Player> active) {
      for (Player p : active) {
         Planet.generateStartingPlanet(p);
      }

      for (int i = 0; i < Main.NUM_PLANETS; i++) {
         Planet.generateRandomPlanet();
      }

      debug("Made planets");
   }

   static void generateMapFromFile(File f) {

   }

   static void generateSymmetricMap(SymmetryType symmetry) {

   }
   
   static void generateSymmetricMap() {
	  Planet.generateCenterPlanet();
	  Planet.generateSymmetricStartingPlanets();
      for (int i = 0; i < Main.NUM_PLANETS / Main.players.length; i++) {
         Planet.generateSymmetricPlanets();
      }

      debug("Made planets");
   }

   static Player isGameOver() {
      return Planet.isGameOver();
   }

   static File writeMap(String fileName) {
      return null;
   }

   static int numUnitsOwnedBy(Player p) {
      return Planet.getNumUnitsInPlanets(p) + Fleet.getNumUnitsInFleets(p);
   }

   static void update() {
      debug("Updating galaxy");
      Planet.updateAll();
      Fleet.updateAll();
   }

   static void clear() {
      Planet.clear();
      Fleet.clear();
   }
}
