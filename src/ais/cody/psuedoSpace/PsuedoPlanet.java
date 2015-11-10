package ais.cody.psuedoSpace;

import ais.cody.Vector;
import galaxy.Planet;
import galaxy.Player;

public class PsuedoPlanet {
	   public Vector position;
	   public int strength;
	   public double productionFrequency;
	   public Planet realPlanet;
	   double partialProduction;
	   public int neutral;
	   
	   public PsuedoPlanet(Planet planet, Player me) {
		   position = new Vector(planet.getCoords());
		   
		   strength = planet.ownedBy(me) ? -planet.getNumUnits() : planet.getNumUnits();   
		   productionFrequency = planet.getProductionFrequency();
		   realPlanet = planet;
		   partialProduction = 0;
		   neutral = planet.ownedBy(null) ? 1 : 0;
	   }
	   
	   public void fleetArrives(int units) {
		   boolean currentOwner = mine();
		   strength += units;
		   neutral = 0;
		   
		   if (currentOwner != mine()) {
			   partialProduction = 0;
		   }
	   }
	   
	   public void fleetDeparts(int units) {
		   strength -= units;
	   }
	   
	   public void advance(int time) {
		   double unitsProduced;
		   if (neutral == 0) {
			   unitsProduced = time * productionFrequency;
			   unitsProduced += partialProduction;
			   partialProduction = unitsProduced - (int)unitsProduced;
			   
			   if (mine())
				   strength -= (int)unitsProduced;
			   else
				   strength += (int)unitsProduced;
		   }
	   }
	   
	   public final double distanceTo(PsuedoPlanet planet) {
	      Vector difference = Vector.sub(position, planet.position);
	      return Vector.abs(difference);
	   }
	   
	   public final double distanceTo(double[] coords) {
	      Vector difference = Vector.sub(position, new Vector(coords));
	      return Vector.abs(difference);
	   }
	   
	   public boolean mine() {
		   return strength < 0;
	   }
	   
	   public String toString() {
		   return "Planet: strength " + strength;
	   }
	   
	   public boolean equals(PsuedoPlanet other) {
		   return (position.equals(other.position) && strength == other.strength);
	   }
}