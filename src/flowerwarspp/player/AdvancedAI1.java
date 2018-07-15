package flowerwarspp.player;

import flowerwarspp.board.MainBoard;
import flowerwarspp.preset.Flower;
import flowerwarspp.preset.Move;
import flowerwarspp.preset.PlayerColor;

import java.util.Collection;

/**
 * Diese Klasse stellt einen Computerspieler mit verbesserter Strategie (Level 1) zur Verfügung.
 * <p>
 * Der zu spielende Zug wird auf Basis eines angepassten Bewertungsalgorithmus des simplen
 * Computerspielers ausgewählt.
 */
public class AdvancedAI1 extends AbstractAI {

	/**
	 * Beschreibt den Faktor, mit welchem die Bewertung von Zügen, die zusammenhängende Beete
	 * bilden, multipliziert werden soll (erstes Element des Rückgabearrays der Methode {@link
	 * #getNeighborScore(Collection)}).
	 */
	private static final int gardenMultiplier = 8;
	/**
	 * Beschreibt den Faktor, mit welchem die Bewertung von Zügen, die Blumen in der Nähe des
	 * Gegners anordnen, multipliziert werden soll (zweites Element des Rückgabearrays der Methode
	 * {@link #getNeighborScore(Collection)}.
	 */
	private static final int notOwnedFlowerMultiplier = 1;
	/**
	 * Beschreibt den Faktor, mit welchem Züge, die Blumen in Paaren anordnen, multipliziert werden
	 * sollen.
	 */
	private static final int flowerPairMultiplier = 2;

	/**
	 * Konstruktor, um eine neue Instanz dieser Klasse zu erstellen.
	 */
	public AdvancedAI1() {
		super();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Der Bewertungsalgorithmus ist eine angepasste Variante des Algorithmus von {@link SimpleAI}.
	 * Ditch-Moves werden immer zuerst genommen, da diese potentiell mehr Punkte bringen.
	 */
	@Override
	protected int getMoveScore(Move move) {

		switch (move.getType()) {
			case Flower:
				/*
				 * Die direkten Nachbarn der ersten Zugblume aus dem Viewer abrufen. Diese werden
				 * benötigt, um zu
				 * überprüfen, ob die beiden zu setzenden Blumen nebeneinander liegen und um den
				 * Score auf Basis der
				 * Nachbarn zu berechnen.
				 */
				final Collection<Flower> firstFlowerNeighbors =
						boardViewer.getDirectNeighbors(move.getFirstFlower());
				final Collection<Flower> secondFlowerNeighbors =
						boardViewer.getDirectNeighbors(move.getSecondFlower());

				// Die Nachbarwertung für zusammenhängende Beete und Cluster berechnen.
				final ScoreResults s1 = getNeighborScore(firstFlowerNeighbors);
				final ScoreResults s2 = getNeighborScore(secondFlowerNeighbors);

				/*
				 * Züge, die Beete oder Gärten schaffen, werden priorisiert, Züge die in der Nähe
				 * des Gegners liegen
				 * werden negativ bewertet.
				 */
				int score = gardenMultiplier * (s1.gardenScore + 1) * (s2.gardenScore + 1) +
						notOwnedFlowerMultiplier * (s1.notOwnedFlowerScore + 1) *
								(s2.notOwnedFlowerScore + 1);

				/*
				 * Falls die beiden zu setzenden Blumen nebeneinander liegen, soll der Score
				 * verdoppelt werden.
				 * So werden Züge mit einzeln gesetzten Blumen immer nur dann gemacht, wenn es
				 * nicht anders geht.
				 */
				if (firstFlowerNeighbors.contains(move.getSecondFlower())) {
					score *= flowerPairMultiplier;
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
	 * läuft für beide Nachbarn gleich, deshalb wurde sie in diese Methode ausgelagert.
	 *
	 * @param flowerNeighbors
	 * 		Die Nachbarn einer Blume, dessen Score berechnet werden soll
	 *
	 * @return Der Score basierend auf den Nachbarn einer Blume
	 */
	private ScoreResults getNeighborScore(Collection<Flower> flowerNeighbors) {
		// Der Rückgabewert ist ein ScoreResults Objekt.
		ScoreResults result = new ScoreResults();

		/*
		 * Durch die direkten Nachbarn der betrachteten Blume iterieren und gardenScore immer
		 * genau dann
		 * inkrementieren, wenn einer dieser direkten Nachbarn der eigenen Farbe gehört.
		 * So werden Beete und Gärten gebildet.
		 * Nachbarn, welche keinem Spieler gehören, inkremementieren notOwnedFlowerScore.
		 */
		for (Flower neighbor : flowerNeighbors) {
			if (boardViewer.getFlowerColor(neighbor) == getPlayerColor()) {
				result.gardenScore++;
			} else if (boardViewer.getFlowerColor(neighbor) == null) {
				result.notOwnedFlowerScore++;
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
	}
}
