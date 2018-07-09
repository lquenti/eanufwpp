package flowerwarspp.io;

import flowerwarspp.preset.*;

import java.util.Scanner;

public class TextInterface implements Requestable, Output {
	private static final String moveRequestPrompt = "Zug eingeben: ";
	private static final String moveFormatError = "Zug konnte nicht gelesen werden.";

	private Scanner inputScanner = new Scanner(System.in);

	private Viewer viewer = null;

	/**
	 * Liest einen Spielzug vom Standard Input ein.
	 *
	 * @return
	 * Der eingelesene Zug, oder <code>null</code>, wenn der eingegebene Zug invalide war.
	 */
	@Override
	public Move request() {
		try {
			System.out.print(moveRequestPrompt);
			Move result = Move.parseMove(inputScanner.nextLine());
			return result;
		} catch (MoveFormatException e) {
			return null;
		}
	}

	private String fgColor(final PlayerColor color) {
		if (color == null) {
			return "";
		}
		switch(color) {
			case Blue: return AnsiColors.BRIGHT_BLUE;
			case Red: return AnsiColors.BRIGHT_RED;
			default: return "";
		}
	}

	private String bgColor(final PlayerColor color) {
		if (color == null) {
			return "";
		}
		switch(color) {
			case Blue: return AnsiColors.BACKGROUND_BLUE;
			case Red: return AnsiColors.BACKGROUND_RED;
			default: return "";
		}
	}

	private StringBuilder drawTriangle(final Flower flower) {
		StringBuilder triangle = new StringBuilder();
		if (flower.getFirst().getRow() == flower.getSecond().getRow()) {
			triangle.append(fgColor(viewer.getDitchColor(new Ditch (
				flower.getFirst(),
				flower.getThird()
			))));
			triangle.append('/');
			triangle.append(bgColor(viewer.getFlowerColor(flower)));
			triangle.append(fgColor(viewer.getDitchColor(new Ditch (
				flower.getFirst(),
				flower.getSecond()
			))));
			triangle.append('_');
			triangle.append(AnsiColors.RESET);
			triangle.append(fgColor(viewer.getDitchColor(new Ditch (
				flower.getSecond(),
				flower.getThird()
			))));
			triangle.append('\\');
			triangle.append(AnsiColors.RESET);
		} else {
			triangle.append(bgColor(viewer.getFlowerColor(flower)));
			triangle.append(" ");
			triangle.append(AnsiColors.RESET);
		}
		return triangle;
	}

	private StringBuilder drawBoard() {
		int size = viewer.getSize();
		StringBuilder board = new StringBuilder(size * size); // TODO: Kapazität genauer einstellen
		for (int i = -1; i < size; i++) {
			for (int j = 0; j < (size-i-1)*2; j++) {
				board.append(' ');
			}
			board.append(AnsiColors.BRIGHT_BLACK);
			board.append(String.format("%2d", size - i));
			board.append(AnsiColors.RESET);
			board.append(' ');
			for (int j = 1; j <= i+1; j++) {
				board.append(drawTriangle(new Flower (
					new Position(j, size - i),
					new Position(j + 1, size - i),
					new Position(j, size - i + 1)
				)));
				if (j != i+1) {
					board.append(drawTriangle(new Flower (
						new Position(j + 1, size - i + 1),
						new Position(j + 1, size - i),
						new Position(j, size - i + 1)
					)));
				}
			}
			board.append('\n');
		}
		board.append(AnsiColors.BRIGHT_BLACK);
		for (int i = 1; i <= size + 1; i++) {
			board.append(String.format("%3d", i));
			board.append(' ');
		}
		board.append(AnsiColors.RESET);
		board.append('\n');
		return board;
	}

	@Override
	public void refresh() throws IllegalStateException {
		if (viewer == null) {
			throw new IllegalStateException("Viewer wurde noch nicht gesetzt.");
		}

		System.out.println();
		System.out.print(drawBoard());
		System.out.println();
		System.out.println("Rot:  " + viewer.getPoints(PlayerColor.Red) + " Punkte");
		System.out.println("Blau: " + viewer.getPoints(PlayerColor.Blue) + " Punkte");
	}

	@Override
	public void setViewer(Viewer viewer) {
		this.viewer = viewer;
		refresh();
	}
}
