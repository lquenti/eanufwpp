package flowerwarspp;

import java.rmi.Naming;
import java.util.Scanner;

import flowerwarspp.preset.*;

import flowerwarspp.board.*;
import flowerwarspp.io.*;
import flowerwarspp.player.*;

public class Main {
	private static int boardSize;
	private static PlayerType redType;
	private static PlayerType blueType;
	private static PlayerType offerType;
	private static int delay;

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
		try {
			ArgumentParser argumentParser = new ArgumentParser(args);
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

		Board board = new MainBoard(boardSize);
		Viewer boardViewer = board.viewer();
		BoardFrame boardFrame = new BoardFrame(board.viewer());
		Requestable input = boardFrame;
		Output output = boardFrame;

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
