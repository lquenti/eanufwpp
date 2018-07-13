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
	public void init(int boardSize, PlayerColor playerColor) throws Exception {
		super.init(boardSize, playerColor);

		// Berechnen des Startpunkts fürs Clustern. Falls das Spielbrett bereits bespielt worden ist (zum Beispiel nach
		// dem Laden) wird sofort geclustert.
		if (!boardViewer.getFlowers(getPlayerColor()).isEmpty())
			startClusteringAt = 0;
		else
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
	protected int getMoveScore(Move move) {
		switch (move.getType()) {
			case Flower:
				// Die direkten Nachbarn der ersten Zugblume aus dem Viewer abrufen. Diese werden benötigt, um zu
				// überprüfen, ob die beiden zu setzenden Blumen nebeneinander liegen.
				final Collection<Flower> firstFlowerNeighbors = boardViewer.getDirectNeighbors(move.getFirstFlower());

				// Die Nachbarwertung für zusammenhängende Beete und Cluster berechnen.
				final int[] s1 = getNeighborScore(move.getFirstFlower());
				final int[] s2 = getNeighborScore(move.getSecondFlower());

				// Züge, die Beete oder Gärten schaffen, werden priorisiert, Züge die Cluster bilden haben geringere
				// Priorität.
				int score = 20 * ( s1[0] + 1 ) * ( s2[0] + 1 ) + ( s1[1] + 1 ) * ( s2[1] + 1 );

				// Falls die beiden zu setzenden Blumen nebeneinander liegen, soll der Score verdoppelt werden.
				// So werden Züge mit einzeln gesetzten Blumen immer nur dann gemacht, wenn es nicht anders geht.
				if (firstFlowerNeighbors.contains(move.getSecondFlower())) {
					score += 15;
				}

				return score;

			case Ditch:
				// Simulieren des Ditch-Zugs auf einem mit dem Copy-Konstruktor erstellten Spielbrett.
				// Falls der Ditch-Move die Punktezahl erhöht, wird er sofort ausgewählt.
				// Andernfalls haben Ditch-Züge eine Bewertung von 0, sodass sie erst ausgeführt werden, wenn keine
				// Blumen mehr gesetzt werden können.
				MainBoard sim = new MainBoard((MainBoard) getBoard());
				sim.make(move);

				if (sim.viewer().getPoints(getPlayerColor()) > boardViewer.getPoints(getPlayerColor()))
					return SCORE_DITCH;
				else
					return 0;

			case End:
				// Falls dieser Spieler weniger Punkte hat als sein Gegner (also durch Beenden des Spiels verlieren
				// würde) wird der End-Zug nicht ausgeführt (stattdessen werden zufällig Ditches gesetzt, in der
				// Hoffnung, dass dadurch Gärten über Beete verbunden werden).
				// Würde dieser Spieler durch Beenden des Spiels jedoch gewinnen, tut er dies sofort.
				if (boardViewer.getPoints(getPlayerColor()) > boardViewer.getPoints(( getPlayerColor() == PlayerColor
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
	 * @return Der Score basierend auf den Nachbarn einer Blume. Das erste Element des Arrays beschreibt den Score für
	 * zusammenhängende Blumen, das zweite Element beschreibt den Score für das Clustering.
	 */
	private int[] getNeighborScore(Flower flower) {
		// Der Rückgabewert ist ein Array der Größe 2.
		int[] res = new int[]{ 0, 0 };

		// Durch die direkten Nachbarn der betrachteten Blume iterieren und das erste Element des Rückgabearrays immer
		// dann inkrementieren, wenn einer dieser direkten Nachbarn der eigenen Farbe gehört.
		// So werden Beete und Gärten gebildet.
		for (Flower neighbor : boardViewer.getDirectNeighbors(flower)) {
			if (boardViewer.getFlowerColor(neighbor) == getPlayerColor())
				res[0]++;
		}

		// Falls ein Zug, welcher ein bestehendes Blumenbeet erweitert, gemacht werden kann, oder falls Beete noch nicht
		// in Clustern angeordnet werden sollen, wird hier die Methode verlassen.
		if (res[0] > 0 || moveNr <= startClusteringAt) return res;

		// Durch alle Nachbarn der zu betrachtenden Blume iterieren und das zweite Element des Rückgabearrays immer dann
		// inkrementieren, wenn einer der direkten Nachbarn des aktuell betrachteten Nachbars schon diesem Spieler
		// gehört.
		// Ist bei den Nachbarn der betrachteten Blume flower eine Blume dabei, welche diesem Spieler bereits gehört,
		// dann wird die Methode sofort verlassen.
		// Dadurch werden Blumen in der Nähe von bestehenden Beeten gesetzt, sodass sie sich mit Ditches verbinden
		// lassen.
		for (Flower neighbor : boardViewer.getAllNeighbors(flower)) {

			if (boardViewer.getFlowerColor(neighbor) == getPlayerColor()) {
				res[1] = - 2;
				return res;
			}

			for (Flower f1 : boardViewer.getDirectNeighbors(neighbor)) {
				if (boardViewer.getFlowerColor(f1) == getPlayerColor())
					res[1]++;
			}
		}
		return res;
	}
}
