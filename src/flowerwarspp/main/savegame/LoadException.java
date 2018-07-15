package flowerwarspp.main.savegame;

import java.io.IOException;

/**
 * Eine Exception, welche geworfen wird, falls während des Ladens eines Spielstands ein Fehler
 * auftritt.
 */
public class LoadException extends Exception {
	/**
	 * Konstruktor, welche eine neue Instanz dieser Klasse mit einer Detail-Nachricht erzeugt.
	 *
	 * @param message
	 * 		Die Nachricht, welche den Grund der Exception detailliert.
	 */
	public LoadException(String message) {
		super(message);
	}

	/**
	 * Copy-Konstruktor, um eine {@link IOException} indirekt in eine LoadException zu verpacken.
	 *
	 * @param e
	 * 		Eine Instanz der Klasse {@link IOException}, deren Daten in dieses Objekt kopiert
	 * 		werden soll.
	 */
	public LoadException(IOException e) {
		super(e);
	}
}
