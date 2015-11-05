package ais;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;
import visualizersthreedee.Vector;

import galaxy.Planet;
import galaxy.Player;
import galaxy.Main;


public class Cucucachu extends Player {
   private static final double FUTURE_WEIGHT = 0;
   private double health;
   private Vector heart; 

   public Cucucachu() {
      super(new Color(255,255,255), "Cucucachu");
   }
   
   public Cucucachu(Color c) {
      super(c, "Cucucachu");
   }
   
   private double planetValue(Planet p) {
	   return p.getNumUnits() + FUTURE_WEIGHT * p.getProductionFrequency();
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
      ArrayList<Planet> myPlanets = new ArrayList<Planet>(PlayerUtils.getPlanetsOwnedByPlayer(planets, this));
      ArrayList<Planet> otherPlanets = new ArrayList<Planet>(PlayerUtils.getPlanetsNotOwnedByPlayer(planets, this));
      ArrayList<Planet> opponentsPlanets = new ArrayList<Planet>(PlayerUtils.getOpponentsPlanets(planets, this));
      ArrayList<Planet> neutralPlanets = new ArrayList<Planet>(PlayerUtils.getPlanetsNotOwnedByPlayer(planets, this));
      Planet target = null;
      Planet attacker = null;
      double least;
      
      neutralPlanets.removeAll(opponentsPlanets);
      
      least = Double.MAX_VALUE;
      for (Planet evil : otherPlanets) 
          for (Planet good : myPlanets) 
        	  if (costOfPlanet(good, evil) < least) {
        		  attacker = good;
        		  target = evil;
        		  least = costOfPlanet(good, evil);
        	  }
      if (target != null && attacker != null && least != Double.MAX_VALUE)
    	  addAction(attacker, target, target.getNumUnits() + 1);
      
      System.out.printf("minimum time cost %f\n", least);
            
      //actions.clear();
  //addAction(from, to, number);
   }
   
   // The amount of time to recoup losses from taking this planet, from the production of this planet
   private double costOfPlanet(Planet attacker, Planet target) {
	   if (target.getNumUnits() >= attacker.getNumUnits())
		   return Double.MAX_VALUE;
	   return (target.getNumUnits() + 1) * target.PRODUCTION_TIME + (target.distanceTo(attacker.getCoords()) * Main.FLEET_SPEED);
   }

   @Override
   protected void newGame() {
      
   }

   @Override
   protected String storeSelf() {
      return null;
   }
}
