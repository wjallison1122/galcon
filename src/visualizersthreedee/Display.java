package visualizersthreedee;

import galaxy.Fleet;
import galaxy.Planet;
import galaxy.Player;
import galaxy.Visualizer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 *
 * @author Jono
 */
public class Display extends Visualizer {

   public Display() {
      super(800, 600, 3);
      //this.addMouseMotionListener(ma);
      //this.addMouseListener(ma);
      //this.addMouseWheelListener(ma);
      //this.addKeyListener(ka);
     // this.setFocusable(true);
      //this.setBackground(Color.black);
     // displayCamera = new Camera(new Vector(0, 0, -100000));
   }

 //  public Camera displayCamera;
 //  public double scrollSpeed = 100000;
 /////  private Vector cameraSpeed = new Vector(0, 0, 0);
 //  private boolean autoTurn = false;
 //  private int mousex, mousey;
 //  private boolean mouseDown = false;
   /*
   private MouseAdapter ma = new MouseAdapter() {
      @Override
      public void mouseDragged(MouseEvent me) {
         if (mouseDown) {
            displayCamera.hRot += (me.getX() - mousex) / (displayCamera.zoom / 3.0);
            displayCamera.vRot += (me.getY() - mousey) / (displayCamera.zoom / 3.0);
            if (displayCamera.vRot > Math.PI / 2) {
               displayCamera.vRot = Math.PI / 2;
            }
            if (displayCamera.vRot < -Math.PI / 2) {
               displayCamera.vRot = -Math.PI / 2;
            }
            mousex = me.getX();
            mousey = me.getY();
            repaint();
         } else {
            mousex = me.getX();
            mousey = me.getY();
            mouseDown = true;
         }
      }

      @Override
      public void mouseMoved(MouseEvent me) {
         mousex = me.getX();
         mousey = me.getY();
         mouseDown = false;
      }

      @Override
      public void mousePressed(MouseEvent me) {

         requestFocus(true);
      }

      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
         if (e.getWheelRotation() < 0) {
            displayCamera.zoom *= 1.05;
         } else {
            displayCamera.zoom *= .95;
         }
         if (displayCamera.zoom < 1000)
            displayCamera.zoom = 1000;
      }
   };
   public KeyAdapter ka = new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
         switch (e.getKeyCode()) {
         case 37: // right
            cameraSpeed.x = -scrollSpeed;
            break;
         case 38: // down
            cameraSpeed.y = -scrollSpeed;
            break;
         case 39: // left
            cameraSpeed.x = scrollSpeed;
            break;
         case 40: // up
            cameraSpeed.y = scrollSpeed;
            break;
         case 16: // in (RShift)
            cameraSpeed.z = -scrollSpeed;
            break;
         case 17: // out (LShift)
            cameraSpeed.z = scrollSpeed;
            break;
         }
      }

      @Override
      public void keyReleased(KeyEvent e) {
         switch (e.getKeyCode()) {
         case 37:
            cameraSpeed.x = 0;
            break;
         case 38:
            cameraSpeed.y = 0;
            break;
         case 39:
            cameraSpeed.x = 0;
            break;
         case 40:
            cameraSpeed.y = 0;
            break;
         case 16:
            cameraSpeed.z = 0;
            break;
         case 17:
            cameraSpeed.z = 0;
            break;
         }
      }
   };*/
   
   /*
      displayCamera.setMouseLocation(new Vector(mousex, mousey, 0));
      if (autoTurn) {
         displayCamera.hRot += .003;
      }
      displayCamera.moveCamera(Vector.scale(cameraSpeed, .01));
      repaint();
    */
   
   @Override
   protected void newGame() {
      // TODO Auto-generated method stub
      
   }

   @Override
   protected void drawPlanet(Planet p, Graphics g) {
      // TODO Auto-generated method stub
      
   }

   @Override
   protected void drawFleet(Fleet f, Graphics g) {
      // TODO Auto-generated method stub
      
   }

   @Override
   protected void drawBackground(Graphics g) {
      // TODO Auto-generated method stub
      
   }

   @Override
   protected void drawPlayerInfo(Player[] players, Graphics g) {
      // TODO Auto-generated method stub
      
   }

   @Override
   protected void drawOther(Graphics g) {
      //displayCamera.draw(planets, g, getWidth() / 2, getHeight());
   }

   @Override
   protected void keystroke(KeyEvent e) {
      
   }
}
