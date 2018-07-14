package flowerwarspp.player;

import flowerwarspp.preset.Move;
import flowerwarspp.preset.MoveType;

/**
 * Implementiert die abstrakte Klasse {@link AbstractPlayer} mit einer zufallsbasierten KI, welche zufällig einen der
 * zur Verfügung stehenden Züge auswählt und auf Anfrage ausgibt.
 */
public class RandomAI extends AbstractAI {


	/**
	 * Gibt immer einen Score gleich 0 zurück, außer für den {@link MoveType#Surrender}-Spielzug. Dieser wird mit -1
	 * bewertet und somit nie ausgeführt.
	 * <p>
	 * Somit wird der {@link AbstractAI}-Klasse die zufallsbasierte Auswahl eines Spielzugs überlassen.
	 *
	 * @param move Der {@link Move} dessen Score berechnet werden soll
	 * @return <code>0</code> für alle Moves, außer der <code>Surrender</code>-Spielzug, mit <code>-1</code>
	 */
	@Override
	protected int getMoveScore(Move move) {
		return 0;
	}
}
