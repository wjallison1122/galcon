package ais.jono;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ais.PlayerWithUtils;
import ais.alphabeta.AlphaBeta;
import ais.alphabeta.Move;
import ais.alphabeta.Position;
import galaxy.Action;
import galaxy.Fleet;
import galaxy.GameSettings;
import galaxy.Planet;
import galaxy.Player;

public class SearchAI extends PlayerWithUtils {

    private static enum Limit {
        TIME, DEPTH
    };

    private static final Limit LIMIT = Limit.TIME;
    private static final int SEARCH_DEPTH = 6;
    private static final int SEARCH_TIME_MILLIS = 1000;
    private static final int FORCE_UPDATE_DELAY = 1000;

    private Set<Integer> knownFleets;

    private static class GamePosition implements Position {

        private List<SearchPlanet> planets;
        private List<FutureAction> actions;
        private boolean playersTurn = true;

        public GamePosition(List<SearchPlanet> planets, List<FutureAction> actions) {
            this.planets = planets;
            this.actions = actions;
        }

        @Override
        public List<Move> getMoves() {
            int myUnits = 0;
            int theirUnits = 0;
            for (SearchPlanet p : planets) {
                if (p.owner.equals(PlanetOwner.PLAYER)) {
                    myUnits += p.units;
                }
                if (p.owner.equals(PlanetOwner.OPPONENT)) {
                    theirUnits += p.units;
                }
            }
            for (FutureAction fa : actions) {
                if (fa != null) {
                    if (fa.owner.equals(PlanetOwner.PLAYER)) {
                        myUnits += fa.count;
                    }
                    if (fa.owner.equals(PlanetOwner.OPPONENT)) {
                        theirUnits += fa.count;
                    }
                }
            }
            if (myUnits == 0 || theirUnits == 0) {
                return new ArrayList<>();
            }
            List<Move> potentialMoves = new ArrayList<>();
            potentialMoves.add(null);
            for (SearchPlanet p : planets) {
                if (playersTurn ? p.owner.equals(PlanetOwner.PLAYER) : p.owner.equals(PlanetOwner.OPPONENT)) {
                    for (int i = 0; i < planets.size(); i++) {
                        final int finali = i;
                        if (planets.get(i).owner.equals(PlanetOwner.NOBODY)
                                && !actions.stream().anyMatch(fa -> fa != null && fa.target == finali)) {
                            if (p.units > planets.get(i).units + 1) {
                                potentialMoves.add(new FutureAction(p.base, i, p.owner, planets.get(i).units + 1,
                                        (int)Math.ceil(p.distanceTo(planets.get(i)) / GameSettings.FLEET_SPEED)));
                            }
                        } else if (!p.equals(planets.get(i))) {
                            if (p.units > 0) {
                                potentialMoves.add(new FutureAction(p.base, i, p.owner, 1,
                                        (int)Math.ceil(p.distanceTo(planets.get(i)) / GameSettings.FLEET_SPEED)));
                            }
                            if (p.units > 1) {
                                potentialMoves.add(new FutureAction(p.base, i, p.owner, p.units,
                                        (int)Math.ceil(p.distanceTo(planets.get(i)) / GameSettings.FLEET_SPEED)));
                            }
                            if (p.units > 40) {
                                potentialMoves.add(new FutureAction(p.base, i, p.owner, p.units / 2,
                                        (int)Math.ceil(p.distanceTo(planets.get(i)) / GameSettings.FLEET_SPEED)));
                            }
                        }
                    }
                }
            }

            return potentialMoves;
        }

        @Override
        public void doMove(Move move) {
            playersTurn = !playersTurn;
            actions.add((FutureAction)move);
            if (move != null) {
                planets.get(((FutureAction)move).target).units -= ((FutureAction)move).count;
            }
        }

        @Override
        public void undoMove() {
            playersTurn = !playersTurn;
            FutureAction last = actions.remove(actions.size() - 1);
            if (last != null) {
                planets.get(last.target).units += last.count;
            }
        }

        @Override
        public int evaluate() {
            double myProduction = 0;
            double theirProduction = 0;
            for (int i = 0; i < planets.size(); i++) {
                PlanetOwner eventualOwner = getEventualOwner(planets.get(i), i, actions);
                if (eventualOwner.equals(PlanetOwner.PLAYER)) {
                    myProduction += 1.0 / planets.get(i).productionDelay;
                } else if (eventualOwner.equals(PlanetOwner.OPPONENT)) {
                    theirProduction += 1.0 / planets.get(i).productionDelay;
                }
            }
            int myUnits = 0;
            int theirUnits = 0;
            for (SearchPlanet p : planets) {
                if (p.owner.equals(PlanetOwner.PLAYER)) {
                    myUnits += p.units;
                }
                if (p.owner.equals(PlanetOwner.OPPONENT)) {
                    theirUnits += p.units;
                }
            }
            for (FutureAction fa : actions) {
                if (fa != null) {
                    if (fa.owner.equals(PlanetOwner.PLAYER)) {
                        myUnits += fa.count;
                    }
                    if (fa.owner.equals(PlanetOwner.OPPONENT)) {
                        theirUnits += fa.count;
                    }
                }
            }
            if (myUnits == 0) {
                return -1000000000;
            }
            if (theirUnits == 0) {
                return 1000000000;
            }
            return (int)((myProduction - theirProduction) * 100000) + myUnits - theirUnits;
        }

        @Override
        public boolean currentlyMaximizing() {
            return playersTurn;
        }

    }

    private class SearchPlanet {
        public final Planet base;
        public PlanetOwner owner;
        public int productionDelay;
        public int updateCount;
        public int units;

        public SearchPlanet(SearchPlanet other) {
            base = other.base;
            owner = other.owner;
            productionDelay = other.productionDelay;
            updateCount = other.updateCount;
            units = other.units;
        }

        public SearchPlanet(Planet planet, Player player) {
            base = planet;
            owner = getOwner(planet, player);
            productionDelay = planet.PRODUCTION_TIME;
            updateCount = planet.getLifespan();
            units = planet.getNumUnits();
        }

        double distanceTo(SearchPlanet p) {
            return base.distanceTo(p.base);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj.equals(base)) {
                return true;
            } else {
                if (obj instanceof SearchPlanet) {
                    return base.equals(((SearchPlanet)obj).base);
                } else {
                    return false;
                }
            }
        }
    }

    private static class FutureAction implements Move {
        public final Planet source;
        public final int target;
        public final PlanetOwner owner;
        public final int time;
        public final int count;

        public FutureAction(Planet source, int target, PlanetOwner owner, int count, int time) {
            this.source = source;
            this.target = target;
            this.owner = owner;
            this.count = count;
            this.time = time;
        }

        public FutureAction(Fleet f, Player player, List<SearchPlanet> planets) {
            source = null;
            time = f.ticsLeft();
            count = f.getNumUnits();
            if (f.ownedBy(player)) {
                owner = PlanetOwner.PLAYER;
            } else {
                owner = PlanetOwner.OPPONENT;
            }
            int target = -1;
            for (int i = 0; i < planets.size(); i++) {
                if (planets.get(i).base == f.DESTINATION) {
                    target = i;
                    break;
                }
            }
            this.target = target;
        }
    }

    private static PlanetOwner getEventualOwner(SearchPlanet p, int index, List<FutureAction> actions) {
        PlanetOwner current = p.owner;
        int updateCount = p.updateCount % p.productionDelay;
        int previousUnits = 0;
        int unitCount = p.units;
        int currentTime = 0;

        actions = actions.stream().filter(fa -> fa != null && fa.target == index)
                .sorted((a, b) -> Integer.compare(a.time, b.time)).collect(Collectors.toList());
        for (FutureAction fa : actions) {
            if (fa != null) {
                int passingTime = fa.time - currentTime;
                if (current != PlanetOwner.NOBODY) {
                    updateCount += passingTime;
                    int unitsToAdd = (updateCount + p.productionDelay - 1) / p.productionDelay - previousUnits;
                    previousUnits += unitsToAdd;
                    unitCount += unitsToAdd;
                }
                if (fa.owner == current) {
                    unitCount += fa.count;
                } else {
                    unitCount -= fa.count;
                    if (unitCount == 0) {
                        current = PlanetOwner.NOBODY;
                    }
                    if (unitCount < 0) {
                        unitCount = -unitCount;
                        current = fa.owner;
                    }
                }
                currentTime += passingTime;
            }
        }
        return current;
    }

    public SearchAI() {
        this(Color.orange);
    }

    private Planet[] planets;

    public SearchAI(Color c) {
        super(c, "Search AI");
        setHandler(new PlayerHandler() {
            @Override
            public Collection<Action> turn(Fleet[] fleets) {
                return makeTurn(fleets);
            }

            @Override
            public void newGame(Planet[] newMap) {
                planets = newMap;
                nextGame();
            }
        });
    }

    private static int analyzeTickTime = 0;
    private static int count = 0;

    protected void nextGame() {
        analyzeTickTime = 0;
        count = 0;
        knownFleets = new HashSet<>();
    }

    protected Collection<Action> makeTurn(Fleet[] fleets) {
        LinkedList<Action> acts = new LinkedList<Action>();
        for (Fleet f : fleets) {
            if (!knownFleets.contains(f.ID)) {
                knownFleets.add(f.ID);
                analyzeTickTime = 0;
            }
        }

        Iterator<Integer> iter = knownFleets.iterator();
        IteratorLoop: while (iter.hasNext()) {
            int fleetId = iter.next();
            for (Fleet f : fleets) {
                if (f.ID == fleetId) {
                    continue IteratorLoop;
                }
            }
            analyzeTickTime = 0;
            iter.remove();
        }

        if (analyzeTickTime++ % FORCE_UPDATE_DELAY == 0) {
            List<SearchPlanet> searchPlanets = new ArrayList<>();
            for (Planet p : planets) {
                searchPlanets.add(new SearchPlanet(p, this));
            }
            List<FutureAction> actions = new ArrayList<>();
            for (Fleet f : fleets) {
                actions.add(new FutureAction(f, this, searchPlanets));
            }

            GamePosition position = new GamePosition(searchPlanets, actions);
            System.out.println("Starting analysis. Eval " + ++count + ": " + position.evaluate());
            AlphaBeta ab = new AlphaBeta(position);
            FutureAction fa;
            if (LIMIT == Limit.TIME) {
                fa = (FutureAction)ab.analyze(SEARCH_TIME_MILLIS);
            } else {
                fa = (FutureAction)ab.analyzeDepth(SEARCH_DEPTH);
            }
            System.out.println("Analysis Complete");
            if (fa != null) {
                analyzeTickTime = 0;
                acts.add(makeAction(fa.source, planets[fa.target], fa.count));
            }
        }

        return acts;
    }
}
