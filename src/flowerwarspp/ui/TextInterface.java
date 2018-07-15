package flowerwarspp.ui;

import flowerwarspp.main.savegame.SaveGame;
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
	private static final String moveFormatErrorMessage = "Zug konnte nicht gelesen werden.";

	/**
	 * Eine vordefinierte Nachricht einer {@link Exception}, welche geworfen wird,
	 * wenn der Spieler einen nicht validen Zug angegeben hat.
	 */
	private static final String invalidMoveMessage = "Der eingegebene Zug ist nicht erlaubt.";

	/**
	 * Der {@link Viewer}, durch den der {@link Output} auf das {@link Board} schaut.
	 */
	private Viewer viewer = null;

	/**
	 * Der {@link Scanner}, der Eingaben von der Texteingabe einliest.
	 */
	private Scanner inputScanner = new Scanner(System.in);

	/**
	 * Das {@link SaveGame} des Spiels, das auf diesem {@link Output} gespielt wird.
	 */
	private SaveGame saveGame;

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
					System.out.println(invalidMoveMessage);
					move = null;
				}
			} catch (MoveFormatException e) {
				System.out.println(moveFormatErrorMessage);
				move = null;
			}
		}

		return move;
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
	private StringBuilder drawTriangle(Flower flower) {
		StringBuilder triangle = new StringBuilder();
		if (flower.getFirst().getRow() == flower.getSecond().getRow()) {
			Ditch leftDitch = new Ditch(flower.getFirst(), flower.getThird());
			triangle.append(GameColors.getAnsiDitchColor(viewer.getDitchColor(leftDitch)));
			triangle.append('/');
			triangle.append(GameColors.ANSI_RESET);

			triangle.append(GameColors.getAnsiFlowerColor(viewer.getFlowerColor(flower)));
			Ditch bottomDitch = new Ditch(flower.getFirst(), flower.getSecond());
			triangle.append(GameColors.getAnsiDitchColor(viewer.getDitchColor(bottomDitch)));
			triangle.append('_');
			triangle.append(GameColors.ANSI_RESET);

			Ditch rightDitch = new Ditch(flower.getSecond(), flower.getThird());
			triangle.append(GameColors.getAnsiDitchColor(viewer.getDitchColor(rightDitch)));
			triangle.append('\\');
			triangle.append(GameColors.ANSI_RESET);
		} else {
			triangle.append(GameColors.getAnsiFlowerColor(viewer.getFlowerColor(flower)));
			triangle.append(" ");
			triangle.append(GameColors.ANSI_RESET);
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
			board.append(GameColors.ANSI_GRID);
			board.append(String.format("%2d", size - i));
			board.append(GameColors.ANSI_RESET);
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
		board.append(GameColors.ANSI_GRID);
		for (int i = 1; i <= size + 1; i++) {
			board.append(String.format("%3d", i));
			board.append(' ');
		}
		board.append(GameColors.ANSI_RESET);
		board.append('\n');
		return board;
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setViewer(Viewer viewer) {
		this.viewer = viewer;
		refresh();
	}

	/**
	 * {@inheritDoc} Diese Implementation tut nichts, da diese Ausgabe das Speichern von
	 * Spielständen nicht unterstützt.
	 */
	@Override
	public void setSaveGame(SaveGame saveGame) {}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showEndMessage(String message) {
		System.out.println(message);
	}
}
