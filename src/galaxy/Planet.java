package galaxy;


public final class Planet extends Unit {
   public final int RADIUS, PRODUCTION_TIME;
   private boolean recentlyConquered = false;
   
   Planet(Player owner, int numUnits, int radius, int prodTime, double ... coords) {
      super(owner, numUnits, coords);
      RADIUS = radius;
      PRODUCTION_TIME = prodTime;
   }

   void update() {
      if(!ownedBy(null) && gameTic() % PRODUCTION_TIME == 0) {
         numUnits++;
      }
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
   
   @Override
   protected Planet clone() {
      return new Planet(owner, numUnits, RADIUS, PRODUCTION_TIME, getCoords());
   }
}
