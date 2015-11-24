package galaxy;


public final class Fleet extends Unit {
   public static final double SPEED = FLEET_SPEED;

   private final Planet DESTINATION;
   private boolean hasHit = false;
   
   Fleet(int units, Player owner, Planet destination, double ... coords) {
      super(owner, units, coords);

      if (destination == null) {
         throw new NullPointerException("Fleet destination was null.");
      }
      DESTINATION = destination;
      
      Galaxy.addFleet(this);
   }

   public Planet getDestination() {
      return DESTINATION;
   }

   public boolean isTargeting(Planet p) {
      return DESTINATION.equals(p);
   }

   public double distanceLeft() {
      return distanceTo(DESTINATION);
   }
   
   public boolean hasHit() {
      return hasHit;
   }

   public static Fleet[] getAllFleets() {
      return Galaxy.getAllFleets();
   }

   boolean update() {
      double[] targetCoords = DESTINATION.getCoords();
      double[] fleetCoords = getCoords();
      double distance = distanceLeft();

      for (int i = 0; i < DIMENSIONS.length; i++) {
         fleetCoords[i] += (targetCoords[i] - fleetCoords[i]) / distance * SPEED;
      }

      setCoords(fleetCoords);

      if(distance - SPEED < 0) {
         setCoords(targetCoords);
         hasHit = true;
         DESTINATION.hitBy(this);
         return true;
      }

      return false;
   }

   static int getNumUnitsInFleets(Player p) {
      return Galaxy.getNumUnitsInFleets(p);
   }
}



