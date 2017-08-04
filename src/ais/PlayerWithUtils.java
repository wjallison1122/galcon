package ais;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import galaxy.Fleet;
import galaxy.Planet;
import galaxy.Player;

public class PlayerWithUtils extends Player {

    protected PlayerWithUtils(Color c, String name) {
        super(c, name);
    }

    public int getPlayersIncomingFleetCount(Planet planet, Fleet[] fleets, Player player) {
        int rtn = 0;
        for (Fleet f : fleets) {
            if (f.DESTINATION.equals(planet) && f.ownedBy(player)) {
                rtn += f.getNumUnits();
            }
        }
        return rtn;
    }

    public int getOpponentsIncomingFleetCount(Planet planet, Fleet[] fleets, Player player) {
        int rtn = 0;
        for (Fleet f : fleets) {
            if (f.DESTINATION.equals(planet) && !f.ownedBy(player)) {
                rtn += f.getNumUnits();
            }
        }
        return rtn;
    }

    public List<Fleet> getFleetsOfPlayer(Fleet[] fleets, Player player) {
        return Arrays.asList(fleets).stream().filter((fleet) -> fleet.ownedBy(player)).collect(Collectors.toList());
    }

    public List<Fleet> getOpponentsFleets(Fleet[] fleets, Player player) {
        return Arrays.asList(fleets).stream().filter((fleet) -> !fleet.ownedBy(player)).collect(Collectors.toList());
    }

    public int getMyUnitCount(Fleet[] fleets, Planet[] planets, Player player) {
        return Arrays.asList(fleets).stream().filter((fleet) -> fleet.ownedBy(player))
                .collect(Collectors.summingInt((fleet) -> fleet.getNumUnits()))
                + Arrays.asList(planets).stream().filter((planet) -> planet.ownedBy(player))
                        .collect(Collectors.summingInt((planet) -> planet.getNumUnits()));
    }

    public int getOpponentUnitCount(Fleet[] fleets, Planet[] planets, Player player) {
        return Arrays.asList(fleets).stream().filter((fleet) -> fleet.ownedByOpponentOf(player))
                .collect(Collectors.summingInt((fleet) -> fleet.getNumUnits()))
                + Arrays.asList(planets).stream().filter((planet) -> planet.ownedByOpponentOf(player))
                        .collect(Collectors.summingInt((planet) -> planet.getNumUnits()));
    }

    public List<Planet> sortByDistance(List<Planet> planets, Planet planet) {
        List<Planet> rtn = new ArrayList<Planet>(planets);
        Collections.sort(rtn, (a, b) -> {
            return Double.compare(a.distanceTo(planet), b.distanceTo(planet));
        });
        return rtn;
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
}
