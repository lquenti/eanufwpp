package flowerwarspp.player;

import flowerwarspp.board.MainBoard;
import flowerwarspp.preset.Flower;
import flowerwarspp.preset.Move;
import flowerwarspp.preset.PlayerColor;

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
	protected int getMoveScore(Move move) {

		switch (move.getType()) {
			case Flower:
				// Die direkten Nachbarn der ersten Zugblume aus dem Viewer abrufen. Diese werden benötigt, um zu
				// überprüfen, ob die beiden zu setzenden Blumen nebeneinander liegen und um den Score auf Basis der
				// Nachbarn zu berechnen.
				final Collection<Flower> firstFlowerNeighbors = boardViewer.getDirectNeighbors(move.getFirstFlower());
				final Collection<Flower> secondFlowerNeighbors = boardViewer.getDirectNeighbors(move.getSecondFlower());

				// Die Nachbarwertung für zusammenhängende Beete und Cluster berechnen.
				final int[] s1 = getNeighborScore(firstFlowerNeighbors);
				final int[] s2 = getNeighborScore(secondFlowerNeighbors);

				// Züge, die Beete oder Gärten schaffen, werden priorisiert, Züge die in der Nähe des Gegners liegen
				// werden negativ bewertet.
				int score = 4 * ( s1[0] + 1 ) * ( s2[0] + 1 ) - ( s1[1] + 1 ) * ( s2[1] + 1 );

				// Falls die beiden zu setzenden Blumen nebeneinander liegen, soll der Score verdoppelt werden.
				// So werden Züge mit einzeln gesetzten Blumen immer nur dann gemacht, wenn es nicht anders geht.
				if (firstFlowerNeighbors.contains(move.getSecondFlower()))
					score *= 2;

				return score;

			case Ditch:
				// Simulieren des Ditch-Zugs auf einem mit dem Copy-Konstruktor erstellten Spielbrett.
				// Falls der Ditch-Move die Punktezahl erhöht, wird er sofort ausgewählt.
				// Andernfalls haben Ditch-Züge eine Bewertung von 0, sodass sie erst ausgeführt werden, wenn keine
				// Blumen mehr gesetzt werden können.
				MainBoard sim = new MainBoard((MainBoard) getBoard());
				sim.make(move);

				if (sim.viewer().getPoints(getPlayerColour()) > boardViewer.getPoints(getPlayerColour()))
					return SCORE_DITCH;
				else
					return 0;

			case End:
				// Falls dieser Spieler weniger Punkte hat als sein Gegner (also durch Beenden des Spiels verlieren
				// würde) wird der End-Zug nicht ausgeführt (stattdessen werden zufällig Ditches gesetzt, in der
				// Hoffnung, dass dadurch Gärten über Beete verbunden werden).
				// Würde dieser Spieler durch Beenden des Spiels jedoch gewinnen, tut er dies sofort.
				if (boardViewer.getPoints(getPlayerColour()) > boardViewer.getPoints(( getPlayerColour() == PlayerColor
						.Red ) ? PlayerColor.Blue : PlayerColor.Red))
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
	private int[] getNeighborScore(Collection<Flower> flowerNeighbors) {
		// Der Rückgabewert ist ein Array der Größe 2.
		int[] res = new int[] {0,0};

		// Durch die direkten Nachbarn der betrachteten Blume iterieren und das erste Element des Rückgabearrays immer
		// dann inkrementieren, wenn einer dieser direkten Nachbarn der eigenen Farbe gehört.
		// So werden Beete und Gärten gebildet.
		// Nachbarn, welche dem Gegner gehören, inkremementieren das zweite Element des Arrays.
		for (Flower neighbor : flowerNeighbors) {
			if (boardViewer.getFlowerColor(neighbor) == getPlayerColour())
				res[0]++;

			if (boardViewer.getFlowerColor(neighbor) == ( getPlayerColour() == PlayerColor.Red ? PlayerColor.Blue
					: PlayerColor.Red ))
				res[1]++;
		}
		return res;
	}
}
