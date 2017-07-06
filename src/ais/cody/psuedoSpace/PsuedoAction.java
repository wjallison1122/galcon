package ais.cody.psuedoSpace;

public class PsuedoAction {
    public PsuedoPlanet from;
    public PsuedoPlanet to;
    public int numUnits;

    public PsuedoAction(PsuedoPlanet from, PsuedoPlanet to, int numUnits) {
        this.from = from;
        this.to = to;
        this.numUnits = numUnits;
    }

    public PsuedoFleet act() {
        from.fleetDeparts(numUnits);
        return new PsuedoFleet(this);
    }

    public String toString() {
        return "Sending " + numUnits + " from " + from.strength + " to " + to.strength;
    }

}
