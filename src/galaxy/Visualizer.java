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
   protected final int WIN_WIDTH, WIN_HEIGHT;
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

      WIN_WIDTH = winWidth;
      WIN_HEIGHT = winHeight;

      Visualizer listener = this;
      frame = new JFrame() {{
         setName("Galcon AI Challenge");
         setSize(WIN_WIDTH, WIN_HEIGHT);
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
   
   protected int getWidth() {
      return panel.getWidth();
   }
   
   protected int getHeight() {
      return panel.getHeight();
   }

   final void nextGame(LinkedList<Player> active) {
      planets = Planet.getAllPlanets();
      players = active;
      newGame();
   }

   protected abstract void newGame();

   final void update() {
      BufferedImage image = new BufferedImage(WIN_WIDTH, WIN_HEIGHT, BufferedImage.TYPE_INT_ARGB);
      Graphics g = image.getGraphics();

      // Switch to being drawAllUnits(planets, fleets)
      drawBackground(g);
      drawPlanets(planets, g);
      drawFleets(Fleet.getAllFleets(), g);
      drawOther(g);
      drawPlayerInfo(players, g);
      if (mouseOverInfo != null) {
         mouseOverInfo.draw(g);
      }

      bufferImage = image;
      repaint();
   }
   
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
      return Galaxy.numUnitsOwnedBy(p);
   }

   protected abstract void drawBackground(Graphics g);

   protected abstract void drawPlanets(Planet[] planets, Graphics g);

   protected abstract void drawFleets(Fleet[] fleets, Graphics g);

   protected abstract void drawPlayerInfo(LinkedList<Player> players, Graphics g);

   protected abstract void drawOther(Graphics g);

   protected void keystroke(KeyEvent e){}

   @Override
   public final void keyPressed(KeyEvent e) {
      char command = (char)e.getKeyCode();
      if(command == 'Q') {
         System.exit(0);
      } else if (command == 'S') {
         Main.skipGame();
      } else if (command == ' ') {
         Main.togglePause();
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
   public void keyReleased(KeyEvent arg0) {}
   @Override
   public void keyTyped(KeyEvent arg0) {}
   @Override
   public void mouseClicked(MouseEvent e){}
   @Override
   public void mouseEntered(MouseEvent e){}
   @Override
   public void mouseExited(MouseEvent e){}
   @Override
   public void mouseReleased(MouseEvent e){}
   @Override
   public void mouseDragged(MouseEvent arg0) {}
   @Override
   public void mouseMoved(MouseEvent arg0) {}
   @Override
   public void mouseWheelMoved(MouseWheelEvent arg0) {}
}
