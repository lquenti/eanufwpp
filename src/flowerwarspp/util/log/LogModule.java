package flowerwarspp.util.log;

/**
 * Die Module, aus welchen geloggt werden soll.
 */
public enum LogModule {
	/**
	 * Generisches Modul, im {@link Log}-Singleton werden mit diesem Modul als {@link Log#logModule} alle Module
	 * geloggt.
	 */
	ALL {
		@Override
		public String toString() {
			return "(GENERIC)";
		}
	},
	/**
	 * Modul des Hauptprogramms. Log-Einträge aus dem Hauptprogramm sollten dieses Modul benutzen.
	 */
	MAIN {
		@Override
		public String toString() {
			return "(MAIN)";
		}
	},
	/**
	 * Modul des Spielbretts. Log-Einträge aus dem Spielbrett sollten dieses Modul benutzen.
	 */
	BOARD {
		@Override
		public String toString() {
			return "(BOARD)";
		}
	},
	/**
	 * Modul des Spielers. Log-Einträge aus der Spieler-Klassen sollten dieses Modul benutzen.
	 */
	PLAYER {
		@Override
		public String toString() {
			return "(PLAYER)";
		}
	},
	/**
	 * Modul des User Interfaces. Log-Einträge aus UI-Klassen sollten dieses Modul benutzen.
	 */
	UI {
		@Override
		public String toString() {
			return "(UI)";
		}
	}
}
