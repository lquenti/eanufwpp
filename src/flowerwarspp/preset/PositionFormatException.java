package flowerwarspp.preset;

/**
 * Eine {@code PositionFormatException} wird geworfen, wenn beim Parsen einer Position ein Fehler auftritt.
 *
 * @author Dominick Leppich
 */
public class PositionFormatException extends IllegalArgumentException {
    public PositionFormatException(String msg) {
        super(msg);
    }

    public PositionFormatException(String msg, Throwable e) {
        super(msg, e);
    }
}
