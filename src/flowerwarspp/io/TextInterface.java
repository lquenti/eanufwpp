package flowerwarspp.io;

import flowerwarspp.preset.Move;
import flowerwarspp.preset.MoveFormatException;
import flowerwarspp.preset.Requestable;

import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class TextInterface implements Requestable {
	private static final String moveRequestPrompt = "Zug eingeben: ";
	private static final String moveFormatError = "Zug konnte nicht gelesen werden.";

	/**
	 * Liest einen Spielzug von der Standardeingabe ein. Der Nutzer wird solange aufgefordert,
	 * einen Zug einzugeben, bis er einen gültigen Zug eingibt.
	 * @return Der eingelesene Zug
	 */
	@Override
	public Move request() {
		Scanner inputScanner = new Scanner(System.in, StandardCharsets.UTF_8.name());
		Move result = null;
		while (result == null) {
			try {
				System.out.print(moveRequestPrompt);
				result = Move.parseMove(inputScanner.nextLine());
			} catch (NoSuchElementException | MoveFormatException e) {
				System.out.println(moveFormatError);
			}
		}
		inputScanner.close();
		return result;
	}
}
