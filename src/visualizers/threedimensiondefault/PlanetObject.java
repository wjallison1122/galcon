package visualizers.threedimensiondefault;

import java.awt.Color;

/**
 *
 * @author Jono
 */
public class PlanetObject {
    public Vector location;
    public Vector velocity;
    public Vector acceleration;
    public Color color = Color.gray;
    public double mass;
    public double radius;
    public String name;

    public PlanetObject(Vector _location, double _mass) {
        location = _location;
        mass = _mass;
        velocity = new Vector();
        acceleration = new Vector();
        radius = Math.pow(mass, 1.0 / 3) / 1000;
    }

    public PlanetObject(Vector _location, Vector _velocity, double _mass) {
        location = _location;
        mass = _mass;
        velocity = _velocity;
        acceleration = new Vector();
        radius = Math.pow(mass, 1.0 / 3) / 1000;
    }

    public PlanetObject(Vector _location, Vector _velocity, double _mass, double _radius) {
        location = _location;
        mass = _mass;
        velocity = _velocity;
        acceleration = new Vector();
        radius = _radius;
    }

    public PlanetObject(Vector _location, Vector _velocity, double _mass, double _radius, String _name) {
        location = _location;
        mass = _mass;
        velocity = _velocity;
        acceleration = new Vector();
        radius = _radius;
        name = _name;
    }

    @Override
    public String toString() {
        return "Location: " + location + "\nvelocity: " + velocity + "\nMass: " + mass + "\nRadius: " + radius;
    }
}
