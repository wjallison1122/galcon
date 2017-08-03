package galaxy;

public class Coords {
    private double[] coords;

    public Coords(double... coords) {
        this.coords = coords.clone();
    }

    public Coords(Coords coords) {
        this.coords = coords.getCoords();
    }

    // TODO check for misuse
    public final double[] getCoords() {
        return coords.clone();
    }

    final void setCoords(Coords coords) {
        this.coords = coords.getCoords();
    }

    public final double distanceTo(double... otherCoords) {
        double sum = 0;
        for (int i = 0; i < coords.length; i++) {
            sum += Math.pow(coords[i] - otherCoords[i], 2);
        }
        return Math.sqrt(sum);
    }

    public final double distanceTo(Coords c) {
        return distanceTo(c.getCoords());
    }

    public final int dimensions() {
        return coords.length;
    }

    public Coords sum(Coords other) {
        double[] sum = other.getCoords();
        for (int i = 0; i < coords.length; i++) {
            sum[i] += coords[i];
        }
        return new Coords(sum);
    }

    public Coords subtract(Coords other) {
        return sum(other.multiply(-1));
    }

    public Coords multiply(double value) {
        double[] product = getCoords();
        for (int i = 0; i < product.length; i++) {
            product[i] *= value;
        }
        return new Coords(product);
    }

    @Override
    public String toString() {
        String str = "";
        for (double d : coords) {
            str += d + ", ";
        }
        return str.substring(0, str.length() - 2);
    }
}
