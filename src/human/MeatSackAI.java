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

   public MeatSackAI() {
      super(Color.CYAN, "Fleshling");
      new MeatSackDisplay(this);
   }

   @Override
   public void turn() {
      pendingActions.clear();
      
      if (!autoAdvance) {
         while (!singleAdvance && !autoAdvance) {
            try {
               Thread.sleep(100);
            } catch (InterruptedException ex) {
               ex.printStackTrace();
               throw new RuntimeException(ex);
            }
         }
         
      }
      singleAdvance = false;
   }
   
   public void finishTurn() {
      singleAdvance = true;
   }
   
   public Planet[] getPlanets() {
      return planets;
   }
   
   public void addAction(Action action) {
      actions.add(action);
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

   @Override
   protected void newGame() {
      // TODO Auto-generated method stub
      
   }

   @Override
   protected String storeSelf() {
      // TODO Auto-generated method stub
      return null;
   }
}
