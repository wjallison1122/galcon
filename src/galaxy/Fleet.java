package galaxy;

import java.util.Iterator;
import java.util.LinkedList;

public final class Fleet extends Unit {
   public static final double SPEED = Main.FLEET_SPEED;
   private static int id = 0;
   public final int ID = id++;

   private final Planet DESTINATION;

   private static LinkedList<Fleet> fleets = new LinkedList<Fleet>();

   Fleet(int units, Player owner, Planet destination, double ... coords) {
      super(owner, units, coords);
      
      if (destination == null) {
         throw new NullPointerException("Fleet destination was null.");
      }
      DESTINATION = destination;

      fleets.add(this);
   }
   
   public Planet getDestination() {
      return DESTINATION;
   }

   public double distanceLeft() {
      return distanceTo(DESTINATION);
   }

   static Fleet[] getAllFleets() {
      Fleet[] armada = new Fleet[fleets.size()];
      int i = 0;
      for (Fleet f : fleets) {
         armada[i++] = f;
      }
      return armada;
   }

   static void updateAll() {
      Iterator<Fleet> fleeterator = fleets.iterator();
      while (fleeterator.hasNext()) {
         if (fleeterator.next().update()) {
            fleeterator.remove();
         }
      }
   }

   private boolean update() {
      double[] targetCoords = DESTINATION.getCoords();
      double[] fleetCoords = getCoords();
      double distance = distanceLeft();
      
      for (int i = 0; i < Main.DIMENSIONS.length; i++) {
         fleetCoords[i] += (targetCoords[i] - fleetCoords[i]) / distance * SPEED;
      }
      
      setCoords(fleetCoords);

      if(distance - SPEED < 0) {
         DESTINATION.hitBy(this);
         return true;
      }
      
      return false;
   }

   static void clear() {
      fleets.clear();
   }

   static boolean checkWinner(Player winner) {
      for (Fleet f : fleets) {
         if (!f.ownedBy(winner)) {
            return false;
         }
      }
      return true;
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
}



