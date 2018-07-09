package flowerwarspp.player;

import flowerwarspp.preset.*;

import java.util.Collection;

public class AdvancedAI1 extends BaseAI {
	private static final int SCORE_DITCH = 1000;

	public AdvancedAI1() {
		super();
	}

	@Override
	protected int getMoveScore( final Move move ) {

		switch ( move.getType() ) {
			case Flower:
				// Obtain the direct neighbors of both flowers.
				final Collection<Flower> firstFlowerNeighbors = boardViewer.getDirectNeighbors(move.getFirstFlower());
				final Collection<Flower> secondFlowerNeighbors = boardViewer.getDirectNeighbors(move.getSecondFlower());

				final int[] s1 = getNeighborScore(firstFlowerNeighbors);
				final int[] s2 = getNeighborScore(secondFlowerNeighbors);

				// Calculate the score as indicated by the strategy.
				int score = 4 * ( s1[0] + 1 ) * ( s2[0] + 1 ) - ( s1[1] + 1 ) * ( s2[1] + 1 );

				// If both flowers are attached (i.e. if they're neighbors) double the score.
				if ( firstFlowerNeighbors.contains(move.getSecondFlower()) )
					score *= 2;

				return score;

			case Ditch:
				// TODO: Actually make some score calculation here. For now using ditch moves whenever possible is fine
				return SCORE_DITCH;

			default:
				return - 1;
		}

	}

	private int[] getNeighborScore( Collection<Flower> flowerNeighbors ) {
		int[] res = new int[2];

		for ( final Flower neighbor : flowerNeighbors ) {
			if ( boardViewer.getFlowerColor(neighbor) == getPlayerColour() )
				res[0]++;

			if ( boardViewer.getFlowerColor(neighbor) == ( getPlayerColour() == PlayerColor.Red ? PlayerColor.Blue
					: PlayerColor.Red ) )
				res[1]++;
		}
		return res;
	}
}
