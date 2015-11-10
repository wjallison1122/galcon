package visualizers.threedimensionserver;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class GalconServer extends WebSocketServer {

   public GalconServer( int port ) throws UnknownHostException {
      super( new InetSocketAddress( port ) );
   }

   public GalconServer( InetSocketAddress address ) {
      super( address );
   }

   @Override
   public void onOpen( WebSocket conn, ClientHandshake handshake ) {
      System.out.println( conn.getRemoteSocketAddress().getAddress().getHostAddress() + " visualizer added" );
   }

   @Override
   public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
      System.out.println( conn + " visualizer removed" );
   }

   @Override
   public void onMessage( WebSocket conn, String message ) {
      System.out.println( conn + " send message: " + message);
   }
   
   @Override
   public void onError( WebSocket conn, Exception ex ) {
      ex.printStackTrace();
      if( conn != null ) {
         // some errors like port binding failed may not be assignable to a specific websocket
      }
   }

   /**
    * Sends <var>text</var> to all currently connected WebSocket clients.
    * 
    * @param text
    *            The String to send across the network.
    * @throws InterruptedException
    *             When socket related I/O errors occur.
    */
   public void sendToAll(byte[] data) {
      Collection<WebSocket> con = connections();
      synchronized (con) {
         for( WebSocket c : con ) {
            c.send(data);
         }
      }
   }
}
