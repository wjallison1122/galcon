package ais.jono;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ais.PlayerWithUtils;
import galaxy.Coords;
import galaxy.Fleet;
import galaxy.Planet;

public class ContestPlanetsAI extends PlayerWithUtils {
    private static final int MIN_AGGRESSIVE_DEFENSE = 10;
    private static final int MIN_DEFENSIVE_DEFENSE = 2;
    private static final double BASE_DISTANCE_FACTOR = 20;
    private static final double DISTANCE_WEIGHTING = 0.2;
    private static final double AGGRESSION = 2.5;

    private boolean contest;

    private Set<Planet> mine;

    public ContestPlanetsAI() {
        this(Color.GREEN);
    }

    public ContestPlanetsAI(Color c) {
        super(c, "Contest Planets AI");
    }

    public double getValue(Planet p, Coords averageLocation, double variance) {
        double distanceFactor = (variance + BASE_DISTANCE_FACTOR)
                / (averageLocation.distanceTo(p) + BASE_DISTANCE_FACTOR);
        return (p.getColor().equals(Color.GRAY) ? 1.0 : AGGRESSION) * Math.pow(distanceFactor, DISTANCE_WEIGHTING)
                / p.PRODUCTION_TIME / (10 + p.getNumUnits());
    }

    @Override
    protected void turn() {
        List<Planet> myPlanets = getPlanetsOwnedByPlayer(planets, this);
        for (Planet p : myPlanets) {
            if (getCurrentEventualOwner(p, fleets, this) == PlanetOwner.PLAYER) {
                mine.add(p);
            }
        }
        if (myPlanets.size() == 0) {
            return;
        }
        List<Planet> otherPlanets = getPlanetsNotOwnedByPlayer(planets, this);

        boolean defending = false;
        Planet target = null;
        int needed = 0;
        for (Planet p : mine) {
            if (getCurrentEventualOwner(p, fleets, this) != PlanetOwner.PLAYER) {
                needed = getOpponentsIncomingFleetCount(p, fleets, this) - p.getNumUnits()
                        - getPlayersIncomingFleetCount(p, fleets, this) + MIN_DEFENSIVE_DEFENSE;
                needed = Math.max(needed, 2);
                target = p;
                defending = true;
                break;
            }
        }

        if (!defending) {
            if (contest) {
                contest();
                return;
            }
        }

        Coords average = center(myPlanets);
        double variance = variance(myPlanets);

        if (target == null) {
            double best = Double.MIN_VALUE;
            for (Planet p : otherPlanets) {
                double value = getValue(p, average, variance);
                if (value > best) {
                    if (getPlayersIncomingFleetCount(p, fleets, this) == 0) {
                        target = p;
                        best = value;
                    }
                }
            }
            if (target == null) {
                return;
            }
            needed = target.getNumUnits() + 20;
        }

        int available = 0;
        for (Planet p : myPlanets) {
            if (p != target) {
                int contribution = p.getNumUnits() - getIncomingFleetCount(p, fleets)
                        - (defending ? MIN_DEFENSIVE_DEFENSE : MIN_AGGRESSIVE_DEFENSE);

                if (available + contribution > needed) {
                    addAction(p, target, needed - available);
                    available += contribution;
                    break;
                }
                available += contribution;
                addAction(p, target, contribution);
            }
        }

        if (available < needed) {
            clearActions();
        }
    }

    private void contest() {
        List<Planet> myPlanets = getPlanetsOwnedByPlayer(planets, this);
        List<Planet> theirPlanets = getOpponentsPlanets(planets, this);

        if (myPlanets.size() > 1 || theirPlanets.size() > 1) {
            contest = false;
            return;
        }

        if (take != null) {
            int toSendToTake = 0;
            while (!isEventualOwner(take, (int) Math.ceil(myPlanets.get(0).distanceTo(take) / FLEET_SPEED),
                    toSendToTake)) {
                toSendToTake++;
            }
            if (toSendToTake > 0) {
                addAction(myPlanets.get(0), take, toSendToTake);
            }
        }

        for (Fleet fleet : getOpponentsFleets(fleets, this)) {
            if (retake.contains(fleet.DESTINATION)) {
                int distance = (int) Math.ceil(myPlanets.get(0).distanceTo(fleet.DESTINATION) / FLEET_SPEED);
                int fleetDistance = (int) Math.ceil(fleet.distanceLeft() / FLEET_SPEED);
                if (distance > fleetDistance) {
                    int toSend = 0;
                    while (!isEventualOwner(fleet.DESTINATION, distance, toSend)) {
                        toSend++;
                    }
                    if (toSend > 0) {
                        addAction(myPlanets.get(0), fleet.DESTINATION, toSend);
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

    private boolean isEventualOwner(Planet p, int time, int amount) {
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
        for (Fleet f : Arrays.asList(fleets).stream().filter((fleet) -> fleet.DESTINATION == p)
                .collect(Collectors.toList())) {
            PlanetAction action = new PlanetAction();
            action.time = (int) Math.ceil(f.distanceLeft() / FLEET_SPEED);
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

    Planet take;
    List<Planet> retake;

    @Override
    protected void newGame() {
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

        int distance = (int) Math.ceil(me.distanceTo(them) / FLEET_SPEED);
        int distanceProduction = distance / me.PRODUCTION_TIME;

        Planet best = null;
        double bestValue = Double.MIN_VALUE;

        for (Planet p : unownedPlanets) {
            int toMe = (int) Math.ceil(p.distanceTo(me) / FLEET_SPEED);
            int toThem = (int) Math.ceil(p.distanceTo(them) / FLEET_SPEED);
            if (toMe <= toThem) {
                int takenContribution = 0;
                if (distance - toMe * 2 > 0) {
                    takenContribution = (int) Math.floor((distance - toMe * 2) / p.PRODUCTION_TIME);
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
            int toMe = (int) Math.ceil(p.distanceTo(me) / FLEET_SPEED);
            int toThem = (int) Math.ceil(p.distanceTo(them) / FLEET_SPEED);
            if (toMe >= toThem) {
                int takenContribution = 0;
                if (distance - toThem * 2 > 0) {
                    takenContribution = (int) Math.floor((distance - toThem * 2) / p.PRODUCTION_TIME);
                }
                if (p.getNumUnits() + 1 - takenContribution < distanceProduction) {
                    retake.remove(p);
                }
            }
        }
    }

    @Override
    protected String storeSelf() {
        return null;
    }
}
