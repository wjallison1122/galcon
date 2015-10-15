package galaxy;


public class Action {
   private final Planet START;
   private final Planet TARGET;
   private final int NUM_UNITS;

   public Action(Planet start, Planet target, int numUnits) {
      START = start;
      TARGET = target;
      NUM_UNITS = numUnits;
   }

   void doAction(Player p) {
      if (START.ownedBy(p)) {
         START.sendFleet(TARGET, NUM_UNITS);
      }
   }
}
