package flowerwarspp.player;

import flowerwarspp.preset.Move;
import flowerwarspp.preset.Player;

import java.util.Collection;
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
    private final Random randomFlower = new Random();
    /**
     * Default-Konstruktor, welcher dieses Objekt mit Standardwerten versieht.
     */
    public RandomAI () {
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
        return getRandomPossibleMove();
    }

    /**
     * Gibt einen zufällig ausgewählten, validen Spielzug zurück, dieser wird ausgewählt aus den Spielzügen, die dem
     * Spieler zur Verfügung stehen.
     *
     * @return Ein zufällig ausgewählter Spielzug.
     * @throws Exception Falls kein Zug zufällig ausgewählt werden konnte.
     */
    private Move getRandomPossibleMove() throws Exception {
        //TODO: Potentially filter for surrender moves, although just surrendering might be an available option to the AI.

        // Get the Collection of possible moves for the current player.
        final Collection<Move> possibleMoves = this.boardViewer.getPossibleMoves();

        // Get a random index in the Collection for selection.
        final int randomIdx = randomFlower.nextInt(possibleMoves.size());

        // Stream the Collection and skip the amount of elements indicated by randomIdx.
        final Optional<Move> randomMove = possibleMoves.stream().skip(randomIdx).findFirst();

        // If there is an element at that index, all fine and dandy, otherwise throw an exception.
        if ( ! randomMove.isPresent() ) throw new Exception(exception_NoMove);

        // If we have survived up until here, return the value (i.e. the Move).
        return randomMove.get();
    }
}
