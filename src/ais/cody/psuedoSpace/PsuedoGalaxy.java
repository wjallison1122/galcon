package ais.cody.psuedoSpace;

import java.util.ArrayList;

import galaxy.Fleet;
import galaxy.Planet;
import galaxy.Player;

public class PsuedoGalaxy {
	public ArrayList<PsuedoPlanet> psuedoPlanets;
	public ArrayList<PsuedoFleet> psuedoFleets;
	
	public PsuedoGalaxy(Planet[] planets, Fleet[] fleets, Player me) {
		psuedoPlanets = new ArrayList<PsuedoPlanet>();
		psuedoFleets = new ArrayList<PsuedoFleet>();
		
		for (Planet planet : planets)
			psuedoPlanets.add(new PsuedoPlanet(planet, me));
		
		for (Fleet fleet : fleets)
			psuedoFleets.add(new PsuedoFleet(fleet, me, psuedoPlanets));
	}
	
	public void advance(int time, ArrayList<PsuedoAction> psuedoActions) {
		for (PsuedoPlanet psuedoPlanet : psuedoPlanets)
			psuedoPlanet.advance(time);
		for (PsuedoFleet psuedoFleet : psuedoFleets)
			if (psuedoFleet.advance(time))
				psuedoFleets.remove(psuedoFleet);
		for (PsuedoAction psuedoAction : psuedoActions)
			psuedoAction.act();
	}
	
	public ArrayList<PsuedoPlanet> myPlanets() {
		ArrayList<PsuedoPlanet> myPlanets = new ArrayList<PsuedoPlanet>();
		
		for (PsuedoPlanet psuedoPlanet : psuedoPlanets)
			if (psuedoPlanet.mine())
				myPlanets.add(psuedoPlanet);
		
		return myPlanets;
	}
	
	public int psuedoPlanetStrength(PsuedoPlanet psuedoPlanet) {
	   int planetStrength = psuedoPlanet.strength;
	   
	   for (PsuedoFleet psuedoFleet : psuedoFleets) 
		   if (psuedoFleet.target.equals(psuedoPlanet))
			   planetStrength += psuedoFleet.strength;
		   
	   
	   return planetStrength;
		
	}
}
