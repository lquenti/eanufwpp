package flowerwarspp.player;

import flowerwarspp.board.MainBoard;
import flowerwarspp.main.savegame.SaveGame;
import flowerwarspp.ui.Output;
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

	// TODO: Javadoc

	/**
	 * Referenz auf ein Objekt welches {@link Output} implementiert. Mit diesem Objekt kann lokal das entfernt
	 * stattfindene Spiel mitverfolgt werden.
	 */
	private Output output;

	/**
	 * Das Spielbrett des Spielers.
	 */
	private Board board;

	/**
	 * Ein {@link SaveGame}-Objekt, mit welchem der entfernte Spieler das Spiel speichern kann.
	 */
	private SaveGame saveGame = null;

	/**
	 * Referenz auf ein Objekt einer Klasse welche das Interface {@link Player} implementiert. Diese Referenz wird
	 * benutzt, um die Funktionalität des Spielers über das Netzwerk zu sichern.
	 */
	private Player player;

	/**
	 * Default-Konstruktor, welcher einen neuen übergebenen Spieler als Netzwerkspieler mit einem bestehenden Objekt
	 * einer Klasse, welche das Interface {@link Player} implementiert, initialisiert.
	 *
	 * @param player Der Spieler, welcher dem Server durch dieses Objekt Züge mitteilen soll.
	 * @param output Das Objekt, auf welchem das aktuelle Spielgeschehen lokal angezeigt wird.
	 * @throws RemoteException Falls während der Netzwerkkommunikation ein Fehler aufgetreten ist.
	 */
	public RemotePlayer(final Player player, final Output output) throws RemoteException {
		this.player = player;
		this.output = output;
	}

	/**
	 * Konstruktor, welcher zusätzlich zu {@link RemotePlayer#RemotePlayer(Player, Output)} auch noch eine
	 * Referenz auf ein {@link SaveGame}-Objekt zum Speichern des Spielstands.
	 *
	 * @param player   Der Spieler, welcher dem Server durch dieses Objekt Züge mitteilen soll.
	 * @param output   Das Objekt, auf welchem das aktuelle Spielgeschehen lokal angezeigt wird.
	 * @param saveGame Referenz auf ein {@link SaveGame}-Objekt zum Speichern des Spiels.
	 * @throws RemoteException Falls während der Netzwerkkommunikation ein Fehler aufgetreten ist.
	 */
	public RemotePlayer(final Player player, final Output output, final SaveGame saveGame) throws RemoteException {
		this(player, output);
		this.saveGame = saveGame;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Move request() throws Exception, RemoteException {
		final Move result = player.request();
		board.make(result);

		if (saveGame != null)
			saveGame.add(result);

		output.refresh();
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void confirm(final Status status) throws Exception, RemoteException {
		player.confirm(status);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(final Move opponentMove, final Status status) throws Exception, RemoteException {
		player.update(opponentMove, status);
		board.make(opponentMove);

		if (saveGame != null)
			saveGame.add(opponentMove);

		output.refresh();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(final int boardSize, final PlayerColor color) throws Exception, RemoteException {
		board = new MainBoard(boardSize);
		final Viewer boardViewer = board.viewer();
		output.setViewer(boardViewer);

		player.init(boardSize, color);
	}
}
