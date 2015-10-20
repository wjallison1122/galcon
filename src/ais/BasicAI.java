package ais;

import galaxy.Action;
import galaxy.Planet;
import galaxy.Player;

import java.awt.Color;

public class BasicAI extends Player {

   public BasicAI() {
      super(Color.BLUE, "James");

   }

   @Override
   protected void turn() {
      Planet hitter = null;
      Planet hitted = null;

      for (Planet p : planets) {
         if (p.ownedBy(this) && (hitter == null || Math.random() > .5)) {
            hitter = p;
         }

         if (!p.ownedBy(this) && (hitted == null || Math.random() > .8)) {
            hitted = p;
         }
      }

      if (hitter != null && hitted != null) {
         actions.add(new Action(hitter, hitted, (int) (Math.random() * hitter.getNumUnits() / 4)));
      }
   }

   @Override
   protected void newGame() {

   }
   
   @Override
   protected String storeSelf() {
      return "";
   }
}
