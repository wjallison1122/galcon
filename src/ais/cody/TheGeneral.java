package ais.cody;

import galaxy.Fleet;
import galaxy.Planet;
import galaxy.Player;

import java.awt.Color;
import java.util.ArrayList;

import ais.PlayerUtils;
import ais.cody.psuedoSpace.PsuedoPlanet;
import ais.cody.psuedoSpace.PsuedoAction;
import ais.cody.psuedoSpace.PsuedoGalaxy;


public class TheGeneral extends Player {
	private static final double FUTURE_COEFFICIENT = 2;
	private static final double DISTANCE_COEFFICIENT = .3;
	private double health;
	private Vector heart; 
	private PsuedoGalaxy psuedoGalaxy;
	
   public TheGeneral() {
      super(Color.ORANGE, "The General");
   }
   
//   private double planetValue(Planet p) {
//	   return p.getNumUnits() + FUTURE_COEFFICIENT * p.getProductionFrequency();
//   }
   
   private void calculateHealth() {
	   double[] planetPosition;
	   Vector planetVector;
	   
	   health = 0;
	   heart = new Vector(0.,0.,0.);
	   
	   for (Planet p : PlayerUtils.getPlanetsOwnedByPlayer(planets, this)) {
		   this.health += p.getNumUnits();
		   
		   planetPosition = p.getCoords();
		   planetVector = new Vector(planetPosition[0], planetPosition[1], planetPosition[2]);
		   planetVector = Vector.scale(planetVector, p.getNumUnits());
		   heart = Vector.add(heart, planetVector);
	   }
	   
	   heart = Vector.scale(heart, 1. / health);
   }
   
   @Override
   protected void turn() {
      
      calculateHealth();
      psuedoGalaxy = new PsuedoGalaxy(planets, fleets, this);
      findBestActions();
   }
   
   private void findBestActions() {
      PsuedoPlanet target = null;
      ArrayList<PsuedoPlanet> attackers = new ArrayList<PsuedoPlanet>();      
      int[] unitsToSend;
      int i;
      double distance;
      double least;
      double cost;
      double weightedCost;
      double targetCost = 0;
      int strength;
      int totalToSend = 0;
      boolean canAttack;
      Move move;
      
      // find the best planet to send units to
	  least = Double.MAX_VALUE;
	  for (PsuedoPlanet to : psuedoGalaxy.psuedoPlanets) {
		  cost = costOfPlanet(to);
		  weightedCost = cost;
		  distance = to.distanceTo(Vector.getCoords(heart));
		  weightedCost += DISTANCE_COEFFICIENT * distance;
		  weightedCost -= (FUTURE_COEFFICIENT * to.productionFrequency);
		  if (cost >= 0 && weightedCost < least) {
			  target = to;
			  least = weightedCost;
		  }
	  }
	  
	  
	  // determine who should send units
	  move = new Move();
	  if (target != null && least != Double.MAX_VALUE) {
		  
	      targetCost = costOfPlanet(target);
    	  for (PsuedoPlanet from : psuedoGalaxy.myPlanets()) {
    		  if (psuedoGalaxy.psuedoPlanetStrength(from) < -1)
    			  attackers.add(from);
    	  }
		  
		  if (!attackers.isEmpty()) {
			  unitsToSend = new int[attackers.size()];
	    	  canAttack = true;
			  while (totalToSend <= targetCost && canAttack) {
	    		  canAttack = false;
	    		  i = 0;
		    	  for (PsuedoPlanet from : attackers) {
		    		  strength = psuedoGalaxy.psuedoPlanetStrength(from);
		    		  if (-strength - unitsToSend[i] > 2) {
		    			  unitsToSend[i]++;
		    			  totalToSend++;
		    			  canAttack = true;
		    		  }
		    		  i++;
		    	  }
	    	  }
			  
			  if (totalToSend > targetCost) {
	    		  i = 0;
	    		  for (PsuedoPlanet from : attackers)
	    			  move.addPsuedoAction(new PsuedoAction(from, target, -unitsToSend[i++]));
			  }
		  }
	  }
	  
	  move.commit();
   }
   
   // How many units will it take to capture a planet?
   private double costOfPlanet(PsuedoPlanet target) {
	   double cost = Double.MAX_VALUE;
	   double timeCost;
	   
	   timeCost = (target.productionFrequency * (target.distanceTo(Vector.getCoords(heart)) / Fleet.SPEED)) + 1;
	   
	   if (target.neutral == 1) {
		   cost = psuedoGalaxy.psuedoPlanetStrength(target) + 1;
	   }
	   else if (target.mine()) {
		   cost = psuedoGalaxy.psuedoPlanetStrength(target);
	   }
	   else {
		   cost = (psuedoGalaxy.psuedoPlanetStrength(target) + 1) + timeCost;
	   }
	   
	   return cost;
   }

   @Override
   protected void newGame() {
      
   }

   @Override
   protected String storeSelf() {
      return null;
   }
  
   private class Move {
	   ArrayList<PsuedoAction> psuedoActions;
	   	   
	   public Move() {
		   psuedoActions = new ArrayList<PsuedoAction>();
	   }
	   
	   public void addPsuedoAction(PsuedoAction psuedoAction) {
		   psuedoActions.add(psuedoAction);
	   }
	   
	   public void commit() {
		   for (PsuedoAction psuedoAction: psuedoActions) {
			   addAction(psuedoAction.from.realPlanet, psuedoAction.to.realPlanet, -psuedoAction.numUnits);
		   }
	   }
   }
   
}
