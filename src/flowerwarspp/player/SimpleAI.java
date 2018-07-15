package flowerwarspp.player;

import flowerwarspp.preset.Flower;
import flowerwarspp.preset.Move;
import flowerwarspp.preset.MoveType;

import java.util.Collection;

/**
 * Implementiert die abstrakte Klasse {@link AbstractPlayer} mit einer simplen KI. Diese KI bedient sich einer
 * limitierten Bewertungsstrategie zur Auswahl eines Zuges und gibt diesen auf Anforderung zurück.
 */
public class SimpleAI extends AbstractAI {

	/**
	 * Default-Konstruktor, welcher dieses Objekt mit Standardwerten versieht.
	 */
	public SimpleAI() {
		super();
	}

	/**
	 * Methode zur Berechnung des Scores eines gegebenen Zuges nach der gegebenen simplen Strategie der
	 * Projektbeschreibung. Wir werden diese Methode in Implementationen verbesserter KIs überschreiben und anpassen.
	 *
	 * @param move Der {@link Move} dessen Score berechnet werden soll
	 * @return Der Score des Spielzugs
	 */
	protected int getMoveScore(Move move) {

		/*
		 * Es werden nur Blumen-Züge bewertet, Ditch-Züge werden genau dann zufällig ausgewählt, wenn es keine möglichen
		 * Blumen-Züge mehr gibt.
		 */
		if (move.getType().equals(MoveType.Ditch)) return 0;

		// Der einfache Computer-Spieler gibt niemals auf und beendet niemals das Spiel.
		if (! move.getType().equals(MoveType.Flower)) return - 1;

		int n1 = 0;
		int n2 = 0;
		int score;

		/*
		 * Die direkten Nachbarn der ersten Zugblume aus dem Viewer abrufen. Diese werden benötigt, um zu
		 * überprüfen, ob die beiden zu setzenden Blumen nebeneinander liegen und um den Score auf Basis der
		 * Nachbarn zu berechnen.
		*/
		final Collection<Flower> firstFlowerNeighbors = boardViewer.getDirectNeighbors(move.getFirstFlower());
		final Collection<Flower> secondFlowerNeighbors = boardViewer.getDirectNeighbors(move.getSecondFlower());

		/*
		 * Iterieren durch alle Nachbarn beider Zug-Blumen. Die Zählvariable wird nur genau dann inkrementiert, wenn
		 * unter den Nachbarn Blumen eigener Farbe existieren.
		*/
		for (Flower neighbor : firstFlowerNeighbors) {
			if (boardViewer.getFlowerColor(neighbor) == getPlayerColor())
				n1++;
		}

		for (Flower neighbor : secondFlowerNeighbors) {
			if (boardViewer.getFlowerColor(neighbor) == getPlayerColor())
				n2++;
		}

		// Score nach der vorgegebenen Strategie berechnen.
		score = ( n1 + 1 ) * ( n2 + 1 );

		/*
		 * Falls die beiden zu setzenden Blumen nebeneinander liegen, soll der Score verdoppelt werden.
		 * So werden Züge mit einzeln gesetzten Blumen immer nur dann gemacht, wenn es nicht anders geht.
		 */
		if (firstFlowerNeighbors.contains(move.getSecondFlower()))
			score *= 2;

		return score;
	}
}
