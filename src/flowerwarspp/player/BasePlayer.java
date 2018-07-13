package flowerwarspp.player;

import flowerwarspp.board.MainBoard;
import flowerwarspp.preset.*;
import flowerwarspp.util.log.Log;
import flowerwarspp.util.log.LogLevel;
import flowerwarspp.util.log.LogModule;

import java.rmi.RemoteException;

import static flowerwarspp.util.log.LogLevel.*;
import static flowerwarspp.util.log.LogModule.PLAYER;


/**
 * Abstrakte Basis-Klasse welche die grundlegende Implementation eines Spielers beschreibt, welcher die Anforderungen
 * des Interfaces {@link Player} erfüllt. Die einzige abstrakte Methode deren Implementation gefordert wird, ist {@link
 * #requestMove()}. Diese Methode fordert einen Zug vom jeweiligen Spieler an, und leitet diesen Zug an die Methode
 * {@link #request()} weiter.
 *
 * @author Michael Merse
 */
abstract class BasePlayer implements flowerwarspp.preset.Player {

	/**
	 * Eine vordefinierte Nachricht einer {@link Exception}, welche geworfen wird, wenn der Spieler noch nicht
	 * initialisiert worden ist.
	 */
	private static final String exception_NoInit =
			"Der Spieler muss zuerst initialisiert werden!";

	/**
	 * Eine vordefinierte Nachricht einer {@link Exception}, welche geworfen wird, wenn eine der Methoden {@link
	 * #confirm(Status)}, {@link #request()} oder {@link #update(Move, Status)} zu einem unerwarteten Zeitpunkt
	 * aufgerufen worden wird.
	 */
	private static final String exception_UnexpectedCall =
			"Unerwarteter Methoden-Aufruf.";

	/**
	 * Eine vordefinierte Nachricht einer {@link Exception}, welche geworfen wird, wenn {@link #request()} zum falschen
	 * Zeitpunkt aufgerufen worden ist.
	 */
	private static final String exception_CycleRequest =
			exception_UnexpectedCall + " Es haette update() aufgerufen werden sollen.";

	/**
	 * Eine vordefinierte Nachricht einer {@link Exception}, welche geworfen wird, wenn {@link #update(Move, Status)}
	 * zum falschen Zeitpunkt aufgerufen worden ist.
	 */
	private static final String exception_CycleUpdate =
			exception_UnexpectedCall + " Es haette confirm() aufgerufen werden sollen.";

	/**
	 * Eine vordefinierte Nachricht einer {@link Exception}, welche geworfen wird, wenn {@link #confirm(Status)} zum
	 * falschen Zeitpunkt aufgerufen worden ist.
	 */
	private static final String exception_CycleConfirm =
			exception_UnexpectedCall + " Es haette request() aufgerufen werden sollen.";

	/**
	 * Eine vordefinierte Nachricht einer {@link Exception}, welche geworfen wird, wenn es eine Disparität zwischen den
	 * Status des Hauptprogramms und des eigenen Spielbretts gab.
	 */
	private static final String exception_StatusError =
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
	private PlayerColor playerColour;
	/**
	 * Wird genutzt, um den aktuellen Status des Spieler-Lebenszyklus (also der Zyklus, welcher im Interface {@link
	 * flowerwarspp.preset.Player} diktiert wird) darzustellen.
	 */
	private PlayerFunction cycleState;

	/**
	 * Ein <code>default</code>-Konstruktor, welcher die Instanzvariablen mit Basiswerten initialisiert.
	 */
	protected BasePlayer() {
		this.playerColour = PlayerColor.Red;
		this.board = null;
		this.cycleState = PlayerFunction.NULL;
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
		if (cycleState == PlayerFunction.NULL) {
			log(ERROR, "request() was called before player was initialized");
			throw new Exception(exception_NoInit);
		}
		if (cycleState != PlayerFunction.REQUEST) {
			log(ERROR, "request() was called at the wrong time");
			throw new Exception(exception_CycleRequest);
		}

		// Ein Zug wird über die abstrakte Methode requestMove() angefordert.
		final Move move = requestMove();

		log(DEBUG, "move of type " + move.getType() + " returned from player through request(): " + move);

		// Dieser angeforderte Zug wird auf dem eigenen Spielbrett ausgeführt.
		board.make(move);

		// Spieler-Lifecycle aktualisieren.
		cycleState = PlayerFunction.CONFIRM;

		return move;
	}

	/**
	 * Fordert einen Zug vom Spieler an, wie der {@link Move} angefordert wird, wird der jeweiligen Implementation der
	 * abstrakten Klasse überlassen.
	 *
	 * @return Der vom Spieler zurückgegebene Zug.
	 * @throws Exception Falls der jeweilige Spieler keinen Zug angeben konnte.
	 */
	/* INFO: Method is abstract because requesting a move from the player works differently with each implementation */
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
		if (cycleState == PlayerFunction.NULL) {
			log(ERROR, "confirm() was called before player was initialized");
			throw new Exception(exception_NoInit);
		}
		if (cycleState != PlayerFunction.CONFIRM) {
			log(ERROR, "confirm() was called at the wrong time");
			throw new Exception(exception_CycleConfirm);
		}

		// Validieren der Status der Bretter des Hauptprogramms und diesen Spielers.
		final Status playerBoardState = boardViewer.getStatus();

		log(DEBUG, "board status on confirm() = " + playerBoardState);

		if (! playerBoardState.equals(status)) {
			log(ERROR, "confirm(): status of player board and main program are not the same");
			throw new Exception(exception_StatusError);
		}

		// Spieler-Lifecycle aktualisieren.
		cycleState = PlayerFunction.UPDATE;
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
		if (cycleState == PlayerFunction.NULL) {
			log(ERROR, "update() was called before player was initialized");
			throw new Exception(exception_NoInit);
		}
		if (cycleState != PlayerFunction.UPDATE) {
			log(ERROR, "update() was called at the wrong time");
			throw new Exception(exception_CycleUpdate);
		}

		log(DEBUG, "received enemy move " + opponentMove + " and status " + status);

		// Den Spielzug des Gegners auf dem eigenen Spielbrett ausführen.
		board.make(opponentMove);

		// Validieren der Status der Bretter des Hauptprogramms und diesen Spielers.
		final Status playerBoardStatus = boardViewer.getStatus();

		if (! playerBoardStatus.equals(status)) {
			log(ERROR, "update(): status of player board and main program are not the same");
			throw new Exception(exception_StatusError);
		}

		// Spieler-Lifecycle aktualisieren.
		cycleState = PlayerFunction.REQUEST;
	}

	/**
	 * Initialisiert einen Spieler, indem dieser mit einem neuen Spielbrett passender Größe und der gewünschten Farbe
	 * versehen wird. Falls diese Methode waärend eines laufenden Spiels aufgerufen wird, wird dieses beendet und mit
	 * dem neu initialisierten Spieler wird ein neues Spiel begonnen.
	 *
	 * @param boardSize    Spielbrettgröße
	 * @param playerColour Farbe des Spielers
	 * @throws Exception       Falls während der Initialisierung ein Fehler auftrat
	 * @throws RemoteException falls bei der Netzwerkkommunikation etwas schief gelaufen ist
	 */
	/* TODO: Properly handle beginning a new game (i.e. calling init() in the middle of a running game) */
	@Override
	public void init(int boardSize, PlayerColor playerColour) throws Exception, RemoteException {

		// Instanzvariablen der Spielerfarben setzen.
		this.playerColour = playerColour;

		// Falls der cycleState schon gesetzt worden ist (also nicht mehr gleich NULL ist), wird das Spielbrett zurück
		// gesetzt, um ein neues Spiel zu starten.
		// Falls des Spielbrett noch nicht gesetzt worden ist, wird ein neues Brett gegebener Größe erzeugt und der
		// zugehörige Viewer gesetzt.
		if (board == null || cycleState != PlayerFunction.NULL) {
			board = new MainBoard(boardSize);
		}

		boardViewer = board.viewer();

		// Der Status des Spieler-Lifecycles wird entsprechend der Spielerfarbe gesetzt. Der rote Spieler beginnt mit
		// request(), der Blaue mit update().
		if (playerColour == PlayerColor.Red) {
			cycleState = PlayerFunction.REQUEST;

		} else {
			cycleState = PlayerFunction.UPDATE;
		}

		log(INFO, "Initialized new player with colour " + playerColour + " on a board with size " + boardSize);
	}

	/**
	 * Gibt die Farbe dieses Spielers zurück.
	 *
	 * @return Die Farbe des Spielers
	 */
	public PlayerColor getPlayerColour() {
		return playerColour;
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
		Log.log(level, PLAYER, "Player " + playerColour + ": " + message);
	}

	/**
	 * Ein unterstützender enum um die Ausführung der durch das Interface {@link flowerwarspp.preset.Player} verlangten
	 * Methoden in der korrekten Reihenfolge zu sichern.
	 *
	 * @see flowerwarspp.preset.Player
	 */
	protected enum PlayerFunction {
		NULL,
		REQUEST,
		CONFIRM,
		UPDATE
	}
}
