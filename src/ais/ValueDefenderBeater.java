package ais;

import java.awt.Color;
import java.util.List;

import galaxy.Action;
import galaxy.Planet;
import galaxy.Player;

public class ValueDefenderBeater extends Player {

   public ValueDefenderBeater() {
      super(Color.BLUE, "Value Defender Beater");
   }

   @Override
   protected void newGame() {
      sentFleet = false;
   }
   
   private boolean sentFleet = false;

   @Override
   protected void turn() {
      List<Planet> myPlanets = PlanetUtils.getPlanetsOwnedByPlayer(planets, this);
      List<Planet> otherPlanets = PlanetUtils.getPlanetsNotOwnedByPlayer(planets, this);
      List<Planet> opponentsPlanets = PlanetUtils.getOpponentsPlanets(planets, this);
      
      Planet smallestUnoccupied = otherPlanets.stream().min((a,b) -> Integer.compare(a.getNumUnits(), b.getNumUnits())).get();
      
      if (!sentFleet) {
         sentFleet = true;
         addAction(myPlanets.get(0), smallestUnoccupied, smallestUnoccupied.getNumUnits() + 1);
      }
      
      if (opponentsPlanets.size() == 1) {
         Planet target = opponentsPlanets.get(0);
         for (Planet p : myPlanets) {
            addAction(p,target,1000);
         }
      }
   }

   @Override
   protected String storeSelf() {
      return null;
   }
   
}
