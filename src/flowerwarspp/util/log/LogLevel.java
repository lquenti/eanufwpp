package flowerwarspp.util.log;

/**
 * Enum für die verschiedenen Log-Level. Die Level sind in aufsteigender Reihenfolge sortiert, so
 * ist zum Beispiel {@link #ERROR} der Level mit der höchsten Priorität.
 */
public enum LogLevel {

	/**
	 * Generischer Fallback-Level. Sollte nicht benutzt werden, dient lediglich zur
	 * Fehlerbehandlung im Logger.
	 */
	NONE {
		@Override
		public String toString() {
			return "[NONE]";
		}
	}, /**
	 * Log-Level für Daten-Dumps. Falls viele Daten auf einmal geloggt werden sollen, sollte dieser
	 * Level benutzt werden.
	 * <p>
	 * Neue Log-Einträge diesen Levels werden nicht automatisch geschrieben (geflushed), selbst
	 * wenn
	 * {@link Log#flushOnLog} gesetzt ist, damit die Performance beibehalten wird. Als Nachteil
	 * wächst so unter Umständen der verwendete {@link StringBuffer} an.
	 */
	DUMP {
				@Override
				public String toString() {
					return "[DUMP]";
				}
			},
	/**
	 * Log-Level für Debug-Nachrichten.
	 */
	DEBUG {
				@Override
				public String toString() {
					return "[DEBUG]";
				}
			},
	/**
	 * Log-Level für Informationen.
	 */
	INFO {
				@Override
				public String toString() {
					return "[INFO]";
				}
			},
	/**
	 * Log-Level für Warnungen.
	 */
	WARNING {
				@Override
				public String toString() {
					return "[WARNING]";
				}
			},
	/**
	 * Log-Level für Fehlermeldungen.
	 */
	ERROR {
				@Override
				public String toString() {
					return "[ERROR]";
				}
			}
}
