package mapmakers;

import java.util.LinkedList;

import galaxy.MapMaker;
import galaxy.Player;

public class RandomMapMaker extends MapMaker {

    @Override
    protected void makeMap(LinkedList<Player> active) {
        for (Player p : active) {
            generateStartingPlanet(p);
        }

        for (int i = active.size(); i < NUM_PLANETS; i++) {
            generateRandomPlanet();
        }
    }

    private void generateRandomPlanet() {
        int numUnits = (int)(Math.random() * MAX_NEUTRAL_UNITS);
        int radius = (int)(Math.random() * (MAX_RADIUS - MIN_RADIUS) + MIN_RADIUS);
        int prodTime = (int)((1 - ((double)radius - MIN_RADIUS) / (MAX_RADIUS - MIN_RADIUS))
                * (MAX_PRODUCE_TIME - MIN_PRODUCE_TIME) + MIN_PRODUCE_TIME);
        double[] coords = makeLocation(radius);
        makePlanet(null, numUnits, radius, prodTime, coords);
    }

    private void generateStartingPlanet(Player owner) {
        makePlanet(owner, 100, MAX_RADIUS, MIN_PRODUCE_TIME, makeLocation(MAX_RADIUS));
    }

    private double[] makeLocation(int radius) {
        double[] coords = DIMENSIONS.getCoords();
        do {
            for (int i = 0; i < coords.length; i++) {
                coords[i] = Math.random() * (coords[i] - radius * 2) + radius;
            }
        } while (checkOverlappingPlanets(radius, coords));
        return coords;
    }
}
