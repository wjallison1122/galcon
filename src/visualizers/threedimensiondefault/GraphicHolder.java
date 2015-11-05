package visualizers.threedimensiondefault;

import galaxy.Fleet;
import galaxy.Planet;
import galaxy.Unit;

import java.awt.Color;

/**
 *
 * @author Jono
 */
public class GraphicHolder {

   public static int[] DIMESIONS;
   
   public GraphicHolder(Planet planet) {
        unit = planet;
        if (planet.getCoords().length == 2) {
           location = new Vector(
                 planet.getCoords()[0] - DIMESIONS[0] / 2,
                 planet.getCoords()[1] - DIMESIONS[1] / 2, 0);
        } else {
           location = new Vector(
                 planet.getCoords()[0] - DIMESIONS[0] / 2,
                 planet.getCoords()[1] - DIMESIONS[1] / 2,
                 planet.getCoords()[2] - DIMESIONS[2] / 2);
        }
        name = new Integer(planet.getNumUnits()).toString();
        radius = planet.RADIUS;
        drawColor = planet.getColor();
        units = planet.getNumUnits();
        production = planet.PRODUCTION_TIME;
    }
   
   public GraphicHolder(Fleet fleet) {
      unit = fleet;
      if (fleet.getCoords().length == 2) {
         location = new Vector(fleet.getCoords()[0] - DIMESIONS[0] / 2,
               fleet.getCoords()[1] - DIMESIONS[1] / 2, 0);
      } else {
         location = new Vector(fleet.getCoords()[0] - DIMESIONS[0] / 2,
               fleet.getCoords()[1] - DIMESIONS[1] / 2, fleet.getCoords()[2]
                     - DIMESIONS[2] / 2);
      }
      name = new Integer(fleet.getNumUnits()).toString();
      radius = Math.pow(fleet.getNumUnits(), 1.0/3.0);
      drawColor = fleet.getColor();
      units = fleet.getNumUnits();
   }
   
    public Unit unit;
    public String name;
    public Vector location;
    public double radius;
    public Color drawColor;
    public int units;
    public int production;
    public double screenRadius;
    public Vector screenLocation;
}
