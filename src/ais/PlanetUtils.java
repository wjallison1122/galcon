package ais;

import galaxy.Fleet;
import galaxy.Planet;
import galaxy.Player;

import java.awt.Color;
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
         if (p.getColor().equals(Color.GRAY)) {
            rtn.add(p);
         }
      }
      return rtn;
   }
   
   public static List<Planet> getOwnedPlanets(Planet[] planets) {
      ArrayList<Planet> rtn = new ArrayList<>();
      for (Planet p : planets) {
         if (!p.getColor().equals(Color.GRAY)) {
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
   
   public static List<Planet> sortByDistance(List<Planet> planets, Planet planet) {
      List<Planet> rtn = new ArrayList<Planet>(planets);
      Collections.sort(rtn, (a, b) -> {
         return Double.compare(a.distanceTo(planet), b.distanceTo(planet));
      });
      return rtn;
   }
}
