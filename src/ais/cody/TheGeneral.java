package ais.cody;

import galaxy.Fleet;
import galaxy.Planet;
import galaxy.Player;

import java.awt.Color;
import java.util.ArrayList;

import ais.PlayerUtils;


public class TheGeneral extends Player {
	private static final double FUTURE_COEFFICIENT = 1;
	private static final double DISTANCE_COEFFICIENT = .3;
	private static final double ATTACK_THRESHOLD = 0;
//	private double health;
//	private Vector heart; 

   public TheGeneral() {
      super(Color.ORANGE, "The General");
   }
   
   public TheGeneral(Color c) {
      super(c, "The General");
   }
   
//   private double planetValue(Planet p) {
//	   return p.getNumUnits() + FUTURE_COEFFICIENT * p.getProductionFrequency();
//   }
   
//   private void calculateHealth() {
//	   double[] planetPosition;
//	   Vector planetVector;
//	   Vector center = new Vector(0.,0.,0.);
//	   
//	   health = 0;
//	   heart = new Vector(0.,0.,0.);
//	   
//	   for (Planet p : this.planets) {
//		   this.health += planetValue(p);
//		   
//		   planetPosition = p.getCoords();
//		   planetVector = new Vector(planetPosition[0], planetPosition[1], planetPosition[2]);
//		   center = Vector.add(center, planetVector);
//		   planetVector = Vector.scale(planetVector, p.getNumUnits());
//		   heart = Vector.add(heart, planetVector);
//	   }
//	   
//	   heart = Vector.scale(heart, 1. / health);
//	   
//	   for (Planet p : this.planets) {
//		   this.health += p.getNumUnits();
//		   
//		   planetPosition = p.getCoords();
//		   planetVector = new Vector(planetPosition[0], planetPosition[1], planetPosition[2]);
//		   planetVector = Vector.scale(planetVector, p.getNumUnits());
//		   heart = Vector.add(heart, planetVector);
//	   }
//   }
   
   @Override
   protected void turn() {
      ArrayList<Planet> myPlanets = new ArrayList<Planet>(PlayerUtils.getPlanetsOwnedByPlayer(planets, this));
      ArrayList<Planet> otherPlanets = new ArrayList<Planet>(PlayerUtils.getPlanetsNotOwnedByPlayer(planets, this));
      ArrayList<Planet> opponentsPlanets = new ArrayList<Planet>(PlayerUtils.getOpponentsPlanets(planets, this));
      ArrayList<Planet> neutralPlanets = new ArrayList<Planet>(PlayerUtils.getPlanetsNotOwnedByPlayer(planets, this));
      Planet target = null;
      Planet attacker = null;
      Planet closestNeutral = null;
      double closestNeutralDistance;
      double distance;
      double least;
      double cost;
      double weightedCost;
      int unitsToSend = 0;
      
      neutralPlanets.removeAll(opponentsPlanets);

      //attack
      least = Double.MAX_VALUE;
      for (Planet from : myPlanets) {
          closestNeutralDistance = Double.MAX_VALUE;
    	  for (Planet to : planets) {
    		  distance = to.distanceTo(from.getCoords());
        	  cost = costOfPlanet(from, to);
        	  weightedCost = cost;
        	  weightedCost += DISTANCE_COEFFICIENT * distance;
        	  weightedCost -= (FUTURE_COEFFICIENT * to.getProductionFrequency());
        	  if (cost >= 0 && weightedCost < least) {
        		  attacker = from;
        		  target = to;
        		  least = weightedCost;
            	  unitsToSend = (int)cost + 2;
        	  }
        	  else if (cost >= 0 && weightedCost < ATTACK_THRESHOLD) {
            	  unitsToSend = (int)cost + 2;
            	  addAction(from, to, unitsToSend);
        	  }
        	  
        	  if (to.ownedBy(null) && distance < closestNeutralDistance) {
        		  closestNeutralDistance = distance;
        		  closestNeutral = to;
        	  }
          }
      }
      if (target != null && attacker != null && least != Double.MAX_VALUE)
    	  addAction(attacker, target, unitsToSend);
      
      //move units
      
      
   }
   
   // How many units will it take to capture a planet?
   private double costOfPlanet(Planet attacker, Planet target) {
	   double cost = Double.MAX_VALUE;
	   double timeCost = timeCost(attacker, target);
	   
	   if (target.ownedBy(null)) {
		   if (target.getNumUnits() < attacker.getNumUnits()) {
			   cost =  (planetStrength(target) + 1);
		   }
	   }
	   else if (target.ownedBy(this)) {
			   cost = ((double)planetStrength(target));
	   }
	   else {
		   if (target.getNumUnits() < attacker.getNumUnits() + timeCost) {
			   cost = ((double)planetStrength(target) + 1) + timeCost;
			}
	   }
	   return cost;
   }
   
   private double timeCost(Planet from, Planet to) {
	   return (to.getProductionFrequency() * (to.distanceTo(from.getCoords()) / Fleet.SPEED)) + 1;
   }
   
//   private boolean alreadyAttacked(Planet target) {
//	   return (planetStrength(target) < 0);
//   }

   private int planetStrength(Planet planet) {
	   int myStrength;
	   int enemyStrength;
	   Fleet[] allFleets = Fleet.getAllFleets();
	   
	   if (planet.ownedBy(this)) {
		   myStrength = planet.getNumUnits();
		   enemyStrength = 0;
	   }
	   else {
		   myStrength = 0;
		   enemyStrength = planet.getNumUnits();
	   }
	   
	   for (Fleet fleet : allFleets) 
		   if (fleet.getDestination().equals(planet)) {
			   if (fleet.ownedBy(this)) {
				   myStrength += fleet.getNumUnits();
			   }
			   else
				   enemyStrength += fleet.getNumUnits();
		   }
	   
	   return enemyStrength - myStrength;
   }
   
   @Override
   protected void newGame() {
      
   }

   @Override
   protected String storeSelf() {
      return null;
   }
}
