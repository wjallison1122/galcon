package visualizersthreedee;

import galaxy.Fleet;
import galaxy.Planet;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 *
 * @author Jono
 */
public class Camera {

    public Vector location;
    public double hRot;
    public double vRot;
    public double zoom = 1000;
    public double perspectiveDistance = 1.0;
    public double objectScale = 1;
    public boolean isOrtho = false;
    public boolean drawLabels = true;
    private double screenX;
    private double screenY;
    private Vector normal = new Vector();
    public GraphicHolder[] drawList;
    private Vector lateral = new Vector();
    private Vector horizontal = new Vector();

    public Camera(Vector _location) {
        location = _location;
    }

    public void draw(List<Planet> planetList, List<Fleet> fleetList, Graphics g, int x, int y) {
        precalcDrawing();
        int fontSize = 12;
        double fontXOffset = fontSize / 3;
        drawList = new GraphicHolder[planetList.size() + fleetList.size()];
        for (int i = 0; i < planetList.size(); i++) {
            GraphicHolder h = new GraphicHolder(planetList.get(i));
            h.location = Vector.add(h.location, location);
            h.screenLocation = new Vector();
            h.screenLocation.z = distance(h.location);
            drawList[i] = h;
        }
        for (int i = planetList.size(); i < planetList.size() + fleetList.size(); i++) {
           GraphicHolder h = new GraphicHolder(fleetList.get(i - planetList.size()));
           h.location = Vector.add(h.location, location);
           h.screenLocation = new Vector();
           h.screenLocation.z = distance(h.location);
           drawList[i] = h;
       }
        quickSort(drawList, 0, drawList.length - 1);
        for (GraphicHolder gh : drawList) {
            if (gh.screenLocation.z > 0.001) { //.001 to avoid glitches with following objects and rounding errors
                calcBasicCoords(gh);
                if (!isOrtho) {
                    scaleForDistance(gh);
                }
                //fill object
                g.setColor(gh.drawColor);
                g.fillOval(
                        (int) (gh.screenLocation.x - gh.screenRadius) + x,
                        (int) (gh.screenLocation.y - gh.screenRadius) + y,
                        (int) (gh.screenRadius * 2),
                        (int) (gh.screenRadius * 2));
                //draw circle around object
                g.setColor(Color.WHITE);
                g.drawOval(
                        (int) (gh.screenLocation.x - gh.screenRadius) + x,
                        (int) (gh.screenLocation.y - gh.screenRadius) + y,
                        (int) (gh.screenRadius * 2),
                        (int) (gh.screenRadius * 2));
                //draw unit value
                String unitStr = Integer.toString(gh.units);
                g.setColor(Color.CYAN);
                g.setFont(new Font("Arial", Font.PLAIN, fontSize));
                g.drawString(unitStr,
                      (int) (gh.screenLocation.x - fontXOffset * unitStr.length()) + x,
                      (int) (gh.screenLocation.y + (fontSize - 1)/ 2) + y);
            }
        }
    }

    private void calcBasicCoords(GraphicHolder gh) {
        gh.screenRadius = gh.radius * zoom * objectScale;
        gh.screenLocation.x = -Vector.dot(gh.location, lateral) / Vector.dot(lateral, lateral) * zoom + screenX;
        gh.screenLocation.y = -Vector.dot(gh.location, horizontal) / Vector.dot(horizontal, horizontal) * zoom + screenY;
    }

    private void scaleForDistance(GraphicHolder gh) {
        gh.screenRadius = gh.screenRadius / (gh.screenLocation.z / perspectiveDistance);
        gh.screenLocation.x = gh.screenLocation.x / (gh.screenLocation.z / perspectiveDistance);
        gh.screenLocation.y = gh.screenLocation.y / (gh.screenLocation.z / perspectiveDistance);
    }

    private double distance(Vector Point) {
        return Vector.dot(Point, normal);
    }

    private void precalcDrawing() {
        normal = new Vector(-Math.cos(hRot) * Math.cos(vRot), Math.sin(hRot) * Math.cos(vRot), -Math.sin(vRot));
        lateral = new Vector(Math.sin(hRot), Math.cos(hRot), 0);
        horizontal = Vector.cross(normal, lateral);
    }

    private static void quickSort(GraphicHolder arr[], int left, int right) {
        //modified from code found online at stackOverflow.com
        int i = left, j = right;
        GraphicHolder tmp;
        double pivot = arr[(left + right) / 2].screenLocation.z;

        /* partition */
        while (i <= j) {
            while (arr[i].screenLocation.z > pivot) {
                i++;
            }
            while (arr[j].screenLocation.z < pivot) {
                j--;
            }
            if (i <= j) {
                tmp = arr[i];
                arr[i] = arr[j];
                arr[j] = tmp;
                i++;
                j--;
            }
        }
        /* recursion */
        if (left < j) {
            quickSort(arr, left, j);
        }
        if (i < right) {
            quickSort(arr, i, right);
        }
    }

    public void moveCamera(Vector amt) {
        location = Vector.add(Vector.scale(lateral, amt.x), location);
        location = Vector.add(Vector.scale(normal, amt.y), location);
        location = Vector.add(Vector.scale(horizontal, amt.z), location);
    }
    
}

