package galaxy;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;

public abstract class Player {
	public final Color COLOR;
	public final String NAME;
	private static int currentId = 0;
	final int ID;
	protected LinkedList<Action> actions;
	
	/*
	 *  Arbitrary creation of new players does not matter since ID is forced unique and only main-created
	 *  players can actually get asked for turns. 
	 */
	protected Player(Color c, String name) {
	   COLOR = c;
	   NAME = name;
	   ID = currentId++;
	}
	
	LinkedList<Action> getActions() {
	   return actions == null ? new LinkedList<Action>() : actions;
	}
   
   protected abstract void turn();
   
   protected static boolean areEqual(Player p1, Player p2) {
      try {
         return p1.ID == p2.ID;
      } catch (Exception e) {
         return false;
      }
   }
   
   protected int numUnitsOwnedBy(Player p) {
      return Galaxy.numUnitsOwnedBy(p);
   }
   
   protected LinkedList<Planet> getAllPlanets() {
      return Planet.getAllPlanets();
   }
   
   protected LinkedList<Fleet> getAllFleets() {
      return Fleet.getAllFleets();
   }
   
   @SuppressWarnings("unchecked")
   protected LinkedList<Planet> getPlanetsOwnedBy(Player p) {
      return (LinkedList<Planet>) getUnitsOwnedBy(getAllPlanets(), p);
   }
   
   @SuppressWarnings("unchecked")
   protected LinkedList<Fleet> getFleetsOwnedBy(Player p) {
      return (LinkedList<Fleet>) getUnitsOwnedBy(getAllFleets(), p);
   }
   
   private LinkedList<? extends Unit> getUnitsOwnedBy(LinkedList<? extends Unit> units, Player p) {
      Iterator<? extends Unit> iter = units.iterator();
      
      while (iter.hasNext()) {
         if (!iter.next().ownedBy(p)) {
            iter.remove();
         }
      }
      
      return units;
   }
}











