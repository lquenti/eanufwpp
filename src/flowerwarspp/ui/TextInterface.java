package flowerwarspp.ui;

import flowerwarspp.preset.*;
import flowerwarspp.util.Convert;

import java.util.Scanner;

/**
 * Eine Klasse, die ein Command Line Interface implementiert.
 * Stellt Spielfunktionen mittels ASCII-Art und Texteingabe zur Verfügung.
 */
public class TextInterface implements Requestable, Output {
	/**
	 * Ein vordefinierter String, der nach einem {@link Move} fragt.
	 */
	private static final String moveRequestPrompt = "Zug eingeben: ";

	/**
	 * Eine vordefinierte Nachricht für eine {@link MoveFormatException}, das heißt für
	 * eine Exception die geworfen wird, wenn das Format des Zuges, den der interaktive
	 * Spieler eingegeben hat, ingültig ist.
	 */
	private static final String moveFormatError = "Zug konnte nicht gelesen werden.";

	/**
	 * Eine vordefinierte Nachricht einer {@link Exception}, welche geworfen wird,
	 * wenn der Spieler einen nicht validen Zug angegeben hat.
	 */
	private static final String exception_InvalidMove =
		"Der vom Spieler uebergegebene Zug ist nicht valide.";

	private Viewer viewer = null;

	private Scanner inputScanner = new Scanner(System.in);

	/**
	 * Liest einen Spielzug vom Standard Input ein.
	 *
	 * @return
	 * Der eingelesene Zug, oder <code>null</code>, wenn der eingegebene Zug invalide war.
	 */
	@Override
	public Move request() {
		Move move = null;
		while (move == null) {
			try {
				System.out.print(moveRequestPrompt);
				move = Move.parseMove(inputScanner.nextLine());

				if (!this.viewer.possibleMovesContains(move)) {
					System.out.println(exception_InvalidMove);
					move = null;
				}
			} catch (MoveFormatException e) {
				return null;
			}
		}

			return move;
	}

	/**
	 * Akquiriert die Vordergrundfarbe für eine {@link PlayerColor}.
	 *
	 * @param color
	 * Die {@link PlayerColor}, für die der Farbstring zurückgegeben werden soll.
	 *
	 * @return
	 * Der zur {@link PlayerColor} gehörende Vordergrundfarbe.
	 */
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

	/**
	 * Akquiriert die Hintergrundfarbe für eine {@link PlayerColor}.
	 *
	 * @param color
	 * Die {@link PlayerColor}, für die der Farbstring zurückgegeben werden soll.
	 *
	 * @return
	 * Der zur {@link PlayerColor} gehörende Hintergrundfarbe.
	 */
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

	/**
	 * Erstellt ein Dreieck aus ASCII-Zeichen, gegebenenfalls mit einer Farbe.
	 *
	 * @param flower
	 * Die zu zeichnende {@link Flower}.
	 *
	 * @return
	 * Ein {@link StringBuilder}, der das Dreieck enthält.
	 */
	private StringBuilder drawTriangle(final Flower flower) {
		StringBuilder triangle = new StringBuilder();
		if (flower.getFirst().getRow() == flower.getSecond().getRow()) {
			triangle.append(fgColor(viewer.getDitchColor(new Ditch (
				flower.getFirst(),
				flower.getThird()
			))));
			triangle.append('/');
			triangle.append(AnsiColors.RESET);
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

	/**
	 * Zeichnet das {@link Board} aus ASCII-Art.
	 *
	 * @return
	 * Einen {@link StringBuilder}, der eine Textdarstellung des Spielbretts enthält.
	 */
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

		if (viewer.getStatus() != Status.Ok) {
			System.out.println();
			System.out.println(Convert.statusToText(viewer.getStatus()));
		}
	}

	@Override
	public void setViewer(Viewer viewer) {
		this.viewer = viewer;
		refresh();
	}
}
