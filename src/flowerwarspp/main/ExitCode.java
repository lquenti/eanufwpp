package flowerwarspp.main;

/**
 * Dieser Enum definiert global die verwendeten Exit-Codes, welche das Programm an das Betriebssystem zurück gibt.
 */
public enum ExitCode {
	/**
	 * Dieser Exit-Code signalisiert ein problemloses Durchlaufen des Programms ohne Fehler. Standardrückgabewert,
	 * falls keine Fehler aufgetreten sind.
	 */
	OK {
		@Override
		public String toString() {
			return "Das Spiel wurde ordnungsgemäß beendet.";
		}
	},

	/**
	 * Dieser Exit-Code signalisiert, dass während des Durchlaufens des Programms eine Inkonsistenz der Status des
	 * Spielbretts eines Spielers und dem Spielbrett des Hauptprogramms aufgetreten ist.
	 */
	STATE_INCONSISTENT {
		@Override
		public String toString() {
			return "Fehler: Der Spielbrettzustand des Hauptprogramms stimmt nicht mit dem Spielbrettzustand eines " +
					"Spielers überein.";
		}
	},

	/**
	 * Dieser Exit-Code signalisiert, dass die Verbindung zu einem entfernten
	 * {@link flowerwarspp.player.RemotePlayer} verloren worden ist.
	 */
	CONNECTION_LOST {
		@Override
		public String toString() {
			return "Fehler: Verbindung zum entfernten Spieler verloren.";
		}
	},

	/**
	 * Dieser Exit-Code signalisiert, dass einer der Spieler keinen Zug zurück geben konnte obwohl auf dem Spielbrett
	 * des Hauptprogramms noch Züge dieses Spielers möglich sind.
	 */
	NO_MOVE {
		@Override
		public String toString() {
			return "Fehler: Ein Spieler konnte keinen Zug erzeugen.";
		}
	},

	/**
	 * Dieser Exit-Code signalisiert, dass während des Ladens eines Spielstands ein Fehler aufgetreten ist.
	 */
	LOAD_ERROR {
		@Override
		public String toString() {
			return "Der gegebene Spielstand konnte nicht geladen werden.";
		}
	},

	/**
	 * Dieser Exit-Code signalisiert, dass ein entfernter Spieler nicht an der Adresse gefunden werden konnte, welche
	 * vom Benutzer angegeben worden ist.
	 */
	REMOTE_NOT_FOUND {
		@Override
		public String toString() {
			return "Der angebene entfernte Spieler konnte nicht im Netzwerk gefunden werden.";
		}
	},

	/**
	 * Dieser Exit-Code signalisiert, dass während des Wartens zwischen Spielzügen in der Game-Loop oder beim Replay
	 * eines Spielstands der Thread unterbrochen worden ist.
	 */
	THREAD_INTERRUPTED {
		@Override
		public String toString() {
			return "Während des Wartens zwischen Spielzügen ist ein Fehler aufgetreten.";
		}
	},

	/**
	 * Dieser Exit-Code signalisiert, dass ein eigener Spieler nicht im Netzwerk angeboten werden konnte.
	 */
	OFFER_ERROR {
		@Override
		public String toString() {
			return "Der Spieler konnte nicht im Netzwerk angeboten werden.";
		}
	},

	/**
	 * Dieser Exit-Code signalisiert, dass während der Initialisierung mindestens einer der Spieler ein Fehler
	 * aufgetreten ist.
	 */
	PLAYER_INIT_ERROR {
		@Override
		public String toString() {
			return "Während der Initialisierung der Spieler ist ein Fehler aufgetreten.";
		}
	},

	/**
	 * Dieser Exit-Code signalisiert, dass ein Spieler einen nicht legalen Spielzug getätigt hat.
	 */
	ILLEGAL_MOVE {
		@Override
		public String toString() {
			return "Fehler: Der von der Spielsteuerung erhaltene Spielzug ist nicht mit dem Zustand des lokalen " +
					"Spielbretts vereinbar.";
		}
	}
}