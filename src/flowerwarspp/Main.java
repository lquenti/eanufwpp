package flowerwarspp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.rmi.*;
import java.util.*;

import flowerwarspp.board.*;
import flowerwarspp.io.*;
import flowerwarspp.player.*;
import flowerwarspp.preset.*;
import flowerwarspp.util.log.Log;
import flowerwarspp.util.log.LogLevel;
import flowerwarspp.util.log.LogModule;

public class Main {
	private static int boardSize;
	private static PlayerType redType;
	private static PlayerType blueType;
	private static PlayerType offerType;
	private static int delay;
	private static boolean debug;

	private static void quitWithUsage() {
		System.out.println("Verwendung:");
		System.out.println("flowerwarspp.Main -size <Spielfeldgröße> -red <Spielertyp> -blue <Spielertyp> -delay <Verzögerung> (optional:) --debug");
		System.out.println();
		System.out.println("Spielfeldgröße: Zahl zwischen 3 und 30");
		System.out.println("Spielertyp:     \"human\", \"remote\", \"random\", \"simple\", oder \"adv1\"");
		System.out.println("Verzögerung:    Zeit zwischen Zügen in Millisekunden");
		System.out.println("Debug:          Zeigt Debug-Information im Game-Log an. Optionaler Flag (hat keine Argumente)");
		System.exit(1);
	}

	private static void parseArguments(String[] args) {

		try {
			// set up
			ArgumentParser argumentParser = new ArgumentParser(args);

			// If we want to offer the player, set that variable and return
			try {
				offerType = argumentParser.getOffer();
				return;
			} catch(ArgumentParserException e){
				offerType = null;
			}

			// Parse board size
			boardSize = argumentParser.getSize();
			redType = argumentParser.getRed();
			blueType = argumentParser.getBlue();
			delay = argumentParser.getDelay();

			// Validate board size
			if (boardSize < 3 || boardSize > 30 || delay < 0) {
				quitWithUsage();
			}

			try {
				debug = argumentParser.isDebug();
			} catch ( ArgumentParserException e ) {
				debug = false;
			}

		} catch ( ArgumentParserException e ) {

			quitWithUsage();
		}
	}

	private  static void init() {

	}

	public static void main(String[] args) {
		BoardFrame boardFrame = new BoardFrame();
		Requestable input = boardFrame;
		Output output = boardFrame;

		parseArguments(args);

		if (offerType != null) {
			try {
				Player offeredPlayer = Players.createPlayer(offerType, input);
				Players.offerPlayer(new RemotePlayer(offeredPlayer, output));
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		if (debug)
			Log.getInstance().setLogLevel(LogLevel.DEBUG);
		else
			Log.getInstance().setLogLevel(LogLevel.INFO);

		Log.getInstance().setOutput(System.err);


		Board board = new MainBoard(boardSize);
		Viewer boardViewer = board.viewer();
		boardFrame.setViewer(boardViewer);

		Player currentPlayer = Players.createPlayer(redType, input);
		Player oppositePlayer = Players.createPlayer(blueType, input);

		try {
			currentPlayer.init(boardSize, PlayerColor.Red);
			oppositePlayer.init(boardSize, PlayerColor.Blue);

			while (boardViewer.getStatus() == Status.Ok) {
				Move move = currentPlayer.request();
				board.make(move);
				currentPlayer.confirm(boardViewer.getStatus());
				oppositePlayer.update(move, boardViewer.getStatus());
				output.refresh();

				Player t = currentPlayer;
				currentPlayer = oppositePlayer;
				oppositePlayer = t;

				Thread.sleep(delay);
			}
		} catch (Exception e) {
			System.out.println("Ein Fehler ist aufgetreten:");
			e.printStackTrace();
		}
	}
}
