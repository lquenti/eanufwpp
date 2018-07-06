package flowerwarspp.player;

import flowerwarspp.preset.*;

import java.util.Collection;

/**
 * Implementiert die abstrakte Klasse {@link BasePlayer} mit einer simplen KI. Diese KI bedient sich einer limitierten
 * Bewertungsstrategie zur Auswahl eines Zuges und gibt diesen auf Anforderung zurück.
 *
 * @author Michael Merse
 */
public class SimpleAI extends BaseAI {

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
	protected int getMoveScore( final Move move ) {

		// TODO: Maybe check if the passed move is actually valid, but for now we won't bother.

		// We are only concerned with moves that actually make flowers.
		if ( ! move.getType().equals(MoveType.Flower) ) return - 1;

		int n1 = 0;
		int n2 = 0;
		int score;

		// Obtain the direct neighbors of both flowers.
		final Collection<Flower> firstFlowerNeighbors = boardViewer.getDirectNeighbors(move.getFirstFlower());
		final Collection<Flower> secondFlowerNeighbors = boardViewer.getDirectNeighbors(move.getSecondFlower());

		// Iterate through all the first flower's neighbours and calculate the score.
		for ( final Flower neighbor : firstFlowerNeighbors ) {
			if ( boardViewer.getFlowerColor(neighbor) == getPlayerColour() )
				n1++;
		}

		// Iterate through all the second flower's neighbours and calculate the score.
		for ( final Flower neighbor : secondFlowerNeighbors ) {
			if ( boardViewer.getFlowerColor(neighbor) == getPlayerColour() )
				n2++;
		}

		// Calculate the score as indicated by the strategy.
		score = ( n1 + 1 ) * ( n2 + 1 );

		// If both flowers are attached (i.e. if they're neighbors) double the score.
		if ( firstFlowerNeighbors.contains(move.getSecondFlower()) )
			score *= 2;

		return score;
	}

}
