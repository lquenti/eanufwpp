package flowerwarspp.preset;

/**
 * Eine {@code DitchFormatException} wird geworfen, wenn beim Parsen eines Grabens ein Fehler auftritt.
 *
 * @author Dominick Leppich
 */
public class DitchFormatException extends IllegalArgumentException {
    public DitchFormatException(String msg) {
        super(msg);
    }

    public DitchFormatException(String msg, Throwable e) {
        super(msg, e);
    }
}
