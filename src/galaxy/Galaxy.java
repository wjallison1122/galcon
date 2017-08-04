package galaxy;

import java.util.ArrayList;
import java.util.Iterator;

final class Galaxy {
    private ArrayList<Planet> planets;
    private ArrayList<Fleet> fleets = new ArrayList<Fleet>();

    void update() {
        for (Planet p : planets) {
            p.update();
        }

        Iterator<Fleet> fleeterator = fleets.iterator();
        while (fleeterator.hasNext()) {
            Fleet f = fleeterator.next();
            f.update();
            if (f.hasHit()) {
                fleeterator.remove();
            }
        }
    }

    void addFleet(Fleet f) {
        if (f.getNumUnits() > 0) {
            fleets.add(f);
        }

    }

    ArrayList<Fleet> getFleets() {
        return new ArrayList<Fleet>(fleets);
    }

    ArrayList<Planet> getPlanets() {
        return new ArrayList<Planet>(planets);
    }

    Player checkWinner() {
        Player winner = checkPlanetWinner();
        for (Fleet f : fleets) {
            if (!f.ownedBy(winner)) {
                return null;
            }
        }
        return winner;
    }

    Player checkPlanetWinner() {
        int i = -1;
        Player p = null;
        // Finds first planet owned by a player
        while (++i < planets.size() && (p = planets.get(i).getOwner()) == null) {
            ;
        }
        // Sees if any planets are owned by another player
        while (++i < planets.size() && !planets.get(i).ownedByOpponentOf(p)) {
            ;
        }
        return i == planets.size() ? p : null;
    }

    void nextGame(Planet[] newMap) {
        if (planets != null) {
            for (Planet p : planets) {
                p.terminate();
            }
            fleets.clear();
        }
        planets = new ArrayList<Planet>();
    }

    @Override
    public String toString() {
        return "";
    }

    int numUnitsOwnedBy(Player p) {
        return numUnitsInPlanets(p) + numUnitsInFleets(p);
    }

    int numUnitsInPlanets(Player p) {
        int count = 0;
        for (Planet f : planets) {
            if (f.ownedBy(p)) {
                count += f.numUnits;
            }
        }
        return count;
    }

    int numUnitsInFleets(Player p) {
        int count = 0;
        for (Fleet f : fleets) {
            if (f.ownedBy(p)) {
                count += f.getNumUnits();
            }
        }
        return count;
    }
}
