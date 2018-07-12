package flowerwarspp.player;

import flowerwarspp.board.MainBoard;
import flowerwarspp.preset.Flower;
import flowerwarspp.preset.Move;
import flowerwarspp.preset.PlayerColor;

import java.util.Collection;

/**
 * Diese Klasse stellt einen Computerspieler mit verbesserter Strategie (Level 2) zur Verfügung.
 * <p>
 * Der zu spielende Zug wird auf Basis eines angepassten Bewertungsalgorithmus des verbesserten Computerspielers (Level
 * 1) ausgewählt.
 *
 * @author Michael Merse
 */
public class AdvancedAI2 extends BaseAI {

	/**
	 * Der Zug, ab welchem Cluster gebildet werden sollen.
	 */
	private int startClusteringAt;

	/**
	 * Die Anzahl an Zügen, die dieser Spieler ausgeführt hat, seid Beginn des Spiels.
	 */
	private int moveNr = 0;

	/**
	 * Konstruktor, um eine neue Instanz dieser Klasse zu erstellen.
	 */
	public AdvancedAI2() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Move requestMove() throws Exception {

		if (moveNr <= startClusteringAt)
			moveNr++;

		return super.requestMove();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(int boardSize, PlayerColor playerColour) throws Exception {
		super.init(boardSize, playerColour);
		startClusteringAt = ( boardSize * boardSize ) / 20;
		moveNr = 0;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Der Bewertungsalgorithmus ist eine angepasste Variante des Algorithmus von {@link AdvancedAI2}. Ditch-Moves, die
	 * den Score verbessern, werden immer zuerst genommen. Es wird außerdem versucht, die eigenen Blumen/Gärten in
	 * Clustern anzulegen, sodass möglichst lange kontinuierliche Verbindungen zwischen Gärten entstehen.
	 */
	@Override
	protected int getMoveScore(final Move move) {
		switch (move.getType()) {
			case Flower:
				// Obtain the direct neighbors of both flowers.
				final Collection<Flower> firstFlowerNeighbors = boardViewer.getDirectNeighbors(move.getFirstFlower());

				final int[] s1 = getNeighborScore(move.getFirstFlower());
				final int[] s2 = getNeighborScore(move.getSecondFlower());

				// Calculate the score as indicated by the strategy.
				int score = 20 * ( s1[0] + 1 ) * ( s2[0] + 1 ) + ( s1[1] + 1 ) * ( s2[1] + 1 );


				// If both flowers are attached (i.e. if they're neighbors) double the score.
				if (firstFlowerNeighbors.contains(move.getSecondFlower())) {
					score += 15;
				}

				return score;

			case Ditch:
				// Simulate and check if ditch actually increases our points.
				// This way we won't make duplicate/useless ditch moves.
				// Works perfectly performance-wise, unwanted delay is close to none.
				MainBoard sim = new MainBoard((MainBoard) getBoard());
				sim.make(move);

				if (sim.viewer().getPoints(getPlayerColour()) > boardViewer.getPoints(getPlayerColour()))
					return SCORE_DITCH;
				else
					return 0;

			case End:
				if (boardViewer.getPoints(getPlayerColour()) > boardViewer.getPoints(( getPlayerColour() == PlayerColor
						.Red ) ? PlayerColor.Blue : PlayerColor.Red))
					return SCORE_END;

			case Surrender:
			default:
				return - 1;
		}
	}

	/**
	 * Berechnet den Score für die Nachbarblumen beider Blumen eines Flower-Moves. Die Berechnung läuft für beide Blumen
	 * gleich, deshalb wurde sie in diese Methode ausgelagert.
	 *
	 * @param flower Die Blume, dessen Score berechnet werden soll
	 * @return Der Score basierend auf den Nachbarn einer Blume
	 */
	private int[] getNeighborScore(Flower flower) {
		int[] res = new int[2];
		for (final Flower neighbor : boardViewer.getDirectNeighbors(flower)) {
			if (boardViewer.getFlowerColor(neighbor) == getPlayerColour())
				res[0]++;
		}

		if (res[0] > 0 || moveNr <= startClusteringAt) return res;

		for (final Flower neighbor : boardViewer.getAllNeighbors(flower)) {

			if (boardViewer.getFlowerColor(neighbor) == getPlayerColour()) {
				res[1] = - 2;
				return res;
			}

			for (Flower f1 : boardViewer.getDirectNeighbors(neighbor)) {
				if (boardViewer.getFlowerColor(f1) == getPlayerColour())
					res[1]++;
			}
		}
		return res;
	}
}
