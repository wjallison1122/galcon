package ais.basicai;

import galaxy.Planet;
import galaxy.Player;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;

public class BasicRandomAI extends Player {

   int tic = 0;

   public BasicRandomAI() {
      super(Color.RED, "BasicRandomAI");
   }

   @Override
   protected void turn() {
      if (tic++ % 200 == 0) {
         makeThingsUp();
      }
   }

   void makeThingsUp() {
      LinkedList<Planet> targets = new LinkedList<Planet>();
      ArrayList<Planet> myPlanets = new ArrayList<Planet>();
      for (Planet p : planets) {
         if (p.ownedBy(this)) {
            myPlanets.add(p);
         } else if ((p.ownedByOpponentOf(this) && Math.random() > .8) || true) {
            targets.add(p);
         }
      }

      for (Planet p : targets) {
         Planet myPlan = myPlanets.get((int)(Math.random() * myPlanets.size()));
         addAction(myPlan, p, Math.max(20, (int)(Math.random() * myPlan.getNumUnits() * .8)));
      }
   }
}





