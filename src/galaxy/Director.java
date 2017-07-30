package galaxy;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

final class Director extends GameSettings {
    private int rounds = 0;
    private Matcher mm = null;
    private Visualizer visualizer = createVisualizer(this);
    private MapMaker maps = createMapMaker();
    private Galaxy galaxy = new Galaxy();
    private LinkedList<Player> active;
    private HashMap<Player, Integer> numUnitsInFleets = new HashMap<Player, Integer>(),
            numUnitsInPlanets = new HashMap<Player, Integer>();
    private int tic = 0;
    private Player[] players = new Player[suppliers.size()];
    {
        for (Supplier<Player> supplier : suppliers) {
            Player p = supplier.get();
            players[p.ID] = p;
        }
    }

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

    Director() {
        for (int i = 0; i < PLAYERS_PER_GAME; i++) {
            mm = new Matcher(mm);
        }

        // TODO can this be done with a finishGame call?
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

        LinkedList<Action> actions = new LinkedList<Action>();

        for (Player p : active) {
            actions.addAll(p.turn(galaxy.getFleets()));
        }

        for (Action a : actions) {
            galaxy.addFleet(a.doAction());
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
        System.out.println(winner == null ? "NULL" : winner.NAME + " wins!");
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
            p.newGame(galaxy.getPlanets());
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

    void reverseMap() {
        finishGame(null, maps.getReversedMap());
    }

    void togglePause() {
        pause = !pause;
    }

    final int numUnitsOwnedBy(Player p) {
        return numUnitsInPlanets(p) + numUnitsInFleets(p);
    }

    final int numUnitsInPlanets(Player p) {
        Integer numUnits = numUnitsInPlanets.get(p);
        return numUnits == null ? 0 : numUnits;
    }

    final int numUnitsInFleets(Player p) {
        Integer numUnits = numUnitsInFleets.get(p);
        return numUnits == null ? 0 : numUnits;
    }

    /******************* MATCHER *****************/

    class Matcher {
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
