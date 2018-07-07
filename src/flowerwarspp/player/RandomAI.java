package flowerwarspp.player;

import flowerwarspp.preset.Move;
import flowerwarspp.preset.Player;
import flowerwarspp.util.log.LogLevel;

import java.util.Optional;
import java.util.Random;

/**
 * Implementiert die abstrakte Klasse {@link BasePlayer} mit einer zufallsbasierten KI, welche zufällig einen der zur
 * Verfügung stehenden Züge auswählt und auf Anfrage ausgibt.
 *
 * @author Michael Merse
 */
public class RandomAI extends BasePlayer {

	/**
	 * Eine vordefinierte Nachricht einer {@link Exception}, welche geworfen wird, wenn kein Spielzug zufällig
	 * ausgewählt werden konnte.
	 */
	private static final String exception_NoMove =
			"Die zufallsbasierte KI konnte keinen Zug auswaehlen.";

	/**
	 * <code>private</code> Random Number Generator, um die zufällige Auswahl eines Spielzugs mit Hilfe von
	 * Pseudozufallszahlen leisten zu können.
	 */
	private final Random flowerRNG;

	/**
	 * Default-Konstruktor, welcher dieses Objekt mit Standardwerten versieht.
	 */
	public RandomAI() {
		super();
		flowerRNG = new Random();
	}

	/**
	 * Fordert einen Zug an, nach den Vorgaben der Interface-Methode {@link Player#request()}. Aus den auf dem
	 * Spielbrett möglichen und validen Zügen wird ein Zug zufällig ausgewählt und zurück gegeben.
	 *
	 * @return Der vom Spieler angeforderte Zug
	 * @throws Exception Falls der Spieler nicht in der Lage war, einen Zug zu liefern oder falls diese Methode zum
	 *                   falschen Zeitpunkt innerhalb des Zyklus aufgerufen worden ist
	 */
	protected Move requestMove() throws Exception {

		// TODO: For now just return any random move. More checks might be necessary depending on our needs.

		final Move move = getRandomPossibleMove();

		if (move == null) {
			log(LogLevel.ERROR, "random AI could not return a random move");
			throw new Exception(exception_NoMove);
		} else
			return move;
	}

	/**
	 * Gibt einen zufällig ausgewählten, validen Spielzug zurück, dieser wird ausgewählt aus den Spielzügen, die dem
	 * Spieler zur Verfügung stehen.
	 *
	 * @return Ein zufällig ausgewählter Spielzug.
	 */
	private Move getRandomPossibleMove() {
		//TODO: Potentially filter for surrender moves, although just surrendering might be an available option to the AI.

		if (this.boardViewer.getPossibleMoves().size() == 0) return null;

		// Get a random index in the Collection for selection.
		final int randomIdx = flowerRNG.nextInt(this.boardViewer.getPossibleMoves().size());

		// Stream the Collection and skip the amount of elements indicated by randomIdx.
		return this.boardViewer.getPossibleMoves().stream().skip(randomIdx).findFirst().orElse(null);
	}
}
