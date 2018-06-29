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
		Player redPlayer = new InteractivePlayer(input);
		Player bluePlayer = new RandomAI();

		try {
			redPlayer.init(boardSize, PlayerColor.Red);
			bluePlayer.init(boardSize, PlayerColor.Blue);

			while (boardViewer.getStatus() == Status.Ok) {
				Move move = redPlayer.request();
				board.make(move);
				redPlayer.confirm(boardViewer.getStatus());
				bluePlayer.update(move, boardViewer.getStatus());
				output.refresh();

				move = bluePlayer.request();
				board.make(move);
				bluePlayer.confirm(boardViewer.getStatus());
				redPlayer.update(move, boardViewer.getStatus());
				output.refresh();
			}
		} catch (Exception e) {
			System.out.println("Ein Fehler ist aufgetreten:");
			System.out.println(e);
		}
	}
}
