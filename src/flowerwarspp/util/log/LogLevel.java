package flowerwarspp.util.log;

/**
 * Enum für die verschiedenen Log-Level. Die Level sind in aufsteigender Reihenfolge sortiert, so ist zum Beispiel
 * {@link #ERROR} der Level mit der höchsten Priorität.
 */
public enum LogLevel {

	/**
	 * Generischer Fallback-Level. Sollte nicht benutzt werden, dient lediglich zur Fehlerbehandlung im Logger.
	 */
	NONE,
	/**
	 * Log-Level für Daten-Dumps. Falls viele Daten auf einmal geloggt werden sollen, sollte dieser Level benutzt
	 * werden.
	 */
	DUMP,
	/**
	 * Log-Level für Debug-Nachrichten.
	 */
	DEBUG,
	/**
	 * Log-Level für Informationen.
	 */
	INFO,
	/**
	 * Log-Level für Warnungen.
	 */
	WARNING,
	/**
	 * Log-Level für Fehlermeldungen.
	 */
	ERROR
}
