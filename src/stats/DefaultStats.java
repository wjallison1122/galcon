package stats;

import galaxy.Player;
import galaxy.Stats;

import java.util.LinkedList;

public class DefaultStats extends Stats {
   int wins = 0, losses = 0;

   public DefaultStats(Player p) {
      super(p);
   }

   protected void updateStats(LinkedList<Player> active, Player winner) {
      // List of active is so more stats can be added later.
      // For now, just win/loss record
      if (winner == P) {
         wins++;
      } else {
         losses++;
      }
   }



   protected void reportStats() {
      System.out.println("Had " + wins + " wins and " + losses + " losses.");
   }
}
