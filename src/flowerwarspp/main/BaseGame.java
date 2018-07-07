package flowerwarspp.main;

import flowerwarspp.board.MainBoard;
import flowerwarspp.io.BoardFrame;
import flowerwarspp.io.Output;
import flowerwarspp.player.Players;
import flowerwarspp.player.RemotePlayer;
import flowerwarspp.preset.*;
import flowerwarspp.util.log.Log;
import flowerwarspp.util.log.LogLevel;
import flowerwarspp.util.log.LogModule;

import java.rmi.RemoteException;
import java.util.Arrays;

public class BaseGame {
	private int boardSize;
	private Board board;
	private Viewer viewer;
	private PlayerType redType;
	private PlayerType blueType;
	private PlayerType offerType;
	private int delay;
	private boolean logActive;
	private Requestable input;
	private Output output;
	private Player currentPlayer;
	private Player oppositePlayer;

	private static final int ERRORCODE_INVALID_ARGS = 1;

	BaseGame( String[] args ) {

		parseArguments(args);

		if ( offerType != null ) {
			try {
				Player offeredPlayer = Players.createPlayer(offerType, input);
				Players.offerPlayer(new RemotePlayer(offeredPlayer, output));
			} catch ( RemoteException e ) {
				Log.log0(LogLevel.ERROR, LogModule.MAIN, "There was an error offering the player: "
						+ e.getMessage());
				e.printStackTrace();
			}
		} else {
			init();
			run();
		}

	}

	private void init() {
		if ( logActive )
			Log.getInstance().setLogLevel(LogLevel.DEBUG);
		else
			Log.getInstance().setLogLevel(LogLevel.INFO);

		Log.getInstance().setOutput(System.err);

		BoardFrame boardFrame = new BoardFrame();
		input = boardFrame;
		output = boardFrame;

		board = new MainBoard(boardSize);
		viewer = board.viewer();
		output.setViewer(viewer);

		currentPlayer = Players.createPlayer(redType, input);
		oppositePlayer = Players.createPlayer(blueType, input);

		try {
			currentPlayer.init(boardSize, PlayerColor.Red);
			oppositePlayer.init(boardSize, PlayerColor.Blue);
		} catch ( Exception e ) {
			Log.log0(LogLevel.ERROR, LogModule.MAIN, "There was an error initializing the players: "
					+ currentPlayer + " and " + oppositePlayer);
			System.out.println("Waehrend der Initialisierung der Spieler ist ein Fehler aufgetreten:");
			e.printStackTrace();
		}
	}

	private void run() {
		try {
			while ( viewer.getStatus() == Status.Ok ) {
				Move move = currentPlayer.request();
				board.make(move);
				currentPlayer.confirm(viewer.getStatus());
				oppositePlayer.update(move, viewer.getStatus());
				output.refresh();

				Player t = currentPlayer;
				currentPlayer = oppositePlayer;
				oppositePlayer = t;

				Thread.sleep(delay);
			}
		} catch ( Exception e ) {
			Log.log0(LogLevel.ERROR, LogModule.MAIN, "There was an error during the game loop: "
					+ e.getMessage());
			System.out.println("Es ist ein Fehler aufgetreten:");
			e.printStackTrace();
		}
	}

	private static void quitWithUsage() {
		System.out.println("Verwendung:");
		System.out.println("flowerwarspp.main.Main -size <Spielfeldgröße> -red <Spielertyp> -blue <Spielertyp> -delay <Verzögerung> (optional:) --debug");
		System.out.println();
		System.out.println("Spielfeldgröße: Zahl zwischen 3 und 30");
		System.out.println("Spielertyp:     \"human\", \"remote\", \"random\", \"simple\", oder \"adv1\"");
		System.out.println("Verzögerung:    Zeit zwischen Zügen in Millisekunden");
		System.out.println("Debug:          Zeigt Debug-Information im Game-Log an. Optionaler Flag (hat keine Argumente)");
		System.exit(ERRORCODE_INVALID_ARGS);
	}

	private void parseArguments( String[] args ) {

		try {
			// set up
			ArgumentParser argumentParser = new ArgumentParser(args);

			// If we want to offer the player, set that variable and return
			try {
				offerType = argumentParser.getOffer();
				return;
			} catch ( ArgumentParserException e ) {
				offerType = null;
			}

			// Parse board size
			boardSize = argumentParser.getSize();
			redType = argumentParser.getRed();
			blueType = argumentParser.getBlue();
			delay = argumentParser.getDelay();

			// Validate board size
			if ( boardSize < 3 || boardSize > 30 || delay < 0 ) {
				throw new ArgumentParserException("Groeße des Spielfelds ist nicht gueltig.");
			}

			try {
				logActive = argumentParser.isDebug();
			} catch ( ArgumentParserException e ) {
				logActive = false;
			}

		} catch ( ArgumentParserException e ) {

			Log.log0(LogLevel.ERROR, LogModule.MAIN, "Invalid arguments passed: " + Arrays.toString(args));
			quitWithUsage();
		}
	}
}
