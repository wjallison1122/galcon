package visualizer;

import galaxy.Fleet;
import galaxy.Planet;
import galaxy.VisualizerInterface;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Visualizer extends VisualizerInterface {
   
   protected Visualizer(LinkedList<Planet> planets, LinkedList<Fleet> fleets) {
      super(planets, fleets);
   }

   private static final Image PLANET_IMAGE = new ImageIcon("planetGray.png").getImage();


   boolean debugMode;
   
   
   @Override
   public void planetHit(Planet p, Fleet f) {
      
   }

   @Override
   public void drawPlanet(Planet p, Graphics g) {
      
   }

   @Override
   public void drawFleet(Fleet f, Graphics g) {
      
   }

   private void drawPlanet(Graphics g, Planet p) {
      Color c = p.getColor();
      g.setColor(c);
      g.drawImage(PLANET_IMAGE, X - RADIUS, Y - RADIUS, RADIUS * 2, RADIUS * 2, null);
      g.setColor(new Color(c.getRed(),
            c.getGreen(),
            c.getBlue(),
            Math.min(70 + numUnits, 170)));
      g.fillOval(X - RADIUS, Y - RADIUS, RADIUS * 2, RADIUS * 2);

      g.setColor(invertColor(c));
      g.setFont(FONT);
      g.drawString("" + numUnits, (int)(X - 0.7 * MIN_RADIUS), (int)(Y + 0.7 * MIN_RADIUS));
   }
   
   /**
    * Double buffered graphics
    */
   public void paint(Graphics g) {
      debug("Painting");
      if (bufferImage == null) {
         bufferImage = createImage(this.getSize().width,this.getSize().height);
         bufferGraphics = bufferImage.getGraphics();
      }

      clearScreen(bufferGraphics);
      Planet.drawAll(bufferGraphics);
      Fleet.drawAll(bufferGraphics);
      director.drawCurrentPlayerInfo(bufferGraphics);
      if (USE_EXPLOSIONS) {
         Explosion.drawAll(bufferGraphics);
      }

      g.drawImage(bufferImage,0,0,this);
      debug("Done painting");
   }
   
   private void clearScreen(Graphics g) {
      g.clearRect(0, 0, WIN_WIDTH, WIN_HEIGHT);
      g.drawImage(STAR_BACKGROUND, 0, TOP_BAR_HEIGHT, WIN_WIDTH, WIN_HEIGHT, null);
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
      
      final int X, Y, RADIUS;

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
      Explosion(int x, int y, int radius) {
         X = x;
         Y = y;
         RADIUS = radius;
         int numParticles = (MAX_PARTICLES_PER_EXPLOSION - MIN_PARTICLES_PER_EXPLOSION) * 
                            (RADIUS - Planet.MIN_RADIUS) / (Planet.MAX_RADIUS - Planet.MIN_RADIUS);
         while (numParticles-- > 0) {
            new Particle();
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
         
         Particle() {
            double theta = Math.random() * 2 * Math.PI;
            double pRad = Math.random() * RADIUS;
            double pSpeed = Math.random() * (PARTICLE_SPEED_MAX - PARTICLE_SPEED_MIN) + PARTICLE_SPEED_MIN;
            x = X + pRad * Math.cos(theta);
            y = Y + pRad * Math.sin(theta);
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
}
