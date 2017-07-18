package galaxy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.function.Supplier;

import ais.cody.ValueCapture;
import ais.tyler.TylerAI;
import mapmakers.RandomMapMaker;
import mapmakers.SymmetricMapMaker;
import visualizers.threedimensiondefault.Display;
import visualizers.twodimensiondefault.DefaultVisualizer;

enum SymmetryType {
    VERTICAL, HORIZONTAL, DIAGONAL, RADIAL,
}

// TODO Switch to holder pattern
enum VisualizerType {
    NONE, TWO_D, THREE_D,
}

enum MapType {
    RANDOM, SYMMETRICAL
}

public class GameSettings {
    public static final boolean debugMode = true, logGame = false;

    private static BufferedWriter gameLog = logGame ? makeLogFile("galconset-" + formatDate(new Date())) : null;
    static Supplier<Player> p1 = new Supplier<Player>() {
        @Override
        public Player get() {
            return new ValueCapture();
        }
    };
    static Supplier<Player> p2 = new Supplier<Player>() {
        @Override
        public Player get() {
            return new TylerAI();
        }
    };
    static ArrayList<Supplier<Player>> suppliers = new ArrayList<Supplier<Player>>();
    static {
        suppliers.add(p1);
        suppliers.add(p2);
    }
    public static final int PLAYERS_PER_GAME = 2;
    public static final int NUM_PLANETS = 16;

    private final MapType map = MapType.RANDOM;
    private static final VisualizerType vis = VisualizerType.NONE;
    public final static int FRAME_TIME = 10;
    public static final Coords DIMENSIONS = new Coords(
            (vis == VisualizerType.TWO_D) ? new double[] {800, 800} : new double[] {1000, 1000, 1000});

    public static final int FLEET_SPEED = 2;
    public final int NUM_ROUNDS = 5000;
    public final int TIC_LIMIT = 50000;
    final boolean reverseEachMap = true;

    public static final int MAX_RADIUS = 50;
    public static final int MIN_RADIUS = 12;
    public static final int MAX_NEUTRAL_UNITS = 50;
    public static final int MIN_PRODUCE_TIME = 34;
    public static final int MAX_PRODUCE_TIME = 100;

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

    final Visualizer createVisualizer(Director director) {
        if (vis == null) {
            return null;
        }
        switch (vis) {
            case TWO_D:
                return new DefaultVisualizer(DIMENSIONS.getCoords()).setDirector(director);
            case THREE_D:
                return new Display(DIMENSIONS.getCoords()).setDirector(director);
            case NONE:
                return null;
            default:
                return null;
        }
    }

    public static final void debug(Object o) {
        if (debugMode) {
            System.out.println(o);
        }
    }

    public static final void debugError(Object o) {
        if (debugMode) {
            error(o);
        }
    }

    public static final void error(Object o) {
        System.err.println(o);
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
