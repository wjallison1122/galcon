package galaxy;

import java.util.HashMap;
import java.util.LinkedList;


final class Director extends GameSettings {
   private int rounds = 0;
   private Matcher mm = null;
   private Visualizer visualizer = createVisualizer();
   private MapMaker maps = createMapMaker();
   private Galaxy galaxy = new Galaxy();
   private LinkedList<Player> active;
   
   private static HashMap<Player, Integer> numUnitsInFleets, numUnitsInPlanets;
   private static HashMap<Player, Stats> playerStats = new HashMap<Player, Stats>();
   private static int tic = 0;

   Director() {
      for (Player p : players) {
         playerStats.put(p, createStats(p));
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
      numUnitsInFleets = new HashMap<Player, Integer>();
      numUnitsInPlanets = new HashMap<Player, Integer>();

      for (Player p : active) {
         numUnitsInFleets.put(p, galaxy.numUnitsInFleets(p));
         numUnitsInPlanets.put(p, galaxy.numUnitsInPlanets(p));
      }

      for (Player p : active) {
         p.doTurn(galaxy.getAllFleets());
      }

      for (Player p : active) {
         for (Action a : p.getActions()) {
            galaxy.addFleet(a.doAction());
         }
      }

      galaxy.update();
      visualizer.update(galaxy.getAllFleets());

      Player winner = galaxy.checkWinner();
      if (winner != null) {
         Stats.updateAllStats(active, winner);
         newGame();
      }

      tic++;
   }

   boolean usingVisualizer() {
      return visualizer != null;
   }

   static int getTic() {
      return tic;
   }

   private void newGame() {
      tic = 0;

      active = mm.getPlayers();
      mm.update();

      // TODO add re-mirror, re-play
      galaxy.nextGame(active, maps.getMap(active));

      for (Player p : active) {
         p.nextGame(galaxy.getAllPlanets());
      }

      if (usingVisualizer()) {
         visualizer.nextGame(active, galaxy.getAllPlanets());
      }
   }

   /**
    * When manually skipped game
    */
   void skipGame() {
      Stats.updateAllStats(active, null);
      newGame();
   }

   static final int numUnitsOwnedBy(Player p) {
      return numUnitsInPlanets(p) + numUnitsInFleets(p);
   }

   static final int numUnitsInPlanets(Player p) {
      Integer numUnits = numUnitsInPlanets.get(p);
      return numUnits == null ? 0 : numUnits;
   }

   static final int numUnitsInFleets(Player p) {
      Integer numUnits = numUnitsInFleets.get(p);
      return numUnits == null ? 0 : numUnits;
   }

   /******************* MATCHER *****************/

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





