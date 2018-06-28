package flowerwarspp.player;

import flowerwarspp.preset.Move;
import flowerwarspp.preset.Player;
import flowerwarspp.preset.Requestable;

import java.rmi.RemoteException;

/**
 * Implementiert die abstrakte Klasse {@link BasePlayer} mit einem interaktiven Spieler. Ueber ein Objekt einer Klasse,
 * welche das Interface {@link Requestable} implementiert, wird ein Zug von einem interaktiven Spieler angefordert.
 *
 * @author Michael Merse
 */
public class InteractivePlayer extends BasePlayer {

    /**
     * Eine vordefinierte Nachricht einer {@link Exception}, welche geworfen wird, wenn der interaktive Spieler keinen
     * Spielzug angeben konnte.
     */
    private static final String exception_NoMove =
            "Es konnte kein Zug vom interaktiven Spieler angefordert werden.";

    /**
     * Eine vordefinierte Nachricht einer {@link Exception}, welche geworfen wird, wenn der interaktive Spieler einen
     * nicht validen Zug angegeben hat.
     */
    private static final String exception_InvalidMove =
            "Der vom Spieler uebergegebene Zug ist nicht valide.";

    /**
     * Wird genutzt, um Spielzuege vom Spieler anzufordern.
     */
    private Requestable input;

    /**
     * Konstruiert einen neuen interaktiven Spieler. Einem interaktiven Spieler muss ein Objekt einer Klasse welche
     * {@link Requestable} implementiert zugewiesen werden, damit das Anfordern eines Spielzugs sichergestellt ist.
     *
     * @param input Ein Objekt einer Klasse welche {@link Requestable} implementiert, welches genutzt wird um einen
     *              Spielzug vom Spieler anzufordern.
     */
    public InteractivePlayer( Requestable input ) {
        this.input = input;
    }

    /**
     * Fordert einen Zug an, nach den Vorgaben der Interface-Methode {@link Player#request()}. Das Anfordern eines Zuges
     * wird geleistet durch ein Objekt einer Klasse welches die Schnittstelle {@link flowerwarspp.preset.Requestable}
     * implementiert.
     *
     * @return Der vom Spieler angeforderte Zug
     * @throws Exception       Falls der Spieler nicht in der Lage war, einen Zug zu liefern oder falls diese Methode
     *                         zum falschen Zeitpunkt innerhalb des Zyklus aufgerufen worden ist
     * @throws RemoteException Falls ein Fehler waehrend der Netzwerk-Kommunikation aufgetreten ist
     * @see Requestable
     */
    protected Move requestMove() throws Exception, RemoteException {
        //TODO:
        Move playerMove = input.request();

        if ( playerMove == null ) throw new Exception(exception_NoMove);

        // Make sure the move is actually valid (i.e. it's in the list of possible moves)
        if ( ! this.boardViewer.getPossibleMoves().contains(playerMove) ) throw new Exception(exception_InvalidMove);

        return playerMove;
    }
}
