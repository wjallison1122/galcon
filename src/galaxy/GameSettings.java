package galaxy;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import stats.DefaultStats;
import visualizers.DefaultVisualizer;
import visualizersthreedee.*;
import ais.*;
import ais.jono.*;

class GameSettings {
   static boolean debugMode = false, logGame = false;

   static BufferedWriter gameLog = logGame ? makeLogFile("galconset-" + formatDate(new Date())) : null;

   static Player [] players = {new DistanceValueDefenderAI(new Color(50,100,0)), new ValueCapture()};
   
   static final int[] DIMENSIONS = {1000, 1000, 1000};
   static final int NUM_PLANETS = 16;
   static Visualizer visualizer = new Display(DIMENSIONS);

//   static final int[] DIMENSIONS = {1280, 720};
//   static final int NUM_PLANETS = 16;
//   static Visualizer visualizer = new DefaultVisualizer(DIMENSIONS);



   public static final int FLEET_SPEED = 2;

   static final int PLAYERS_PER_GAME = 2;
   static final int NUM_ROUNDS = 5000;

   static final int FRAME_TIME = 10;
   Director director = new Director();

   static Stats createStats(Player p) {
      return new DefaultStats(p);
   }



   final void debug(String str) {
      if (debugMode) {
         System.out.println(str);
      }
   }

   final static BufferedWriter makeLogFile(String filename) {
      try {
         return new BufferedWriter(new FileWriter(new File(filename)));
      } catch (IOException e) {
         System.err.println("Couldn't make log file.");
         System.exit(0);
         return null;
      }
   }

   final static String formatDate(Date date) {
      String str = "";
      str += date.getTime();
      return str;
   }

   final static int worldSize() {
      int prod = 1;
      for (int i : DIMENSIONS) {
         prod *= i;
      }
      return prod;
   }
}
