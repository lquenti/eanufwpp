package flowerwarspp.preset;

/**
 * Eine {@code DitchFormatException} wird geworfen, wenn beim Parsen eines Grabens ein Fehler auftritt.
 *
 * @author Dominick Leppich
 */
public class DitchFormatException extends IllegalArgumentException {
    public DitchFormatException(final String msg) {
        super(msg);
    }

    public DitchFormatException(final String msg, final Throwable e) {
        super(msg, e);
    }
}
