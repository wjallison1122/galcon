package galaxy;

import java.util.Iterator;
import java.util.LinkedList;

final class Galaxy {
    private Planet[] planets;
    private LinkedList<Fleet> fleets = new LinkedList<Fleet>();

    void update() {
        for (Planet p : planets) {
            p.update();
        }

        Iterator<Fleet> fleeterator = fleets.iterator();
        while (fleeterator.hasNext()) {
            if (fleeterator.next().update()) {
                fleeterator.remove();
            }
        }
    }

    void addFleet(Fleet f) {
        // TODO Should this throw some sort of exception on a null?
        if (f != null) {
            fleets.add(f);
        }
    }

    Fleet[] getAllFleets() {
        return fleets.toArray(new Fleet[fleets.size()]);
    }

    Planet[] getAllPlanets() {
        return planets.clone();
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
        while (++i < planets.length && (p = planets[i].getOwner()) == null)
            ;
        // Sees if any planets are owned by another player
        while (++i < planets.length && !planets[i].ownedByOpponentOf(p))
            ;
        return i == planets.length ? p : null;
    }

    void nextGame(Planet[] newMap) {
        if (planets != null) {
            for (Planet p : planets) {
                p.terminate();
            }
            // TODO Could fleets come back from dead with this?
            fleets.clear();
        }
        planets = newMap;
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
