package flowerwarspp.player;

import flowerwarspp.io.MoveRequestable;
import flowerwarspp.preset.Move;
import flowerwarspp.preset.Player;
import flowerwarspp.preset.PlayerType;
import flowerwarspp.preset.Requestable;

import java.rmi.RemoteException;

public class InteractivePlayer extends BasePlayer {

    private static final String exception_NoMove =
            "Es konnte kein Zug vom interaktiven Spieler angefordert werden.";

    private Requestable input;

    public InteractivePlayer (Requestable input) {
        this.input = input;
    }

    /**
     * Fordert einen Zug an, nach den Vorgaben der Interface-Methode {@link Player#request()}. Das Anfordern eines Zuges
     * wird geleistet durch ein Objekt der Klasse {@link flowerwarspp.io.MoveRequestable} welches die Schnittstelle
     * {@link flowerwarspp.preset.Requestable} implementiert.
     *
     * @return Der vom Spieler angeforderte Zug
     * @throws Exception       Falls der Spieler nicht in der Lage war, einen Zug zu liefern oder falls diese Methode
     *                         zum falschen Zeitpunkt innerhalb des Zyklus aufgerufen worden ist
     * @throws RemoteException Falls ein Fehler waehrend der Netzwerk-Kommunikation aufgetreten ist
     * @see MoveRequestable
     */
    protected Move requestMove() throws Exception, RemoteException {
        //TODO:
        Move playerMove = input.request();

        if ( playerMove == null ) throw new Exception(exception_NoMove);

        return playerMove;
    }
}
