package flowerwarspp.player;

import flowerwarspp.io.MoveRequestable;
import flowerwarspp.preset.Move;
import flowerwarspp.preset.Player;
import flowerwarspp.preset.PlayerType;

import java.rmi.RemoteException;

public class InteractivePlayer extends BasePlayer {

    private static final String exception_NoMove =
            "Es konnte kein Zug vom interaktiven Spieler angefordert werden.";

    public static final PlayerType playerType = PlayerType.HUMAN;

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
    @Override
    public Move request() throws Exception, RemoteException {
        // State validation
        if ( getCycleState() == PlayerFunction.NULL ) throw new Exception(exception_NoInit);
        if ( getCycleState() != PlayerFunction.REQUEST ) throw new Exception(exception_CycleRequest);

        // Request the move using a MoveRequestable object
        MoveRequestable requestMove = new MoveRequestable();

        // We are not concerned with the validity of the move that we request from the player just now,
        // the board logic handles verification of the move.
        Move playerMove = requestMove.request();

        if ( playerMove == null ) throw new Exception(exception_NoMove);

        this.board.make(playerMove);

        // Update state
        this.cycleState = PlayerFunction.CONFIRM;

        return playerMove;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }
}
