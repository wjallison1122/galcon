package galaxy;

public class Coords extends GameSettings {
    private double[] coords;

    public Coords(double... coords) {
        this.coords = coords;
    }

    /**
     * Creates an Coords with dimension given
     * All values will be zero per language spec 4.12.5
     *
     * @param dimensions The dimension of the coords
     */
    public Coords(int dimensions) {
        this.coords = new double[dimensions];
    }

    public final double[] getCoords() {
        return coords.clone();
    }

    public final int getDimensions() {
        return coords.length;
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

    public final double distanceTo(Coords c) {
        return distanceTo(c.getCoords());
    }

    public Coords sum(Coords other) {
        double[] sum = other.getCoords();
        for (int i = 0; i < coords.length; i++) {
            sum[i] += coords[i];
        }
        return new Coords(sum);
    }

    public Coords multiply(double value) {
        double[] product = getCoords();
        for (int i = 0; i < product.length; i++) {
            product[i] *= value;
        }
        return new Coords(product);
    }
}
