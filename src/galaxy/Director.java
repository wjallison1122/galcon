package galaxy;

import java.util.HashMap;
import java.util.LinkedList;

final class Director extends GameSettings {
    private int rounds = 0;
    private Matcher mm = null;
    private Visualizer visualizer = createVisualizer();
    private MapMaker maps = createMapMaker();
    private Galaxy galaxy = new Galaxy();
    private LinkedList<Player> active;
    private static HashMap<Player, Integer> numUnitsInFleets = new HashMap<Player, Integer>(),
            numUnitsInPlanets = new HashMap<Player, Integer>();
    private static int tic = 0;

    Director() {
        for (int i = 0; i < PLAYERS_PER_GAME; i++) {
            mm = new Matcher(mm);
        }

        newGame(maps.getNewMap(mm.nextMatchup()));
    }

    boolean done() {
        return rounds > NUM_ROUNDS;
    }

    void next() {
        numUnitsInFleets.clear();
        numUnitsInPlanets.clear();

        for (Player p : active) {
            numUnitsInFleets.put(p, galaxy.numUnitsInFleets(p));
            numUnitsInPlanets.put(p, galaxy.numUnitsInPlanets(p));
        }

        for (Player p : active) {
            p.doTurn(galaxy.getFleets());
        }

        for (Player p : active) {
            for (Action a : p.getActions()) {
                galaxy.addFleet(a.doAction());
            }
        }

        galaxy.update();
        if (visualizer != null) {
            visualizer.update(galaxy.getFleets());
        }

        tic++;

        Player winner = galaxy.checkWinner();
        if (winner != null) {
            finishGame(winner);
        } else if (tic > TIC_LIMIT) {
            finishGame(null);
        }
    }

    void finishGame(Player winner, Planet[] newMap) {
        for (Player p : active) {
            p.endGame(winner);
        }
        newGame(newMap);
    }

    void finishGame(Player winner) {
        finishGame(winner,
                maps.hasRevsered() || !reverseEachMap ? maps.getNewMap(mm.nextMatchup()) : maps.getReversedMap());
    }

    boolean usingVisualizer() {
        return visualizer != null;
    }

    private void newGame(Planet[] map) {
        tic = 0;

        galaxy.nextGame(map);

        for (Player p : active) {
            p.nextGame(galaxy.getPlanets());
        }

        if (usingVisualizer()) {
            visualizer.nextGame(active, galaxy.getPlanets());
        }
    }

    /**
     * When manually skipped game
     */
    void skipGame() {
        finishGame(null);
    }

    void restartGame() {
        finishGame(null, maps.getExistingMap());
    }

    void startReversedGame() {
        finishGame(null, maps.getReversedMap());
    }

    static final int numUnitsOwnedBy(Player p) {
        return numUnitsInPlanets(p) + numUnitsInFleets(p);
    }

    static final int numUnitsInPlanets(Player p) {
        Integer numUnits = numUnitsInPlanets.get(p);
        return numUnits == null ? 0 : numUnits;
    }

    static final int numUnitsInFleets(Player p) {
        Integer numUnits = numUnitsInFleets.get(p);
        return numUnits == null ? 0 : numUnits;
    }

    /******************* MATCHER *****************/

    private class Matcher {
        private Matcher next, prev;
        private int player;

        Matcher(Matcher previous) {
            prev = previous;
            if (prev != null) {
                player = prev.player + 1;
                prev.next = this;
            } else {
                player = 0;
            }
        }

        LinkedList<Player> nextMatchup() {
            active = getPlayers();
            update();
            return active;
        }

        void update() {
            player++;
            if (player == players.length) {
                player = ++prev.player + 1;
                if (player == players.length) {
                    player = prev.overflow();
                }
            }
        }

        int overflow() {
            if (prev == null) { // Full set of games completed - reset to start
                rounds++;
                player = 0;
                Matcher search = next;
                while (search != null) {
                    search.player = search.prev.player + 1;
                    search = search.next;
                }
                return 1; // This will have been called by its own 'next'
            } else {
                if (prev.player == player - 2) {
                    prev.player++;
                    player = prev.overflow();
                } else {
                    player = ++prev.player + 1;
                }
                return player + 1;
            }
        }

        LinkedList<Player> getPlayers() {
            LinkedList<Player> set = prev != null ? prev.getPlayers() : new LinkedList<Player>();
            set.add(players[player]);
            return set;
        }
    }
}
