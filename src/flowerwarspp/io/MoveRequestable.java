package flowerwarspp.io;

import flowerwarspp.preset.Move;
import flowerwarspp.preset.MoveFormatException;
import flowerwarspp.preset.Requestable;

import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class MoveRequestable implements Requestable
{
	private static final String moveRequestPrompt =
			"Please enter a move.";
	private static final String noMoveEnteredError =
			"No line was found.";

	// TODO: Find out what a "sensible reaction" means for the MoveFormatEx.
	@Override
	public Move request() throws Exception
	{
		try (Scanner inputScanner = new Scanner(System.in,
												StandardCharsets.UTF_8.name()))
		{
			System.out.println(moveRequestPrompt);
			String moveString = inputScanner.nextLine();
			Move parsedMove = Move.parseMove(moveString);
			return parsedMove;
		}
		catch (NoSuchElementException | MoveFormatException e)
		{
			return null;
		}
	}
}
