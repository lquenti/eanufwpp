package flowerwarspp;

import flowerwarspp.preset.*;

import flowerwarspp.board.*;
import flowerwarspp.io.*;
import flowerwarspp.player.*;

public class Main {
	private static int boardSize;

	public static void main(String[] args) {
		try {
			ArgumentParser argumentParser = new ArgumentParser(args);
			boardSize = argumentParser.getSize();
		} catch (ArgumentParserException e) {
			System.out.println("Verwendung:");
			System.out.println("flowerwarspp.Main -size <Spielfeldgröße>");
			System.exit(1);
		}

		if (boardSize < 3 || boardSize > 30) {
			System.out.println("Spielfeldgröße muss zwischen 3 und 30 liegen!");
			System.exit(1);
		}

		Board board = new MainBoard(boardSize);
		Viewer boardViewer = board.viewer();
		Requestable input = new TextInterface();
		Output output = new BoardFrame(board.viewer());

		Player currentPlayer = new InteractivePlayer(input);
		Player oppositePlayer = new RandomAI();

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
			}
		} catch (Exception e) {
			System.out.println("Ein Fehler ist aufgetreten:");
			System.out.println(e);
		}
	}
}
