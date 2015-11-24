package mapmakers;

import galaxy.MapMaker;
import galaxy.Planet;
import galaxy.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

public class MapFromFile extends MapMaker {
   
   BufferedReader file;
   
   public MapFromFile(BufferedReader file) {
      this.file = file;
   }

   @Override
   protected void makeMap(LinkedList<Player> active) {
      Planet[] planets = new Planet[NUM_PLANETS];
      for (int i = 0; i < NUM_PLANETS; i++) {
         try {
            planets[i] = makePlanetFromString(file.readLine(), active);
         } catch (IOException e) {
            System.out.println("Couldn't read file.");
            System.exit(0);
         }
      }
   }
   
   private Planet makePlanetFromString(String str, LinkedList<Player> active) {
      Scanner s = new Scanner(str);

      double[] coords = new double[DIMENSIONS.length];
      for (int i = 0; i < DIMENSIONS.length; i++) {
         coords[i] = s.nextDouble();
      }
 
      Planet p = makePlanet(active.get(s.nextInt()), 
            s.nextInt(), s.nextInt(), s.nextInt(), coords);
      s.close();
      return p;
   }

}















