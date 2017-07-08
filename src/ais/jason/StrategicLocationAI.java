package ais.jason;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ais.PlayerWithUtils;
import galaxy.Coords;
import galaxy.Planet;

public class StrategicLocationAI extends PlayerWithUtils {
    // private static final double HAVE_ADVANTAGE_DEFENSE = 10;
    // private static final double HAVE_ADVANTAGE_OFFENSE = 20;
    // private static final double ARE_TIED_OFFENSE = 15;
    // private static final double ARE_TIED_DEFENSE = 15;
    // private static final double AT_DISADVANTAGE_DEFENSE = 20;
    // private static final double AT_DISADVANTAGE_OFFENSE = 10;

    private static final double NORMAL_SPARE_UNIT_RATIO = 1;
    private static final double AGGRESSIVE_SPARE_UNIT_RATIO = 1;
    private static final double DEFENSIVE_SPARE_UNIT_RATIO = 0.3;

    private static final double UNIT_ADVANTAGE_RATIO = 1.1;
    private static final double UNIT_DISADVANTAGE_RATIO = 0.9;
    private static final double PRODUCTION_ADVANTAGE_RATIO = 1.05;
    private static final double PRODUCTION_DISADVANTAGE_RATIO = 0.95;

    private static final double BASE_DISTANCE_FACTOR = 20;
    private static final double DISTANCE_WEIGHTING = 0.3;
    private static final double AGGRESSION = 2.5;
    private static final double AGGRESSION_MULTIPLIER = 100;
    private static final int unitBaseCost = 2;

    private ArrayList<PlanetUtility> allPlanetInfo;
    // private double averageStrategicValue;

    /** PlayMode details
    ***************************************************************
    ********************************** PRODUCTION *****************
    ******************  MORE        *  SAME        *  LESS        *
    ***************************************************************
    ********-> MORE  *  CONFIDENT   *  CONFIDENT   *  AGGRESSIVE  *
    * UNITS -> SAME  *  NORMAL      *  NORMAL      *  DESPARATE   *
    ********-> LESS  *  DEFENSIVE   *  DESPARATE   *  DESPARATE   *
    ***************************************************************
    */

    private enum PlayMode {
        CONFIDENT, AGGRESSIVE, NORMAL, DEFENSIVE, DESPARATE
    }

    private class PlanetUtility {
        Planet planet;
        PlanetOwner eventualOwner;
        int units;
        int extraUnits;
        int minBaseConquerCost;
        double baseStrategicValue;
        double offensiveValue;
        double defensiveValue;
    }

    public StrategicLocationAI() {
        super(new Color(0, 100, 100), "Strategic Location AI");
    }

    public StrategicLocationAI(Color c) {
        super(c, "Strategic Location AI");
    }

    public double getBaseValue(PlanetUtility p, Coords averageLocation, double variance) {
        double distanceFactor = (variance + BASE_DISTANCE_FACTOR)
                / (averageLocation.distanceTo(p.planet) + BASE_DISTANCE_FACTOR);
        return (getCurrentEventualOwner(p.planet, fleets, this) == PlanetOwner.NOBODY ? 1.0 : AGGRESSION)
                * Math.pow(distanceFactor, DISTANCE_WEIGHTING) / p.planet.PRODUCTION_TIME / (unitBaseCost + p.units);
    }

    public double getOverallValue(PlanetUtility p, Coords averageLocation, double variance) {
        double distanceFactor = (variance + BASE_DISTANCE_FACTOR)
                / (averageLocation.distanceTo(p.planet) + BASE_DISTANCE_FACTOR);
        double baseValue = (getCurrentEventualOwner(p.planet, fleets, this) == PlanetOwner.NOBODY ? 1.0 : AGGRESSION)
                * Math.pow(distanceFactor, DISTANCE_WEIGHTING) / p.planet.PRODUCTION_TIME / (unitBaseCost + p.units);
        double stratValue = getDefensiveValue(p.planet, baseValue) / getOffensiveValue(p.planet, baseValue);
        return baseValue * stratValue;
    }

    public double getAggressiveValue(PlanetUtility p, Coords averageLocation, double variance) {
        double distanceFactor = (variance + BASE_DISTANCE_FACTOR)
                / (averageLocation.distanceTo(p.planet) + BASE_DISTANCE_FACTOR);
        double baseValue = (getCurrentEventualOwner(p.planet, fleets, this) == PlanetOwner.NOBODY ? 1.0
                : AGGRESSION * AGGRESSION_MULTIPLIER) * Math.pow(distanceFactor, DISTANCE_WEIGHTING)
                / p.planet.PRODUCTION_TIME / (unitBaseCost + p.units);
        // double stratValue = getDefensiveValue(p.planet, baseValue) /
        // getOffensiveValue(p.planet, baseValue);
        return baseValue;
    }

    public double getDefensiveValue(Planet p, double baseValue) {
        Planet nearestOwn = getNearestOwnedPlanet(planets, p, this);
        double distToAlly = nearestOwn != null ? p.distanceTo(nearestOwn) : Integer.MAX_VALUE;

        return baseValue / distToAlly; // the nearer our own planets are the
                                       // better they can be defended
    }

    public double getOffensiveValue(Planet p, double baseValue) {
        Planet nearestEnemy = getNearestEnemyPlanet(planets, p, this);
        double distToEnemy = nearestEnemy != null ? p.distanceTo(nearestEnemy) : Integer.MAX_VALUE;

        return baseValue / distToEnemy; // the nearer our enemy's planets are
                                        // the faster we can strike them
    }

    public ArrayList<PlanetUtility> sortByBaseValue(List<PlanetUtility> planets, Coords averageLocation,
            double variance) {
        ArrayList<PlanetUtility> rtn = new ArrayList<PlanetUtility>(planets);
        Collections.sort(rtn, (a, b) -> {
            return Double.compare(getBaseValue(b, averageLocation, variance),
                    getBaseValue(a, averageLocation, variance));
        });
        return rtn;
    }

    public ArrayList<PlanetUtility> sortByOverallValue(List<PlanetUtility> planets, Coords averageLocation,
            double variance) {
        ArrayList<PlanetUtility> rtn = new ArrayList<PlanetUtility>(planets);
        Collections.sort(rtn, (a, b) -> {
            return Double.compare(getOverallValue(b, averageLocation, variance),
                    getOverallValue(a, averageLocation, variance));
        });
        return rtn;
    }

    public ArrayList<PlanetUtility> sortByAggressiveValue(List<PlanetUtility> planets, Coords averageLocation,
            double variance) {
        ArrayList<PlanetUtility> rtn = new ArrayList<PlanetUtility>(planets);
        Collections.sort(rtn, (a, b) -> {
            return Double.compare(getAggressiveValue(b, averageLocation, variance),
                    getAggressiveValue(a, averageLocation, variance));
        });
        return rtn;
    }

    public ArrayList<PlanetUtility> sortByOffensiveValue(List<PlanetUtility> planets, Coords averageLocation,
            double variance) {
        ArrayList<PlanetUtility> rtn = new ArrayList<PlanetUtility>(planets);
        Collections.sort(rtn, (a, b) -> {
            return Double.compare(b.offensiveValue, a.offensiveValue);
        });
        return rtn;
    }

    public ArrayList<PlanetUtility> sortByDefensiveValue(List<PlanetUtility> planets, Coords averageLocation,
            double variance) {
        ArrayList<PlanetUtility> rtn = new ArrayList<PlanetUtility>(planets);
        Collections.sort(rtn, (a, b) -> {
            return Double.compare(b.defensiveValue, a.defensiveValue);
        });
        return rtn;
    }

    @Override
    protected void turn() {
        List<Planet> myPlanets = getPlanetsOwnedByPlayer(planets, this);
        // List<Planet> notMyPlanets =
        // getPlanetsNotOwnedByPlayer(planets,
        // this);
        // List<Planet> enemyPlanets = getOpponentsPlanets(planets,
        // this);
        // List<Planet> unownedPlanets =
        // getUnoccupiedPlanets(planets);

        int myTotalUnits = getMyUnitCount(fleets, planets, this);
        int enemyTotalUnits = getOpponentUnitCount(fleets, planets, this);
        int mySpareUnits = 0;
        double myProduction = getMyTotalProductionFrequency(planets, this);
        double enemyProduction = getEnemyTotalProductionFrequency(planets, this);
        double currentUnitRatio = (myTotalUnits + 0.0001) / (enemyTotalUnits + 0.0001);
        double currentProductionRatio = (myProduction + 0.0001) / (enemyProduction + 0.0001);

        Coords averageLocation = center(myPlanets);
        double variance = variance(myPlanets);

        PlayMode mode;
        if (currentUnitRatio > UNIT_ADVANTAGE_RATIO) { // cover more units
            if (currentProductionRatio > PRODUCTION_DISADVANTAGE_RATIO) { // cover
                                                                          // more/same
                                                                          // production
                mode = PlayMode.CONFIDENT;
            } else { // cover less production
                mode = PlayMode.AGGRESSIVE;
            }
        } else if (currentUnitRatio < UNIT_DISADVANTAGE_RATIO) { // cover less
                                                                 // units
            if (currentProductionRatio > PRODUCTION_ADVANTAGE_RATIO) { // cover
                                                                       // more
                                                                       // production
                mode = PlayMode.DEFENSIVE;
            } else { // cover less/equal production
                mode = PlayMode.DESPARATE;
            }
        } else { // cover same units
            if (currentProductionRatio > PRODUCTION_DISADVANTAGE_RATIO) { // cover
                                                                          // more/same
                                                                          // production
                mode = PlayMode.NORMAL;
            } else { // cover less production
                mode = PlayMode.DESPARATE;
            }
        }

        // System.out.println("myUnits: " + myTotalUnits + " enemyUnits: " +
        // enemyTotalUnits);
        // System.out.println("unitRatio: " + currentUnitRatio + " prdnRatio: "
        // +
        // currentProductionRatio);
        // System.out.println(mode);

        // aggregate information about planets
        for (int i = 0; i < allPlanetInfo.size(); i++) {
            PlanetUtility temp = allPlanetInfo.get(i);
            temp.units = temp.planet.getNumUnits();
            temp.eventualOwner = getCurrentEventualOwner(temp.planet, fleets, this);
            temp.minBaseConquerCost = getUnitsToCapture(temp.planet, fleets, this);
            temp.extraUnits = temp.units - getOpponentsIncomingFleetCount(temp.planet, fleets, this);
            temp.defensiveValue = getDefensiveValue(temp.planet, temp.baseStrategicValue);
            temp.offensiveValue = getOffensiveValue(temp.planet, temp.baseStrategicValue);

            // System.out.println("num: "+i+" own: "+temp.planet.getOwner()+"
            // units:
            // "+temp.units+" evenOwn: "+temp.eventualOwner+" conCost:
            // "+temp.minBaseConquerCost);
        }

        // defense loop
        for (PlanetUtility p : allPlanetInfo) {
            if (p.planet.ownedBy(this) && p.eventualOwner != PlanetOwner.PLAYER) {
                for (PlanetUtility other : allPlanetInfo) {
                    if (other.planet.ownedBy(this) && other != p && other.minBaseConquerCost < 0) { // planet
                                                                                                    // is
                                                                                                    // under
                                                                                                    // no
                                                                                                    // threat
                                                                                                    // itself
                                                                                                    // and
                                                                                                    // not
                                                                                                    // the
                                                                                                    // same
                                                                                                    // one
                        // send as many units as required or as many as can be
                        // spared if not enough
                        int willSend = other.extraUnits > p.minBaseConquerCost ? p.minBaseConquerCost
                                : other.extraUnits;
                        other.units -= willSend;
                        other.extraUnits -= willSend;
                        p.minBaseConquerCost -= willSend;
                        addAction(other.planet, p.planet, willSend);
                    }
                }
            }
        }

        for (PlanetUtility p : allPlanetInfo) {
            if (p.minBaseConquerCost < 0 && p.units > 0) {
                mySpareUnits += p.units - getOpponentsIncomingFleetCount(p.planet, fleets, this);
            }
        }

        ArrayList<PlanetUtility> targets;
        currentUnitRatio = currentUnitRatio > 1 ? 1 : currentUnitRatio;

        switch (mode) {
            case CONFIDENT:
                mySpareUnits *= AGGRESSIVE_SPARE_UNIT_RATIO;
                // mySpareUnits = (int) Math.max(mySpareUnits *
                // AGGRESSIVE_SPARE_UNIT_RATIO,
                // mySpareUnits * currentUnitRatio);
                // //AGGRESSIVE_SPARE_UNIT_RATIO;
                targets = sortByAggressiveValue(allPlanetInfo, averageLocation, variance);
                break;
            case AGGRESSIVE:
                mySpareUnits *= AGGRESSIVE_SPARE_UNIT_RATIO;
                // mySpareUnits = (int) Math.max(mySpareUnits *
                // AGGRESSIVE_SPARE_UNIT_RATIO,
                // mySpareUnits * currentUnitRatio);
                targets = sortByOverallValue(allPlanetInfo, averageLocation, variance);
                break;
            case DEFENSIVE:
                mySpareUnits *= DEFENSIVE_SPARE_UNIT_RATIO;
                // mySpareUnits = (int) Math.max(mySpareUnits *
                // DEFENSIVE_SPARE_UNIT_RATIO,
                // mySpareUnits * currentUnitRatio);
                targets = sortByDefensiveValue(allPlanetInfo, averageLocation, variance);
                break;
            case DESPARATE:
                mySpareUnits *= AGGRESSIVE_SPARE_UNIT_RATIO;
                // mySpareUnits = (int) Math.min(mySpareUnits *
                // AGGRESSIVE_SPARE_UNIT_RATIO,
                // mySpareUnits * currentUnitRatio);
                targets = sortByOverallValue(allPlanetInfo, averageLocation, variance);
                break;
            case NORMAL:
                mySpareUnits *= NORMAL_SPARE_UNIT_RATIO;
                // mySpareUnits = (int) Math.min(mySpareUnits *
                // NORMAL_SPARE_UNIT_RATIO,
                // mySpareUnits * currentUnitRatio);
                targets = sortByOverallValue(allPlanetInfo, averageLocation, variance);
                break;
            default:
                System.out.println("Error - in default of switch");
                return;
        }

        // System.out.println("spareUnits: " + mySpareUnits);

        for (int i = 0; i < allPlanetInfo.size() && targets.size() > 0; i++) {
            PlanetUtility current = allPlanetInfo.get(i);

            if (targets.get(0).planet.ownedBy(this)) {
                targets.remove(0);
                i--;
                continue;
            }

            if (current.planet.ownedBy(this)) {
                PlanetUtility temp = targets.get(0);
                // System.out.println("target: " + temp.planet.getOwner() + " u:
                // " +
                // temp.planet.getNumUnits());
                int extraUnitsNeeded = 3;
                if (temp.planet.ownedByOpponentOf(this)) {
                    extraUnitsNeeded += (int) temp.planet.distanceTo(current.planet)
                            / (FLEET_SPEED * temp.planet.PRODUCTION_TIME);
                }
                if (mySpareUnits > temp.minBaseConquerCost) {
                    if (current.minBaseConquerCost < 0 && current.units > 0) {
                        int willSend = current.extraUnits > temp.minBaseConquerCost + extraUnitsNeeded
                                ? temp.minBaseConquerCost + extraUnitsNeeded
                                : current.extraUnits;
                        current.units -= willSend;
                        current.extraUnits -= willSend;
                        temp.minBaseConquerCost -= willSend;
                        if (willSend == temp.minBaseConquerCost) {
                            targets.remove(0);
                        }
                        addAction(current.planet, temp.planet, willSend);
                    }
                } else {
                    targets.remove(0);
                    i--;
                }
            }
        }

    }

    @Override
    protected void newGame() {
        allPlanetInfo = new ArrayList<PlanetUtility>();

        for (Planet p : planets) {
            PlanetUtility newPU = new PlanetUtility();
            newPU.planet = p;

            // Add distance to each planet (if this one will just add 0)
            for (Planet other : planets) {
                newPU.baseStrategicValue += p.distanceTo(other);
            }

            // Set base strategic values to the inverse of the total distance
            newPU.baseStrategicValue = 1000 / newPU.baseStrategicValue;
            allPlanetInfo.add(newPU);
            // averageStrategicValue += newPU.baseStrategicValue;
        }

        // averageStrategicValue /= allPlanetInfo.size();
    }

    @Override
    protected String storeSelf() {
        return null;
    }
}
