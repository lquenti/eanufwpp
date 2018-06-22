package flowerwarspp.player;

import flowerwarspp.preset.Move;
import flowerwarspp.preset.MoveType;
import flowerwarspp.preset.Player;
import flowerwarspp.preset.PlayerType;

import java.rmi.RemoteException;

public class SimpleAI extends BasePlayer {

    private static final String exception_NoMove =
            "Die einfache KI konnte keinen Zug auswaehlen.";

    public static final PlayerType playerType = PlayerType.SIMPLE_AI;

    /**
     * Fordert einen Zug an, nach den Vorgaben der Interface-Methode {@link Player#request()}. Diese Methode bedient
     * sich einer einfachen Strategie zur Bewertung und anschliessend zur Auswahl eines Zuges.
     *
     * @return Der vom Spieler angeforderte Zug
     * @throws Exception       Falls der Spieler nicht in der Lage war, einen Zug zu liefern oder falls diese Methode
     *                         zum falschen Zeitpunkt innerhalb des Zyklus aufgerufen worden ist
     * @throws RemoteException Falls ein Fehler waehrend der Netzwerk-Kommunikation aufgetreten ist
     */
    // TODO: Implement the actual weighted algorithm
    @Override
    public Move request() throws Exception, RemoteException {
        // State validation
        if ( this.getCycleState() == PlayerFunction.NULL ) throw new Exception(exception_NoInit);
        if ( this.getCycleState() != PlayerFunction.REQUEST ) throw new Exception(exception_CycleRequest);

        for ( Move move :
                this.getPossibleMoves() ) {
            if (!move.getType().equals(MoveType.Flower)) continue;

            // TODO: Check if the neighbouring fields have plants for both flowers
        }

        // Update state
        this.cycleState = PlayerFunction.CONFIRM;

        return null;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }
}
