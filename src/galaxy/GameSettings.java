package galaxy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import mapmakers.RandomMapMaker;
import mapmakers.SymmetricMapMaker;
import stats.DefaultStats;
import visualizers.threedimensiondefault.Display;
import visualizers.twodimensiondefault.DefaultVisualizer;
import ais.jono.ContestInfluenceAI;
import ais.jono.GoodAI;
import ais.tyler.TylerDefenderAI;
import ais.tyler.TylerRandomAI;

enum SymmetryType{VERTICAL,HORIZONTAL,DIAGONAL,RADIAL,}

// TODO Switch to holder pattern
enum VisualizerType{TWO_D,THREE_D,SERVER}

enum MapType{RANDOM,SYMMETRICAL}

enum StatsType{DEFAULT}

public class GameSettings {
    public static final boolean debugMode = true, logGame = false;

    private static BufferedWriter gameLog = logGame ? makeLogFile("galconset-" + formatDate(new Date())) : null;
    private static Player p1 = new GoodAI(false);
    private static Player p2 = new TylerRandomAI();
    public Player [] players = {p1, p2};
    public static final int PLAYERS_PER_GAME = 2;

    public static final int NUM_PLANETS = 16;

    private final MapType map = MapType.RANDOM;
    private final StatsType stats = StatsType.DEFAULT;
    private final VisualizerType vis = VisualizerType.TWO_D;
    public final static int FRAME_TIME = 10;
    //   public final int[] DIMENSIONS = {1000, 1000, 1000};
    public final int[] DIMENSIONS = (vis == VisualizerType.TWO_D) ? new int[] {800, 800} : new int[] {1000, 1000, 1000};

    public static final int FLEET_SPEED = 2;
    public final int NUM_ROUNDS = 5000;
    public final int TIC_LIMIT = 50000;
    final boolean reverseEachMap = true;

    public static final int MAX_RADIUS = 50;
    public static final int MIN_RADIUS = 12;
    public static final int MAX_NEUTRAL_UNITS = 50;
    public static final int MIN_PRODUCE_TIME = 34;
    public static final int MAX_PRODUCE_TIME = 100;

    final Stats createStats(Player p) {
        switch (stats) {
            case DEFAULT:
                return new DefaultStats(p);
            default:
                return null;
        }
    }

    final MapMaker createMapMaker() {
        switch (map) {
            case RANDOM:
                return new RandomMapMaker();
            case SYMMETRICAL:
                return new SymmetricMapMaker();
            default:
                return null;
        }
    }

    final Visualizer createVisualizer() {
        if (vis == null) return null;
        switch (vis) {
            case TWO_D:
                return new DefaultVisualizer(DIMENSIONS);
            case THREE_D:
                return new Display(DIMENSIONS);
            case SERVER:
                return null;
            default:
                return null;
        }
    }

    public static final void debug(String str) {
        if (debugMode) {
            System.out.println(str);
        }
    }

    public static final void debugError(String str) {
        if (debugMode) {
            error(str);
        }
    }

    public static final void error(String str) {
        System.err.println(str);
    }

    public static final int gameTic() {
        return Director.getTic();
    }

    static final BufferedWriter makeLogFile(String filename) {
        try {
            return new BufferedWriter(new FileWriter(new File(filename)));
        } catch (IOException e) {
            System.err.println("Couldn't make log file.");
            System.exit(0);
            return null;
        }
    }

    static final void writeToLog(String str) {
        try {
            gameLog.write(str);
        } catch (IOException e) {
            error("Couldn't write to log file");
        }
    }

    public static final String formatDate(Date date) {
        String str = "";
        str += date.getTime();
        return str;
    }


}
