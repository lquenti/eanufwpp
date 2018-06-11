package flowerwarspp.preset;

/**
 * Eine {@code MoveFormatException} wird geworfen, wenn beim Parsen eines Zuges ein Fehler auftritt.
 *
 * @author Dominick Leppich
 */
public class MoveFormatException extends IllegalArgumentException {
    public MoveFormatException(String msg) {
        super(msg);
    }

    public MoveFormatException(String msg, Throwable e) {
        super(msg, e);
    }
}
