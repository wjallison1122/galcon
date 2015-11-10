package visualizers.threedimensionserver;

import galaxy.Fleet;
import galaxy.Planet;
import galaxy.Player;
import galaxy.Visualizer;

import java.awt.Graphics;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.java_websocket.server.WebSocketServer;

public class VisualizerServer extends Visualizer {
   private static final long serialVersionUID = 1L;
   
   private GalconServer srv;
   private List<Socket> clients;

   public VisualizerServer(int port) {
      super(400, 300, 3);
      try {
         
         srv = new GalconServer(port);
         srv.start();
      } catch (UnknownHostException e) {
         e.printStackTrace();
      }
      
   }

   @Override
   protected void newGame() {
      // TODO Auto-generated method stub
      
   }

   @Override
   protected void drawBackground(Graphics g) {
      // TODO Auto-generated method stub
      
   }
   
   private int updateCount = 0;

   @Override
   protected void drawPlanets(Planet[] planets, Graphics g) {
      if (updateCount++ % 200 == 0) {
         ByteBuffer byteBuffer = ByteBuffer.allocate(planets.length * 32 + 4);
         
         byteBuffer.putInt(planets.length);
         for (Planet p : planets) {
            byteBuffer.put(serialize(p));
         }
         srv.sendToAll(byteBuffer.array());
      }
   }
   
   private byte[] serialize(Planet p) {
      byte[] rtn = new byte[32];
      return ByteBuffer.wrap(rtn)
            .putDouble(p.RADIUS)
            .putDouble(p.getCoords()[0])
            .putDouble(p.getCoords()[1])
            .putDouble(p.getCoords()[2])
            .array();
   }

   @Override
   protected void drawFleets(Fleet[] fleets, Graphics g) {
      // TODO Auto-generated method stub
      
   }

   @Override
   protected void drawPlayerInfo(Player[] players, Graphics g) {
      // TODO Auto-generated method stub
      
   }

   @Override
   protected void drawOther(Graphics g) {
   }

}
