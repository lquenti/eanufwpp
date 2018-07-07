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

public class Main {
	private static int boardSize;
	private static PlayerType redType;
	private static PlayerType blueType;
	private static PlayerType offerType;
	private static int delay;
	private static boolean debug;

	private static void quitWithUsage() {
		System.out.println("Verwendung:");
		System.out.println("flowerwarspp.Main -size <Spielfeldgröße> -red <Spielertyp> -blue <Spielertyp> -delay <Verzögerung>");
		System.out.println();
		System.out.println("Spielfeldgröße: Zahl zwischen 3 und 30");
		System.out.println("Spielertyp:     \"human\", \"remote\", \"random\" oder \"simple\"");
		System.out.println("Verzögerung:    Zeit zwischen Zügen in Millisekunden");
		System.exit(1);
	}

	public static void main(String[] args) {
		BoardFrame boardFrame = new BoardFrame();
		Requestable input = boardFrame;
		Output output = boardFrame;

		ArgumentParser argumentParser = null;
		try {
			argumentParser = new ArgumentParser(args);
		} catch (ArgumentParserException e) {
			quitWithUsage();
		}

		try {
			offerType = argumentParser.getOffer();
		} catch(ArgumentParserException e){
			offerType = null;
		}

		if (offerType != null) {
			try {
				Player offeredPlayer = Players.createPlayer(offerType, input);
				Players.offerPlayer(new RemotePlayer(offeredPlayer, output));
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		try {
			boardSize = argumentParser.getSize();
			redType = argumentParser.getRed();
			blueType = argumentParser.getBlue();
			delay = argumentParser.getDelay();
		} catch (ArgumentParserException e) {
			quitWithUsage();
		}

		if (boardSize < 3 || boardSize > 30 || delay < 0) {
			quitWithUsage();
		}

		try {
			debug = argumentParser.isDebug();
		} catch ( ArgumentParserException e ) {
			debug = false;
		}

		if (debug)
			Log.getInstance().setLogLevel(LogLevel.DEBUG);
		else
			Log.getInstance().setLogLevel(LogLevel.INFO);

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
