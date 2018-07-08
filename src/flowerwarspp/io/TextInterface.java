package flowerwarspp.io;

import flowerwarspp.preset.Move;
import flowerwarspp.preset.MoveFormatException;
import flowerwarspp.preset.Requestable;

import java.util.Scanner;

public class TextInterface implements Requestable {
	private static final String moveRequestPrompt = "Zug eingeben: ";
	private static final String moveFormatError = "Zug konnte nicht gelesen werden.";

	private Scanner inputScanner = new Scanner(System.in);

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
}
