package ais.jono;

import galaxy.Planet;
import galaxy.Player;

import java.awt.Color;
import java.util.List;

import ais.PlanetUtils;

public class CaptureNearestAI extends Player {

   public CaptureNearestAI() {
      super(new Color(10,10,10), "Grow Empire AI");
   }

   @Override
   protected void newGame() {
      
   }

   @Override
   protected void turn() {
      List<Planet> myPlanets = PlanetUtils.getPlanetsOwnedByPlayer(planets, this);
      List<Planet> otherPlanets = PlanetUtils.getPlanetsNotOwnedByPlayer(planets, this);
      
      for (Planet p : myPlanets) {
         for (Planet pOther : PlanetUtils.sortByDistance(otherPlanets, p)) {
            if (p.getNumUnits() - PlanetUtils.getIncomingFleetCount(p, fleets) > 3 * pOther.getNumUnits()) {
               int currentCount = PlanetUtils.getPlayersIncomingFleetCount(pOther, fleets, this);
               if (currentCount == 0) {
                  addAction(p, pOther, 2 * pOther.getNumUnits());
                  break;
               }
            }
         }
      }
   }

   @Override
   protected String storeSelf() {
      return null;
   }
   
}
