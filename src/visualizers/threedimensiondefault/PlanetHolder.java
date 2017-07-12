package visualizers.threedimensiondefault;

import galaxy.Planet;

import java.awt.Color;

/**
 *
 * @author Jono
 */
public class PlanetHolder {

    public PlanetHolder(Planet planet) {
        this.planet = planet;

        if (planet.dimensions() == 2) {
            location = new Vector(planet.getCoords()[0], planet.getCoords()[1], 0);
        } else {
            location = new Vector(planet.getCoords()[0], planet.getCoords()[1], planet.getCoords()[2]);
        }

        radius = planet.RADIUS;
        drawColor = planet.getColor();
    }

    public Planet planet;
    public String name;
    public boolean isSun = false;
    public Vector location;
    public double radius;
    public Color drawColor;
    public double screenRadius;
    public Vector screenLocation;
}
