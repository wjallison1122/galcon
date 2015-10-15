package galaxy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import ais.BasicAI;
import ais.OtherAI;

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
 * This version also added a consistent style and a stylecheck. Stylecheck and
 * instructions to implement it can be found at: 
 * 
 * Note: No sapce beteween LinkedList and <>
 * 
 * This comment was written on: 1/14/15
 **************************************************************************/

/*** Version 2.0.1 ***/

class Main implements ActionListener {

   private static Player [] players = {new BasicAI(), new OtherAI()};

   public static boolean debugMode = false;
   
   //Window setup data
   public static final int WIN_WIDTH = 1280;
   public static final int WIN_HEIGHT = 800; 

   public static final boolean USE_GRAPHICS = true, USE_EXPLOSIONS = true;

   public static final int PLANET_DENSITY = 64000; // Planets per square units
   public static final int NUM_PLANETS = WIN_WIDTH * WIN_HEIGHT / PLANET_DENSITY;//16;
   public static final int FLEET_SPEED = 2;
   public static final int DIMENSIONS = 2;

   public static final int FRAME_TIME = 1;
   
   public static final int PLAYERS_PER_GAME = 2;
   public static final int NUM_ROUNDS = 5;

   private Timer clock;

   private Director director;
   private static boolean pause = false;
   private static boolean skipGame = false;

   public static void main(String[] args) {
      Player p = new BasicAI();
      p.turn();
      new Main();
   }
   
   private void debug(String str) {
      if (debugMode) {
         System.out.println(str);         
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

   private Main() {
      debug("Starting");
      director = new Director(players, PLAYERS_PER_GAME, NUM_ROUNDS);
      debug("Made director");

      if (USE_GRAPHICS) {
         clock = new Timer(FRAME_TIME, this);
         clock.start();
      }
      else {
         while (!director.done()) {
            actionPerformed(null);
         }
      }
      
      debug("Finished main");
   }

   public void actionPerformed(ActionEvent e) {
      if (skipGame) {
         skipGame = false;
         director.skipGame();
      }
      
      if (!pause) {
         debug("Doign next");
         director.next();
         debug("Done next");         
      }
      
      if (clock.isRunning() && director.done()) {
         clock.stop();
      }
   }
   
   static void togglePause() {
      pause = !pause;
   }
   
   static void skipGame() {
      skipGame = true;
   }
}
