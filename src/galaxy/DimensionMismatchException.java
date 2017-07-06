package galaxy;

@SuppressWarnings("serial")
public class DimensionMismatchException extends RuntimeException {

    public DimensionMismatchException(String msg) {
        super(msg);
    }
}
