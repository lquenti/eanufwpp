package flowerwarspp.main;

import java.rmi.RemoteException;

import flowerwarspp.board.MainBoard;
import flowerwarspp.io.*;
import flowerwarspp.main.savegame.SaveGame;
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

	private SaveGame saveGame;

	/**
	 * Startet ein neues Spiel. Das Spiel wird initialisiert und der Life-Cycle des Spiel gestartet.
	 *
	 * @param gameParameters Die Parameter wie sie auf der Kommandozeile übergeben worden sind
	 */
	Game( final GameParameters gameParameters ) {

		this.gameParameters = gameParameters;

		if ( this.gameParameters.getDebug() )
			Log.setLogLevel(LogLevel.DEBUG);
		else
			Log.setLogLevel(LogLevel.INFO);

		Log.setOutput(System.err);

		if ( this.gameParameters.getText() ) {
			final TextInterface textInterface = new TextInterface();
			input = textInterface;
			output = textInterface;
		} else {
			final BoardFrame boardFrame = BoardFrame.getInstance();
			input = boardFrame;
			output = boardFrame;
		}

		if (gameParameters.getQuiet()) {
			output = new DummyOutput();
		}

		if ( this.gameParameters.getOfferType() != null ) {
			offer();
		} else if ( this.gameParameters.getSaveGameName() != null ) {
			loadGame();
			run();
		} else if (gameParameters.getNumberOfGames() > 1) {
			runGameWithStats();
		} else {
			init();
			run();
		}
	}

	private void runGameWithStats() {
		int n = gameParameters.getNumberOfGames();

		int redWins = 0;
		int redPoints = 0;
		int blueWins = 0;
		int bluePoints = 0;
		int draws = 0;

		for (int i = 0; i < n; i++) {
			Log.log(LogLevel.INFO, LogModule.MAIN, "Starting game " + (i+1) + " of " + n);

			init();
			switch (run()) {
				case RedWin: redWins++; break;
				case BlueWin: blueWins++; break;
				case Draw: draws++; break;
			}

			redPoints += viewer.getPoints(PlayerColor.Red);
			bluePoints += viewer.getPoints(PlayerColor.Blue);
		}

		System.out.println();
		System.out.println("==============================================");
		System.out.println("All games finished with the following results:");
		System.out.println("==============================================");
		System.out.println();
		System.out.println("Red player wins: " + redWins + " (" + (double)redWins/n*100 + "%)");
		System.out.println("Blue player wins: " + blueWins + " (" + (double)blueWins/n*100 + "%)");
		System.out.println("Draws: " + draws + " (" + (double)draws/n*100 + "%)");
		System.out.println();
		System.out.println("Average points for red player: " + (double)redPoints / n);
		System.out.println("Average points for blue player: " + (double)bluePoints / n);
	}

	private void loadGame() {

		Log.log(LogLevel.DEBUG, LogModule.MAIN, "Started loading savegame " +
				gameParameters.getSaveGameName());

		try {
			saveGame = SaveGame.load(gameParameters.getSaveGameName());
		} catch ( Exception e ) {
			Log.log(LogLevel.ERROR, LogModule.MAIN, "There was an error loading the save game:");
			Log.log(LogLevel.ERROR, LogModule.MAIN, e.getMessage());
			System.out.println("Der Spielstand" + gameParameters.getSaveGameName() + " konnte nicht geladen werden.");
			return;
		}

		if ( gameParameters.getReplaySpeed() > 0 ) {
			board = new MainBoard(gameParameters.getBoardSize());
		} else {
			board = saveGame.initBoard();
		}

		viewer = board.viewer();
		output.setViewer(viewer);

		if ( gameParameters.getReplaySpeed() > 0 )
			replay(gameParameters.getReplaySpeed());

		Log.log(LogLevel.INFO, LogModule.MAIN, "Savegame " + gameParameters.getSaveGameName() + " loaded");

		if ( board.viewer().getTurn() == PlayerColor.Red ) {

			currentPlayer = Players.createPlayer(gameParameters.getRedType(), input, new MainBoard(board));
			oppositePlayer = Players.createPlayer(gameParameters.getBlueType(), input, new MainBoard(board));
		} else {

			oppositePlayer = Players.createPlayer(gameParameters.getRedType(), input, new MainBoard(board));
			currentPlayer = Players.createPlayer(gameParameters.getBlueType(), input, new MainBoard(board));
		}
		initPlayers();

	}

	private void initPlayers() {

		Log.log(LogLevel.INFO, LogModule.MAIN, "Initializing players.");

		try {
			currentPlayer.init(gameParameters.getBoardSize(), PlayerColor.Red);
			oppositePlayer.init(gameParameters.getBoardSize(), PlayerColor.Blue);
		} catch ( Exception e ) {
			Log.log(LogLevel.ERROR, LogModule.MAIN, "There was an error initializing the players: "
					+ e.getMessage());
			System.out.println("Waehrend der Initialisierung der Spieler ist ein Fehler aufgetreten:");
			e.printStackTrace();
		}
	}

	/**
	 * Erzeugt einen Spieler und bietet ihn im Netzwerk an.
	 */
	private void offer() {

		Log.log(LogLevel.INFO, LogModule.MAIN, "Offering player " + gameParameters.getOfferType() + " on " +
				"the network.");

		try {
			Player offeredPlayer = Players.createPlayer(gameParameters.getOfferType(), input);
			Players.offerPlayer(new RemotePlayer(offeredPlayer, output));
		} catch ( RemoteException e ) {
			Log.log(LogLevel.ERROR, LogModule.MAIN, "There was an error offering the player in the network.");
		}
	}

	/**
	 * Initialisiert das Spiel. Das Spielbrett und die beiden Spieler werden initialisiert.
	 */
	private void init() {

		board = new MainBoard(gameParameters.getBoardSize());
		saveGame = new SaveGame(gameParameters.getBoardSize());

		Log.log(LogLevel.INFO, LogModule.MAIN, "Initialized main board.");

		currentPlayer = Players.createPlayer(gameParameters.getRedType(), input, new MainBoard(board));
		oppositePlayer = Players.createPlayer(gameParameters.getBlueType(), input, new MainBoard(board));

		Log.log(LogLevel.INFO, LogModule.MAIN, "Players created.");

		initPlayers();

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
	private Status run() {

		Log.log(LogLevel.INFO, LogModule.MAIN, "Starting main game loop.");

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

	private void replay( final long replayDelay ) {

		if ( gameParameters.getSaveGameName() == null ) return;

		Log.log(LogLevel.INFO, LogModule.MAIN, "Starting replay of loaded savegame: "
				+ gameParameters.getSaveGameName());

		for ( final Move aSaveGame : saveGame ) {
			board.make(aSaveGame);
			output.refresh();
			try {
				Thread.sleep(replayDelay);
			} catch ( InterruptedException e ) {
				e.printStackTrace();
			}
		}
	}
}
