package visualizers;

import galaxy.Fleet;
import galaxy.Planet;
import galaxy.Player;
import galaxy.Visualizer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.ImageIcon;

@SuppressWarnings("serial")
public class DefaultVisualizer extends Visualizer {

   public DefaultVisualizer(int [] dimensions) {
      super(dimensions[0], dimensions[1], 2);
   }

   private static final Image STAR_BACKGROUND = new ImageIcon("SpacePic.jpg").getImage();
   private static final Image PLANET_IMAGE = new ImageIcon("planetGray.png").getImage();
   private static final Font FONT = new Font("Monospaced", Font.BOLD, 18);

   @Override
   protected void drawPlanets(Planet[] planets, Graphics g) {
      for (Planet p : planets) {
         drawPlanet(p, g);
      }
   }

   private void drawPlanet(Planet p, Graphics g) {
      double[] coords = p.getCoords();
      final int X = (int)coords[0], Y = (int)coords[1];
      Color c = p.getColor();
      g.setColor(c);
      g.drawImage(PLANET_IMAGE, X - p.RADIUS, Y - p.RADIUS, p.RADIUS * 2, p.RADIUS * 2, null);
      g.setColor(new Color(c.getRed(),
            c.getGreen(),
            c.getBlue(),
            Math.min(70 + p.getNumUnits(), 170)));
      g.fillOval(X - p.RADIUS, Y - p.RADIUS, p.RADIUS * 2, p.RADIUS * 2);

      g.setColor(invertColor(c));
      g.setFont(FONT);
      g.drawString("" + p.getNumUnits(), (int)(X - 0.7 * Planet.MIN_RADIUS), (int)(Y + 0.7 * Planet.MIN_RADIUS));
      
      
      if (checkRecentlyConquered(p)) {
         new Explosion(coords[0], coords[1], p.RADIUS);
      }
   }

   @Override
   protected void drawFleets(Fleet[] fleets, Graphics g) {
      for (Fleet f : fleets) {
         drawFleet(f, g);
      }
   }

   private void drawFleet(Fleet f, Graphics g) {
      if (f == null) {
         debug("wft?)')");
      }


      int arbitraryRadius = 10 + f.getNumUnits() / 5;
      double[] coords = f.getCoords();
      final int X = (int)coords[0], Y = (int)coords[1];
      Color c = f.getColor();
      g.setColor(c);
      g.fillOval(X - arbitraryRadius, Y - arbitraryRadius, arbitraryRadius * 2, arbitraryRadius * 2);
      g.setFont(FONT);
      g.setColor(invertColor(c));
      g.drawString("" + f.getNumUnits(), X - 8, Y + 5);
   }

   @Override
   protected void drawBackground(Graphics g) {
      g.clearRect(0, 0, WIN_WIDTH, WIN_HEIGHT);
      g.drawImage(STAR_BACKGROUND, 0, 0, WIN_WIDTH, WIN_HEIGHT, null);
   }


   @Override
   protected void drawOther(Graphics g) {
      Explosion.drawAll(g);
      Explosion.updateAll();
   }

   Color invertColor(Color c) {
      return new Color(255 - c.getRed(),
            255 - c.getGreen(),
            255 - c.getBlue());
   }

   static class Explosion {
      static final int MIN_PARTICLES_PER_EXPLOSION = 15;
      static final int MAX_PARTICLES_PER_EXPLOSION = 30;
      static final double PARTICLE_SPEED_MIN = 0.5;
      static final double PARTICLE_SPEED_MAX = 1.3;

      private LinkedList<Particle> particles = new LinkedList<Particle>();
      private static LinkedList<Explosion> explosions = new LinkedList<Explosion>();

      public static final Image PARTICLE = new ImageIcon("particle.png").getImage();
      public static final int RADIUS_MIN = 5;
      public static final int RADIUS_MAX = 10;
      public static final double DELTA_RADIUS_MIN = 0.05;
      public static final double DELTA_RADIUS_MAX = 0.2;

      /**
       * Creates an explosion at x, y. 
       * Explosions are a collection of particles created from x, y
       * Explosions are removed when all of their particles are removed
       * Particles are removed when they go far enough
       * Could easily make just one big list of particles and have an
       *    "explosion" be adding to that list of particles, with each
       *    particle being checked directly rather than through an 
       *    explosion. However, no real difference in processing speed
       *    with this current method and it allows for potential tweaks
       *    to be made more easily in the future. 
       * @param x X coord of planet
       * @param y Y coord of planet
       * @param radius Radius of planet
       */
      Explosion(double x, double y, int radius) {
         int numParticles = (MAX_PARTICLES_PER_EXPLOSION - MIN_PARTICLES_PER_EXPLOSION) * 
               (radius - Planet.MIN_RADIUS) / (Planet.MAX_RADIUS - Planet.MIN_RADIUS);
         while (numParticles-- > 0) {
            new Particle(x, y, radius);
         }
         explosions.add(this);
      }

      static void clear() {
         explosions.clear();
      }

      static void updateAll() {
         Iterator<Explosion> it = explosions.iterator();
         while (it.hasNext()) {
            if (it.next().update()) {
               it.remove();
            }
         }
      }

      private boolean update() {
         Iterator<Particle> it = particles.iterator();
         while (it.hasNext()) {
            if (it.next().update() < 0) {
               it.remove();
            }
         }
         return particles.size() == 0;
      }

      static void drawAll(Graphics g) {
         for (Explosion e : explosions) {
            e.draw(g);
         }
      }

      void draw(Graphics g) {
         for (Particle p : particles) {
            p.draw(g);
         }
      }

      private class Particle {
         private double x, y, dx, dy, radius, dradius;

         Particle(double px, double py, int pRad) {
            double theta = Math.random() * 2 * Math.PI;
            double rad = Math.random() * pRad;
            double pSpeed = Math.random() * (PARTICLE_SPEED_MAX - PARTICLE_SPEED_MIN) + PARTICLE_SPEED_MIN;
            x = px + rad * Math.cos(theta);
            y = py + rad * Math.sin(theta);
            dx = pSpeed * Math.cos(theta);
            dy = pSpeed * Math.sin(theta);
            radius = Math.random() * (RADIUS_MAX - RADIUS_MIN) + RADIUS_MIN;
            dradius = Math.random() * (DELTA_RADIUS_MAX - DELTA_RADIUS_MIN) + DELTA_RADIUS_MIN;
            particles.add(this);
         }

         void draw(Graphics g) {
            if (radius > 0) {
               g.drawImage(PARTICLE, (int)(x - radius), (int)(y - radius), (int)(radius * 2), (int)(radius * 2), null);
            }
         }

         double update() {
            x += dx;
            y += dy;
            radius -= dradius;
            return radius;
         }
      }
   }

   @Override
   protected void drawPlayerInfo(Player[] players, Graphics g) {
      final int FONT_HEIGHT = 40;
      Font font = new Font("Monospaced", Font.PLAIN, FONT_HEIGHT);
      g.setFont (font);

      int offset = 1;
      for (Player p : players) {
         g.setColor(Color.DARK_GRAY);
         g.drawString(p.NAME + ": " + numUnitsOwnedBy(p), 10, FONT_HEIGHT * offset - 1);
         g.drawString(p.NAME + ": " + numUnitsOwnedBy(p), 10, FONT_HEIGHT * offset + 1);
         g.drawString(p.NAME + ": " + numUnitsOwnedBy(p), 10 - 1, FONT_HEIGHT * offset);
         g.drawString(p.NAME + ": " + numUnitsOwnedBy(p), 10 + 1, FONT_HEIGHT * offset);
         g.setColor(p.COLOR);
         g.drawString(p.NAME + ": " + numUnitsOwnedBy(p), 10, FONT_HEIGHT * offset++);
      }
   }

   @Override
   protected void newGame() {
      Explosion.clear();
   }

   @Override
   protected void keystroke(KeyEvent e) {
      // TODO Auto-generated method stub
      
   }
}
