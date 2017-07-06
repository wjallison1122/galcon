package ais.cody.psuedoSpace;

import galaxy.Fleet;
import galaxy.GameSettings;
import galaxy.Player;

import java.util.ArrayList;

import ais.cody.Vector;

public class PsuedoFleet extends GameSettings {
    public PsuedoPlanet target;
    public int strength;
    public int turnsLeft;

    public PsuedoFleet(PsuedoAction psuedoAction) {
        target = psuedoAction.to;
        strength = psuedoAction.numUnits;
        turnsLeft = (int) (target.distanceTo(Vector.getCoords(psuedoAction.from.position)) / FLEET_SPEED);
    }

    public PsuedoFleet(Fleet fleet, Player me, ArrayList<PsuedoPlanet> psuedoPlanets) {
        for (PsuedoPlanet psuedoPlanet : psuedoPlanets)
            if (psuedoPlanet.realPlanet.equals(fleet.DESTINATION))
                target = psuedoPlanet;

        strength = fleet.ownedBy(me) ? -fleet.getNumUnits() : fleet.getNumUnits();
        turnsLeft = (int) (fleet.distanceLeft() / FLEET_SPEED);
    }

    public PsuedoFleet(PsuedoFleet psuedoFleet) {
        target = psuedoFleet.target;
        strength = psuedoFleet.strength;
        turnsLeft = psuedoFleet.turnsLeft;
    }

    public void advance(int time) {
        turnsLeft -= time;
        if (turnsLeft <= 0)
            target.fleetArrives(strength);
    }

    public boolean mine() {
        return strength < 0;
    }

    public String toString() {
        return "Fleet: Sending " + strength + " units to planet " + target.strength;
    }
}
