package ais.oldais;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import galaxy.Coords;
import galaxy.Fleet;
import galaxy.Planet;
import galaxy.Player;
import galaxy.Unit;

public abstract class LegacyPlayerWithUtils extends Player {

    protected LegacyPlayerWithUtils(Color c, String name) {
        super(c, name);
    }

    public boolean ownedByMe(Unit u) {
        return u != null && u.ownedBy(this);
    }

    public Coords getProductionWeightedCenter(List<Planet> list) {
        if (list.size() == 0) {
            return null; // whoever would do this, screw you
        }
        double[] rtn = new double[list.get(0).dimensions()];
        double weights = 0;
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < rtn.length; j++) {
                rtn[j] += list.get(i).getCoords()[j] * list.get(i).getProductionFrequency();
                weights += list.get(i).getProductionFrequency();
            }
        }
        if (weights == 0) {
            weights = 1;
        }
        for (int j = 0; j < rtn.length; j++) {
            rtn[j] = rtn[j] / list.size() / weights;
        }
        return new Coords(rtn);
    }

    public Coords getUnitCountWeightedCenter(List<Planet> list) {
        if (list.size() == 0) {
            return null; // whoever would do this, screw you
        }
        double[] rtn = new double[list.get(0).dimensions()];
        double weights = 0;
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < rtn.length; j++) {
                rtn[j] += list.get(i).getCoords()[j] * list.get(i).getNumUnits();
                weights += list.get(i).getNumUnits();
            }
        }
        if (weights == 0) {
            weights = 1;
        }
        for (int j = 0; j < rtn.length; j++) {
            rtn[j] = rtn[j] / list.size() / weights;
        }
        return new Coords(rtn);
    }

    public Coords center(List<? extends Coords> coords) {
        if (coords.size() == 0) {
            return null;
        }

        Coords avg = new Coords(new double[coords.get(0).dimensions()]);
        for (Coords c : coords) {
            avg = avg.sum(c);
        }
        return avg.multiply(1. / coords.size());
    }

    public double variance(List<? extends Coords> coords) {
        double[] average = center(coords).getCoords();
        double[] values = new double[average.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = 0;
        }
        for (int i = 0; i < coords.size(); i++) {
            double[] loc = coords.get(i).getCoords();
            for (int j = 0; j < values.length; j++) {
                values[j] += (loc[j] - average[j]) * (loc[j] - average[j]);
            }
        }
        double rtn = 0.0;
        if (coords.size() >= 2) {
            for (int i = 0; i < values.length; i++) {
                rtn += values[i] / (coords.size() - 1);
            }
        }
        return Math.sqrt(rtn);
    }

    public List<Planet> getPlanetsOwnedByPlayer(ArrayList<Planet> planets, Player player) {
        ArrayList<Planet> rtn = new ArrayList<>();
        for (Planet p : planets) {
            if (p.ownedBy(player)) {
                rtn.add(p);
            }
        }
        return rtn;
    }

    public List<Planet> getPlanetsNotOwnedByPlayer(ArrayList<Planet> planets, Player player) {
        ArrayList<Planet> rtn = new ArrayList<>();
        for (Planet p : planets) {
            if (!p.ownedBy(player)) {
                rtn.add(p);
            }
        }
        return rtn;
    }

    public List<Planet> getUnoccupiedPlanets(ArrayList<Planet> planets) {
        ArrayList<Planet> rtn = new ArrayList<>();
        for (Planet p : planets) {
            if (p.isNeutral()) {
                rtn.add(p);
            }
        }
        return rtn;
    }

    public List<Planet> getOwnedPlanets(Planet[] planets) {
        ArrayList<Planet> rtn = new ArrayList<>();
        for (Planet p : planets) {
            if (!p.isNeutral()) {
                rtn.add(p);
            }
        }
        return rtn;
    }

    public List<Planet> getOpponentsPlanets(ArrayList<Planet> planets, Player player) {
        return planets.stream().filter((p) -> p.ownedByOpponentOf(player)).collect(Collectors.toList());
    }

    public Planet getNearestPlanet(Planet[] planets, Planet planet) {
        double bestDistance = Double.MAX_VALUE;
        Planet rtn = null;
        for (Planet p : planets) {
            double thisDistance = planet.distanceTo(p);
            if (thisDistance < bestDistance) {
                bestDistance = thisDistance;
                rtn = p;
            }
        }
        return rtn;
    }

    public int getIncomingFleetCount(Planet p, ArrayList<Fleet> fleets) {
        int rtn = 0;
        for (Fleet f : fleets) {
            if (f.DESTINATION.equals(p)) {
                rtn += f.getNumUnits();
            }
        }
        return rtn;
    }

    public int getPlayersIncomingFleetCount(Planet planet, ArrayList<Fleet> fleets, Player player) {
        int rtn = 0;
        for (Fleet f : fleets) {
            if (f.DESTINATION.equals(planet) && f.ownedBy(player)) {
                rtn += f.getNumUnits();
            }
        }
        return rtn;
    }

    public int getOpponentsIncomingFleetCount(Planet planet, ArrayList<Fleet> fleets, Player player) {
        int rtn = 0;
        for (Fleet f : fleets) {
            if (f.DESTINATION.equals(planet) && !f.ownedBy(player)) {
                rtn += f.getNumUnits();
            }
        }
        return rtn;
    }

    public List<Planet> sortByDistance(List<Planet> planets, Planet planet) {
        List<Planet> rtn = new ArrayList<Planet>(planets);
        Collections.sort(rtn, (a, b) -> {
            return Double.compare(a.distanceTo(planet), b.distanceTo(planet));
        });
        return rtn;
    }

    public List<Fleet> getFleetsOfPlayer(ArrayList<Fleet> fleets, Player player) {
        return fleets.stream().filter((fleet) -> fleet.ownedBy(player)).collect(Collectors.toList());
    }

    public List<Fleet> getOpponentsFleets(ArrayList<Fleet> fleets, Player player) {
        return fleets.stream().filter((fleet) -> !fleet.ownedBy(player)).collect(Collectors.toList());
    }

    public int distOfFarthestFleet(List<Fleet> fleets, Planet p) {
        int maxDist = 0;
        for (Fleet f : fleets) {
            if (f.targeting(p)) {
                int dist = f.ticsLeft();
                if (dist > maxDist) {
                    maxDist = dist;
                }
            }
        }
        return maxDist;
    }

    public int getMyUnitCount(ArrayList<Fleet> fleets, ArrayList<Planet> planets, Player player) {
        return fleets.stream().filter((fleet) -> fleet.ownedBy(player))
                .collect(Collectors.summingInt((fleet) -> fleet.getNumUnits()))
                + planets.stream().filter((planet) -> planet.ownedBy(player))
                        .collect(Collectors.summingInt((planet) -> planet.getNumUnits()));
    }

    public int getOpponentUnitCount(ArrayList<Fleet> fleets, ArrayList<Planet> planets, Player player) {
        return fleets.stream().filter((fleet) -> fleet.ownedByOpponentOf(player))
                .collect(Collectors.summingInt((fleet) -> fleet.getNumUnits()))
                + planets.stream().filter((planet) -> planet.ownedByOpponentOf(player))
                        .collect(Collectors.summingInt((planet) -> planet.getNumUnits()));
    }

    public enum PlanetOwner {
        NOBODY, PLAYER, OPPONENT;
    }

    public PlanetOwner getOwner(Unit u, Player p) {
        if (u.ownedBy(p)) {
            return PlanetOwner.PLAYER;
        } else if (u.ownedByOpponentOf(p)) {
            return PlanetOwner.OPPONENT;
        } else {
            return PlanetOwner.NOBODY;
        }
    }

    public PlanetOwner getCurrentEventualOwner(Planet p, ArrayList<Fleet> fleets, Player player) {
        PlanetOwner current;
        if (p.ownedBy(player)) {
            current = PlanetOwner.PLAYER;
        } else if (p.ownedByOpponentOf(player)) {
            current = PlanetOwner.OPPONENT;
        } else {
            current = PlanetOwner.NOBODY;
        }
        int updateCount = p.getLifespan() % p.PRODUCTION_TIME;
        int previousUnits = 0;
        int unitCount = p.getNumUnits();
        int currentTime = 0;
        for (Fleet f : fleets.stream().filter((fleet) -> fleet.DESTINATION == p)
                .sorted((a, b) -> Double.compare(a.ticsLeft(), b.ticsLeft())).collect(Collectors.toList())) {
            int passingTime = f.ticsLeft() - currentTime;
            if (current != PlanetOwner.NOBODY) {
                updateCount += passingTime;
                int unitsToAdd = (updateCount + p.PRODUCTION_TIME - 1) / p.PRODUCTION_TIME - previousUnits;
                previousUnits += unitsToAdd;
                unitCount += unitsToAdd;
            }
            if ((f.ownedBy(player) && current == PlanetOwner.PLAYER)
                    || (f.ownedByOpponentOf(player) && current == PlanetOwner.OPPONENT)) {
                unitCount += f.getNumUnits();
            } else {
                unitCount -= f.getNumUnits();
                if (unitCount == 0) {
                    current = PlanetOwner.NOBODY;
                }
                if (unitCount < 0) {
                    unitCount = -unitCount;
                    if (f.ownedBy(player)) {
                        current = PlanetOwner.PLAYER;
                    } else {
                        current = PlanetOwner.OPPONENT;
                    }
                }
            }
            currentTime += passingTime;
        }
        return current;
    }

    public int getUnitsToCapture(Planet p, Fleet[] fleets, Player player) {
        PlanetOwner current;
        if (p.ownedBy(player)) {
            current = PlanetOwner.PLAYER;
        } else if (p.ownedByOpponentOf(player)) {
            current = PlanetOwner.OPPONENT;
        } else {
            current = PlanetOwner.NOBODY;
        }
        int updateCount = p.getLifespan() % p.PRODUCTION_TIME;
        int previousUnits = 0;
        int unitCount = p.getNumUnits();
        int currentTime = 0;
        for (Fleet f : Arrays.asList(fleets).stream().filter((fleet) -> fleet.DESTINATION == p)
                .sorted((a, b) -> Double.compare(a.ticsLeft(), b.ticsLeft())).collect(Collectors.toList())) {
            int passingTime = f.ticsLeft() - currentTime;
            if (current != PlanetOwner.NOBODY) {
                updateCount += passingTime;
                int unitsToAdd = (updateCount + p.PRODUCTION_TIME - 1) / p.PRODUCTION_TIME - previousUnits;
                previousUnits += unitsToAdd;
                unitCount += unitsToAdd;
            }
            if ((f.ownedBy(player) && current == PlanetOwner.PLAYER)
                    || (f.ownedByOpponentOf(player) && current == PlanetOwner.OPPONENT)) {
                unitCount += f.getNumUnits();
            } else {
                unitCount -= f.getNumUnits();
                if (unitCount == 0) {
                    current = PlanetOwner.NOBODY;
                }
                if (unitCount < 0) {
                    unitCount = -unitCount;
                    if (f.ownedBy(player)) {
                        current = PlanetOwner.PLAYER;
                    } else {
                        current = PlanetOwner.OPPONENT;
                    }
                }
            }
            currentTime += passingTime;
        }

        return current == PlanetOwner.PLAYER ? -unitCount : unitCount;
    }

    public Planet getNearestOwnedPlanet(Planet[] planets, Planet planet, Player player) {
        double bestDistance = Double.MAX_VALUE;
        Planet rtn = null;
        for (Planet p : planets) {
            // Get the nearest owned planet that is not the planet being checked
            if (p.ownedBy(player) && !p.equals(planet)) {
                double thisDistance = planet.distanceTo(p);
                if (thisDistance < bestDistance) {
                    bestDistance = thisDistance;
                    rtn = p;
                }
            }
        }
        return rtn;
    }

    public int getEnemyUnitsOnPlanets(Planet[] planets, Player player) {
        int unitCount = 0;
        for (Planet p : planets) {
            if (!p.ownedBy(player) && !p.isNeutral()) {
                unitCount += p.getNumUnits();
            }
        }
        return unitCount;
    }

    public int getMyUnitsOnPlanets(Planet[] planets, Player player) {
        int unitCount = 0;
        for (Planet p : planets) {
            if (p.ownedBy(player)) {
                unitCount += p.getNumUnits();
            }
        }
        return unitCount;
    }

    public int getEnemyUnitsInFleets(Fleet[] fleets, Player player) {
        int unitCount = 0;
        for (Fleet f : fleets) {
            if (!f.ownedBy(player) && !f.isNeutral()) {
                unitCount += f.getNumUnits();
            }
        }
        return unitCount;
    }

    public int getMyUnitsInFleets(ArrayList<Fleet> fleets, Player player) {
        int unitCount = 0;
        for (Fleet f : fleets) {
            if (f.ownedBy(player)) {
                unitCount += f.getNumUnits();
            }
        }
        return unitCount;
    }

    public double getMyTotalProductionFrequency(Planet[] planets, Player player) {
        double productionFrequency = 0;
        for (Planet p : planets) {
            if (p.ownedBy(player)) {
                productionFrequency += p.getProductionFrequency();
            }
        }
        return productionFrequency;
    }

    public double getEnemyTotalProductionFrequency(Planet[] planets, Player player) {
        double productionFrequency = 0;
        for (Planet p : planets) {
            if (!p.ownedBy(player) && !p.isNeutral()) {
                productionFrequency += p.getProductionFrequency();
            }
        }
        return productionFrequency;
    }
}
