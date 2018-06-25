package flowerwarspp.player;

import flowerwarspp.preset.Move;
import flowerwarspp.preset.Player;
import flowerwarspp.preset.PlayerType;

import java.rmi.RemoteException;
import java.util.Optional;

public class RandomAI extends BasePlayer {

    private static final String exception_NoMove =
            "Die zufallsbasierte KI konnte keinen Zug auswaehlen.";

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

        //TODO: Filter for surrender moves and use proper RNG for selection. Also, scrap stream

        Optional <Move> randomMove = this.getPossibleMoves().stream().findAny();

        // TODO: Might have to handle some more stuff here, but for now we do the sensible thing and throw
        if (!randomMove.isPresent()) throw new Exception(exception_NoMove);

        return randomMove.get();
    }
}
