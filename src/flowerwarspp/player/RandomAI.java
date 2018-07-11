package flowerwarspp.player;

import flowerwarspp.preset.Move;
import flowerwarspp.preset.MoveType;

/**
 * Implementiert die abstrakte Klasse {@link BasePlayer} mit einer zufallsbasierten KI, welche zufällig einen der zur
 * Verfügung stehenden Züge auswählt und auf Anfrage ausgibt.
 *
 * @author Michael Merse
 */
public class RandomAI extends BaseAI {


	/**
	 * Gibt immer einen Score gleich 0 zurück, außer für den {@link MoveType#Surrender}-Spielzug. Dieser wird mit -1
	 * bewertet und somit nie ausgeführt.
	 * <p>
	 * Somit wird der {@link BaseAI}-Klasse die zufallsbasierte Auswahl eines Spielzugs überlassen.
	 *
	 * @param move Der {@link Move} dessen Score berechnet werden soll
	 * @return <code>0</code> für alle Moves, außer der <code>Surrender</code>-Spielzug, mit <code>-1</code>
	 */
	@Override
	protected int getMoveScore( final Move move ) {
		return 0;
	}
}
