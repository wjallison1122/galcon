package ais.cody.psuedoSpace;

import java.util.ArrayList;

import ais.cody.Vector;
import galaxy.Fleet;
import galaxy.Player;

public class PsuedoFleet {
	public PsuedoPlanet target;
	public int strength;
	public int turnsLeft;
	
	public PsuedoFleet(PsuedoAction psuedoAction) {
		target = psuedoAction.to;
		strength = psuedoAction.numUnits;
		turnsLeft = (int)(target.distanceTo(Vector.getCoords(psuedoAction.from.position)) / Fleet.SPEED);
	}
	
	public PsuedoFleet(Fleet fleet, Player me, ArrayList<PsuedoPlanet> psuedoPlanets) {		
		for (PsuedoPlanet psuedoPlanet : psuedoPlanets) 
			if (psuedoPlanet.realPlanet.equals(fleet.getDestination()))
				target = psuedoPlanet;
		
		strength = fleet.ownedBy(me) ? -fleet.getNumUnits() : fleet.getNumUnits();
		turnsLeft = (int)(fleet.distanceLeft() / Fleet.SPEED);
	}
	
	public boolean advance(int time) {
		turnsLeft -= time;
		if (time <= 0) {
			target.fleetArrives(strength);
			return true;
		}
		return false;
	}
	
	public boolean mine() {
		return strength < 0;
	}
	
	public String toString() {
		return "Fleet: Sending " + strength + " units to planet " + target.strength;
	}
}
