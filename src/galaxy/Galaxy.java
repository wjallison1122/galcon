package galaxy;

import java.io.File;
import java.util.LinkedList;

class Galaxy
{
   private static void debug(String str) {
      if (Main.debugMode) {
         System.out.println(str);
      }
   }
 
   static void generateRandomMap(LinkedList<Player> active) {
      for (int i = 0; i < Main.NUM_PLANETS; i++) {
         Planet.generatePlanet();
      }
      
      for (Player p : active) {
         Planet.generateStartingPlanet(p);
      }
      
      debug("Made planets");
   }

   static void generateMapFromFile(File f) {

   }

   static void generateSymmetricMap() {

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

   static void clear() {
      Planet.clear();
      Fleet.clear();
   }
}
