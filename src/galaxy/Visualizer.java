package galaxy;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class Visualizer extends GameSettings implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
   private BufferedImage bufferImage;
   private Planet[] planets;
   private LinkedList<Player> players;
   private JFrame frame;
   private JPanel panel = new JPanel() {
      @Override
      public void paint(Graphics g) {
         g.drawImage(bufferImage,0,0,this);
      }
   };
   
   protected MouseOverInfo mouseOverInfo;


   protected interface MouseOverInfo {
      void draw(Graphics g);
   }

   protected Visualizer(int winWidth, int winHeight, int dimensions) {
      if (dimensions != DIMENSIONS.length) {
         throw new DimensionMismatchException("Visualizer does not match galaxy dimension space.");
      }
      
      Visualizer listener = this;
      frame = new JFrame() {{
         setName("Galcon AI Challenge");
         setSize(winWidth, winHeight);
         setContentPane(panel);
         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         setResizable(true);
         setVisible(true);
         addKeyListener(listener);
         addMouseListener(listener);
         addMouseMotionListener(listener);
         addMouseWheelListener(listener);
      }};
   }
   
   protected final int getWidth() {
      return panel.getWidth();
   }
   
   protected final int getHeight() {
      return panel.getHeight();
   }

   final void nextGame(LinkedList<Player> active, Planet[] newMap) {
      planets = newMap;
      players = active;
      newGame();
   }

   protected abstract void newGame();

   final void update(Fleet[] fleets) {
      BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
      Graphics g = image.getGraphics();

      // Switch to being drawAllUnits(planets, fleets)
      drawBackground(g);
      drawPlanets(planets, g);
      drawFleets(fleets, g);
      drawOther(g);
      drawPlayerInfo(players, g);
      if (mouseOverInfo != null) {
         mouseOverInfo.draw(g);
      }

      bufferImage = image;
      repaint();
   }
   
   // Why put this separate but not final?
   protected void repaint() {
      frame.repaint();
   }
   
   protected void requestFocus() {
      panel.requestFocus(true);
   }

   protected final boolean checkRecentlyConquered(Planet p) {
      return p.checkRecentlyConquered();
   }

   protected final int numUnitsOwnedBy(Player p) {
      return Director.numUnitsOwnedBy(p);
   }

   protected static final int numUnitsInPlanets(Player p) {
      return Director.numUnitsInPlanets(p);
   }

   protected static final int numUnitsInFleets(Player p) {
      return Director.numUnitsInFleets(p);
   }

   protected abstract void drawBackground(Graphics g);

   protected abstract void drawPlanets(Planet[] planets, Graphics g);

   protected abstract void drawFleets(Fleet[] fleets, Graphics g);

   protected abstract void drawPlayerInfo(LinkedList<Player> players, Graphics g);

   protected abstract void drawOther(Graphics g);

   protected void keystroke(KeyEvent e) {}

   @Override
   public final void keyPressed(KeyEvent e) {
      char command = (char)e.getKeyCode();
      if(command == 'Q') {
         System.exit(0);
      } else if (command == 'S') {
         Main.skipGame();
      } else if (command == ' ') {
         Main.togglePause();
      } else if (command == 'R') {
         Main.restartGame();
      } else if (command == 'T') {
         Main.reverseMap();
      }

      keystroke(e);
   }

   protected void mouseOverPress(MouseEvent e, Planet[] planets) {}
   
   protected void mousePress(MouseEvent e) {}

   @Override
   public final void mousePressed(MouseEvent e){
      mouseOverPress(e, planets);
      mousePress(e);
   }

   @Override
   public void keyReleased(KeyEvent e) {}
   @Override
   public void keyTyped(KeyEvent e) {}
   @Override
   public void mouseClicked(MouseEvent e) {}
   @Override
   public void mouseEntered(MouseEvent e) {}
   @Override
   public void mouseExited(MouseEvent e) {}
   @Override
   public void mouseReleased(MouseEvent e) {}
   @Override
   public void mouseDragged(MouseEvent e) {}
   @Override
   public void mouseMoved(MouseEvent e) {}
   @Override
   public void mouseWheelMoved(MouseWheelEvent e) {}
}
