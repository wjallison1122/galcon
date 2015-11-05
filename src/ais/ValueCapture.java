package ais;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;
import visualizersthreedee.Vector;

import galaxy.Planet;
import galaxy.Player;
import galaxy.Main;
import galaxy.Fleet;


public class ValueCapture extends Player {
	private static final double FUTURE_COEFFICIENT = 0;
	private static final double DISTANCE_COEFFICIENT = 0;
	private double health;
	private Vector heart; 

   public ValueCapture() {
      super(new Color(255,255,255), "ValueCapture");
   }
   
   public ValueCapture(Color c) {
      super(c, "ValueCapture");
   }
   
   private double planetValue(Planet p) {
	   return p.getNumUnits() + FUTURE_COEFFICIENT * p.getProductionFrequency();
   }
   
   private void calculateHealth() {
	   double[] planetPosition;
	   Vector planetVector;
	   Vector center = new Vector(0.,0.,0.);
	   
	   health = 0;
	   heart = new Vector(0.,0.,0.);
	   
	   for (Planet p : this.planets) {
		   this.health += planetValue(p);
		   
		   planetPosition = p.getCoords();
		   planetVector = new Vector(planetPosition[0], planetPosition[1], planetPosition[2]);
		   center = Vector.add(center, planetVector);
		   planetVector = Vector.scale(planetVector, p.getNumUnits());
		   heart = Vector.add(heart, planetVector);
	   }
	   
	   heart = Vector.scale(heart, 1. / health);
	   
	   for (Planet p : this.planets) {
		   this.health += p.getNumUnits();
		   
		   planetPosition = p.getCoords();
		   planetVector = new Vector(planetPosition[0], planetPosition[1], planetPosition[2]);
		   planetVector = Vector.scale(planetVector, p.getNumUnits());
		   heart = Vector.add(heart, planetVector);
	   }
   }
   
   @Override
   protected void turn() {
      ArrayList<Planet> myPlanets = new ArrayList<Planet>(PlanetUtils.getPlanetsOwnedByPlayer(planets, this));
      ArrayList<Planet> otherPlanets = new ArrayList<Planet>(PlanetUtils.getPlanetsNotOwnedByPlayer(planets, this));
      ArrayList<Planet> opponentsPlanets = new ArrayList<Planet>(PlanetUtils.getOpponentsPlanets(planets, this));
      ArrayList<Planet> neutralPlanets = new ArrayList<Planet>(PlanetUtils.getPlanetsNotOwnedByPlayer(planets, this));
      Planet target = null;
      Planet attacker = null;
      double least;
      double cost;
      double weightedCost;
      int unitsToSend = 0;
      
      neutralPlanets.removeAll(opponentsPlanets);
      
      least = Double.MAX_VALUE;
      for (Planet to : planets) {
          for (Planet from : myPlanets) {
        	  cost = costOfPlanet(from, to);
        	  weightedCost = cost;
        	  weightedCost += DISTANCE_COEFFICIENT * to.distanceTo(from.getCoords());
        	  weightedCost -= (FUTURE_COEFFICIENT * to.getProductionFrequency());
        	  if (cost >= 0 && weightedCost < least) {
        		  attacker = from;
        		  target = to;
        		  least = weightedCost;
            	  unitsToSend = (int)cost + 2;
        	  }
          }
      }
      
      if (target != null && attacker != null && least != Double.MAX_VALUE)
    	  addAction(attacker, target, unitsToSend);
   }
   
   // How many units will it take to capture a planet?
   private double costOfPlanet(Planet attacker, Planet target) {
	   double cost = Double.MAX_VALUE;
	   
	   if (target.ownedBy(null)) {
		   if (target.getNumUnits() < attacker.getNumUnits()) {
			   cost =  (planetStrength(target) + 1);
		   }
	   }
	   else if (target.ownedBy(this)) {
		   if (target.getNumUnits() < attacker.getNumUnits()) {
			   cost = ((double)planetStrength(target));
		   }
	   }
	   else {
		   if (target.getNumUnits() < attacker.getNumUnits()) {
			   cost = ((double)planetStrength(target) + 1) + (target.getProductionFrequency() * (target.distanceTo(attacker.getCoords()) / Main.FLEET_SPEED));
		   }
	   }
	   return cost;
   }
   
   private boolean alreadyAttacked(Planet target) {
	   return (planetStrength(target) < 0);
   }

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
