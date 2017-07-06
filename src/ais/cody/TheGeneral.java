package ais.cody;

import galaxy.Fleet;
import galaxy.Player;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;

import ais.cody.psuedoSpace.PsuedoPlanet;
import ais.cody.psuedoSpace.PsuedoAction;
import ais.cody.psuedoSpace.PsuedoGalaxy;

public class TheGeneral extends Player {
    private static final double FUTURE_COEFFICIENT = 1;
    private static final double DISTANCE_COEFFICIENT = .3;
    private static final int TIME_TO_PREDICT = 5000;
    private static final int PREDICTION_INCREMENT = 100;
    private static final double PRODUCTION_VALUE = 2;
    private static final double SPREAD_VALUE = 0;
    private PsuedoGalaxy psuedoGalaxy;
    private ArrayList<Move> potentialMoves;

    public TheGeneral() {
        super(Color.ORANGE, "The General");
        potentialMoves = new ArrayList<Move>();
    }

    public TheGeneral(Color c) {
        super(c, "The General");
        potentialMoves = new ArrayList<Move>();
    }

    @Override
    protected void turn() {

        psuedoGalaxy = new PsuedoGalaxy(planets, fleets, this);
        // System.out.println(psuedoGalaxy);
        findBestActions(psuedoGalaxy);
        if (potentialMoves.size() > 0) {
            potentialMoves.get(0).commit();
            // System.out.println("Picked move: " + potentialMoves.get(0));
        }

    }

    private void findBestActions(PsuedoGalaxy psuedoGalaxy) {
        ArrayList<PsuedoPlanet> attackers;
        int[] unitsToSend;
        int i;
        double distance;
        double cost;
        double weightedCost;
        double targetCost = 0;
        int strength;
        int totalToSend;
        boolean canAttack;
        Move move;
        double currentValue;
        potentialMoves = new ArrayList<Move>();
        ArrayList<Move> invalidMoves = new ArrayList<Move>();

        for (PsuedoPlanet to : psuedoGalaxy.psuedoPlanets) {
            totalToSend = 0;
            move = new Move();
            cost = costOfPlanet(to);
            weightedCost = cost;
            distance = to.distanceTo(Vector.getCoords(psuedoGalaxy.heart));
            weightedCost += DISTANCE_COEFFICIENT * distance;
            weightedCost -= (FUTURE_COEFFICIENT * to.productionFrequency);
            move.cost = cost;

            // determine who should send units
            if (cost >= 0) {
                attackers = new ArrayList<PsuedoPlanet>();
                // System.out.print("New possible move:");
                targetCost = costOfPlanet(to);
                for (PsuedoPlanet from : psuedoGalaxy.myPlanets()) {
                    if (psuedoGalaxy.psuedoPlanetStrength(from) < -1) {
                        attackers.add(from);
                    }
                }

                if (!attackers.isEmpty()) {
                    unitsToSend = new int[attackers.size()];
                    canAttack = true;
                    while (totalToSend <= targetCost && canAttack) {
                        canAttack = false;
                        i = 0;

                        for (PsuedoPlanet from : attackers) {
                            if (!from.equals(to)) {
                                strength = psuedoGalaxy.psuedoPlanetStrength(from);
                                if (-strength - unitsToSend[i] > 2) {
                                    unitsToSend[i]++;
                                    totalToSend++;
                                    canAttack = true;
                                }
                            }
                            i++;
                        }
                    }

                    if (canAttack && totalToSend > targetCost) {
                        i = 0;
                        for (PsuedoPlanet from : attackers) {
                            move.addPsuedoAction(new PsuedoAction(from, to, -(unitsToSend[i])));
                            i++;
                        }
                        move.unitsSent = totalToSend;
                        // System.out.println(move);
                        potentialMoves.add(move);
                    }
                }
            }
        }

        currentValue = psuedoGalaxy.health(PRODUCTION_VALUE, SPREAD_VALUE);

        for (Move potentialMove : potentialMoves) {
            if (potentialMove.cost < 0)
                invalidMoves.add(potentialMove);
            else {
                potentialMove.evaluate();
                if (potentialMove.value > currentValue) {
                    invalidMoves.add(potentialMove);
                    // if (potentialMove.equals(potentialMoves.get(0)))
                    // System.out.println("found bad move: " + (currentValue - potentialMove.value)
                    // + "\n " + potentialMove);
                }
            }

        }

        potentialMoves.removeAll(invalidMoves);
        Collections.sort(potentialMoves);
        // System.out.println("start");
        // for (Move potentialMove : potentialMoves) {
        // System.out.println(potentialMove.value);
        // }

    }

    private Move findBestAction(PsuedoGalaxy psuedoGalaxy) {
        PsuedoPlanet target = null;
        ArrayList<PsuedoPlanet> attackers = new ArrayList<PsuedoPlanet>();
        int[] unitsToSend;
        int i;
        double distance;
        double least;
        double cost;
        double weightedCost;
        double targetCost = 0;
        int strength;
        int totalToSend = 0;
        boolean canAttack;
        Move move = null;

        // find the best planet to send units to
        least = Double.MAX_VALUE;
        for (PsuedoPlanet to : psuedoGalaxy.psuedoPlanets) {
            cost = costOfPlanet(to);
            weightedCost = cost;
            distance = to.distanceTo(Vector.getCoords(psuedoGalaxy.heart));
            weightedCost += DISTANCE_COEFFICIENT * distance;
            weightedCost -= (FUTURE_COEFFICIENT * to.productionFrequency);
            if (cost >= 0 && weightedCost < least) {
                target = to;
                least = weightedCost;
            }
        }

        // determine who should send units
        if (target != null && least != Double.MAX_VALUE) {
            move = new Move();

            targetCost = costOfPlanet(target);
            for (PsuedoPlanet from : psuedoGalaxy.myPlanets()) {
                if (psuedoGalaxy.psuedoPlanetStrength(from) < -1)
                    attackers.add(from);
            }

            if (!attackers.isEmpty()) {
                unitsToSend = new int[attackers.size()];
                canAttack = true;
                while (totalToSend <= targetCost && canAttack) {
                    canAttack = false;
                    i = 0;
                    for (PsuedoPlanet from : attackers) {
                        strength = psuedoGalaxy.psuedoPlanetStrength(from);
                        if (-strength - unitsToSend[i] > 2) {
                            unitsToSend[i]++;
                            totalToSend++;
                            canAttack = true;
                        }
                        i++;
                    }
                }

                if (totalToSend > targetCost) {
                    i = 0;
                    for (PsuedoPlanet from : attackers)
                        move.addPsuedoAction(new PsuedoAction(from, target, -unitsToSend[i++]));
                }
            }
        }

        return move;
    }

    private Move findWorstAction(PsuedoGalaxy psuedoGalaxy) {
        PsuedoPlanet target = null;
        ArrayList<PsuedoPlanet> attackers = new ArrayList<PsuedoPlanet>();
        int[] unitsToSend;
        int i;
        double distance;
        double max;
        double cost;
        double weightedCost;
        double targetCost = 0;
        int strength;
        int totalToSend = 0;
        boolean canAttack;
        Move move = null;

        // find the best planet to send units to
        max = -Double.MAX_VALUE;
        for (PsuedoPlanet to : psuedoGalaxy.psuedoPlanets) {
            cost = costOfPlanetEnemy(to);
            weightedCost = cost;
            distance = to.distanceTo(Vector.getCoords(psuedoGalaxy.enemyHeart));
            weightedCost -= DISTANCE_COEFFICIENT * distance;
            weightedCost -= (FUTURE_COEFFICIENT * to.productionFrequency);
            if (cost <= 0 && weightedCost > max) {
                target = to;
                max = weightedCost;
            }
        }

        // determine who should send units
        if (target != null && max != Double.MIN_VALUE) {
            move = new Move();

            targetCost = -costOfPlanet(target);
            for (PsuedoPlanet from : psuedoGalaxy.enemyPlanets()) {
                if (psuedoGalaxy.psuedoPlanetStrength(from) > 1)
                    attackers.add(from);
            }

            if (!attackers.isEmpty()) {
                unitsToSend = new int[attackers.size()];
                canAttack = true;
                while (totalToSend <= targetCost && canAttack) {
                    canAttack = false;
                    i = 0;
                    for (PsuedoPlanet from : attackers) {
                        strength = psuedoGalaxy.psuedoPlanetStrength(from);
                        if (-strength - unitsToSend[i] > 2) {
                            unitsToSend[i]++;
                            totalToSend++;
                            canAttack = true;
                        }
                        i++;
                    }
                }

                if (totalToSend > targetCost) {
                    i = 0;
                    for (PsuedoPlanet from : attackers)
                        move.addPsuedoAction(new PsuedoAction(from, target, unitsToSend[i++]));
                }
            }
        }

        return move;
    }

    // How many units will it take to capture a planet?
    private double costOfPlanet(PsuedoPlanet target) {
        double cost = Double.MAX_VALUE;
        double timeCost;

        timeCost = (target.productionFrequency
                * (target.distanceTo(Vector.getCoords(psuedoGalaxy.heart)) / FLEET_SPEED)) + 1;

        if (target.neutral == 1) {
            cost = psuedoGalaxy.psuedoPlanetStrength(target) + 1;
        } else if (target.mine()) {
            cost = psuedoGalaxy.psuedoPlanetStrength(target);
        } else {
            cost = (psuedoGalaxy.psuedoPlanetStrength(target) + 1) + timeCost;
        }

        return cost;
    }

    // How many units will it take to capture a planet?
    private double costOfPlanetEnemy(PsuedoPlanet target) {
        double cost = Double.MIN_VALUE;
        double timeCost;

        timeCost = -(target.productionFrequency
                * (target.distanceTo(Vector.getCoords(psuedoGalaxy.enemyHeart)) / FLEET_SPEED)) - 1;

        if (target.neutral == 1) {
            cost = psuedoGalaxy.psuedoPlanetStrength(target) - 1;
        } else if (target.enemy()) {
            cost = psuedoGalaxy.psuedoPlanetStrength(target);
        } else {
            cost = (psuedoGalaxy.psuedoPlanetStrength(target) - 1) + timeCost;
        }

        return cost;
    }

    @Override
    protected void newGame() {

    }

    @Override
    protected String storeSelf() {
        return null;
    }

    private class Move implements Comparable<Move> {
        ArrayList<PsuedoAction> psuedoActions;
        PsuedoPlanet to;
        double cost;
        double value;
        int unitsSent;

        public Move() {
            psuedoActions = new ArrayList<PsuedoAction>();
        }

        public void addPsuedoAction(PsuedoAction psuedoAction) {
            psuedoActions.add(psuedoAction);
            to = psuedoAction.to;
        }

        public void evaluate() {
            PsuedoGalaxy model = new PsuedoGalaxy(psuedoGalaxy);
            Move myMove;
            Move enemyMove;
            ArrayList<PsuedoAction> combinedActions;
            value = 0;

            myMove = this;

            for (int i = 0; i < TIME_TO_PREDICT; i += PREDICTION_INCREMENT) {
                combinedActions = new ArrayList<PsuedoAction>();

                enemyMove = findWorstAction(model);
                if (myMove != null)
                    combinedActions.addAll(myMove.psuedoActions);
                if (enemyMove != null)
                    combinedActions.addAll(enemyMove.psuedoActions);

                model.advance(PREDICTION_INCREMENT, combinedActions);
                myMove = findBestAction(model);
            }

            value = model.health(PRODUCTION_VALUE, SPREAD_VALUE);
        }

        public void commit() {
            for (PsuedoAction psuedoAction : psuedoActions) {
                // System.out.println(" Commiting action " + psuedoAction);
                addAction(psuedoAction.from.realPlanet, psuedoAction.to.realPlanet, -psuedoAction.numUnits);
            }
        }

        @Override
        public int compareTo(Move other) {
            return (int) this.value - (int) other.value;
        }

        public String toString() {
            return "Send " + unitsSent + " units to planet with " + to.strength + " units, for value of " + value;
        }
    }

}
