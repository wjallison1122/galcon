package galaxy;

import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;


class Director {
   private Player[] players = Main.players;
   private int rounds = 0, tic = 0;
   private Matcher mm = null;

   private LinkedList<Player> active;

   private void debug(String str) { 
      if (Main.debugMode) {
         System.out.println(str);
      }
   }

   Director() {
      for (int i = 0; i < players.length; i++) {
         Main.createStats(players[i]);
      }

      for (int i = 0; i < Main.PLAYERS_PER_GAME; i++) {
         mm = new Matcher(mm);
      }

      newGame();

      debug("Finished director");
   }

   boolean done() {
      return rounds > Main.NUM_ROUNDS;
   }

   void reportStats() {
      Stats.reportAllStats();
   }

   void next() {
      debug("Running... " + tic);
      LinkedList<SimpleEntry<Player, LinkedList<Action>>> actions = new LinkedList<SimpleEntry<Player, LinkedList<Action>>>();
      for (Player p : active) {
         debug("Turn of: " + p.NAME);
         p.doTurn();
         actions.add(new SimpleEntry<Player, LinkedList<Action>>(p, p.getActions()));
      }

      for (SimpleEntry<Player, LinkedList<Action>> turn : actions) {
         LinkedList<Action> acts = turn.getValue();
         for (Action a : acts) {
            a.doAction(turn.getKey(), tic);
         }
      }
      
      Galaxy.update();

      Player winner = Galaxy.isGameOver();
      if (winner != null) {
         Stats.updateAllStats(active, winner);
         newGame();
      }

      tic++;
   }

   private void newGame() {
      active = mm.getPlayers();

      Galaxy.clear();
      Galaxy.generateRandomMap(active);
      
      for (Player p : active) {
         p.nextGame();
      }
      
      Main.resetVisualizer();
      
      mm.update();
      tic = 0;
   }

   /**
    * When manually skipped game
    */
   void skipGame() {
      Stats.updateAllStats(active, null);
      newGame();
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





