package galaxy;

import java.util.LinkedList;


public abstract class Stats {

   static LinkedList<Stats> stats = new LinkedList<Stats>();
   protected final Player P;

   public Stats(Player p) {
      P = p;
      stats.add(this);
   }


   static void reportAllStats() {
      for (Stats s : stats) {
         s.reportStats();
      }
   }

   static void updateAllStats(LinkedList<Player> active, Player winner) {
      for (Stats s : stats) {
         if (active.contains(s.P)) {
            s.updateStats(active, winner);
         }
      }
   }

   protected abstract void updateStats(LinkedList<Player> active, Player winner);

   protected abstract void reportStats();


}



