package galaxy;

public class Coords extends GameSettings {
    private double[] coords;

    public Coords(double... coords) {
        this.coords = coords;
    }

    public final double[] getCoords() {
        return coords.clone();
    }

    final void setCoords(double... coords) {
        if (coords.length != DIMENSIONS.length) {
            throw new DimensionMismatchException("Invalid dimensions of coordinates given.");
        }
        this.coords = coords.clone();
    }

    public final double distanceTo(double... otherCoords) {
        double sum = 0;
        for (int i = 0; i < DIMENSIONS.length; i++) {
            sum += Math.pow(coords[i] - otherCoords[i], 2);
        }
        return Math.sqrt(sum);
    }
}
