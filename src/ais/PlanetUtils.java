package ais;

import galaxy.Fleet;
import galaxy.Planet;
import galaxy.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlanetUtils {
   
   public static List<Planet> getPlanetsOwnedByPlayer(Planet[] planets, Player player) {
      ArrayList<Planet> rtn = new ArrayList<>();
      for (Planet p : planets) {
         if (p.ownedBy(player)) {
            rtn.add(p);
         }
      }
      return rtn;
   }
   
   public static List<Planet> getPlanetsNotOwnedByPlayer(Planet[] planets, Player player) {
      ArrayList<Planet> rtn = new ArrayList<>();
      for (Planet p : planets) {
         if (!p.ownedBy(player)) {
            rtn.add(p);
         }
      }
      return rtn;
   }
   
   public static List<Planet> getUnoccupiedPlanets(Planet[] planets) {
      ArrayList<Planet> rtn = new ArrayList<>();
      for (Planet p : planets) {
         if (p.isNeutral()) {
            rtn.add(p);
         }
      }
      return rtn;
   }
   
   public static List<Planet> getOwnedPlanets(Planet[] planets) {
      ArrayList<Planet> rtn = new ArrayList<>();
      for (Planet p : planets) {
         if (!p.isNeutral()) {
            rtn.add(p);
         }
      }
      return rtn;
   }
   
   public static List<Planet> getOpponentsPlanets(Planet[] planets, Player player) {
      List<Planet> rtn = getOwnedPlanets(planets);
      rtn.removeAll(getPlanetsOwnedByPlayer(planets,player));
      return rtn;
   }
   
   public static Planet getNearestPlanet(Planet[] planets, Planet planet) {
      double bestDistance = Double.MAX_VALUE;
      Planet rtn = null;
      for (Planet p : planets) {
         double thisDistance = planet.distanceTo(p);
         if (thisDistance < bestDistance) {
             bestDistance = thisDistance;
             rtn = p;
         }
      }
      return rtn;
   }
   
   public static Planet getNearestOwnedPlanet(Planet[] planets, Planet planet, Player player) {
      double bestDistance = Double.MAX_VALUE;
      Planet rtn = null;
      for (Planet p : planets) {
         //Get the nearest owned planet that is not the planet being checked
         if (p.ownedBy(player) && !p.equals(planet)) {
            double thisDistance = planet.distanceTo(p);
            if (thisDistance < bestDistance) {
               bestDistance = thisDistance;
               rtn = p;
            }
         }
      }
      return rtn;
   }
   
   public static Planet getNearestNotOwnedPlanet(Planet[] planets, Planet planet, Player player) {
      double bestDistance = Double.MAX_VALUE;
      Planet rtn = null;
      for (Planet p : planets) {
         if (!p.ownedBy(player)) {
            double thisDistance = planet.distanceTo(p);
            if (thisDistance < bestDistance) {
               bestDistance = thisDistance;
               rtn = p;
            }
         }
      }
      return rtn;
   }
   
   public static Planet getNearestEnemyPlanet(Planet[] planets, Planet planet, Player player) {
      double bestDistance = Double.MAX_VALUE;
      Planet rtn = null;
      for (Planet p : getOpponentsPlanets(planets, player)) {
         double thisDistance = p.distanceTo(planet);
         if (thisDistance < bestDistance) {
             bestDistance = thisDistance;
             rtn = p;
         }
      }
      return rtn;
   }
   
   public static int getIncomingFleetCount(Planet p, Fleet[] fleets) {
      int rtn = 0;
      for (Fleet f : fleets) {
         if (f.getDestination().equals(p)) {
            rtn += f.getNumUnits();
         }
      }
      return rtn;
   }
   
   public static int getPlayersIncomingFleetCount(Planet planet, Fleet[] fleets, Player player) {
      int rtn = 0;
      for (Fleet f : fleets) {
         if (f.getDestination().equals(planet) && f.ownedBy(player)) {
            rtn += f.getNumUnits();
         }
      }
      return rtn;
   }
   
   public static int getOpponentsIncomingFleetCount(Planet planet, Fleet[] fleets, Player player) {
      int rtn = 0;
      for (Fleet f : fleets) {
         if (f.getDestination().equals(planet) && !f.ownedBy(player)) {
            rtn += f.getNumUnits();
         }
      }
      return rtn;
   }
   
   public static int getEnemyUnitsOnPlanets(Planet[] planets, Player player) {
      int unitCount = 0;
      for(Planet p: planets) {
         if(!p.ownedBy(player) && !p.isNeutral()) {
            unitCount += p.getNumUnits();
         }
      }
      return unitCount;
   }
   
   public static int getMyUnitsOnPlanets(Planet[] planets, Player player) {
      int unitCount = 0;
      for(Planet p: planets) {
         if(p.ownedBy(player)) {
            unitCount += p.getNumUnits();
         }
      }
      return unitCount;
   }
   
   public static int getEnemyUnitsInFleets(Fleet[] fleets, Player player) {
      int unitCount = 0;
      for(Fleet f: fleets) {
         if(!f.ownedBy(player) && !f.isNeutral()) {
            unitCount += f.getNumUnits();
         }
      }
      return unitCount;
   }
   
   public static int getMyUnitsInFleets(Fleet[] fleets, Player player) {
      int unitCount = 0;
      for(Fleet f: fleets) {
         if(f.ownedBy(player)) {
            unitCount += f.getNumUnits();
         }
      }
      return unitCount;
   }
   
   public static List<Planet> sortByDistance(List<Planet> planets, Planet planet) {
      List<Planet> rtn = new ArrayList<Planet>(planets);
      Collections.sort(rtn, (a, b) -> {
         return Double.compare(a.distanceTo(planet), b.distanceTo(planet));
      });
      return rtn;
   }
}
