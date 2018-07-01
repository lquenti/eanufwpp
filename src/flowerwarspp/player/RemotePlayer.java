package flowerwarspp.player;

import flowerwarspp.preset.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Mit diesem Netzwerk-Spieler kann jede Implementation der Schnittstelle {@link Player} einer anderen Implementation
 * von FlowerWarsPP angeboten werden.
 *
 * @author Michael Merse
 */
public class RemotePlayer
        extends UnicastRemoteObject
        implements Player {

    /**
     * Referenz auf ein Objekt einer Klasse welche das Interface {@link Player} implementiert. Diese Referenz wird
     * benutzt, um die Funktionalität des Spielers über das Netzwerk zu sichern.
     */
    private final Player player;

    /**
     * Default-Konstruktor, welcher einen neuen Netzwerkspieler mit einem bestehenden Objekt einer Klasse, welche das
     * Interface {@link Player} implementiert, initialisiert.
     */
    public RemotePlayer( Player player ) throws RemoteException {
        this.player = player;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Move request() throws Exception, RemoteException {
        return this.player.request();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void confirm( Status status ) throws Exception, RemoteException {
        this.player.confirm(status);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update( Move opponentMove, Status status ) throws Exception, RemoteException {
        this.player.update(opponentMove, status);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init( int boardSize, PlayerColor color ) throws Exception, RemoteException {
        this.player.init(boardSize, color);
    }
}
