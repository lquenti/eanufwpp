package flowerwarspp.main.savegame;

/**
 * Eine Exception, welche geworfen wird, falls während des Ladens eines Spielstands ein Fehler auftritt.
 */
public class LoadException extends Exception {

	public LoadException() {
		super();
	}

	public LoadException (String message) {
		super(message);
	}
}
