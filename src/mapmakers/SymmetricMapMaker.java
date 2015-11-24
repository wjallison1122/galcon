package mapmakers;

import java.util.LinkedList;

import galaxy.MapMaker;
import galaxy.Planet;
import galaxy.Player;

public class SymmetricMapMaker extends MapMaker {

   @Override
   protected void makeMap(LinkedList<Player> active) {
      generateCenterPlanet();
      generateSymmetricStartingPlanets(active);
      generateSymmetricPlanets(active);
   }

   Planet generateCenterPlanet() {
      double[] coords = new double[DIMENSIONS.length];
      for (int i = 0; i < DIMENSIONS.length; i++) {
         coords[i] = DIMENSIONS[i] / 2;
      }
      return makePlanet(null, MAX_NEUTRAL_UNITS, MAX_RADIUS, MIN_PRODUCE_TIME, coords);
   }

   void generateSymmetricStartingPlanets(LinkedList<Player> players) {
      double[][] locations = getRadialLocations(MAX_RADIUS, players.size());
      int i = 0;
      for (Player p : players) {
         makePlanet(p, 100, MAX_RADIUS, MIN_PRODUCE_TIME, locations[i++]);
      }
   }

   void generateSymmetricPlanets(LinkedList<Player> players) {
      int numUnits = (int) (Math.random() * MAX_NEUTRAL_UNITS);
      int radius = (int) (Math.random() * (MAX_RADIUS - MIN_RADIUS) + MIN_RADIUS);
      int prodTime = (int) ((1 - ((double) radius - MIN_RADIUS) / (MAX_RADIUS - MIN_RADIUS)) * 
            (MAX_PRODUCE_TIME - MIN_PRODUCE_TIME) + MIN_PRODUCE_TIME);

      double[][] locations = getRadialLocations(radius, players.size());
      for (int i = 0; i < locations.length; i++) {
         makePlanet(null, numUnits, radius, prodTime, locations[i]);
      }
   }


   private double[][] getRadialLocations(int planetRadius, int numPlayers) {
      if (DIMENSIONS.length == 1) {
         return get1DLocations(planetRadius, numPlayers);
      } else if (DIMENSIONS.length == 2) {
         return get2DRadialLocations(planetRadius, numPlayers);
      } else {
         return getNDRadialLocations(planetRadius, numPlayers);
      }
   }

   private double[][] get1DLocations(int planetRadius, int numPlayers) {
      if (numPlayers != 2) {
         //throw new Exception("Wrong number of players. There can only be two players in 1-dimensional space.");
         System.out.println("Wrong number of players. There can only be two players in 1-dimensional space.");
         return new double[0][0];
      }
      double[] coords1 = new double[1];
      double[] coords2 = new double[1];

      do {
         coords1[0] = Math.random() * (DIMENSIONS[0] / 2 - planetRadius * 2) + planetRadius;
      } while (checkOverlappingOtherPlanets(planetRadius, coords1));
      coords2[0] = DIMENSIONS[0] - coords1[0];

      double[][] locs = { coords1, coords2 };
      return locs;
   }

   private double[][] get2DRadialLocations(int planetRadius, int numPlayers) {
      double minDimension = Math.min(DIMENSIONS[0] / 2, DIMENSIONS[1] / 2) - planetRadius * 2;
      double maxAngle = Math.toRadians(360 / numPlayers);

      double randomRadius;
      double randomAngle;
      double[] coords = new double[2];
      do {
         randomRadius = Math.random() * minDimension + planetRadius;
         randomAngle = Math.random() * maxAngle;
         coords[0] = randomRadius * Math.cos(randomAngle) + DIMENSIONS[0] / 2;
         coords[1] = randomRadius * Math.sin(randomAngle) + DIMENSIONS[1] / 2;
      } while (checkOverlappingOtherPlanets(planetRadius, coords));
      // TODO: there might be an edge case where there are a lot of players and a planet is
      // generated very close to the center. It may not overlap with existing planets, but it
      // might overlap with the other planets that will be generated symmetrically to it for
      // the other players.
      //    Best way to fix would be to add a method in the while check that checks symmetric
      //    locations to make sure they won't overlap.
      double[][] locations = new double[numPlayers][];
      locations[0] = coords;
      for (int i = 1; i < numPlayers; i++) {
         double[] newCoords = new double[2];
         randomAngle += maxAngle;
         newCoords[0] = randomRadius * Math.cos(randomAngle) + DIMENSIONS[0] / 2;
         newCoords[1] = randomRadius * Math.sin(randomAngle) + DIMENSIONS[1] / 2;
         locations[i] = newCoords;
      }

      return locations;
   }

   // https://en.wikipedia.org/wiki/N-sphere#Spherical_coordinates
   private double[][] getNDRadialLocations(int planetRadius, int numPlayers) {
      // Get the smallest dimension and make that the
      // max radius of the spherical coordinates
      double minDimension = DIMENSIONS[0];
      for (int i = 1; i < DIMENSIONS.length; i++) {
         if (DIMENSIONS[i] < minDimension) {
            minDimension = DIMENSIONS[i];
         }
      }
      // almost every angle varies from 0-180 degrees
      double maxAngle = Math.toRadians(360 / numPlayers);

      double[] coords = new double[DIMENSIONS.length];
      double[] randomAngles = new double[DIMENSIONS.length - 2];
      double randomRadius;
      double lastAngle;
      do {
         randomRadius = Math.random() * minDimension;
         double calculatedSin = 1;
         for (int i = 0; i < DIMENSIONS.length - 2; i++) {
            randomAngles[i] = Math.random() * maxAngle;
            coords[i] = randomRadius * calculatedSin * Math.cos(randomAngles[i]) + DIMENSIONS[i] / 2;
            calculatedSin *= Math.sin(randomAngles[i]);
         }
         // The last two coords use a diff angle (0-360 degrees).
         lastAngle = Math.random() * maxAngle * 2;
         coords[DIMENSIONS.length - 2] = randomRadius * calculatedSin * Math.cos(lastAngle) + DIMENSIONS[DIMENSIONS.length - 2] / 2;
         coords[DIMENSIONS.length - 1] = randomRadius * calculatedSin * Math.sin(lastAngle) + DIMENSIONS[DIMENSIONS.length - 1] / 2;
      } while (checkOverlappingOtherPlanets(planetRadius, coords));
      // TODO: there might be an edge case where there are a lot of players and a planet is
      // generated very close to the center. It may not overlap with existing planets, but it
      // might overlap with the other planets that will be generated symmetrically to it for
      // the other players.
      //    Best way to fix would be to add a method in the while check that checks symmetric
      //    locations to make sure they won't overlap.

      // get the locations where each of the other player's planets would be
      double[][] locations = new double[numPlayers][];
      locations[0] = coords;
      for (int p = 1; p < numPlayers; p++) {
         double[] newCoords = new double[DIMENSIONS.length];

         // update the angles for the next player
         for (int i = 0; i < randomAngles.length; i++) {
            randomAngles[i] += maxAngle;
         }
         lastAngle += maxAngle * 2;

         // calculate the new coords
         double calculatedSin = 1;
         for (int i = 0; i < DIMENSIONS.length - 2; i++) {
            newCoords[i] = randomRadius * calculatedSin * Math.cos(randomAngles[i]) + DIMENSIONS[i] / 2;
            calculatedSin *= Math.sin(randomAngles[i]);
         }
         // The last two coords use a diff angle (0-360 degrees).
         newCoords[DIMENSIONS.length - 2] = randomRadius * calculatedSin * Math.cos(lastAngle) + DIMENSIONS[DIMENSIONS.length - 2] / 2;
         newCoords[DIMENSIONS.length - 1] = randomRadius * calculatedSin * Math.sin(lastAngle) + DIMENSIONS[DIMENSIONS.length - 1] / 2;
         locations[p] = newCoords;
      }
      return locations;
   }
}
