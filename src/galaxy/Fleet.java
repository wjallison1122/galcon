package galaxy;

import java.util.LinkedList;

public class Fleet extends Unit {
   public static final double SPEED = Main.FLEET_SPEED;

   private Planet destination;

   private static LinkedList<Fleet> fleets = new LinkedList<Fleet>();

   Fleet(int units, Player owner, Planet target, double ... coords) {
      super(owner, units, coords);
      destination = target;

      fleets.add(this);
   }
   
   public Planet getDestination() {
      return destination;
   }

   public double distanceLeft() {
      return distanceTo(destination);
   }

   static LinkedList<Fleet> getAllFleets() {
      LinkedList<Fleet> armada = new LinkedList<Fleet>();
      for (Fleet f : fleets) {
         armada.add(f);
      }
      return armada;
   }

   static void updateAll() {
      for(int i = 0; i < fleets.size(); i++) {
         fleets.get(i).update();
      }
   }

   private void update() {
      double[] targetCoords = destination.getCoords();
      double[] fleetCoords = getCoords();
      double distance = distanceLeft();
      
      for (int i = 0; i < Main.DIMENSIONS; i++) {
         fleetCoords[i] += (targetCoords[i] - fleetCoords[i]) / distance * SPEED;
      }

      if(distance - SPEED < 0) {
         destination.hitBy(this);
         fleets.remove(this);
      }
   }

   static void clear() {
      fleets.clear();
   }

   static Player findWinner() {
      Player winner = null;
      for (Fleet f : fleets) {
         if (winner == null) {
            winner = f.getOwner();
         } else if (!f.ownedBy(winner)) {
            return null;
         }
      }
      return winner;
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



