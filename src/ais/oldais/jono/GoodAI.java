package ais.oldais.jono;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import ais.oldais.LegacyPlayerWithUtils;
import galaxy.Action;
import galaxy.Coords;
import galaxy.Fleet;
import galaxy.GameSettings;
import galaxy.Planet;
import galaxy.Player;

//Tuned for 3d maps
public class GoodAI extends LegacyPlayerWithUtils {

    private List<AIConstants> currentGeneration = new ArrayList<>();
    private AIConstants constants = new AIConstants();
    private final boolean LEARNING;

    private static final int POOL_SIZE = 5;
    private static final int GAMES_PER_SAMPLE = 10;
    private int gameCount = 0;
    private int position = 0;

    public static class AIConstants {
        private final boolean USE_MOVE_FORWARDS;
        private final boolean USE_OLD_VALUE_CALCULATION;
        private final int MIN_DEFENSIVE_DEFENSE;
        private final int MIN_AGGRESSIVE_DEFENSE;
        private final double AGGRESSION;
        private final double BASE_DISTANCE_FACTOR;
        private final double DISTANCE_WEIGHTING;
        private final double UNIT_COUNT_POSITION_WEIGHT;
        private final double UNIT_GEN_POSITION_WEIGHT;
        private final double CAPTURE_SAFTEY_MARGIN;
        private int winCount = 0;

        public AIConstants() {
            // Default Constants
            USE_MOVE_FORWARDS = false;
            USE_OLD_VALUE_CALCULATION = true;
            MIN_AGGRESSIVE_DEFENSE = 10;
            MIN_DEFENSIVE_DEFENSE = 1;
            AGGRESSION = 2.0;
            BASE_DISTANCE_FACTOR = 50;
            DISTANCE_WEIGHTING = 0.2;
            UNIT_COUNT_POSITION_WEIGHT = 0.8;
            UNIT_GEN_POSITION_WEIGHT = 0.2;
            CAPTURE_SAFTEY_MARGIN = 1.02;

            // TylerDefender on 2d
            /*
             * USE_MOVE_FORWARDS = false; USE_OLD_VALUE_CALCULATION = false;
             * MIN_AGGRESSIVE_DEFENSE = 69; MIN_DEFENSIVE_DEFENSE = 13;
             * AGGRESSION = 0.1965957281144417; BASE_DISTANCE_FACTOR =
             * 688.9222691138962; DISTANCE_WEIGHTING = 0.5073434974680578;
             * UNIT_COUNT_POSITION_WEIGHT = 0.4572860707011577;
             * UNIT_GEN_POSITION_WEIGHT = 0.6051810864440077;
             * CAPTURE_SAFTEY_MARGIN = 1.773017786429287;
             */

            // TylerRandom on 2d
            /*
             * USE_MOVE_FORWARDS = true; USE_OLD_VALUE_CALCULATION = false;
             * MIN_AGGRESSIVE_DEFENSE = 39; MIN_DEFENSIVE_DEFENSE = 17;
             * AGGRESSION = 2.0844474549539864; BASE_DISTANCE_FACTOR =
             * 522.294342150184; DISTANCE_WEIGHTING = 0.44668924816334477;
             * UNIT_COUNT_POSITION_WEIGHT = 0.609490464638585;
             * UNIT_GEN_POSITION_WEIGHT = 0.37792441961384515;
             * CAPTURE_SAFTEY_MARGIN = 0.11104507853259545;
             */
        }

        public void printSettings() {
            System.out.println("USE_MOVE_FORWARDS = " + USE_MOVE_FORWARDS + ";");
            System.out.println("USE_OLD_VALUE_CALCULATION = " + USE_OLD_VALUE_CALCULATION + ";");
            System.out.println("MIN_AGGRESSIVE_DEFENSE = " + MIN_AGGRESSIVE_DEFENSE + ";");
            System.out.println("MIN_DEFENSIVE_DEFENSE = " + MIN_DEFENSIVE_DEFENSE + ";");
            System.out.println("AGGRESSION = " + AGGRESSION + ";");
            System.out.println("BASE_DISTANCE_FACTOR = " + BASE_DISTANCE_FACTOR + ";");
            System.out.println("DISTANCE_WEIGHTING = " + DISTANCE_WEIGHTING + ";");
            System.out.println("UNIT_COUNT_POSITION_WEIGHT = " + UNIT_COUNT_POSITION_WEIGHT + ";");
            System.out.println("UNIT_GEN_POSITION_WEIGHT = " + UNIT_GEN_POSITION_WEIGHT + ";");
            System.out.println("CAPTURE_SAFTEY_MARGIN = " + CAPTURE_SAFTEY_MARGIN + ";");
        }

        public AIConstants(boolean umf, boolean ovc, int mad, int mdd, double ag, double bdf, double dw, double ucpr,
                double ugpr, double csm) {
            USE_MOVE_FORWARDS = umf;
            USE_OLD_VALUE_CALCULATION = ovc;
            MIN_AGGRESSIVE_DEFENSE = mad;
            MIN_DEFENSIVE_DEFENSE = mdd;
            AGGRESSION = ag;
            BASE_DISTANCE_FACTOR = bdf;
            DISTANCE_WEIGHTING = dw;
            UNIT_COUNT_POSITION_WEIGHT = ucpr;
            UNIT_GEN_POSITION_WEIGHT = ugpr;
            CAPTURE_SAFTEY_MARGIN = csm;
        }

        public AIConstants(AIConstants a, AIConstants b) {
            Random rnd = new Random();
            USE_MOVE_FORWARDS = getCombination(rnd.nextInt(2), a.USE_MOVE_FORWARDS, b.USE_MOVE_FORWARDS);
            USE_OLD_VALUE_CALCULATION = getCombination(rnd.nextInt(2), a.USE_OLD_VALUE_CALCULATION,
                    b.USE_OLD_VALUE_CALCULATION);
            MIN_AGGRESSIVE_DEFENSE = getCombination(rnd.nextInt(3), a.MIN_AGGRESSIVE_DEFENSE, b.MIN_AGGRESSIVE_DEFENSE);
            MIN_DEFENSIVE_DEFENSE = getCombination(rnd.nextInt(3), a.MIN_DEFENSIVE_DEFENSE, b.MIN_DEFENSIVE_DEFENSE);
            AGGRESSION = getCombination(rnd.nextInt(3), a.AGGRESSION, b.AGGRESSION);
            BASE_DISTANCE_FACTOR = getCombination(rnd.nextInt(3), a.BASE_DISTANCE_FACTOR, b.BASE_DISTANCE_FACTOR);
            DISTANCE_WEIGHTING = getCombination(rnd.nextInt(3), a.DISTANCE_WEIGHTING, b.DISTANCE_WEIGHTING);
            UNIT_COUNT_POSITION_WEIGHT = getCombination(rnd.nextInt(3), a.UNIT_COUNT_POSITION_WEIGHT,
                    b.UNIT_COUNT_POSITION_WEIGHT);
            UNIT_GEN_POSITION_WEIGHT = getCombination(rnd.nextInt(3), a.UNIT_GEN_POSITION_WEIGHT,
                    b.UNIT_GEN_POSITION_WEIGHT);
            CAPTURE_SAFTEY_MARGIN = getCombination(rnd.nextInt(3), a.CAPTURE_SAFTEY_MARGIN, b.CAPTURE_SAFTEY_MARGIN);
        }

        private static double getCombination(int mode, double a, double b) {
            switch (mode) {
                case 0:
                    return a;
                case 1:
                    return b;
                case 2:
                    return (a + b) / 2;
                default:
                    throw new RuntimeException("Unexpected mode");
            }
        }

        private static int getCombination(int mode, int a, int b) {
            switch (mode) {
                case 0:
                    return a;
                case 1:
                    return b;
                case 2:
                    return (a + b) / 2;
                default:
                    throw new RuntimeException("Unexpected mode");
            }
        }

        private static boolean getCombination(int mode, boolean a, boolean b) {
            switch (mode) {
                case 0:
                    return a;
                case 1:
                    return b;
                default:
                    throw new RuntimeException("Unexpected mode");
            }
        }

        public static AIConstants generateRandomConstants() {
            Random rnd = new Random();
            return new AIConstants(rnd.nextBoolean(), rnd.nextBoolean(), rnd.nextInt(100), rnd.nextInt(100),
                    rnd.nextDouble() * 3, rnd.nextDouble() * 1000, rnd.nextDouble() * 3, rnd.nextDouble(),
                    rnd.nextDouble(), rnd.nextDouble() * 1.8 + 0.1);
        }
    }

    Planet take;
    List<Planet> retake;
    private boolean contest;
    private Set<Planet> mine;

    LinkedList<Action> actions;

    private ArrayList<Planet> planets;

    public GoodAI() {
        this(true);
    }

    public GoodAI(boolean learning) {
        this(learning, Color.ORANGE);
    }

    public GoodAI(boolean learning, Color c) {
        super(c, "Jono's Best AI");
        LEARNING = learning;
        if (learning) {
            for (int i = 0; i < POOL_SIZE; i++) {
                currentGeneration.add(AIConstants.generateRandomConstants());
                constants = currentGeneration.get(0);
            }
        }

        setHandler(new PlayerHandler() {
            @Override
            public Collection<Action> turn(ArrayList<Fleet> fleets) {
                return makeTurn(fleets);
            }

            @Override
            public void newGame(ArrayList<Planet> newMap) {
                planets = newMap;
                nextGame();
            }

            @Override
            public void endGame(Player winner) {
                declareWinner(winner);
            }
        });
    }

    protected Collection<Action> makeTurn(ArrayList<Fleet> fleets) {
        actions = new LinkedList<Action>();
        List<Planet> myPlanets = getPlanetsOwnedByPlayer(planets, this);
        if (myPlanets.size() == 0) {
            return actions;
        }

        boolean defending = false;
        Planet target = null;
        int needed = 0;
        Set<Planet> danger = new HashSet<>();
        for (Planet p : mine) {
            if (getCurrentEventualOwner(p, fleets, this) != PlanetOwner.PLAYER) {
                danger.add(p);
            }
        }

        if (!danger.isEmpty()) {
            Planet p;
            if (danger.stream().anyMatch(planet -> planet.ownedBy(this))) {
                p = danger.stream().filter(planet -> planet.ownedBy(this))
                        .min((a, b) -> Integer.compare(a.getNumUnits(), b.getNumUnits())).get();
            } else if (danger.stream().anyMatch(planet -> planet.ownedByOpponentOf(this))) {
                p = danger.stream().filter(planet -> planet.ownedByOpponentOf(this))
                        .min((a, b) -> Integer.compare(a.getNumUnits(), b.getNumUnits())).get();
            } else {
                p = danger.stream().min((a, b) -> Integer.compare(a.getNumUnits(), b.getNumUnits())).get();
            }
            needed = getOpponentsIncomingFleetCount(p, fleets, this) - p.getNumUnits()
                    - getPlayersIncomingFleetCount(p, fleets, this) + constants.MIN_DEFENSIVE_DEFENSE;
            needed = Math.max(needed, 4);
            target = p;
            defending = true;
        }

        if (defending) {
            int available = 0;
            if (target != null) {
                final Planet finalTarget = target;
                for (Planet p : myPlanets.stream().sorted(
                        (Planet a, Planet b) -> Double.compare(a.distanceTo(finalTarget), b.distanceTo(finalTarget)))
                        .collect(Collectors.toList())) {
                    if (p != target) {
                        int contribution = p.getNumUnits() - getIncomingFleetCount(p, fleets)
                                - constants.MIN_DEFENSIVE_DEFENSE;

                        if (available + contribution > needed && needed - available > 0) {
                            actions.add(makeAction(p, target, needed - available));
                            available += contribution;
                            break;
                        }
                        available += contribution;
                        if (contribution > 0) {
                            actions.add(makeAction(p, target, contribution));
                        }
                    }
                }
                if (available < needed) {
                    actions.clear();
                    mine.remove(target);
                }
            }
        } else {
            if (contest) {
                contest(fleets);
            } else {
                evaluatePosition(fleets);
                if (take == null) {
                    if (constants.USE_MOVE_FORWARDS) {
                        moveFleetsForwards();
                    }
                } else {
                    mine.add(take);
                }
            }
        }

        return actions;
    }

    public void moveFleetsForwards() {
        List<Planet> theirPlanets = getOpponentsPlanets(planets, this);
        if (theirPlanets.size() > 0) {
            Coords theirUnitArea = getUnitCountWeightedCenter(theirPlanets);
            Coords theirProductionArea = getProductionWeightedCenter(theirPlanets);
            theirUnitArea = theirUnitArea.multiply(constants.UNIT_COUNT_POSITION_WEIGHT);
            theirProductionArea = theirProductionArea.multiply(constants.UNIT_GEN_POSITION_WEIGHT);
            Coords theirLocation = theirUnitArea.sum(theirProductionArea);
            Optional<Planet> target = mine.stream()
                    .min((a, b) -> Double.compare(theirLocation.distanceTo(a), theirLocation.distanceTo(b)));
            if (target.isPresent()) {
                for (Planet p : getPlanetsOwnedByPlayer(planets, this)) {
                    int toSend = p.getNumUnits() - constants.MIN_AGGRESSIVE_DEFENSE;
                    if (toSend > 0 && p != target.get()) {
                        actions.add(makeAction(p, target.get(), toSend));
                    }
                }
            }
        }
    }

    public double getValue(Planet p, Coords averageLocation, double variance) {
        double distanceFactor = (variance + constants.BASE_DISTANCE_FACTOR)
                / (averageLocation.distanceTo(p) + constants.BASE_DISTANCE_FACTOR);
        return (p.getColor().equals(Color.GRAY) ? 1.0 : constants.AGGRESSION)
                * Math.pow(distanceFactor, constants.DISTANCE_WEIGHTING) / p.PRODUCTION_TIME / (10 + p.getNumUnits());
    }

    private void contest(ArrayList<Fleet> fleets) {
        List<Planet> myPlanets = getPlanetsOwnedByPlayer(planets, this);
        List<Planet> theirPlanets = getOpponentsPlanets(planets, this);

        if (myPlanets.size() > 1 || theirPlanets.size() > 1) {
            contest = false;
            return;
        }

        if (take != null) {
            int toSendToTake = 0;
            while (!isEventualOwner(take, (int)Math.ceil(myPlanets.get(0).distanceTo(take) / GameSettings.FLEET_SPEED),
                    toSendToTake, fleets)) {
                toSendToTake++;
            }
            if (toSendToTake > 0) {
                actions.add(makeAction(myPlanets.get(0), take, toSendToTake));
            }
        }

        for (Fleet fleet : getOpponentsFleets(fleets, this)) {
            if (retake.contains(fleet.DESTINATION)) {
                int distance = (int)Math
                        .ceil(myPlanets.get(0).distanceTo(fleet.DESTINATION) / GameSettings.FLEET_SPEED);
                int fleetDistance = fleet.ticsLeft();
                if (distance > fleetDistance) {
                    int toSend = 0;
                    while (!isEventualOwner(fleet.DESTINATION, distance, toSend, fleets)) {
                        toSend++;
                    }
                    if (toSend > 0) {
                        actions.add(makeAction(myPlanets.get(0), fleet.DESTINATION, toSend));
                    }
                    retake.remove(fleet.DESTINATION);
                    mine.add(fleet.DESTINATION);
                }
            }
        }
    }

    private static class PlanetAction {
        public int time;
        public int amount;
        public PlanetOwner owner;
    }

    private boolean isEventualOwner(Planet p, int time, int amount, ArrayList<Fleet> fleets) {
        PlanetOwner current;
        if (p.ownedBy(this)) {
            current = PlanetOwner.PLAYER;
        } else if (p.ownedByOpponentOf(this)) {
            current = PlanetOwner.OPPONENT;
        } else {
            current = PlanetOwner.NOBODY;
        }
        int updateCount = p.getLifespan() % p.PRODUCTION_TIME;
        int previousUnits = 0;
        int unitCount = p.getNumUnits();
        int currentTime = 0;
        List<PlanetAction> actions = new ArrayList<>();
        for (Fleet f : fleets.stream().filter((fleet) -> fleet.DESTINATION == p).collect(Collectors.toList())) {
            PlanetAction action = new PlanetAction();
            action.time = f.ticsLeft();
            action.amount = f.getNumUnits();
            if (f.ownedBy(this)) {
                action.owner = PlanetOwner.PLAYER;
            } else {
                action.owner = PlanetOwner.OPPONENT;
            }
            actions.add(action);
        }
        PlanetAction player = new PlanetAction();
        player.amount = amount;
        player.time = time;
        player.owner = PlanetOwner.PLAYER;
        actions.add(player);
        actions.sort((a, b) -> Integer.compare(a.time, b.time));
        for (PlanetAction pa : actions) {
            int passingTime = pa.time - currentTime;
            if (current != PlanetOwner.NOBODY) {
                updateCount += passingTime;
                int unitsToAdd = (updateCount + p.PRODUCTION_TIME - 1) / p.PRODUCTION_TIME - previousUnits;
                previousUnits += unitsToAdd;
                unitCount += unitsToAdd;
            }
            if (pa.owner == current) {
                unitCount += pa.amount;
            } else {
                unitCount -= pa.amount;
                if (unitCount == 0) {
                    current = PlanetOwner.NOBODY;
                }
                if (unitCount < 0) {
                    unitCount = -unitCount;
                    current = pa.owner;
                }
            }
            currentTime += passingTime;
        }
        return current == PlanetOwner.PLAYER;
    }

    protected void nextGame() {
        mine = new HashSet<>();
        contest = true;

        List<Planet> myPlanets = getPlanetsOwnedByPlayer(planets, this);
        List<Planet> theirPlanets = getOpponentsPlanets(planets, this);
        List<Planet> unownedPlanets = getUnoccupiedPlanets(planets);

        if (myPlanets.size() != 1 || theirPlanets.size() != 1) {
            throw new RuntimeException("Unexpected starting situation MyPlanets: " + myPlanets.size()
                    + " TheirPlanets: " + theirPlanets.size());
        }

        Planet me = myPlanets.get(0);
        Planet them = theirPlanets.get(0);

        int distance = (int)Math.ceil(me.distanceTo(them) / GameSettings.FLEET_SPEED);
        int distanceProduction = distance / me.PRODUCTION_TIME;

        Planet best = null;
        double bestValue = Double.MIN_VALUE;

        for (Planet p : unownedPlanets) {
            int toMe = (int)Math.ceil(p.distanceTo(me) / GameSettings.FLEET_SPEED);
            int toThem = (int)Math.ceil(p.distanceTo(them) / GameSettings.FLEET_SPEED);
            if (toMe <= toThem) {
                int takenContribution = 0;
                if (distance - toMe * 2 > 0) {
                    takenContribution = (int)Math.floor((distance - toMe * 2) / p.PRODUCTION_TIME);
                }
                if (p.getNumUnits() + 1 - takenContribution < distanceProduction) {
                    double value = 1.0 / p.PRODUCTION_TIME / (100 + p.getNumUnits());
                    if (value > bestValue) {
                        bestValue = value;
                        best = p;
                    }
                }
            }
        }
        take = best;

        retake = new ArrayList<>(unownedPlanets);

        for (Planet p : unownedPlanets) {
            int toMe = (int)Math.ceil(p.distanceTo(me) / GameSettings.FLEET_SPEED);
            int toThem = (int)Math.ceil(p.distanceTo(them) / GameSettings.FLEET_SPEED);
            if (toMe >= toThem) {
                int takenContribution = 0;
                if (distance - toThem * 2 > 0) {
                    takenContribution = (int)Math.floor((distance - toThem * 2) / p.PRODUCTION_TIME);
                }
                if (p.getNumUnits() * constants.CAPTURE_SAFTEY_MARGIN + 1 - takenContribution < distanceProduction) {
                    retake.remove(p);
                }
            }
        }
    }

    private void evaluatePosition(ArrayList<Fleet> fleets) {
        List<Planet> myPlanets = getPlanetsOwnedByPlayer(planets, this);
        List<Planet> theirPlanets = getOpponentsPlanets(planets, this);
        // List<Planet> unownedPlanets = getUnoccupiedPlanets(planets);
        List<Fleet> myFleets = getFleetsOfPlayer(fleets, this);
        List<Fleet> theirFleets = getOpponentsFleets(fleets, this);

        /*
         * if both are true may turn on aggro mode int myUnits =
         * getMyUnitCount(fleets, planets, this); int theirUnits =
         * getOpponentUnitCount(fleets, planets, this); boolean unitAdvantage =
         * myUnits > theirUnits * ADVANTAGE_THRESHOLD;
         *
         * double myProduction =
         * myPlanets.stream().collect(Collectors.summingDouble(p ->
         * p.getProductionFrequency())); double theirProduction =
         * theirPlanets.stream().collect(Collectors.summingDouble(p ->
         * p.getProductionFrequency())); boolean productionAdvantage =
         * myProduction > theirProduction * ADVANTAGE_THRESHOLD;
         */

        Coords myUnitArea = getUnitCountWeightedCenter(myPlanets);
        Coords myProductionArea = getProductionWeightedCenter(myPlanets);
        myUnitArea = myUnitArea.multiply(constants.UNIT_COUNT_POSITION_WEIGHT);
        myProductionArea = myProductionArea.multiply(constants.UNIT_GEN_POSITION_WEIGHT);
        Coords myLocation = myUnitArea.sum(myProductionArea);

        double mySpread = variance(myPlanets);

        Coords theirLocation;
        if (theirPlanets.size() > 0) {
            Coords theirUnitArea = getUnitCountWeightedCenter(theirPlanets);
            Coords theirProductionArea = getProductionWeightedCenter(theirPlanets);
            theirUnitArea = theirUnitArea.multiply(constants.UNIT_COUNT_POSITION_WEIGHT);
            theirProductionArea = theirProductionArea.multiply(constants.UNIT_GEN_POSITION_WEIGHT);
            theirLocation = theirUnitArea.sum(theirProductionArea);
        } else {
            theirLocation = myLocation;
        }

        // double theirSpread = Location.variance(theirPlanets);
        Map<Planet, Double> myInfluence = new HashMap<>();
        for (Planet influencing : myPlanets) {
            for (Planet p : planets) {
                if (influencing == p) {
                    addMap(myInfluence, p, Double.valueOf(p.getNumUnits()));
                } else {
                    double myDistance = influencing.distanceTo(p);
                    double theirDistance = theirLocation.distanceTo(p);
                    double distanceFactor = theirDistance / (myDistance + theirDistance);
                    double influence = influencing.getNumUnits()
                            * Math.pow(distanceFactor, constants.DISTANCE_WEIGHTING);
                    addMap(myInfluence, p, influence);
                }
            }
        }

        for (Fleet f : myFleets) {
            addMap(myInfluence, f.DESTINATION, f.getNumUnits());
        }

        Map<Planet, Double> theirInfluence = new HashMap<>();
        for (Planet influencing : theirPlanets) {
            for (Planet p : planets) {
                if (influencing == p) {
                    addMap(theirInfluence, p, Double.valueOf(p.getNumUnits()));
                } else {
                    double theirDistance = influencing.distanceTo(p);
                    double myDistance = myLocation.distanceTo(p);
                    double distanceFactor = myDistance / (myDistance + theirDistance);
                    double influence = influencing.getNumUnits()
                            * Math.pow(distanceFactor, constants.DISTANCE_WEIGHTING);
                    addMap(theirInfluence, p, influence);
                }
            }
        }

        for (Fleet f : theirFleets) {
            addMap(theirInfluence, f.DESTINATION, f.getNumUnits());
        }

        List<Planet> potentialTargets = new ArrayList<>();
        for (Planet p : planets) {
            if (myInfluence.get(p) - (p.isNeutral() ? p.getNumUnits() : 0) > constants.CAPTURE_SAFTEY_MARGIN
                    * (theirInfluence.containsKey(p) ? theirInfluence.get(p) : 0)) {
                potentialTargets.add(p);
            }
        }

        take = null;
        double bestValue = 0;
        for (Planet p : potentialTargets) {
            if (!mine.contains(p)) {
                double value = getValue(p, myLocation, mySpread);
                if (value > bestValue) {
                    bestValue = value;
                    take = p;
                }
            }
        }
    }

    private static void addMap(Map<Planet, Double> map, Planet p, double val) {
        map.put(p, val + (map.containsKey(p) ? map.get(p) : 0));
    }

    protected void declareWinner(Player winner) {
        if (LEARNING) {
            if (equals(winner)) {
                constants.winCount++;
                //System.out.println("Game " + position + "," + gameCount + " won");
            } else {
                //System.out.println("Game " + position + "," + gameCount + " lost");
            }
            gameCount++;
            if (gameCount == GAMES_PER_SAMPLE) {
                gameCount = 0;
                position++;
                if (position >= currentGeneration.size()) {
                    List<AIConstants> nextGeneration = new ArrayList<>();
                    Collections.shuffle(currentGeneration);
                    Collections.sort(currentGeneration, (a, b) -> -Integer.compare(a.winCount, b.winCount));
                    System.out.println("Best settings:");
                    currentGeneration.get(0).printSettings();
                    System.out.print("Generation complete, performance:");
                    for (AIConstants c : currentGeneration) {
                        System.out.print(" " + c.winCount);
                        c.winCount = 0;
                    }
                    System.out.println();
                    for (int i = 0; i < POOL_SIZE / 2; i++) {
                        nextGeneration.add(currentGeneration.get(i));
                        nextGeneration.add(new AIConstants(currentGeneration.get(i), currentGeneration.get(i + 1)));
                        nextGeneration
                                .add(new AIConstants(currentGeneration.get(i), AIConstants.generateRandomConstants()));
                    }
                    currentGeneration = nextGeneration;
                    position = 0;
                }
                constants = currentGeneration.get(position);
            }
        }
    }
}
