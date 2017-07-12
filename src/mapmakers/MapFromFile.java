package mapmakers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

import galaxy.MapMaker;
import galaxy.Player;

public class MapFromFile extends MapMaker {

    BufferedReader file;

    public MapFromFile(BufferedReader file) {
        this.file = file;
    }

    @Override
    protected void makeMap(LinkedList<Player> active) {
        for (int i = 0; i < NUM_PLANETS; i++) {
            try {
                makePlanetFromString(file.readLine(), active);
            } catch (IOException e) {
                System.out.println("Couldn't read file.");
                System.exit(0);
            }
        }
    }

    private void makePlanetFromString(String str, LinkedList<Player> active) {
        Scanner s = new Scanner(str);

        double[] coords = new double[DIMENSIONS.dimensions()];
        for (int i = 0; i < coords.length; i++) {
            coords[i] = s.nextDouble();
        }

        makePlanet(active.get(s.nextInt()), s.nextInt(), s.nextInt(), s.nextInt(), coords);
        s.close();
    }

}
