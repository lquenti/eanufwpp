package flowerwarspp.util.log;

/**
 * Die Module, aus welchen geloggt werden soll.
 *
 * @author Michael Merse
 */
public enum LogModule {
	/**
	 * Generisches Modul, im {@link Log}-Singleton werden mit diesem Modul als {@link Log#logModule} alle Module
	 * geloggt.
	 */
	ALL,
	/**
	 * Modul des Hauptprogramms. Log-Einträge aus dem Hauptprogramm sollten dieses Modul benutzen.
	 */
	MAIN,
	/**
	 * Modul des Spielbretts. Log-Einträge aus dem Spielbrett sollten dieses Modul benutzen.
	 */
	BOARD,
	/**
	 * Modul der I/O-Klasse. Log-Einträge aus I/O-Klasse sollten dieses Modul benutzen.
	 */
	IO,
	/**
	 * Modul des Spielers. Log-Einträge aus der Spieler-Klassen sollten dieses Modul benutzen.
	 */
	PLAYER
}
