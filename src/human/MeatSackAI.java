package human;

import java.awt.Color;
import java.util.LinkedList;

import galaxy.Action;
import galaxy.Planet;
import galaxy.Player;

public class MeatSackAI extends Player {

   protected MeatSackAI() {
      super(Color.CYAN, "Fleshling");
   }

   @Override
   protected void turn() {
      actions = new LinkedList<Action>();
      
   }
   
   public Planet[] getPlanets() {
      return planets;
   }
   
   //for testing
   void setPlanets(Planet[] planets) {
      this.planets = planets;
   }
}
