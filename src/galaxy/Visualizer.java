package galaxy;

import java.awt.Color;
import java.awt.Font;
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
   private Player[] players;
   protected final int WIN_WIDTH, WIN_HEIGHT;
   private JFrame frame;

   private static final Font mouseOverFont = new Font("Monospaced", Font.PLAIN, 12);
   private MouseOverInfo mouseOverInfo;

   class MouseOverInfo {
      String text;
      int coords[];
      int timeToLive; //in frames
   }

   protected final void debug(String str) {
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
         setResizable(true);
         setVisible(true);
         addKeyListener(face);
         addMouseListener(face);
      }};

      mouseOverInfo = new MouseOverInfo();
   }

   final void nextGame(Player[] active) {
      planets = Planet.getAllPlanets();
      players = active;
      newGame();
   }

   protected abstract void newGame();

   final void update() {
      BufferedImage image = new BufferedImage(WIN_WIDTH, WIN_HEIGHT, BufferedImage.TYPE_INT_ARGB);
      Graphics g = image.getGraphics();

      drawBackground(g);
      drawPlanets(planets, g);
      drawFleets(Fleet.getAllFleets(), g);
      drawPlayerInfo(players, g);
      drawOther(g);
      drawMouseOverText(g);

      bufferImage = image;
      frame.repaint();
   }

   @Override
   public final void paint(Graphics g) {
      g.drawImage(bufferImage,0,0,this);
   }

   protected void drawMouseOverText(Graphics g) {
      if(mouseOverInfo.timeToLive > 0) {
         mouseOverInfo.timeToLive--;
         g.setFont(mouseOverFont);
         g.setColor(Color.LIGHT_GRAY);
         g.drawString(mouseOverInfo.text, mouseOverInfo.coords[0], mouseOverInfo.coords[1]);
      }
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

   protected abstract void drawPlayerInfo(Player[] players, Graphics g);

   protected abstract void drawOther(Graphics g);

   protected void keystroke(KeyEvent e) {

   }

   protected void mousepress(MouseEvent e) {

   }

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

   @Override
   public final void mousePressed(MouseEvent e) {
      if(Main.DIMENSIONS.length != 2) {
         return;
      }

      //Minuses are to offset it to the tip of the mouse pointer
      int mouseCoords[] = {e.getX() - 10, e.getY() - 12};
      for(Planet p : planets) {
         double tempCoords[] = {mouseCoords[0], mouseCoords[1] - p.RADIUS/2};
         if(p.distanceTo(tempCoords) < p.RADIUS) {
            mouseOverInfo.coords = mouseCoords;
            mouseOverInfo.text = p.PRODUCTION_TIME + "production";
            mouseOverInfo.timeToLive = 120;
         }
      }

      mousepress(e);
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
}
