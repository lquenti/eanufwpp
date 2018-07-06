package flowerwarspp.player;

import flowerwarspp.preset.*;
import flowerwarspp.board.*;
import flowerwarspp.io.*;

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

    private Output output;
    private Board board;

    /**
     * Referenz auf ein Objekt einer Klasse welche das Interface {@link Player} implementiert. Diese Referenz wird
     * benutzt, um die Funktionalität des Spielers über das Netzwerk zu sichern.
     */
    private Player player;

    /**
     * Default-Konstruktor, welcher einen neuen Netzwerkspieler mit einem bestehenden Objekt einer Klasse, welche das
     * Interface {@link Player} implementiert, initialisiert.
     */
    public RemotePlayer( Player player, Output output ) throws RemoteException {
        this.player = player;
        this.output = output;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Move request() throws Exception, RemoteException {
        Move result = this.player.request();
        board.make(result);
        output.refresh();
        return result;
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
        board.make(opponentMove);
        output.refresh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init( int boardSize, PlayerColor color ) throws Exception, RemoteException {
        board = new MainBoard(boardSize);
        Viewer boardViewer = board.viewer();
        output.setViewer(boardViewer);

        this.player.init(boardSize, color);
    }
}
