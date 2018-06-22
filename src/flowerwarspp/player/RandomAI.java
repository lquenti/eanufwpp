package flowerwarspp.player;

import flowerwarspp.preset.Move;
import flowerwarspp.preset.Player;
import flowerwarspp.preset.PlayerType;

import java.rmi.RemoteException;
import java.util.Optional;

public class RandomAI extends BasePlayer {

    private static final String exception_NoMove =
            "Die zufallsbasierte KI konnte keinen Zug auswaehlen.";

    public static final PlayerType playerType = PlayerType.RANDOM_AI;

    /**
     * Fordert einen Zug an, nach den Vorgaben der Interface-Methode {@link Player#request()}. Aus den auf dem
     * Spielbrett moeglichen und validen Zuegen wird ein Zug zufaellig ausgewaehlt und zurueck gegeben.
     *
     * @return Der vom Spieler angeforderte Zug
     * @throws Exception       Falls der Spieler nicht in der Lage war, einen Zug zu liefern oder falls diese Methode
     *                         zum falschen Zeitpunkt innerhalb des Zyklus aufgerufen worden ist
     * @throws RemoteException Falls ein Fehler waehrend der Netzwerk-Kommunikation aufgetreten ist
     */
    @Override
    public Move request() throws Exception, RemoteException {
        // State validation
        if ( this.getCycleState() == PlayerFunction.NULL ) throw new Exception(exception_NoInit);
        if ( this.getCycleState() != PlayerFunction.REQUEST ) throw new Exception(exception_CycleRequest);

        Optional <Move> randomMove = this.getPossibleMoves().stream().findAny();

        // TODO: Might have to handle some more stuff here, but for now we do the sensible thing and throw
        if (!randomMove.isPresent()) throw new Exception(exception_NoMove);

        // Apply the randomly selected move to this player's board
        this.board.make(randomMove.get());

        // Update state
        this.cycleState = PlayerFunction.CONFIRM;

        return randomMove.get();
    }

    public PlayerType getPlayerType() {
        return playerType;
    }
}
