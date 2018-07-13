package flowerwarspp.player;

import flowerwarspp.board.MainBoard;
import flowerwarspp.preset.*;
import flowerwarspp.util.log.Log;
import flowerwarspp.util.log.LogLevel;
import flowerwarspp.util.log.LogModule;

import java.rmi.RemoteException;

import static flowerwarspp.player.BasePlayer.PlayerFunction.*;
import static flowerwarspp.util.log.LogLevel.*;
import static flowerwarspp.util.log.LogModule.PLAYER;


/**
 * Abstrakte Basis-Klasse welche die grundlegende Implementation eines Spielers beschreibt, welcher die Anforderungen
 * des Interfaces {@link Player} erfüllt. Die einzige abstrakte Methode deren Implementation gefordert wird, ist {@link
 * #requestMove()}. Diese Methode fordert einen Zug vom jeweiligen Spieler an, und leitet diesen Zug an die Methode
 * {@link #request()} weiter.
 */
abstract class BasePlayer implements flowerwarspp.preset.Player {

	/**
	 * Eine vordefinierte Nachricht einer {@link Exception}, welche geworfen wird, wenn der Spieler noch nicht
	 * initialisiert worden ist.
	 */
	private static final String noInitMessage =
			"Der Spieler muss zuerst initialisiert werden!";

	/**
	 * Eine vordefinierte Nachricht einer {@link Exception}, welche geworfen wird, wenn eine der Methoden {@link
	 * #confirm(Status)}, {@link #request()} oder {@link #update(Move, Status)} zu einem unerwarteten Zeitpunkt
	 * aufgerufen worden wird.
	 */
	private static final String unexpectedCallMessage =
			"Unerwarteter Methoden-Aufruf.";

	/**
	 * Eine vordefinierte Nachricht einer {@link Exception}, welche geworfen wird, wenn {@link #request()} zum falschen
	 * Zeitpunkt aufgerufen worden ist.
	 */
	private static final String cycleRequestMessage =
			unexpectedCallMessage + " Es haette update() aufgerufen werden sollen.";

	/**
	 * Eine vordefinierte Nachricht einer {@link Exception}, welche geworfen wird, wenn {@link #update(Move, Status)}
	 * zum falschen Zeitpunkt aufgerufen worden ist.
	 */
	private static final String cycleUpdateMessage =
			unexpectedCallMessage + " Es haette confirm() aufgerufen werden sollen.";

	/**
	 * Eine vordefinierte Nachricht einer {@link Exception}, welche geworfen wird, wenn {@link #confirm(Status)} zum
	 * falschen Zeitpunkt aufgerufen worden ist.
	 */
	private static final String cycleConfirmMessage =
			unexpectedCallMessage + " Es haette request() aufgerufen werden sollen.";

	/**
	 * Eine vordefinierte Nachricht einer {@link Exception}, welche geworfen wird, wenn es eine Disparität zwischen den
	 * Status des Hauptprogramms und des eigenen Spielbretts gab.
	 */
	private static final String statusErrorMessage =
			"Der Status des Hauptprogramms und der Status des Spielbretts dieses Spielers stimmen nicht ueberein!";
	/**
	 * Stellt diesem Objekt ein eigenes {@link Board} zur Verfügung, um die Durchführung der eigenen und gegnerischen
	 * Züge nachbilden zu können.
	 */
	protected Board board;
	/**
	 * Ermöglicht den Zugriff auf relevante Daten des Spielbretts, welche für die Verifikation und die Ausarbeitung von
	 * Spielzügen benötigt werden.
	 */
	protected Viewer boardViewer;
	/**
	 * Die Farbe dieses Spielers, nach den Vorgaben des enums {@link PlayerColor}.
	 */
	private PlayerColor playerColor;
	/**
	 * Wird genutzt, um den aktuellen Status des Spieler-Lebenszyklus (also der Zyklus, welcher im Interface {@link
	 * flowerwarspp.preset.Player} diktiert wird) darzustellen.
	 */
	private PlayerFunction cycleState;

	/**
	 * Ein <code>default</code>-Konstruktor, welcher die Instanzvariablen mit Basiswerten initialisiert.
	 */
	protected BasePlayer() {
		this.playerColor = PlayerColor.Red;
		this.board = null;
		this.cycleState = NULL;
	}

	/**
	 * Methode zum Anfordern eines Zugs.
	 *
	 * @return Der vom Spieler geforderte Zug
	 * @throws Exception       Falls der Spieler nicht in der Lage war, einen Zug zu liefern oder falls diese Methode
	 *                         zum falschen Zeitpunkt innerhalb des Zyklus aufgerufen worden ist
	 * @throws RemoteException falls bei der Netzwerkkommunikation etwas schief gelaufen ist
	 */
	@Override
	public final Move request() throws Exception, RemoteException {
		// Den Status des Spieler-Lifecycles validieren.
		if (cycleState == NULL) {
			log(ERROR, "request() was called before player was initialized");
			throw new Exception(noInitMessage);
		}
		if (cycleState != REQUEST && cycleState != INITIAL) {
			log(ERROR, "request() was called at the wrong time");
			throw new Exception(cycleRequestMessage);
		}

		// Ein Zug wird über die abstrakte Methode requestMove() angefordert.
		final Move move = requestMove();

		log(DEBUG, "move of type " + move.getType() + " returned from player through request(): " + move);

		// Dieser angeforderte Zug wird auf dem eigenen Spielbrett ausgeführt.
		board.make(move);

		// Spieler-Lifecycle aktualisieren.
		cycleState = CONFIRM;

		return move;
	}

	/**
	 * Fordert einen Zug vom Spieler an, wie der {@link Move} angefordert wird, wird der jeweiligen Implementation der
	 * abstrakten Klasse überlassen.
	 *
	 * @return Der vom Spieler zurückgegebene Zug.
	 * @throws Exception Falls der jeweilige Spieler keinen Zug angeben konnte.
	 */
	protected abstract Move requestMove() throws Exception;

	/**
	 * Stellt die vom Interface {@link flowerwarspp.preset.Player} geforderte Methode {@link
	 * flowerwarspp.preset.Player#confirm(Status)} bereit.
	 *
	 * @param status Status des Spielbretts des Hauptprogramms nach Ausführen des zuletzt mit {@link #request()}
	 *               geholten Zuges
	 * @throws Exception       Falls sich der eigene Status und der Status des Hauptprogramms unterscheiden oder falls
	 *                         diese Methode zum falschen Zeitpunkt innerhalb des Zyklus aufgerufen worden ist
	 * @throws RemoteException falls bei der Netzwerkkommunikation etwas schief gelaufen ist
	 */
	@Override
	public final void confirm(Status status) throws Exception, RemoteException {
		// Den Status des Spieler-Lifecycles validieren.
		if (cycleState == NULL) {
			log(ERROR, "confirm() was called before player was initialized");
			throw new Exception(noInitMessage);
		}
		if (cycleState != CONFIRM && cycleState != INITIAL) {
			log(ERROR, "confirm() was called at the wrong time");
			throw new Exception(cycleConfirmMessage);
		}

		// Validieren der Status der Bretter des Hauptprogramms und diesen Spielers.
		final Status playerBoardState = boardViewer.getStatus();

		log(DEBUG, "board status on confirm() = " + playerBoardState);

		if (! playerBoardState.equals(status)) {
			log(ERROR, "confirm(): status of player board and main program are not the same");
			throw new Exception(statusErrorMessage);
		}

		// Spieler-Lifecycle aktualisieren.
		cycleState = UPDATE;
	}

	/**
	 * Stellt die vom Interface {@link flowerwarspp.preset.Player} geforderte Methode {@link
	 * flowerwarspp.preset.Player#update(Move, Status)} bereit.
	 *
	 * @param opponentMove Zug des Gegenspielers
	 * @param status       Status des Spielbretts des Hauptprogramms nach Ausführen des Zuges des Gegenspielers
	 * @throws Exception       Falls sich die Status des eigenen Spielbretts nach Ausführen des gegnerischen Zuges und
	 *                         des Hauptprogramms unterscheiden oder falls diese Methode zum falschen Zeitpunkt
	 *                         innerhalb des Zyklus aufgerufen worden ist
	 * @throws RemoteException falls bei der Netzwerkkommunikation etwas schief gelaufen ist
	 */
	@Override
	public final void update(Move opponentMove, Status status) throws Exception, RemoteException {
		// Den Status des Spieler-Lifecycles validieren.
		if (cycleState == NULL) {
			log(ERROR, "update() was called before player was initialized");
			throw new Exception(noInitMessage);
		}
		if (cycleState != UPDATE && cycleState != INITIAL) {
			log(ERROR, "update() was called at the wrong time");
			throw new Exception(cycleUpdateMessage);
		}

		log(DEBUG, "received enemy move " + opponentMove + " and status " + status);

		// Den Spielzug des Gegners auf dem eigenen Spielbrett ausführen.
		board.make(opponentMove);

		// Validieren der Status der Bretter des Hauptprogramms und diesen Spielers.
		final Status playerBoardStatus = boardViewer.getStatus();

		if (! playerBoardStatus.equals(status)) {
			log(ERROR, "update(): status of player board and main program are not the same");
			throw new Exception(statusErrorMessage);
		}

		// Spieler-Lifecycle aktualisieren.
		cycleState = REQUEST;
	}

	/**
	 * Initialisiert einen Spieler, indem dieser mit einem neuen Spielbrett passender Größe und der gewünschten Farbe
	 * versehen wird. Falls diese Methode waärend eines laufenden Spiels aufgerufen wird, wird dieses beendet und mit
	 * dem neu initialisierten Spieler wird ein neues Spiel begonnen.
	 *
	 * @param boardSize    Spielbrettgröße
	 * @param playerColor Farbe des Spielers
	 * @throws Exception       Falls während der Initialisierung ein Fehler auftrat
	 * @throws RemoteException falls bei der Netzwerkkommunikation etwas schief gelaufen ist
	 */
	@Override
	public void init(int boardSize, PlayerColor playerColor) throws Exception, RemoteException {

		// Instanzvariablen der Spielerfarben setzen.
		this.playerColor = playerColor;

		// Falls der cycleState schon gesetzt worden ist (also nicht mehr gleich NULL ist), wird das Spielbrett zurück
		// gesetzt, um ein neues Spiel zu starten.
		// Falls des Spielbrett noch nicht gesetzt worden ist, wird ein neues Brett gegebener Größe erzeugt und der
		// zugehörige Viewer gesetzt.
		if (board == null || cycleState != NULL) {
			board = new MainBoard(boardSize);
		}

		boardViewer = board.viewer();

		// Der Status des Spieler-Lifecycles wird gesetzt.
		cycleState = INITIAL;

		log(INFO, "Initialized new player with color " + playerColor + " on a board with size " + boardSize);
	}

	/**
	 * Gibt die Farbe dieses Spielers zurück.
	 *
	 * @return Die Farbe des Spielers
	 */
	public PlayerColor getPlayerColor() {
		return playerColor;
	}

	/**
	 * Getter-Methode für das Spielbrett dieser Klasse. Wird nur von erbenden Klassen verwendet.
	 * @return Das Spielbrett des Spielers.
	 */
	protected Board getBoard() {
		return board;
	}

	/**
	 * Setzt das Spielbrett des Spielers.
	 * @param board Das neue zu verwendene Spielbrett.
	 */
	public void setBoard(Board board) {
		this.board = board;
	}

	/**
	 * Wrapper für die {@link Log#log(LogLevel, LogModule, String)}-Methode, welcher das {@link LogModule} setzt und den
	 * Spieler anhand seiner Farbe identifiziert.
	 *
	 * @param level   Der Log-Level der Nachricht
	 * @param message Die Nachricht des Log-Eintrags
	 */
	protected void log(LogLevel level, String message) {
		Log.log(level, PLAYER, "Player " + playerColor + ": " + message);
	}

	/**
	 * Ein unterstützender enum um die Ausführung der durch das Interface {@link flowerwarspp.preset.Player} verlangten
	 * Methoden in der korrekten Reihenfolge zu sichern.
	 *
	 * @see flowerwarspp.preset.Player
	 */
	protected enum PlayerFunction {
		/**
		 * Dieser Status signalisiert, dass der Spieler noch nicht mit init() initialisiert worden ist. {@link #NULL}
		 * ist demnach der Standartwert des {@link #cycleState} nach Aufrufen des Konstruktors.
		 */
		NULL,
		/**
		 * Dieser Status signalisiert, dass als nächstes die Funktion {@link #request()} aufgerufen werden soll.
		 */
		REQUEST,
		/**
		 * Dieser Status signalisiert, dass als nächstes die Funktion {@link #confirm(Status)}aufgerufen werden
		 * soll.
		 */
		CONFIRM,
		/**
		 * Dieser Status signalisiert, dass als nächstes die Funktion {@link #update(Move, Status)} aufgerufen werden
		 * soll.
		 */
		UPDATE,
		/**
		 * Dieser Status signalisiert, dass der Spieler mit {@link #init(int, PlayerColor)} initialisiert worden ist. Da
		 * es durchaus sein kann, dass der blaue Spieler ein Spiel mit {@link #request()} eröffnet (zum Beispiel wenn
		 * das Spiel geladen worden ist und in dem Spielstand, Rot den letzten Zug gemacht hat). Der Status
		 * <code>INITIAL</code> erlaubt sowohl das Aufrufen von {@link #request()} also auch von
		 * {@link #update(Move, Status)}. Nach dieser anfänglichen Ausnahme ist der {@link #cycleState} jedoch fest
		 * vorgegeben.
		 */
		INITIAL
	}
}
