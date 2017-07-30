package ais.otherai;

import java.awt.Color;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;

import ais.PlayerWithUtils;
import galaxy.Action;
import galaxy.Fleet;
import galaxy.Planet;
import galaxy.Player;
import galaxy.Unit;

/**
 * A heuristic based greedy algorithm. Searches through planets, creating a plan
 * for the planet and then assigning a "profit value" for enacting that plan.
 * All most valuable plans are then enacted.
 *
 * @author user
 *
 */
public class MaxPowerLazer extends PlayerWithUtils {
    HashMap<Planet, LinkedList<Fleet>> fleetsTargeting = new HashMap<Planet, LinkedList<Fleet>>();
    PriorityQueue<PlanetValuer> plans = new PriorityQueue<PlanetValuer>();

    private Planet[] planets;

    int previd = 0;

    protected MaxPowerLazer(Color c, String name) {
        super(Color.CYAN, "MaxPowerLazer");
        setHandler(new PlayerHandler() {
            @Override
            public Collection<Action> turn(Fleet[] fleets) {
                return makeTurn(fleets);
            }

            @Override
            public void newGame(Planet[] newMap) {
                planets = newMap;
                for (Planet p : planets) {
                    LinkedList<Fleet> fleetList = new LinkedList<Fleet>();
                    fleetsTargeting.put(p, fleetList);
                    plans.add(new PlanetValuer(p, fleetList));
                }
            }
        });
    }

    private Collection<Action> makeTurn(Fleet[] fleets) {
        LinkedList<Action> actions = new LinkedList<Action>();
        cleanFleetsTargeting();
        for (Fleet f : fleets) {
            if (f.ID > previd) {
                fleetsTargeting.get(f.DESTINATION).add(f);
            }
        }

        previd = Unit.getLatestID();
        return actions;
    }

    void cleanFleetsTargeting() {
        for (LinkedList<Fleet> fleetList : fleetsTargeting.values()) {
            Iterator<Fleet> fleeterator = fleetList.iterator();
            while (fleeterator.hasNext()) {
                if (fleeterator.next().hasHit()) {
                    fleeterator.remove();
                }
            }
        }
    }

    class PlanetValuer implements Comparable<PlanetValuer> {
        Planet home;
        LinkedList<Fleet> fleetsTargeting;
        LinkedList<MockAction> plan = new LinkedList<MockAction>();

        int value;

        PlanetValuer(Planet home, LinkedList<Fleet> fleetsTargeting) {
            this.home = home;
            this.fleetsTargeting = fleetsTargeting;
            revalue();
        }

        void revalue() {
            value = determineValue();
        }

        int determineValue() {
            if (home.isNeutral()) {
                valueNeutralPlanet();
            } else if (home.ownedByOpponentOf(MaxPowerLazer.this)) {
                return valueEnemyPlanet();
            } else if (home.ownedBy(MaxPowerLazer.this)) {
                return valueMyPlanet();
            }

            return -1;
        }

        int valueMyPlanet() {
            if (fleetsTargeting.size() > 0) {
                int myInbound = 0, enemyInbound = 0;

                for (Fleet f : fleetsTargeting) {
                    if (ownedByMe(f)) {
                        myInbound += f.getNumUnits();
                    } else {
                        enemyInbound += f.getNumUnits();
                    }
                }

                return 0;
            } else {
                return -1;
            }
        }

        int valueEnemyPlanet() {
            return 0;
        }

        int valueNeutralPlanet() {
            if (fleetsTargeting.size() > 0) {
                return 0;
            } else {
                PriorityQueue<Planet> enp = planetsNearOwnedBy(home, MaxPowerLazer.this);
                int unitsMadeBeforeEnemyHit = (int)((home.distanceTo(enp.peek()) / FLEET_SPEED) / home.PRODUCTION_TIME);
                int prodDiff = unitsMadeBeforeEnemyHit - home.getNumUnits();
                // If I lose more units taking over the planet than can be made
                // before the enemy
                // hits the planet
                if (prodDiff < 0) {
                    // TODO check if close enemy planets can actually send
                    // enough units to help.

                    return -1;
                }

                PriorityQueue<Planet> myp = planetsNearOwnedBy(home, MaxPowerLazer.this);
                if (myp.peek().getNumUnits() > home.getNumUnits()) {

                }
                return 0;
            }
        }

        PriorityQueue<Planet> planetsNearOwnedBy(Planet target, Player owner) {
            PriorityQueue<Planet> orderedPlanets = new PriorityQueue<Planet>(11, new Comparator<Planet>() {
                @Override
                public int compare(Planet p1, Planet p2) {
                    return (int)(target.distanceTo(p1) - target.distanceTo(p2));
                }
            });

            for (Planet p : planets) {
                if (p.ownedBy(owner)) {
                    orderedPlanets.add(p);
                }
            }

            return orderedPlanets;
        }

        @Override
        public int compareTo(PlanetValuer pv) {
            return value - pv.value;
        }
    }

    class MockAction {
        Planet start, end;
        int numUnits;

        public MockAction(Planet start, Planet end, int numUnits) {
            this.start = start;
            this.end = end;
            this.numUnits = numUnits;
        }

        Action build() {
            return makeAction(start, end, numUnits);
        }
    }
}
