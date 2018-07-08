package flowerwarspp.main;

import java.rmi.ConnectException;
import java.rmi.RemoteException;

import flowerwarspp.board.MainBoard;
import flowerwarspp.io.*;
import flowerwarspp.player.*;
import flowerwarspp.preset.*;
import flowerwarspp.util.log.*;

/**
 * Diese Klasse realisiert das Hauptprogramm. Ein neues Spiel wird auf Basis der an {@link Main} übergebenen Argumente
 * initialisiert, anschließend wird eine Game-Loop gestartet.
 */
public class Game {

	/**
	 * Das Spielbrett des Hauptprogramms.
	 */
	private MainBoard board;

	/**
	 * Referenz auf eine Klasse welche das Interface {@link Viewer} implementiert, um auf den {@link Status} des
	 * Spielbretts zugreifen zu können.
	 */
	private Viewer viewer;

	/**
	 * Eine Referenz auf eine Klasse welche das Interface {@link Requestable} implementiert, um Züge von einem {@link
	 * InteractivePlayer} anzufordern.
	 */
	private Requestable input;

	/**
	 * Eine Referenz auf eine Klasse welche das Interface {@link Output} implementiert, um das Spiel für den Benutzer
	 * sichtbar und nachvollziehbar zu machen.
	 */
	private Output output;

	/**
	 * Eine Referenz auf den Spieler, der in der aktuellen Iteration der Game-Loop am Zug ist.
	 */
	private Player currentPlayer;

	/**
	 * Eine Referenz auf den Spieler, der in der aktuellen Iteration der Game-Loop nicht am Zug ist.
	 */
	private Player oppositePlayer;

	private GameParameters gameParameters;

	/**
	 * Startet ein neues Spiel. Das Spiel wird initialisiert und der Life-Cycle des Spiel gestartet.
	 *
	 * @param gameParameters Die Parameter wie sie auf der Kommandozeile übergeben worden sind
	 */
	Game( final GameParameters gameParameters ) {

		this.gameParameters = gameParameters;

		if ( gameParameters.getDebug() )
			Log.getInstance().setLogLevel(LogLevel.DEBUG);
		else
			Log.getInstance().setLogLevel(LogLevel.INFO);

		Log.getInstance().setOutput(System.err);

		BoardFrame boardFrame = BoardFrame.getInstance();
		input = gameParameters.getText() ? new TextInterface() : boardFrame;
		output = boardFrame;

		if ( this.gameParameters.getOfferType() != null ) {
			offer();
		} else {
			init();
			run();
		}
	}

	/**
	 * Erzeugt einen Spieler und bietet ihn im Netzwerk an.
	 */
	private void offer() {
		try {
			Player offeredPlayer = Players.createPlayer(this.gameParameters.getOfferType(), input);
			Players.offerPlayer(new RemotePlayer(offeredPlayer, output));
		} catch ( RemoteException e ) {
			Log.log0(LogLevel.ERROR, LogModule.MAIN, "There was an error offering the player: "
					+ e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Initialisiert das Spiel. Das Spielbrett und die beiden Spieler werden initialisiert.
	 */
	private void init() {
		board = new MainBoard(gameParameters.getBoardSize());
		Log.log0(LogLevel.INFO, LogModule.MAIN, "Initialized main board.");

		currentPlayer = Players.createPlayer(gameParameters.getRedType(), input, new MainBoard(board));
		oppositePlayer = Players.createPlayer(gameParameters.getBlueType(), input, new MainBoard(board));

		try {
			currentPlayer.init(gameParameters.getBoardSize(), PlayerColor.Red);
			oppositePlayer.init(gameParameters.getBoardSize(), PlayerColor.Blue);
		} catch ( Exception e ) {
			Log.log0(LogLevel.ERROR, LogModule.MAIN, "There was an error initializing the players: "
					+ currentPlayer + " and " + oppositePlayer);
			System.out.println("Waehrend der Initialisierung der Spieler ist ein Fehler aufgetreten:");
			e.printStackTrace();
		}

		viewer = board.viewer();
		output.setViewer(viewer);
	}

	/**
	 * Startet und verwaltet die Game-Loop. In jeder Iteration wird ein Zug vom aktuellen Spieler angefordert, dieser
	 * Zug wird auf dem Spielbrett ausgeführt, dann werden die Status vom Spielbrett und des aktuellen Spielers mit
	 * {@link Player#confirm(Status)} validiert, der validierte Zug und Status werden dem Gegenspieler mit {@link
	 * Player#update(Move, Status)}  übergeben, abschließend werden aktueller Spieler und Gegenspieler vertauscht und
	 * die nächste Iteration beginnt.
	 */
	private void run() {
		try {
			while ( viewer.getStatus() == Status.Ok ) {
				Move move = null;
				try {
					move = currentPlayer.request();
				} catch ( Exception e ) {
					Log.log0(LogLevel.INFO, LogModule.MAIN, "Player " + currentPlayer + " didn't make a move.");
					move = new Move(MoveType.Surrender);
				}
				board.make(move);
				try {
					currentPlayer.confirm(viewer.getStatus());
					oppositePlayer.update(move, viewer.getStatus());
				} catch ( Exception e ) {}
				output.refresh();

				Player t = currentPlayer;
				currentPlayer = oppositePlayer;
				oppositePlayer = t;

				Thread.sleep(gameParameters.getDelay());
			}
		} catch ( Exception e ) {
			Log.log0(LogLevel.ERROR, LogModule.MAIN, "There was an error during the game loop: "
					+ e.getMessage());
			System.out.println("Es ist ein Fehler aufgetreten:");
			e.printStackTrace();
		}
	}
}
