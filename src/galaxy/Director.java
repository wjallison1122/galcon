package galaxy;

import java.awt.Font;
import java.awt.Graphics;
import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;


class Director {
   private final Player[] players;
   public final int PLAYERS_PER_GAME;
   public final int NUM_ROUNDS;
   private int rounds = 0;
   private Matcher mm = null;
   public final int FONT_HEIGHT = 40;

   private LinkedList<Player> active;

   private void debug(String str) { 
      if (Main.debugMode) {
         System.out.println(str);
      }
   }

   Director(Player [] players, int playersPerGame, int numRounds) {
      this.players = players;
      PLAYERS_PER_GAME = playersPerGame;
      NUM_ROUNDS = numRounds;
      for (int i = 0; i < players.length; i++) {
         mm = new Matcher(mm);
         new Stats(players[i]);
      }
      newGame();
      
      debug("Finished director");
   }

   boolean done() {
      return rounds > NUM_ROUNDS;
   }
   
   void reportStats() {
      Stats.reportAllStats();
   }

   void next() {
      LinkedList<SimpleEntry<Player, LinkedList<Action>>> actions = new LinkedList<SimpleEntry<Player, LinkedList<Action>>>();
      for (Player p : active) {
         actions.add(new SimpleEntry<Player, LinkedList<Action>>(p, p.getActions()));
      }

      for (SimpleEntry<Player, LinkedList<Action>> turn : actions) {
         LinkedList<Action> acts = turn.getValue();
         for (Action a : acts) {
            a.doAction(turn.getKey());
         }
      }
      
      Player winner = Galaxy.isGameOver();
      if (winner != null) {
         Stats.updateAllStats(active, winner);
         newGame();
      }
   }

   private void newGame() {
      active = mm.getPlayers();
      mm.update();
      Galaxy.clear();
      Galaxy.generateRandomMap(active);
   }

   /*
    * When manually skipped game
    */
   void skipGame() {
      Stats.updateAllStats(active, null);
      newGame();
   }



   /*** GFX ***/

   void drawCurrentPlayerInfo(Graphics g)
   {
      Font font = new Font("Monospaced", Font.PLAIN, FONT_HEIGHT);
      g.setFont (font);

      int offset = 1;
      for (Player p : active) {
         g.setColor(p.COLOR);
         g.drawString(p.NAME + ": " + Galaxy.numUnitsOwnedBy(p), 10, FONT_HEIGHT * offset++);
      }
   }
   
   private static class Stats {
      final Player P;
      int wins = 0, losses = 0;
      
      static LinkedList<Stats> stats = new LinkedList<Stats>();
      
      Stats(Player p) {
         P = p;
         stats.add(this);
      }
      
      static void updateAllStats(LinkedList<Player> active, Player winner) {
         for (Stats s : stats) {
            if (active.contains(s.P)) {
               s.updateStats(active, winner);
            }
         }
      }
      
      void updateStats(LinkedList<Player> active, Player winner) {
         // List of active is so more stats can be added later.
         // For now, just win/loss record
         if (winner == P) {
            wins++;
         } else {
            losses++;
         }
      }
      
      static void reportAllStats() {
         for (Stats s : stats) {
            s.reportStats();
         }
      }
      
      void reportStats() {
         System.out.println("Had " + wins + " wins and " + losses + " losses.");
      }
   }

   private class Matcher {
      private Matcher next, prev;
      private int player;

      Matcher(Matcher previous) {
         prev = previous;
         if (prev != null) {
            player = prev.player + 1;
            prev.next = this;
         } else {
            player = 0;
         }
      }

      void update() {
         player++;
         if (player == players.length) {
            player = ++prev.player + 1;
            if (player == players.length) {
               player = prev.overflow();
            }
         }
      }

      int overflow() {
         if (prev == null) { // Full set of games completed - reset to start
            rounds++;
            player = 0;
            Matcher search = next;
            while (search != null) {
               search.player = search.prev.player + 1;
               search = search.next;
            }
            return 1; // This will have been called by its own 'next'
         } else {
            if (prev.player == player - 2) {
               prev.player++;
               player = prev.overflow();
            } else {
               player = ++prev.player + 1;
            }
            return player + 1;
         }
      }

      LinkedList<Player> getPlayers() {
         LinkedList<Player> set = prev != null ? prev.getPlayers() : new LinkedList<Player>();
         set.add(players[player]);
         return set;
      }
   }
}





