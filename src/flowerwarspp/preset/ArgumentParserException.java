package flowerwarspp.preset;

/**
 * Eine {@code ArgumentParserException} wird geworfen, wenn beim Einlesen der Programmargumente ein Fehler auftritt.
 *
 * @author Dominick Leppich
 */
public class ArgumentParserException extends Exception {
    public ArgumentParserException(String msg) {
        super(msg);
    }

    public ArgumentParserException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
