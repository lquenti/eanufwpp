package flowerwarspp.player;

import flowerwarspp.board.MainBoard;
import flowerwarspp.preset.Flower;
import flowerwarspp.preset.Move;
import flowerwarspp.preset.PlayerColor;

import java.util.Collection;

/**
 * Diese Klasse stellt einen Computerspieler mit verbesserter Strategie (Level 2) zur Verfügung.
 * <p>
 * Der zu spielende Zug wird auf Basis eines angepassten Bewertungsalgorithmus des verbesserten
 * Computerspielers (Level 1) ausgewählt.
 */
public class AdvancedAI2 extends AbstractAI {

	/**
	 * Beschreibt den zur Berechnung der Nummer des Zugs ab welchem Cluster bebildet werden sollen
	 * benutzten Divisor der {@link #init(int, PlayerColor)}-Methode.
	 */
	private static final int clusterStartDivisor = 15;
	/**
	 * Beschreibt den Faktor, mit welchem die Bewertung von Zügen die zusammenhängende Beete bilden
	 * multipliziert werden soll (erstes Element des Rückgabearrays der Methode {@link
	 * #getNeighborScore(Flower)}).
	 */
	private static final int gardenMultiplier = 20;
	/**
	 * Beschreibt den Faktor, mit welchem die Bewertung von Zügen, die Blumen in der Nähe des
	 * Gegners anordnen, multipliziert werden soll (zweites Element des Rückgabearrays der Methode
	 * {@link #getNeighborScore(Flower)}.
	 */
	private static final int notOwnedFlowerMultiplier = 1;
	/**
	 * Beschreibt den Faktor, mit welchem die Bewertung von Zügen die Blumen(-beete) in Clustern
	 * anordnen multipliziert werden soll (zweites Element des Rückgabearrays der Methode {@link
	 * #getNeighborScore(Flower)}.
	 */
	private static final int clusterMultiplier = 1;
	/**
	 * Beschreibt die Konstante, welche auf Züge die Blumen in Paaren anordnen aufaddiert werden
	 * soll.
	 */
	private static final int flowerPairSummand = 15;
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

		if (moveNr <= startClusteringAt) {
			moveNr++;
		}

		return super.requestMove();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(int boardSize, PlayerColor playerColor) throws Exception {
		super.init(boardSize, playerColor);

		/*
		 * Berechnen des Startpunkts fürs Clustern. Falls das Spielbrett bereits bespielt worden
		 * ist (zum Beispiel nach
		 * dem Laden) wird sofort geclustert.
		 */
		if (!boardViewer.getFlowers(getPlayerColor()).isEmpty()) {
			startClusteringAt = 0;
		} else {
			startClusteringAt = (boardSize * boardSize) / clusterStartDivisor;
		}

		moveNr = 0;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Der Bewertungsalgorithmus ist eine angepasste Variante des Algorithmus von {@link
	 * AdvancedAI2}. Ditch-Moves, die den Score verbessern, werden immer zuerst genommen. Es wird
	 * außerdem versucht, die eigenen Blumen/Gärten in Clustern anzulegen, sodass möglichst lange
	 * kontinuierliche Verbindungen zwischen Gärten entstehen.
	 */
	@Override
	protected int getMoveScore(Move move) {
		switch (move.getType()) {
			case Flower:
				/*
				 * Die direkten Nachbarn der ersten Zugblume aus dem Viewer abrufen. Diese werden
				 * benötigt, um zu
				 * überprüfen, ob die beiden zu setzenden Blumen nebeneinander liegen.
				 */
				final Collection<Flower> firstFlowerNeighbors =
						boardViewer.getDirectNeighbors(move.getFirstFlower());

				// Die Nachbarwertung für zusammenhängende Beete und Cluster berechnen.
				final ScoreResults s1 = getNeighborScore(move.getFirstFlower());
				final ScoreResults s2 = getNeighborScore(move.getSecondFlower());

				/*
				 * Züge, die Beete oder Gärten schaffen, werden priorisiert, Züge die Cluster
				 * bilden haben geringere
				 * Priorität.
				 */
				int score = gardenMultiplier * (s1.gardenScore + 1) * (s2.gardenScore + 1) +
						notOwnedFlowerMultiplier * (s1.notOwnedFlowerScore + 1) *
								(s2.notOwnedFlowerScore + 1) +
						clusterMultiplier * (s1.clusterScore + 1) * (s2.clusterScore + 1);

				/*
				 * Falls die beiden zu setzenden Blumen nebeneinander liegen, soll der Score
				 * verdoppelt werden.
				 * So werden Züge mit einzeln gesetzten Blumen immer nur dann gemacht, wenn es
				 * nicht anders geht.
				 */
				if (firstFlowerNeighbors.contains(move.getSecondFlower())) {
					score += flowerPairSummand;
				}

				return score;

			case Ditch:
				/*
				 * Simulieren des Ditch-Zugs auf einem mit dem Copy-Konstruktor erstellten
				 * Spielbrett.
				 * Falls der Ditch-Move die Punktezahl erhöht, wird er sofort ausgewählt.
				 * Andernfalls haben Ditch-Züge eine Bewertung von 0, sodass sie erst ausgeführt
				 * werden, wenn keine
				 * Blumen mehr gesetzt werden können.
				 */
				MainBoard sim = new MainBoard((MainBoard) getBoard());
				sim.make(move);

				if (sim.viewer().getPoints(getPlayerColor()) >
						boardViewer.getPoints(getPlayerColor())) {
					return SCORE_DITCH;
				} else {
					return 0;
				}

			case End:
				/*
				 * Falls dieser Spieler weniger Punkte hat als sein Gegner (also durch Beenden des
				 * Spiels verlieren
				 * würde) wird der End-Zug nicht ausgeführt (stattdessen werden zufällig Ditches
				 * gesetzt, in der
				 * Hoffnung, dass dadurch Gärten über Beete verbunden werden).
				 * Würde dieser Spieler durch Beenden des Spiels jedoch gewinnen, tut er dies
				 * sofort.
				 */
				if (boardViewer.getPoints(getPlayerColor()) > boardViewer.getPoints(
						(getPlayerColor() == PlayerColor.Red) ? PlayerColor.Blue :
								PlayerColor.Red)) {
					return SCORE_END;
				}

			case Surrender:
			default:
				return -1;
		}
	}

	/**
	 * Berechnet den Score für die Nachbarblumen beider Blumen eines Flower-Moves. Die Berechnung
	 * läuft für beide Blumen gleich, deshalb wurde sie in diese Methode ausgelagert.
	 *
	 * @param flower
	 * 		Die Blume, dessen Score berechnet werden soll
	 *
	 * @return Der Score basierend auf den Nachbarn einer Blume. Das erste Element des Arrays
	 * beschreibt den Score für zusammenhängende Blumen, das zweite Element beschreibt den Score
	 * für Blumen, die keinem Spieler gehören und das dritte Element beschreibt den Score für das
	 * Clustering.
	 */
	private ScoreResults getNeighborScore(Flower flower) {
		// Der Rückgabewert ist ein Objekt der Klasee ScoreResults.
		ScoreResults result = new ScoreResults();

		/*
		 * Durch die direkten Nachbarn der betrachteten Blume iterieren und gardenScore immer dann
		  * inkrementieren,
		 * wenn einer dieser direkten Nachbarn der eigenen Farbe gehört.
		 * So werden Beete und Gärten gebildet.
		 * Außerdem wird notOwnedFlowerScore immer dann inkrementiert, wenn der betrachtete
		 * Nachbar noch keinem
		 * Spieler gehört.
		 */
		for (Flower neighbor : boardViewer.getDirectNeighbors(flower)) {
			if (boardViewer.getFlowerColor(neighbor) == getPlayerColor()) {
				result.gardenScore++;
			} else if (boardViewer.getFlowerColor(neighbor) == null) {
				result.notOwnedFlowerScore++;
			}
		}

		/*
		 * Falls ein Zug, welcher ein bestehendes Blumenbeet erweitert, gemacht werden kann, oder
		 * falls Beete noch nicht
		 * in Clustern angeordnet werden sollen, wird hier die Methode verlassen.
		 */
		if (result.gardenScore > 0 || moveNr <= startClusteringAt) {
			return result;
		}

		/*
		 * Durch alle Nachbarn der zu betrachtenden Blume iterieren und clusterScore immer dann
		 * inkrementieren, wenn
		 * einer der direkten Nachbarn des aktuell betrachteten Nachbars schon diesem Spieler
		 * gehört.
		 * Ist bei den Nachbarn der betrachteten Blume flower eine Blume dabei, welche diesem
		 * Spieler bereits gehört,
		 * dann wird die Methode sofort verlassen.
		 * Dadurch werden Blumen in der Nähe von bestehenden Beeten gesetzt, sodass sie sich mit
		 * Ditches verbinden
		 * lassen.
		 */
		for (Flower neighbor : boardViewer.getAllNeighbors(flower)) {

			if (boardViewer.getFlowerColor(neighbor) == getPlayerColor()) {
				result.clusterScore = -2;
				return result;
			}

			for (Flower f1 : boardViewer.getDirectNeighbors(neighbor)) {
				if (boardViewer.getFlowerColor(f1) == getPlayerColor()) {
					result.clusterScore++;
				}
			}
		}
		return result;
	}

	/**
	 * Daten-Struct zum Speichern und Verarbeiten von Zugbewertungen. Da nur Daten transportiert
	 * werden, und keine Operationen in Form von Methoden von dieser Klasse bereitgestellt werden,
	 * sind die Instanzvariablen der Klasse <code>package-private</code>.
	 */
	private class ScoreResults {
		/**
		 * Bewertet das Bilden von zusammenhängenden Blumenstrukturen (also von Beeten und Gärten).
		 */
		int gardenScore = 0;

		/**
		 * Bewertet das Setzen von Zügen in der Nähe von Blumen die keinem Spieler gehören.
		 */
		int notOwnedFlowerScore = 0;

		/**
		 * Bewertet das Setzen von Blumen in direkter Nähe zu bestehenden Blumen.
		 */
		int clusterScore = 0;
	}
}
