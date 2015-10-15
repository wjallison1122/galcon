package galaxy;

import java.awt.Graphics;
import java.util.LinkedList;

import javax.swing.JFrame;


@SuppressWarnings("serial")
public abstract class VisualizerInterface extends JFrame {
   
   protected LinkedList<Planet> planets;
   protected LinkedList<Fleet> fleets;
   
   protected void debug(String str) {
      if (Main.debugMode) {
         System.out.println(str);
      }
   }

   protected VisualizerInterface(LinkedList<Planet> planets, LinkedList<Fleet> fleets) {
      this.planets = planets;
      this.fleets = fleets;
   }
   
   public void paint(Graphics g) {
      for (Planet p : planets) {
         drawPlanet(p, g);
      }
   }
   
   abstract protected void planetHit(Planet p, Fleet f);
   
   abstract protected void drawPlanet(Planet p, Graphics g);
   
   abstract protected void drawFleet(Fleet f, Graphics g);
}
