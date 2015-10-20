package galaxy;

import human.MeatSackAI;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import stats.DefaultStats;
import visualizers.DefaultVisualizer;
import ais.BasicAI;

/***************************************************************************
 * ATCS AI Challenge: Galcon
 * 
 * Author: Alexander Lazar
 * 
 * Thanks to: Chris Sauer for the original program design and a large part
 *    of the original code, Yujin Ariza for doing most of the graphics & the 
 *    original matchmaking system and a large part of the code, and Phil
 *    Hassey for originally creating Galcon. 
 * 
 * This program was originally written in 2011 for Menlo's Advanced Topics
 * Computer Science class. The original iteration had just 1v1 games/sets on 
 * random maps, with zero protection against cheating. While the original 
 * program was quite well made for a pair of high school juniors and a senior, 
 * a rewrite was long overdue. This version cleans up and separates the logic,
 * utilizes package protection to prevent cheating and lays the foundation to
 * implement planned advanced features: Reading and writing maps from files, 
 * multiple AIs in a game, symmetric maps, wraparound maps, human player, 
 * simultaneous turns and recording matches. 
 * 
 *
 * This comment was written on: 1/14/15
 **************************************************************************/

/*** Version 2.1.1 ***/

class Main {
   static boolean debugMode = false, logGame = false;

   static BufferedWriter gameLog = logGame ? makeLogFile("galconset-" + formatDate(new Date())) : null;

   static Player [] players = {new BasicAI(), new MeatSackAI()};

   static final int[] DIMENSIONS = {1280, 800};


   static final int PLANET_DENSITY = 64000; // Planets per volume units
   static final int NUM_PLANETS = worldSize() / PLANET_DENSITY;//16;
   static final int FLEET_SPEED = 2;


   static final int PLAYERS_PER_GAME = 2;
   static final int NUM_ROUNDS = 5000;

   static final int FRAME_TIME = 33;
   private static Visualizer visualizer = new DefaultVisualizer(DIMENSIONS);
   private Director director = new Director();
   private static boolean pause = false;
   private static boolean skipGame = false;

   public static void main(String[] args) {
      new Main();
   }

   private void debug(String str) {
      if (debugMode) {
         System.out.println(str);
      }
   }

   private Main() {
      debug("Starting creation " + NUM_PLANETS);

      if (visualizer != null) {
         new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
               if (!director.done()) {
                  next();
                  visualizer.update();
               }
            }
         }, 0, FRAME_TIME);
      } else {
         while (!director.done()) {
            next();
         }
      }

   }

   /**
    * The next game tic
    */
   void next() {
      if (skipGame) {
         skipGame = false;
         director.skipGame();
      }

      if (!pause && !director.done()) {
         director.next();
      }
   }

   /**
    * For make map from text
    * @param ID
    * @return
    */
   static Player getPlayer(int ID) {
      return null;
   }

   /**
    * For visualizer to be able to pause game
    */
   static void togglePause() {
      pause = !pause;
   }

   /**
    * For visualizer to be able to skip games
    */
   static void skipGame() {
      skipGame = true;
   }

   static int worldSize() {
      int prod = 1;
      for (int i : DIMENSIONS) {
         prod *= i;
      }
      return prod;
   }
   
   static void writeToLog(String str) {
      try {
         gameLog.write(str);
      } catch (IOException e) {
         System.err.println("Couldn't write to log file.");
      }
   }
   
   static Stats createStats(Player p) {
      return new DefaultStats(p);
   }
   
   static void resetVisualizer() {
      if (visualizer != null) {
         visualizer.nextGame();
      }
   }

   static BufferedWriter makeLogFile(String filename) {
      try {
         return new BufferedWriter(new FileWriter(new File(filename)));
      } catch (IOException e) {
         System.err.println("Couldn't make log file.");
         System.exit(0);
         return null;
      }
   }

   static String formatDate(Date date) {
      String str = "";
      str += date.getTime();
      return str;
   }
}
