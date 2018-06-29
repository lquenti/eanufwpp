package flowerwarspp;

import flowerwarspp.preset.*;

import flowerwarspp.board.*;
import flowerwarspp.io.*;
import flowerwarspp.player.*;

public class Main {
	public static final int boardSize = 15;
	public static void main(String[] args) {
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
