package ais;

import galaxy.Action;
import galaxy.Player;

import java.awt.Color;
import java.util.LinkedList;

public class BasicAI extends Player {

   public BasicAI() {
      super(Color.BLUE, "James");

   }

   @Override
   protected void turn() {
      actions = new LinkedList<Action>();
   }
}
