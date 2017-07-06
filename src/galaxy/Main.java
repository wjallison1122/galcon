package galaxy;

import java.util.Timer;
import java.util.TimerTask;

final class Main extends GameSettings {
   // TODO Figure out if this is really the best setup
   private static Director director = new Director();
   private static Timer game = new Timer();
   private static boolean pause = false;

   public static void main(String[] args) {
      if (director.usingVisualizer()) {
         game.schedule(new TimerTask() {
            @Override
            public void run() {
               if (!pause && !director.done()) {
                  director.next();
               }
            }
         }, 0, FRAME_TIME);
      } else {
         while (!director.done()) {
            if (!pause) {
               director.next();
            }
         }
      }
   }

   /**
    * TODO
    * For make map from text
    * @param ID
    * @return
    */
   static Player getPlayer(int ID) {
      return null;
   }

   /**
    * For visualizer to be able to pause game
    */
   static void togglePause() {
      pause = !pause;
   }

   /**
    * For visualizer to be able to skip games
    */
   static void skipGame() {
      director.skipGame();
   }

   static void restartGame() {
      director.restartGame();
   }
   
   static void reverseMap() {
      director.startReversedGame();
   }
}
