package galaxy;


public final class Planet extends Unit {
   public static final int MAX_RADIUS = 50;
   public static final int MIN_RADIUS = 12;
   public static final int MAX_NEUTRAL_UNITS = 50;
   public static final int MIN_PRODUCE_TIME = 34;
   public static final int MAX_PRODUCE_TIME = 100;


   public final int RADIUS, PRODUCTION_TIME;
   private int updateCnt = 0;
   private boolean recentlyConquered = false;
   
   Planet(Player owner, int numUnits, int radius, int prodTime, double ... coords) {
      super(owner, numUnits, coords);
      RADIUS = radius;
      PRODUCTION_TIME = prodTime;
   }
   
   
   
 

   //********** END PLANET GENERATION CODE **********//

   boolean update() {
      if(!ownedBy(null) && updateCnt++ % PRODUCTION_TIME == 0) {
         numUnits++;
      }
      return false;
   }

   static Planet[] getAllPlanets() {
      return Galaxy.planets.clone();
   }

   void hitBy(Fleet f) {
      if(f.ownedBy(owner)) {
         numUnits += f.getNumUnits();
      } else {
         numUnits -= f.getNumUnits();
         if(numUnits < 0) {
            recentlyConquered = true; // Variable for visualizer, visualizer resets. 
            owner = f.getOwner();
            numUnits *= -1;
         } else if (numUnits == 0) {
            owner = null;
         }
      }
   }
   
   public double getProductionFrequency() {
	  return 1. / (double)this.PRODUCTION_TIME;
   }

   boolean checkRecentlyConquered() {
      boolean recent = recentlyConquered;
      recentlyConquered = false;
      return recent;
   }

   Fleet sendFleet(Planet target, int numSent) {
      numSent = Math.min(numSent, numUnits);
      if (numSent > 0) {
         numUnits -= numSent;
         return new Fleet(numSent, owner, target, getCoords());
      } else {
         return null;
      }
   }

   static int getNumUnitsInPlanets(Player p)  {
      return Galaxy.getNumUnitsInPlanets(p);
   }
   
   public int getUpdateCount() {
      return updateCnt;
   }
}
