package ais.human;

import galaxy.Planet;
import galaxy.Player;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class MeatSackAI extends Player {
   
   private List<FutureAction> pendingActions = new ArrayList<>();
   private boolean autoAdvance = false;
   private int turnsToFinish = 0;
   private MeatSackDisplay display;
   
   public static class FutureAction {
      Planet source;
      Planet destination;
      int count;
      
      public FutureAction(Planet source, Planet destination, int count) {
         this.source = source;
         this.destination = destination;
         this.count = count;
      }
   }

   public MeatSackAI() {
      super(Color.CYAN, "Fleshling");
      display = new MeatSackDisplay(this);
   }

   @Override
   public void turn() {
      display.updateBase();
      pendingActions.forEach((action) -> addAction(action.source, action.destination, action.count));
      pendingActions.clear();
      
      if (!autoAdvance) {
         while (turnsToFinish == 0 && !autoAdvance) {
            try {
               Thread.sleep(100);
            } catch (InterruptedException ex) {
               ex.printStackTrace();
               throw new RuntimeException(ex);
            }
         }
         
      }
      if (turnsToFinish > 0) turnsToFinish--;
   }
   
   public void finishTurns(int amt) {
      turnsToFinish = amt;
   }
   
   public Planet[] getPlanets() {
      return planets;
   }
   
   public void addAction(FutureAction action) {
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

   @Override
   protected void newGame() {
      display.newGame();
   }

   @Override
   protected String storeSelf() {
      // TODO Auto-generated method stub
      return null;
   }
}
