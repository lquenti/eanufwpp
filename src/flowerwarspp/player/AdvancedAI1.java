package flowerwarspp.player;

import flowerwarspp.board.MainBoard;
import flowerwarspp.preset.*;

import java.util.Collection;

/**
 * Diese Klasse stellt einen Computerspieler mit verbesserter Strategie (Level 1) zur Verfügung.
 * <p>
 * Der zu spielende Zug wird auf Basis eines angepassten Bewertungsalgorithmus des simplen Computerspielers ausgewählt.
 *
 * @author Michael Merse
 */
public class AdvancedAI1 extends BaseAI {

	/**
	 * Konstruktor, um eine neue Instanz dieser Klasse zu erstellen.
	 */
	public AdvancedAI1() {
		super();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Der Bewertungsalgorithmus ist eine angepasste Variante des Algorithmus von {@link SimpleAI}. Ditch-Moves werden
	 * immer zuerst genommen, da diese potentiell mehr Punkte bringen.
	 */
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
				// Simulate and check if ditch actually increases our points.
				// This way we won't make duplicate/useless ditch moves.
				// Works perfectly performance-wise, unwanted delay is close to none.
				MainBoard sim = new MainBoard((MainBoard) getBoard());
				sim.make(move);

				if ( sim.viewer().getPoints(getPlayerColour()) > boardViewer.getPoints(getPlayerColour()) )
					return SCORE_DITCH;
				else
					return 0;

			case End:
				return SCORE_END;

			case Surrender:
			default:
				return - 1;
		}
	}

	/**
	 * Berechnet den Score für die Nachbarblumen beider Blumen eines Flower-Moves. Die Berechnung läuft für beide
	 * Nachbarn glech, deshalb wurde sie in diese Methode ausgelagert.
	 *
	 * @param flowerNeighbors Die Nachbarn einer Blume, dessen Score berechnet werden soll
	 * @return Der Score basierend auf den Nachbarn einer Blume
	 */
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
