package ais;

import galaxy.Action;
import galaxy.Planet;
import galaxy.Player;

import java.awt.Color;

public class OtherAI extends Player {

   public OtherAI() {
      super(Color.RED, "Brian");

   }

   @Override
   protected void turn() {
      debug("Making a turn");
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
         debug("Made an action!");
         actions.add(new Action(hitter, hitted, (int) (Math.random() * hitter.getNumUnits() / 4)));         
      } else {
         debug("Something..." + (hitter == null) + " ? " + (hitted == null));
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
