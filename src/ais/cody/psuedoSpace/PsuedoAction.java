package ais.cody.psuedoSpace;

public class PsuedoAction {
	   public PsuedoPlanet from;
	   public PsuedoPlanet to;
	   public int numUnits;
	   
	   public PsuedoAction(PsuedoPlanet from, PsuedoPlanet to, int numUnits) {
		   this.from = from;
		   this.to = to;
		   this.numUnits = numUnits;
	   }
	   
	   public PsuedoFleet act() {
		   from.strength -= numUnits;
		   return new PsuedoFleet(this);
	   }
	   
}

