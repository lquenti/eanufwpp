package flowerwarspp.main;

import flowerwarspp.board.MainBoard;
import flowerwarspp.main.savegame.LoadException;
import flowerwarspp.main.savegame.SaveGame;
import flowerwarspp.player.InteractivePlayer;
import flowerwarspp.player.NetworkException;
import flowerwarspp.player.Players;
import flowerwarspp.player.RemotePlayer;
import flowerwarspp.preset.*;
import flowerwarspp.ui.DummyOutput;
import flowerwarspp.ui.Output;
import flowerwarspp.ui.TextInterface;
import flowerwarspp.ui.component.BoardFrame;
import flowerwarspp.ui.start.StartupFrame;
import flowerwarspp.util.Convert;
import flowerwarspp.util.log.Log;
import flowerwarspp.util.log.LogLevel;
import flowerwarspp.util.log.LogModule;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;

import static flowerwarspp.main.ExitCode.*;

/**
 * Diese Klasse realisiert das Hauptprogramm.
 */
public class Main {
	/**
	 * Dieser Error-Code wird vom Programm zurück gegeben, falls die übergebenen Argumente nicht
	 * valide sind.
	 */
	private static final int ERRORCODE_INVALID_ARGS = 1;
	/**
	 * Das Spielbrett des Hauptprogramms.
	 */
	private static MainBoard board;
	/**
	 * Referenz auf ein Objekt, welches das Interface {@link Viewer} implementiert, um auf den
	 * {@link Status} des Spielbretts zugreifen zu können.
	 */
	private static Viewer viewer;
	/**
	 * Eine Referenz auf ein Objekt, welches das Interface {@link Requestable} implementiert, um
	 * Züge von einem {@link InteractivePlayer} anzufordern.
	 */
	private static Requestable input;
	/**
	 * Eine Referenz auf ein Objekt, welches das Interface {@link Output} implementiert, um das
	 * Spiel für den Benutzer sichtbar und nachvollziehbar zu machen.
	 */
	private static Output output;
	/**
	 * Der rote Spieler.
	 */
	private static Player redPlayer;
	/**
	 * Der blaue Spieler.
	 */
	private static Player bluePlayer;
	/**
	 * Die von der Kommandezeile gelesenen und gesetzten Parameter für das Starten eines neuen
	 * Spiels.
	 */
	private static GameParameters gameParameters;
	/**
	 * Ein Objekt der Klasse {@link SaveGame}, welche das Laden von Spielen ermöglicht.
	 */
	private static SaveGame saveGame;
	/**
	 * Größe des Spielbretts.
	 */
	private static int boardSize;

	/**
	 * Gibt Informationen zur Verwendendung des Programms auf der Standardausgabe aus und beendet
	 * das Programm mit dem Exit-Code {@link #ERRORCODE_INVALID_ARGS}.
	 */
	static void quitWithUsage() {
		System.out.println("Verwendung:");
		System.out.println(
				"flowerwarspp.main.Main (-offer <Spielertyp> -name <Name> " +
						"[-offerUrl <URL>] [-port" +
						" <Port>] | -size <Spielfeldgröße> -red <Spielertyp> " +
						"[-redUrl <URL>] -blue " +
						"<Spielertyp> [-blueUrl <URL>]) [-delay <Verzögerung>] " +
						"[-load <Spielstandname>] [-replay <Verzögerung>] " +
						"[-games <Anzahl Spiele>] [--debug] [--text] [--quiet] [--help]");
		System.out.println();
		System.out.println("Spielfeldgröße: Zahl zwischen 3 und 30");
		System.out.println(
				"Spielertyp:     \"human\", \"remote\", \"random\", \"simple\", \"adv1\", oder " +
						"\"adv2\"");
		System.out.println("URL:            Adresse eines Netzwerkspielers.");
		System.out.println(
				"Name:           Name, unter dem ein angebotener Spieler erreichbar sein soll.");
		System.out.println(
				"Port:           Port, unter dem ein angebotener Spieler erreichbar sein soll. Der" +
						" Standardwert ist 1099.");
		System.out.println("Verzögerung:    Zeit zwischen Zügen in Millisekunden.");
		System.out.println("Spielstandname: Name des zu ladenden Spielstands, ohne Datei-Endung.");
		System.out.println(
				"Anzahl Spiele:  Anzahl der Spiele, welche nacheinander ausgeführt werden sollen" +
						".");
		System.out.println(
				"Debug:          Zeigt Debug-Information im Log an. Optionale Flag (hat keine " +
						"Argumente)");
		System.out.println(
				"Text:           Die Texteingabe wird verwendet. Optionale Flag (hat keine " +
						"Argumente)");
		System.out.println(
				"Quiet:          Das Programm gibt keine Informationen über den Spielablauf. " +
						"Optionale Flag hat keine Argumente)");
		System.out.println(
				"Help:           Zeigt diese Hilfe an. Das Programm wird dann beendet. Optionale " +
						"Flag (hat keine Argumente)");
		System.exit(ERRORCODE_INVALID_ARGS);
	}

	/**
	 * Einstiegspunkt der ausführbaren Klasse. Falls keine Kommandozeilenargumente übergeben
	 * werden, wird das Startmenü geöffnet, in welchem die Parameter von Hand in einer GUI
	 * gesetzt werden können.
	 *
	 * @param args
	 * 		Kommandozeilenargumente.
	 */
	public static void main(String[] args) {
		if ((args.length == 0) && (! GraphicsEnvironment.isHeadless())) {
			SwingUtilities.invokeLater(StartupFrame::new);
		} else {
			startNewGame(new GameParameters(args));
		}
	}

	/**
	 * Startet ein neues Spiel. Das Spiel wird initialisiert und der Life-Cycle des Spiels
	 * gestartet.
	 *
	 * @param gameParameters
	 * 		Die Parameter wie sie auf der Kommandozeile übergeben worden sind
	 */
	public static void startNewGame(GameParameters gameParameters) {
		Main.gameParameters = gameParameters;
		init();
		start();
	}

	/**
	 * Diese Methode initialisiert ein neues Spiel. Dabei werden die nötigen Instanzvariablen und
	 * Einstellungen gesetzt.
	 */
	private static void init() {
		Log.setOutput(System.err);

		// Dem Logger mitteilen, ob Debug-Nachrichten angezeigt werden sollen, oder nicht.
		if (gameParameters.getDebug()) {
			Log.setLogLevel(LogLevel.DEBUG);
		} else {
			Log.setLogLevel(LogLevel.ERROR);
		}

		// Den Output gemäß der Kommandozeilenparameter initialisieren.
		if (gameParameters.getText() || gameParameters.getQuiet() ||
				gameParameters.getNumberOfGames() > 1) {
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

		boardSize = gameParameters.getBoardSize();
	}

	/**
	 * Startet das Spiel abhängig von den Kommandozeilenparametern. In dieser Methode werden die,
	 * von den anderen Methoden nach oben propagierten, {@link Exception}s aufgefangen und
	 * behandelt. Dadurch ist gewährleistet, dass das Spiel im Falle von auftretenden Fehlern
	 * korrekt terminiert wird.
	 */
	private static void start() {
		// Das Spiel auf Basis der Kommandozeilenparameter starten.

		try {

			if (gameParameters.getOfferType() != null) {
				offer();
			} else if (gameParameters.loadGame()) {
				loadGame();
				run();
				output.showEndMessage(Convert.statusToText(viewer.getStatus()), OK);
			} else if (gameParameters.getNumberOfGames() > 1) {
				runGameWithStats();
			} else {
				initLocalGame();
				run();
				output.showEndMessage(Convert.statusToText(viewer.getStatus()), OK);
			}
		} catch (LoadException e) {
			Log.log(LogLevel.ERROR, LogModule.MAIN,
					"There was an error loading the savegame " + gameParameters.getSaveGameName() +
							": " + e.getMessage());
			output.showEndMessage(LOAD_ERROR);
		} catch (NetworkException e) {
			Log.log(LogLevel.ERROR, LogModule.MAIN, "Remote player could not be found.");
			output.showEndMessage(REMOTE_NOT_FOUND);
		} catch (InterruptedException e) {
			Log.log(LogLevel.ERROR, LogModule.MAIN,
					"Thread interrupted while waiting between moves.");
			output.showEndMessage(THREAD_INTERRUPTED);
		} catch (RemoteException e) {
			Log.log(LogLevel.ERROR, LogModule.MAIN,
					"There was an error offering the player in the " + "network: " +
							e.getMessage());
			output.showEndMessage(OFFER_ERROR);
		} catch (Exception e) {
			Log.log(LogLevel.ERROR, LogModule.MAIN, "There was an error initialising the" +
					" players.");
			output.showEndMessage(PLAYER_INIT_ERROR);
		}
	}

	/**
	 * Erzeugt einen Spieler und bietet ihn im Netzwerk an.
	 *
	 * @throws RemoteException
	 * 		Falls der Spieler nicht im Netzwerk angeboten werden konnte.
	 * @throws NetworkException
	 * 		Falls an der gegebenen URL kein entfernter Spieler gefunden werden konnte.
	 */
	private static void offer() throws RemoteException, NetworkException {
		Log.log(LogLevel.INFO, LogModule.MAIN,
				"Offering player " + gameParameters.getOfferType() + " on " + "the network.");

		Player offeredPlayer = Players.createPlayer(gameParameters.getOfferType(), input,
				gameParameters.getOfferUrl());
		Players.offerPlayer(new RemotePlayer(offeredPlayer, output), gameParameters.getOfferName(),
				gameParameters.getOfferPort());
	}

	/**
	 * Lädt den angegebenen Spielstand und startet mit diesen ein Spiel ab dem Punkt, an dem der
	 * Spielstand gespeichert wurde.
	 *
	 * @throws Exception
	 * 		Falls während des Ladens des Spielstands oder der Initialisierung des Spiels Fehler
	 * 		auftreten.
	 * @throws InterruptedException
	 * 		Falls während des Warten zwischen Spielzügen beim Replay der Thread unterbrochen wird.
	 * @throws LoadException
	 * 		Falls beim Laden des Spielstands aus der Datei ein Fehler aufgetreten ist.
	 * @throws NetworkException
	 * 		Falls an der gegebenen URL kein entfernter Spieler gefunden werden konnte.
	 */
	private static void loadGame()
			throws Exception, InterruptedException, LoadException, NetworkException {
		Log.log(LogLevel.DEBUG, LogModule.MAIN,
				"Started loading savegame " + gameParameters.getSaveGameName());

		/*
		 * Es wird versucht, den verlangten Spielstand zu laden. load(String) kann eine
		 * LoadException werfen, diese
		 * wird dann vom Hauptprogramm in init() vernünftig behandelt.
		 */
		SaveGame loadedSaveGame = SaveGame.load(gameParameters.getSaveGameName());

		boardSize = loadedSaveGame.getBoardSize();

		initBoard();

		// Das Replay des Spielstands wird nun ausgeführt.
		if (gameParameters.getReplaySpeed() > 0) {
			output.setViewer(viewer);
			replay(loadedSaveGame);
		} else {
			replay(loadedSaveGame);
			output.setViewer(viewer);
		}

		Log.log(LogLevel.INFO, LogModule.MAIN,
				"Savegame " + gameParameters.getSaveGameName() + " loaded");

		createPlayers();
		initPlayers();
	}

	/**
	 * Startet und verwaltet die Main-Loop. In jeder Iteration wird ein Zug vom aktuellen Spieler
	 * angefordert, dieser Zug wird auf dem Spielbrett ausgeführt, dann werden die Status vom
	 * Spielbrett und des aktuellen Spielers mit {@link Player#confirm(Status)} validiert, der
	 * validierte Zug und Status werden dem Gegenspieler mit {@link Player#update(Move, Status)}
	 * übergeben, abschließend werden aktueller Spieler und Gegenspieler vertauscht und die nächste
	 * Iteration beginnt.
	 *
	 * @return Der Status nach Ende des aktuellen Spiels
	 *
	 * @throws InterruptedException
	 * 		Falls während des Wartens zwischen Spielzügen der Thread unterbrochen worden ist.
	 */
	private static Status run() throws InterruptedException {
		Log.log(LogLevel.INFO, LogModule.MAIN, "Starting main game loop.");

		// Wir benutzen in der internen Main-Loop Referenzen auf den roten und den blauen Spieler.
		Player currentPlayer;
		Player oppositePlayer;
		if (viewer.getTurn() == PlayerColor.Red) {
			currentPlayer = redPlayer;
			oppositePlayer = bluePlayer;
		} else {
			currentPlayer = bluePlayer;
			oppositePlayer = redPlayer;
		}

		while (viewer.getStatus() == Status.Ok) {
			Log.log(LogLevel.DEBUG, LogModule.MAIN, "Beginning game loop.");

			/*
			 * Es wird versucht, vom aktuellen Spieler einen Zug zu erhalten. Schlägt dies fehl,
			 * also wird eine
			 * Exception geworfen, dann wird dem aktuellen Spieler automatisch der Surrender-Move
			 * zugewiesen.
			 * Startzeit wird für die spätere Berechnung des Delays bestimmt.
			 */
			long startTime = System.currentTimeMillis();

			Move move = null;

			try {
				Log.log(LogLevel.DEBUG, LogModule.MAIN,
						"Requesting move from player " + viewer.getTurn() + ".");
				move = currentPlayer.request();
				Log.log(LogLevel.DEBUG, LogModule.MAIN,
						"Player " + viewer.getTurn() + " returned move " + move);
			} catch (RemoteException e) {
				output.showEndMessage(CONNECTION_LOST);
				return null;
			} catch (Exception e) {
				output.showEndMessage(NO_MOVE);
				return null;
			}

			long endTime = System.currentTimeMillis();
			Thread.sleep(Math.max(0, gameParameters.getDelay() - (endTime - startTime)));

			/*
			 * Der vom aktuellen Spieler übergebene Zug wird auf dem Spielbrett ausgeführt und dem
			  * eigenem saveGame-
			 * Objekt mitgeteilt.
			 */
			Log.log(LogLevel.DEBUG, LogModule.MAIN, "Making move on main board.");
			board.make(move);
			saveGame.add(move);

			try {
				/*
				 * Falls der aktuelle Spieler nicht aufgegeben hat, werden der Status des
				 * Spielbretts des Hauptprogramms
				 * und der Status des Spielbretts des aktuellen Spielers mit confirm verglichen.
				 */
				Log.log(LogLevel.DEBUG, LogModule.MAIN, "Confirming status.");
				currentPlayer.confirm(viewer.getStatus());
				// Dem Gegner werden Zug des aktuellen Spielers und Status des Spielbretts mit
				// update mitgeteilt.
				Log.log(LogLevel.DEBUG, LogModule.MAIN, "Updating opposite player.");
				oppositePlayer.update(move, viewer.getStatus());
			} catch (RemoteException e) {
				output.showEndMessage(CONNECTION_LOST);
				return null;
			} catch (Exception e) {
				output.showEndMessage(STATE_INCONSISTENT);
				return null;
			}

			// Das Output-Objekt wirds aktualisiert um den ausgeführten Zug anzuzeigen.
			Log.log(LogLevel.DEBUG, LogModule.MAIN, "Refreshing output.");
			output.refresh();

			// Abschließend werden die Spieler vertauscht und gewartet, falls gefordert.
			Player t = currentPlayer;
			currentPlayer = oppositePlayer;
			oppositePlayer = t;
		}

		Log.log(LogLevel.INFO, LogModule.MAIN, "Game ended with status " + viewer.getStatus());

		return viewer.getStatus();
	}

	/**
	 * Startet die auf der Kommandezeile verlangte Anzahl von Spielen hintereinander und speichert
	 * die Anzahl der Siege beider Spieler und der Unentschieden, sowie der durchschnittlichen
	 * Anzahl von Punkten beider Spieler nach Ende eines Spiels.
	 *
	 * @throws Exception
	 * 		Falls während der Initialisierung der Spieler oder während des Spielverlaufs Fehler
	 * 		auftreten.
	 * @throws NetworkException
	 * 		Falls an der gegebenen URL kein entfernter Spieler gefunden werden konnte.
	 */
	private static void runGameWithStats() throws Exception, NetworkException {
		int n = gameParameters.getNumberOfGames();

		int redWins = 0;
		int redPoints = 0;
		int blueWins = 0;
		int bluePoints = 0;
		int draws = 0;

		createPlayers();

		for (int i = 0; i < n; i++) {
			System.out.println("Spiel " + (i + 1) + " von " + n + " wird gestartet...");

			initBoard();
			initPlayers();
			output.setSaveGame(saveGame);
			output.setViewer(viewer);

			Status runStatus = run();

			if (runStatus == null) {
				throw new NullPointerException();
			}

			switch (runStatus) {
				case RedWin:
					if (i % 2 == 0) { // Wegen Seitenwechsel
						redWins++;
					} else {
						blueWins++;
					}
					break;
				case BlueWin:
					if (i % 2 == 0) { // Wegen Seitenwechsel
						blueWins++;
					} else {
						redWins++;
					}
					break;
				case Draw:
					draws++;
					break;
			}

			if (i % 2 == 0) { // Wegen Seitenwechsel
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
		System.out.println(
				"Gewonnene Spiele (Rot): " + redWins + " (" + (double) redWins / n * 100 + "%)");
		System.out.println(
				"Gewonnene Spiele (Blau): " + blueWins + " (" + (double) blueWins / n * 100 +
						"%)");
		System.out.println("Unentschieden: " + draws + " (" + (double) draws / n * 100 + "%)");
		System.out.println();
		System.out.println("Durchschnittliche Punktezahl (Rot): " + (double) redPoints / n);
		System.out.println("Durchschnittliche Punktezahl (Blue): " + (double) bluePoints / n);
	}

	/**
	 * Initialisiert das Spiel. Das Spielbrett und die beiden Spieler werden initialisiert.
	 *
	 * @throws Exception
	 * 		Falls während der Initialisierung der Spieler oder des Spielbretts Fehler aufgetreten
	 * 		sind.
	 * @throws NetworkException
	 * 		Falls an der gegebenen URL kein entfernter Spieler gefunden werden konnte.
	 */
	private static void initLocalGame() throws Exception, NetworkException {
		initBoard();
		createPlayers();
		initPlayers();
		output.setSaveGame(saveGame);
		output.setViewer(viewer);
	}

	/**
	 * Initialisiert das Spielbrett.
	 */
	private static void initBoard() {
		// Eine neues Spielbrett wird mit der gegebenen Größe initialisiert.
		board = new MainBoard(boardSize);
		viewer = board.viewer();

		// Zum Speichern des Spiels wird ein neues Objekt der Klasse saveGame erstellt.
		saveGame = new SaveGame(boardSize);

		Log.log(LogLevel.INFO, LogModule.MAIN, "Initialized main board.");
	}

	/**
	 * Führt die in {@link #saveGame} gespeicherten Spielzüge auf dem Spielbrett aus. Falls dies
	 * vom Benutzer via der Kommandozeilenparameter verlangt worden ist, wird zwischen den einzelnen
	 * Züge eine bestimmte Zeit gewartet und der Output aktualisiert, damit das Spielgeschehen
	 * Schritt für Schritt nachvollzogen werden kann.
	 *
	 * @param loadedSaveGame
	 * 		Der geladene Spielstand, welcher mit replay() wiedergegeben werden soll.
	 *
	 * @throws InterruptedException
	 * 		Falls der {@link Thread} während des Wartens unterbrochen worden ist.
	 */
	private static void replay(SaveGame loadedSaveGame) throws InterruptedException {
		Log.log(LogLevel.INFO, LogModule.MAIN,
				"Starting replay of loaded savegame: " + gameParameters.getSaveGameName());

		/*
		 * Mit dem von SaveGame implementierten Iterator wird durch alle Züge iteriert. Diese
		 * werden jeweils auf dem
		 * Spielbrett ausgeführt.
		 */
		for (Move move : loadedSaveGame) {
			board.make(move);
			saveGame.add(move);
			if (gameParameters.getReplaySpeed() > 0) {
				output.refresh();
				Thread.sleep(gameParameters.getReplaySpeed());
			}
		}
	}

	/**
	 * Initialisiert die Spieler.
	 *
	 * @throws NetworkException
	 * 		Falls an der gegebenen URL kein entfernter Spieler gefunden werden konnte.
	 */
	private static void createPlayers() throws NetworkException {
		// Roter und blauer Spieler werden auf Grundlage der Kommandozeilenparameter erstellt.
		if (board == null) {
			redPlayer = Players.createPlayer(gameParameters.getRedType(), input,
					gameParameters.getRedUrl());
			bluePlayer = Players.createPlayer(gameParameters.getBlueType(), input,
					gameParameters.getBlueUrl());
		} else {
			redPlayer = Players.createPlayer(gameParameters.getRedType(), input,
					gameParameters.getRedUrl(), new MainBoard(board));
			bluePlayer = Players.createPlayer(gameParameters.getBlueType(), input,
					gameParameters.getBlueUrl(), new MainBoard(board));
		}

		Log.log(LogLevel.INFO, LogModule.MAIN, "Players created.");
	}

	/**
	 * Initialisiert die beiden Spieler.
	 *
	 * @throws Exception
	 * 		Falls während der Initialisierung ein Fehler aufgetreten ist.
	 */
	private static void initPlayers() throws Exception {
		Log.log(LogLevel.INFO, LogModule.MAIN, "Initializing players.");

		redPlayer.init(boardSize, PlayerColor.Red);
		bluePlayer.init(boardSize, PlayerColor.Blue);
	}
}
