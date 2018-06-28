package flowerwarspp.player;

import flowerwarspp.preset.Move;
import flowerwarspp.preset.Player;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;

/**
 * Implementiert die abstrakte Klasse {@link BasePlayer} mit einer zufallsbasierten KI, welche zufaellig einen der zur
 * Verfuegung stehenden Zuege auswaehlt und auf Anfrage ausgibt.
 *
 * @author Michael Merse
 */
public class RandomAI extends BasePlayer {

    /**
     * Eine vordefinierte Nachricht einer {@link Exception}, welche geworfen wird, wenn kein Spielzug zufaellig
     * ausgewaehlt werden konnte.
     */
    private static final String exception_NoMove =
            "Die zufallsbasierte KI konnte keinen Zug auswaehlen.";

    /**
     * <code>private</code> Random Number Generator, um die zufaellige Auswahl eines Spielzugs mit Hilfe von
     * Pseudozufallszahlen leisten zu koennen.
     */
    private Random random = new Random();

    /**
     * Fordert einen Zug an, nach den Vorgaben der Interface-Methode {@link Player#request()}. Aus den auf dem
     * Spielbrett moeglichen und validen Zuegen wird ein Zug zufaellig ausgewaehlt und zurueck gegeben.
     *
     * @return Der vom Spieler angeforderte Zug
     * @throws Exception       Falls der Spieler nicht in der Lage war, einen Zug zu liefern oder falls diese Methode
     *                         zum falschen Zeitpunkt innerhalb des Zyklus aufgerufen worden ist
     * @throws RemoteException Falls ein Fehler waehrend der Netzwerk-Kommunikation aufgetreten ist
     */
    protected Move requestMove() throws Exception, RemoteException {

        // TODO: For now just return any random move. More checks might be necessary depending on our needs.
        Move randomMove;

        randomMove = getRandomPossibleMove();

        return randomMove;
    }

    /**
     * Gibt einen zufaellig ausgewaehlten, validen Spielzug zurueck, dieser wird ausgewaehlt aus den Spielzuegen, die
     * dem Spieler zur Verfuegung stehen.
     *
     * @return Ein zufaellig ausgewaehlter Spielzug.
     * @throws Exception Falls kein Zug zufaellig ausgewaehlt werden konnte.
     */
    private Move getRandomPossibleMove() throws Exception {
        //TODO: Potentially filter for surrender moves, although just surrendering might be an available option to the AI.

        // Get the Collection of possible moves for the current player.
        Collection<Move> possibleMoves = this.boardViewer.getPossibleMoves();

        // Get a random index in the Collection for selection.
        int randomIdx = random.nextInt(possibleMoves.size());

        // Stream the Collection and skip the amount of elements indicated by randomIdx.
        Optional<Move> randomMove = possibleMoves.stream().skip(randomIdx).findFirst();

        // If there is an element at that index, all fine and dandy, otherwise throw an exception.
        if ( ! randomMove.isPresent() ) throw new Exception(exception_NoMove);

        // If we have survived up until here, return the value (i.e. the Move).
        return randomMove.get();
    }
}
