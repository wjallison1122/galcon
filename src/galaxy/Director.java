package galaxy;

import java.util.LinkedList;


final class Director extends GameSettings {
   private int rounds = 0, tic = 0;
   private Matcher mm = null;
   private Visualizer visualizer = createVisualizer();

   private LinkedList<Player> active;

   Director() {
      for (int i = 0; i < players.length; i++) {
         createStats(players[i]);
      }

      for (int i = 0; i < PLAYERS_PER_GAME; i++) {
         mm = new Matcher(mm);
      }

      newGame();
   }

   boolean done() {
      return rounds > NUM_ROUNDS;
   }

   void reportStats() {
      Stats.reportAllStats();
   }

   void next() {
      for (Player p : active) {
         p.doTurn();
      }

      for (Player p : active) {
         for (Action a : p.getActions()) {
            a.doAction(tic);
         }
      }

      Galaxy.update();
      visualizer.update();

      Player winner = Galaxy.checkWinner();
      if (winner != null) {
         Stats.updateAllStats(active, winner);
         newGame();
      }

      tic++;
   }
   
   boolean usingVisualizer() {
      return visualizer != null;
   }

   private void newGame() {
      active = mm.getPlayers();
      mm.update();

      Galaxy.clear();
      Galaxy.generateRandomMap(active);
//      Galaxy.generateSymmetricMap();

      Player[] activeArray = new Player[active.size()];
      int i = 0;
      for (Player p : active) {
         p.nextGame();
         activeArray[i++] = p;
      }

      if (usingVisualizer()) {
         visualizer.nextGame(active);
      }

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





