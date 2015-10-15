package galaxy;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public abstract class VisualizerInterface extends JFrame implements KeyListener {
   
   //double Buffering stuff
//   private static Graphics bufferGraphics;
//   private static Image bufferImage;
   
   protected final int GAME_WIDTH, GAME_HEIGHT;
   
   Planet[] planets = Planet.getAllPlanets();
   
   protected void debug(String str) {
      if (Main.debugMode) {
         System.out.println(str);
      }
   }

   protected VisualizerInterface(int gameWidth, int gameHeight, int dimensions) {
      if (dimensions != Main.DIMENSIONS) {
         throw new RuntimeException("Visualizer does not match galaxy dimension space.");
      }
      
      GAME_WIDTH = gameWidth;
      GAME_HEIGHT = gameHeight;
      
      setName("Galcon AI Challenge");
      setSize(GAME_WIDTH, GAME_HEIGHT);
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setBackground(Color.GRAY);
      setResizable(false);
      setVisible(true);
      
      debug("Made window");
   }
   
   public void paint(Graphics g) {
      // instanceof isn't normally best practice, but this avoids
      // calls to paint which aren't from JFrame
      if (g == null || !(g instanceof Graphics)) {
         return;
      }
      
      debug("Painting");
      
//***** old 
//      if (bufferImage == null) {
//         bufferImage = createImage(this.getSize().width,this.getSize().height);
//         bufferGraphics = bufferImage.getGraphics();
//      }
//
//      clearScreen(bufferGraphics);
//      Planet.drawAll(bufferGraphics);
//      Fleet.drawAll(bufferGraphics);
//      director.drawCurrentPlayerInfo(bufferGraphics);
//      if (USE_EXPLOSIONS) {
//         Explosion.drawAll(bufferGraphics);
//      }
//
//      g.drawImage(bufferImage,0,0,this);
      
      
      debug("Done painting");
      
      for (Planet p : planets) {
         drawPlanet(p, g);
      }
      
      for (Fleet f : Fleet.getAllFleets()) {
         drawFleet(f, g);
      }
   }
   
   protected boolean checkRecentlyConquered(Planet p) {
      return p.checkRecentlyConquered();
   }
      
   abstract protected void drawPlanet(Planet p, Graphics g);
   
   abstract protected void drawFleet(Fleet f, Graphics g);
   
   abstract protected void drawBackground(Graphics g);
   
   public void keyPressed(KeyEvent e) {
      char command = (char)e.getKeyCode();
      if(command == 'Q') {
         System.exit(0);
      } else if (command == 'S') {
         Main.skipGame();
      } else if (command == ' ') {
         Main.togglePause();
      }
   }
   
   public void keyReleased(KeyEvent arg0) {}
   public void keyTyped(KeyEvent arg0) {}
}
