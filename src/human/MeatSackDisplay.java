package human;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

import galaxy.Main;
import galaxy.Planet;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MeatSackDisplay extends JPanel {
   private JFrame myframe;
   private MeatSackAI player;
   
   private BufferedImage base;
   private Planet selectedPlanet;
   private Planet destinationPlanet;
   private int cursorX;
   private int cursorY;
   private State interfaceState = State.NONE;
   
   private static enum State {
      NONE,
      SEEKING,
      FOUND;
   }

   public static void main(String args[]) {
      int COUNT = 10;
      MeatSackAI ai = new MeatSackAI();
      Planet[] testPlanets = new Planet[COUNT];
      for (int i = 0; i < COUNT; i++) {
         testPlanets[i] = Planet.generatePlanet();
      }
      ai.setPlanets(testPlanets);
      MeatSackDisplay thing = new MeatSackDisplay(ai);
   }

   public MeatSackDisplay(MeatSackAI ai) {
      this.player = ai;
      
      updateBase();
      
      MeatSackDisplay display = this;
      
      myframe = new JFrame() {{
         this.setSize(Main.WIN_WIDTH, Main.WIN_HEIGHT);
         this.setContentPane(display);
         this.setVisible(true);
         this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      }};
      
      Timer timer = new Timer();
      timer.scheduleAtFixedRate(new TimerTask() {
         public void run() {
            myframe.repaint();
         }
      }, 33, 33);
      
      this.addMouseListener(new MouseAdapter() {
         
         @Override
         public void mousePressed(MouseEvent e) {
            selectedPlanet = getPlanetAtLocation(e.getX(), e.getY());
            if (selectedPlanet == null) {
               interfaceState = State.NONE;
            } else {
               interfaceState = State.SEEKING;
            }
         }
         
         @Override
         public void mouseReleased(MouseEvent e) {
            if (interfaceState == State.SEEKING) {
               destinationPlanet = getPlanetAtLocation(e.getX(), e.getY());
               interfaceState = State.FOUND;
            } else {
               interfaceState = State.NONE;
            }
         }
      });
      
      this.addMouseMotionListener(new MouseAdapter() {
         @Override
         public void mouseMoved(MouseEvent e) {
            cursorX = e.getX();
            cursorY = e.getY();
            if (interfaceState == State.SEEKING) {
               destinationPlanet = getPlanetAtLocation(e.getX(), e.getY());
            } else if (interfaceState == State.NONE) {
               selectedPlanet = getPlanetAtLocation(e.getX(), e.getY());
            }
         }
         
         @Override
         public void mouseDragged(MouseEvent e) {
            cursorX = e.getX();
            cursorY = e.getY();
            if (interfaceState == State.SEEKING) {
               destinationPlanet = getPlanetAtLocation(e.getX(), e.getY());
            } else if (interfaceState == State.NONE) {
               selectedPlanet = getPlanetAtLocation(e.getX(), e.getY());
            }
         }
      });
   }

   @Override
   public void paint(Graphics g) {
      
      g.drawImage(getFrame(), 0, 0, null);
   }
   
   public BufferedImage getFrame() {
      BufferedImage frame = new BufferedImage(Main.WIN_WIDTH, Main.WIN_HEIGHT, BufferedImage.TYPE_INT_RGB);
      Graphics2D g = frame.createGraphics();
      g.drawImage(base, 0, 0, null);
      if (interfaceState == State.SEEKING) {
         g.setStroke(new BasicStroke(3.0f));
         g.setColor(Color.WHITE);
         g.drawOval(
               (int) selectedPlanet.getCoords()[0] - selectedPlanet.RADIUS / 2 - 5, 
               (int) selectedPlanet.getCoords()[1] - selectedPlanet.RADIUS / 2 - 5, 
               selectedPlanet.RADIUS + 10, 
               selectedPlanet.RADIUS + 10);
         
         int x = (int) selectedPlanet.getCoords()[0];
         int y = (int) selectedPlanet.getCoords()[1];
         int diffx = x - cursorX;
         int diffy = y - cursorY;
         double factor = Math.sqrt(diffx * diffx + diffy * diffy);
         int fromx1 = x + (int) (diffy * (5 + selectedPlanet.RADIUS / 2) / factor);
         int fromx2 = x - (int) (diffy * (5 + selectedPlanet.RADIUS / 2) / factor);
         int fromy1 = y - (int) (diffx * (5 + selectedPlanet.RADIUS / 2) / factor);
         int fromy2 = y + (int) (diffx * (5 + selectedPlanet.RADIUS / 2) / factor);
         g.drawLine(fromx1, fromy1, cursorX, cursorY);
         g.drawLine(fromx2, fromy2, cursorX, cursorY);
      } else if (interfaceState == State.FOUND) {
         g.setStroke(new BasicStroke(3.0f));
         g.setColor(Color.WHITE);
         g.drawOval(
               (int) selectedPlanet.getCoords()[0] - selectedPlanet.RADIUS / 2 - 5, 
               (int) selectedPlanet.getCoords()[1] - selectedPlanet.RADIUS / 2 - 5, 
               selectedPlanet.RADIUS + 10, 
               selectedPlanet.RADIUS + 10);
         
         g.setStroke(new BasicStroke(3.0f));
         g.setColor(Color.WHITE);
         g.drawOval(
               (int) destinationPlanet.getCoords()[0] - destinationPlanet.RADIUS / 2 - 5, 
               (int) destinationPlanet.getCoords()[1] - destinationPlanet.RADIUS / 2 - 5, 
               destinationPlanet.RADIUS + 10, 
               destinationPlanet.RADIUS + 10);
         
         int x = (int) selectedPlanet.getCoords()[0];
         int y = (int) selectedPlanet.getCoords()[1];
         int destx = (int) destinationPlanet.getCoords()[0];
         int desty = (int) destinationPlanet.getCoords()[1];
         int diffx = x - destx;
         int diffy = y - desty;
         double factor = Math.sqrt(diffx * diffx + diffy * diffy);
         int fromx1 = x + (int) (diffy * (5 + selectedPlanet.RADIUS / 2) / factor);
         int fromx2 = x - (int) (diffy * (5 + selectedPlanet.RADIUS / 2) / factor);
         int fromy1 = y - (int) (diffx * (5 + selectedPlanet.RADIUS / 2) / factor);
         int fromy2 = y + (int) (diffx * (5 + selectedPlanet.RADIUS / 2) / factor);
         g.drawLine(fromx1, fromy1, destx, desty);
         g.drawLine(fromx2, fromy2, destx, desty);
      }
      return frame;
   }
   
   public void updateBase() {
      BufferedImage newImage = new BufferedImage(Main.WIN_WIDTH, Main.WIN_HEIGHT, BufferedImage.TYPE_INT_RGB);
      drawBackground(newImage.getGraphics());
      drawPlanets(newImage.getGraphics());
      base = newImage;
   }

   private void drawBackground(Graphics g) {
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, Main.WIN_WIDTH, Main.WIN_HEIGHT);
   }

   private void drawPlanets(Graphics g) {
      for (Planet p : player.getPlanets()) {
         g.setColor(p.getColor());
         g.fillOval((int) p.getCoords()[0] - p.RADIUS / 2, (int) p.getCoords()[1] -  p.RADIUS / 2, p.RADIUS, p.RADIUS);
      }
   }
   
   private Planet getPlanetAtLocation(int x, int y) {
      for (Planet p : player.getPlanets()) {
         int planetX = (int) p.getCoords()[0];
         int planetY = (int) p.getCoords()[1];
         if (Math.sqrt((x - planetX) * (x - planetX) + (y - planetY) * (y - planetY)) < p.RADIUS) {
            return p;
         }
      }
      return null;
   }
}
