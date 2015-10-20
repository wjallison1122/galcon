package galaxy;


public class Action {
   private final Planet START;
   private final Planet TARGET;
   private final int NUM_UNITS;

   static Player currentTurn;

   public Action(Planet start, Planet target, int numUnits) {
      if (start == null) {
         throw new NullPointerException("Attempted to make action to send fleet from null planet.");
      }

      if (target == null) {
         throw new NullPointerException("Attempted to make action to send fleet to null planet.");
      }

//      if (!start.ownedBy(currentTurn)) {
//         throw new RuntimeException("Player attempted to make an action for a planet not owned by them.");
//      }

      START = start;
      TARGET = target;
      NUM_UNITS = numUnits;
   }

   void doAction(Player p, int tic) {
      if (Unit.unitOwnedBy(START, p)) {
         START.sendFleet(TARGET, NUM_UNITS);
      }
   }

   @Override
   public String toString() {
      return START.ID + " " + NUM_UNITS + " " + TARGET.ID;
   }
}
