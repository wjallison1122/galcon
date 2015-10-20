package human;

import galaxy.Action;
import galaxy.Planet;
import galaxy.Player;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MeatSackAI extends Player {
   
   private List<Action> pendingActions = new ArrayList<>();
   private boolean autoAdvance = false;
   private boolean singleAdvance = false;

   protected MeatSackAI() {
      super(Color.CYAN, "Fleshling");
   }

   @Override
   protected void turn() {
      actions = new LinkedList<Action>(pendingActions);
      pendingActions.clear();
      
      if (autoAdvance) {
         singleAdvance = false;
      } else {
         while (!singleAdvance) {
            try {
               Thread.sleep(100);
            } catch (InterruptedException ex) {
               ex.printStackTrace();
               throw new RuntimeException(ex);
            }
         }
      }
   }
   
   public void finishTurn() {
      singleAdvance = true;
   }
   
   public Planet[] getPlanets() {
      return planets;
   }
   
   public void addAction(Action action) {
      pendingActions.add(action);
   }
   
   //for testing
   void setPlanets(Planet[] planets) {
      this.planets = planets;
   }

   public boolean getAutoAdvance() {
      return autoAdvance;
   }

   public void setAutoAdvance(boolean autoAdvance) {
      this.autoAdvance = autoAdvance;
   }
}
