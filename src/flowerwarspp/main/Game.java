package flowerwarspp.main;

import java.rmi.RemoteException;

import flowerwarspp.board.MainBoard;
import flowerwarspp.io.*;
import flowerwarspp.main.savegame.SaveGame;
import flowerwarspp.player.*;
import flowerwarspp.preset.*;
import flowerwarspp.util.log.*;
import flowerwarspp.util.*;

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
	 * Referenz auf ein Objekt, welches das Interface {@link Viewer} implementiert, um auf den {@link Status} des
	 * Spielbretts zugreifen zu können.
	 */
	private Viewer viewer;

	/**
	 * Eine Referenz auf ein Objekt, welches das Interface {@link Requestable} implementiert, um Züge von einem {@link
	 * InteractivePlayer} anzufordern.
	 */
	private Requestable input;

	/**
	 * Eine Referenz auf ein Objekt, welches das Interface {@link Output} implementiert, um das Spiel für den Benutzer
	 * sichtbar und nachvollziehbar zu machen.
	 */
	private Output output;

	/**
	 * Der rote Spieler.
	 */
	private Player redPlayer;

	/**
	 * Der blaue Spieler.
	 */
	private Player bluePlayer;

	/**
	 * Die von der Kommandezeile gelesenen und gesetzten Parameter für das Starten eines neuen Spiels.
	 */
	private GameParameters gameParameters;

	/**
	 * Ein Objekt der Klasse {@link SaveGame}, welche das Laden und Speichern von Spielen ermöglicht.
	 */
	private SaveGame saveGame;

	/**
	 * Größe des Spielbretts.
	 */
	private int boardSize;

	/**
	 * Startet ein neues Spiel. Das Spiel wird initialisiert und der Life-Cycle des Spiels gestartet.
	 *
	 * @param gameParameters Die Parameter wie sie auf der Kommandozeile übergeben worden sind
	 */
	Game( final GameParameters gameParameters ) {

		this.gameParameters = gameParameters;
		init();
		start();
	}

	/**
	 * Diese private Instanzmethode initialisiert ein neues Spiel. Dabei werden die nötigen Instanzvariablen und
	 * Einstellungen gesetzt.
	 */
	private void init() {
		// Dem Logger mitteilen, ob Debug-Nachrichten angezeigt werden sollen, oder nicht.
		if ( gameParameters.getDebug() )
			Log.setLogLevel(LogLevel.DEBUG);
		else
			Log.setLogLevel(LogLevel.INFO);

		Log.setOutput(System.err);

		// Den Output gemäß der Kommandozeilenparameter initialisieren.
		if ( gameParameters.getText() || gameParameters.getQuiet() || gameParameters.getNumberOfGames() > 1 ) {
			final TextInterface textInterface = new TextInterface();
			input = textInterface;
			output = textInterface;
		} else {
			final BoardFrame boardFrame = BoardFrame.getInstance();
			input = boardFrame;
			output = boardFrame;
		}

		if ( gameParameters.getQuiet() ) {
			output = new DummyOutput();
		}

		boardSize = gameParameters.getBoardSize();
	}

	/**
	 * Startet das Spiel abhängig von den Kommandozeilenparametern. In dieser Methode werden die, von den anderen
	 * Methoden nach oben propagierten, {@link Exception}s aufgefangen und behandelt. Dadurch ist gewährleistet, dass
	 * das Spiel im Falle von auftretenden Fehlern korrekt terminiert wird.
	 */
	private void start() {
		// Das Spiel auf Basis der Kommandozeilenparameter starten.
		if ( gameParameters.getOfferType() != null ) {
			try {
				offer();
			} catch ( RemoteException e ) {
				Log.log(LogLevel.ERROR, LogModule.MAIN, "There was an error offering the player in the " +
						"network: " + e.getMessage());
				System.out.println("Der Spieler konnte nicht im Netzwerk angeboten werden.");
			}
		} else if ( gameParameters.loadGame() ) {
			try {
				loadGame();
				run();
			} catch ( Exception e ) {
				Log.log(LogLevel.ERROR, LogModule.MAIN, "There was an error loading the savegame "
						+ gameParameters.getSaveGameName() + ": " + e.getMessage());
				System.out.println("Der gegebene Spielstand konnte nicht geladen werden.");
			}
		} else if ( gameParameters.getNumberOfGames() > 1 ) {
			try {
				runGameWithStats();
			} catch ( Exception e ) {
				Log.log(LogLevel.ERROR, LogModule.MAIN, "There was an error initializing the players: "
						+ e.getMessage());
				System.out.println("Waehrend der Initialisierung der Spieler ist ein Fehler aufgetreten.");
			}
		} else {
			try {
				initLocalGame();
				run();
			} catch ( Exception e ) {
				Log.log(LogLevel.ERROR, LogModule.MAIN, "There was an error initializing the players: "
						+ e.getMessage());
				System.out.println("Waehrend der Initialisierung der Spieler ist ein Fehler aufgetreten.");
			}
		}
	}

	/**
	 * Startet die auf der Kommandezeile verlangte Anzahl von Spielen hintereinander und speichert die Anzahl der Siege
	 * beider Spieler und der Unentschieden, sowie der durchschnittlichen Anzahl von Punkten beider Spieler nach Ende
	 * eines Spiels.
	 *
	 * @throws Exception Falls während der Initialisierung der Spieler oder während des Spielverlaufs Fehler auftreten.
	 */
	private void runGameWithStats() throws Exception {
		int n = gameParameters.getNumberOfGames();

		int redWins = 0;
		int redPoints = 0;
		int blueWins = 0;
		int bluePoints = 0;
		int draws = 0;

		createPlayers();

		for (int i = 0; i < n; i++) {
			System.out.println("Spiel " + ( i + 1 ) + " von " + n + " wird gestartet...");

			initBoard();
			initPlayers();
			output.setViewer(viewer);

			switch (run()) {
				case RedWin:
					if (i % 2 == 0) {
						redWins++;
					} else {
						blueWins++;
					}
					break;
				case BlueWin:
					if (i % 2 == 0) {
						blueWins++;
					} else {
						redWins++;
					}
					break;
				case Draw:
					draws++;
					break;
			}

			if (i % 2 == 0) {
				redPoints += viewer.getPoints(PlayerColor.Red);
				bluePoints += viewer.getPoints(PlayerColor.Blue);
			} else {
				redPoints += viewer.getPoints(PlayerColor.Blue);
				bluePoints += viewer.getPoints(PlayerColor.Red);
			}

			System.out.println("Wechsle die Seiten...");
			// Spieler tauschen die Seiten
			Player t = redPlayer;
			redPlayer = bluePlayer;
			bluePlayer = t;
		}

		System.out.println();
		System.out.println("=======================================");
		System.out.println("Alle Spiele wurden beendet. Ergebnisse:");
		System.out.println("=======================================");
		System.out.println();
		System.out.println("Gewonnene Spiele (Rot): " + redWins + " (" + (double) redWins / n * 100 + "%)");
		System.out.println("Gewonnene Spiele (Blau): " + blueWins + " (" + (double) blueWins / n * 100 + "%)");
		System.out.println("Unentschieden: " + draws + " (" + (double) draws / n * 100 + "%)");
		System.out.println();
		System.out.println("Durchschnittliche Punktezahl (Rot): " + (double) redPoints / n);
		System.out.println("Durchschnittliche Punktezahl (Blue): " + (double) bluePoints / n);
	}

	/**
	 * Lädt den angegebenen Spielstand und startet mit diesen ein Spiel ab dem Punkt, an dem der Spielstand gespeichert
	 * wurde.
	 *
	 * @throws Exception Falls während des Ladens des Spielstands oder der Initialisierung des Spiels Fehler auftreten.
	 */
	private void loadGame() throws Exception {

		Log.log(LogLevel.DEBUG, LogModule.MAIN, "Started loading savegame " + gameParameters.getSaveGameName());

		// Es wird versucht, den verlangten Spielstand zu laden. load(String) kann eine LoadException werfen, diese
		// wird dann vom Hauptprogramm in init() vernünftig behandelt.
		SaveGame loadedSaveGame = SaveGame.load(gameParameters.getSaveGameName());

		boardSize = saveGame.getBoardSize();

		initBoard();

		output.setViewer(viewer);

		// Das Replay des Spielstands wird nun ausgeführt.
		replay(loadedSaveGame);

		Log.log(LogLevel.INFO, LogModule.MAIN, "Savegame " + gameParameters.getSaveGameName() + " loaded");

		createPlayers();
		initPlayers();
	}

	/**
	 * Initialisiert die beiden Spieler.
	 *
	 * @throws Exception Falls während der Initialisierung ein Fehler aufgetreten ist.
	 */
	private void initPlayers() throws Exception {

		Log.log(LogLevel.INFO, LogModule.MAIN, "Initializing players.");

		redPlayer.init(boardSize, PlayerColor.Red);
		bluePlayer.init(boardSize, PlayerColor.Blue);
	}

	/**
	 * Erzeugt einen Spieler und bietet ihn im Netzwerk an.
	 *
	 * @throws RemoteException Falls der Spieler nicht im Netzwerk angeboten werden konnte.
	 */
	private void offer() throws RemoteException {

		Log.log(LogLevel.INFO, LogModule.MAIN, "Offering player " + gameParameters.getOfferType() + " on " +
				"the network.");

		Player offeredPlayer = Players.createPlayer(gameParameters.getOfferType(), input);
		Players.offerPlayer(new RemotePlayer(offeredPlayer, output));
	}

	/**
	 * Initialisiert das Spielbrett.
	 */
	private void initBoard() {
		// Eine neues Spielbrett wird mit der gegebenen Größe initialisiert.
		board = new MainBoard(boardSize);
		viewer = board.viewer();

		// Zum Speichern des Spiels wird ein neues Objekt der Klasse saveGame erstellt.
		saveGame = new SaveGame(boardSize);

		Log.log(LogLevel.INFO, LogModule.MAIN, "Initialized main board.");
	}

	/**
	 * Initialisiert die Spieler.
	 */
	private void createPlayers() {
		// Roter und blauer Spieler werden auf Grundlage der Kommandozeilenparameter erstellt.
		if (board == null) {
			redPlayer = Players.createPlayer(gameParameters.getRedType(), input);
			bluePlayer = Players.createPlayer(gameParameters.getBlueType(), input);
		} else {
			redPlayer = Players.createPlayer(gameParameters.getRedType(), input, new MainBoard(board));
			bluePlayer = Players.createPlayer(gameParameters.getBlueType(), input, new MainBoard(board));
		}

		Log.log(LogLevel.INFO, LogModule.MAIN, "Players created.");
	}

	/**
	 * Initialisiert das Spiel. Das Spielbrett und die beiden Spieler werden initialisiert.
	 *
	 * @throws Exception Falls während der Initialisierung der Spieler oder des Spielbretts Fehler aufgetreten sind.
	 */
	private void initLocalGame() throws Exception {
		initBoard();
		createPlayers();
		initPlayers();
		output.setViewer(viewer);
	}

	/**
	 * Startet und verwaltet die Game-Loop. In jeder Iteration wird ein Zug vom aktuellen Spieler angefordert, dieser
	 * Zug wird auf dem Spielbrett ausgeführt, dann werden die Status vom Spielbrett und des aktuellen Spielers mit
	 * {@link Player#confirm(Status)} validiert, der validierte Zug und Status werden dem Gegenspieler mit {@link
	 * Player#update(Move, Status)}  übergeben, abschließend werden aktueller Spieler und Gegenspieler vertauscht und
	 * die nächste Iteration beginnt.
	 *
	 * @return Der Status nach Ende des aktuellen Spiels
	 */
	private Status run() {

		// TODO: Refactor!!!

		Log.log(LogLevel.INFO, LogModule.MAIN, "Starting main game loop.");

		Player currentPlayer = null;
		Player oppositePlayer = null;
		if (viewer.getTurn() == PlayerColor.Red) {
			currentPlayer = redPlayer;
			oppositePlayer = bluePlayer;
		} else {
			currentPlayer = bluePlayer;
			oppositePlayer = redPlayer;
		}

		try {
			while ( viewer.getStatus() == Status.Ok ) {
				Log.log(LogLevel.DEBUG, LogModule.MAIN, "Beginning game loop.");
				Move move = null;
				try {
					Log.log(LogLevel.DEBUG, LogModule.MAIN, "Requesting move from player " + viewer.getTurn() + ".");
					move = currentPlayer.request();
					Log.log(LogLevel.DEBUG, LogModule.MAIN, "Player " + viewer.getTurn() + " returned move " + move);
				} catch ( Exception e ) {
					Log.log(LogLevel.INFO, LogModule.MAIN, "Player " + viewer.getTurn() + " didn't make a " +
							"move.");
					Log.log(LogLevel.DEBUG, LogModule.MAIN, "Message: " + e.getMessage());
					move = new Move(MoveType.Surrender);
				}

				Log.log(LogLevel.DEBUG, LogModule.MAIN, "Making move on main board.");
				board.make(move);
				saveGame.add(move);

				try {
					Log.log(LogLevel.DEBUG, LogModule.MAIN, "Confirming status.");
					currentPlayer.confirm(viewer.getStatus());
					Log.log(LogLevel.DEBUG, LogModule.MAIN, "Updating opposite player.");
					oppositePlayer.update(move, viewer.getStatus());
				} catch ( Exception e ) {
					Log.log(LogLevel.DEBUG, LogModule.MAIN, e.getMessage());
				}

				Log.log(LogLevel.DEBUG, LogModule.MAIN, "Refreshing output.");
				output.refresh();

				Player t = currentPlayer;
				currentPlayer = oppositePlayer;
				oppositePlayer = t;

				Thread.sleep(gameParameters.getDelay());
			}

		} catch ( Exception e ) {
			Log.log(LogLevel.ERROR, LogModule.MAIN, "There was an error during the game loop: "
					+ e.getMessage());
			System.out.println("Es ist ein Fehler aufgetreten:");
			e.printStackTrace();
		}

		Log.log(LogLevel.INFO, LogModule.MAIN, "Game ended with status " + viewer.getStatus());

		return viewer.getStatus();
	}

	/**
	 * Führt die in {@link #saveGame} gespeicherten Spielzüge auf dem Spielbrett aus. Falls dies vom Benutzer via der
	 * Kommandozeilenparameter verlangt worden ist, wird zwischen den einzelnen Züge eine bestimmte Zeit gewartet und
	 * der Output aktualisiert, damit das Spielgeschehen Schritt für Schritt nachvollzogen werden kann.
	 *
	 * @throws InterruptedException Falls der {@link Thread} während des Wartens unterbrochen worden ist.
	 */
	private void replay(SaveGame loadedSaveGame) throws InterruptedException {

		Log.log(LogLevel.INFO, LogModule.MAIN, "Starting replay of loaded savegame: "
				+ gameParameters.getSaveGameName());

		// Mit dem von SaveGame implementierten Iterator wird durch alle Züge iteriert. Diese werden jeweils auf dem
		// Spielbrett ausgeführt.
		for ( Move move : loadedSaveGame ) {
			board.make(move);
			if ( gameParameters.getReplaySpeed() > 0 ) {
				output.refresh();
				Thread.sleep(gameParameters.getReplaySpeed());
			}
		}

		output.refresh();
	}
}
