package flowerwarspp.preset;

/**
 * Eine {@code FlowerFormatException} wird geworfen, wenn beim Parsen einer Blume ein Fehler auftritt.
 *
 * @author Dominick Leppich
 */
public class FlowerFormatException extends IllegalArgumentException {
    public FlowerFormatException(String msg) {
        super(msg);
    }

    public FlowerFormatException(String msg, Throwable e) {
        super(msg, e);
    }
}
