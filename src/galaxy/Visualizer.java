package galaxy;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class Visualizer extends JPanel implements KeyListener, MouseListener {
   private BufferedImage bufferImage;
   private Planet[] planets = Planet.getAllPlanets();
   protected final int WIN_WIDTH, WIN_HEIGHT;
   private JFrame frame;

   protected void debug(String str) {
      if (Main.debugMode) {
         System.out.println(str);
      }
   }

   protected Visualizer(int winWidth, int winHeight, int dimensions) {
      if (dimensions != Main.DIMENSIONS.length) {
         throw new RuntimeException("Visualizer does not match galaxy dimension space.");
      }

      WIN_WIDTH = winWidth;
      WIN_HEIGHT = winHeight;

      final Visualizer face = this;
      frame = new JFrame() {{
         setName("Galcon AI Challenge");
         setSize(WIN_WIDTH, WIN_HEIGHT);
         setContentPane(face);
         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         setBackground(Color.GRAY);
         setResizable(false);
         setVisible(true);
         addKeyListener(face);
         addMouseListener(face);
      }};
   }

   void nextGame() {
      planets = Planet.getAllPlanets();
   }

   protected abstract void newGame();

   void update() {
      BufferedImage image = new BufferedImage(WIN_WIDTH, WIN_HEIGHT, BufferedImage.TYPE_INT_ARGB);
      Graphics g = image.getGraphics();

      drawBackground(g);

      for (Planet p : planets) {
         drawPlanet(p, g);
      }

      for (Fleet f : Fleet.getAllFleets()) {
         drawFleet(f, g);
      }

      drawOther(g);

      bufferImage = image;
      frame.repaint();
   }

   @Override
   public void paint(Graphics g) {
      g.drawImage(bufferImage,0,0,this);
   }

   protected boolean checkRecentlyConquered(Planet p) {
      return p.checkRecentlyConquered();
   }

   protected int numUnitsOwnedBy(Player p) {
      return Galaxy.numUnitsOwnedBy(p);
   }

   protected abstract void drawPlanet(Planet p, Graphics g);

   protected abstract void drawFleet(Fleet f, Graphics g);

   protected abstract void drawBackground(Graphics g);

   protected abstract void drawPlayerInfo(Player[] players, Graphics g);

   protected abstract void drawOther(Graphics g);

   protected abstract void keystroke(KeyEvent e);

   @Override
   public void keyPressed(KeyEvent e) {
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
   public void mousePressed(MouseEvent e){}
   @Override
   public void mouseReleased(MouseEvent e){}
}
