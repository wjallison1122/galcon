package galaxy;

import java.awt.Color;
import java.util.LinkedList;

public abstract class Player {
   public final Color COLOR;
   public final String NAME;
   private static int currentId = 0;
   final int ID = currentId++;

   protected Planet[] planets = Planet.getAllPlanets();
   protected LinkedList<Action> actions;


   /*
    *  Arbitrary creation of new players does not matter since ID is forced unique and only main-created
    *  players can actually get asked for turns. 
    */
   protected Player(Color c, String name) {
      COLOR = c;
      NAME = name;
   }

   LinkedList<Action> getActions() {
      return actions == null ? new LinkedList<Action>() : actions;
   }
   
   void doTurn() {
      actions = new LinkedList<Action>();
      turn();
   }
   
   protected abstract void turn();

   /**
    * Checks if two players are equal. Works with null. 
    * @param p1
    * @param p2
    * @return Whether two players are equal. Both null returns true, only one null returns false. 
    */
   protected static boolean areEqual(Player p1, Player p2) {
      return (p1 == null || p2 == null) ? p1 == p2 : p1.ID == p2.ID;
   }

   protected int numUnitsOwnedBy(Player p) {
      return Galaxy.numUnitsOwnedBy(p);
   }
   
   protected int numUnitsInPlanets(Player p) {
      return Planet.getNumUnitsInPlanets(p);
   }
   
   protected int numUnitsInFleets(Player p) {
      return Fleet.getNumUnitsInFleets(p);
   }

   protected LinkedList<Fleet> getAllFleets() {
      return Fleet.getAllFleets();
   }
}











