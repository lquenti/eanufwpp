package flowerwarspp.player;

import flowerwarspp.preset.*;

import java.util.Collection;

public class AdvancedAI1 extends BaseAI {
	public AdvancedAI1() {
		super();
	}

	@Override
	protected int getMoveScore( final Move move ) {
		if (move.getType() == MoveType.Surrender || move.getType() == MoveType.End) return -1;

		if (move.getType() == MoveType.Flower) {

			int n1 = 0;
			int n2 = 0;

			int m1 = 0;
			int m2 = 0;

			int score;

			// Obtain the direct neighbors of both flowers.
			final Collection<Flower> firstFlowerNeighbors = boardViewer.getDirectNeighbors(move.getFirstFlower());
			final Collection<Flower> secondFlowerNeighbors = boardViewer.getDirectNeighbors(move.getSecondFlower());

			// Iterate through all the first flower's neighbours and calculate the score.
			for ( final Flower neighbor : firstFlowerNeighbors ) {
				if ( boardViewer.getFlowerColor(neighbor) == getPlayerColour() )
					n1++;

				if (boardViewer.getFlowerColor(neighbor) == (getPlayerColour() == PlayerColor.Red ? PlayerColor.Blue
						: PlayerColor.Red))
					m1++;
			}

			// Iterate through all the second flower's neighbours and calculate the score.
			for ( final Flower neighbor : secondFlowerNeighbors ) {
				if ( boardViewer.getFlowerColor(neighbor) == getPlayerColour() )
					n2++;

				if (boardViewer.getFlowerColor(neighbor) == (getPlayerColour() == PlayerColor.Red ? PlayerColor.Blue
						: PlayerColor.Red))
					m2++;
			}

			// Calculate the score as indicated by the strategy.
			score = 4 * ( n1 + 1 ) * ( n2 + 1 ) - (m1 + 1) * (m2 + 1);

			// If both flowers are attached (i.e. if they're neighbors) double the score.
			if ( firstFlowerNeighbors.contains(move.getSecondFlower()) )
				score *= 2;

			return score;
		} else {
			/* Ditch Moves right here */

			// TODO: Check if given ditch move connects two flower beds. In that case, we'll immediately use this move.
			// For now, just use ditch moves whener possible. Chances are that they improve the score.
			return 1000;
		}
	}


}
