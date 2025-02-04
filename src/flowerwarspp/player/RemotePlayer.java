package flowerwarspp.player;

import flowerwarspp.board.MainBoard;
import flowerwarspp.main.ExitCode;
import flowerwarspp.main.savegame.SaveGame;
import flowerwarspp.preset.*;
import flowerwarspp.ui.Output;
import flowerwarspp.util.Convert;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Mit diesem Netzwerk-Spieler kann jede Implementation der Schnittstelle {@link Player} einer
 * anderen Implementation von FlowerWarsPP angeboten werden.
 */
public class RemotePlayer extends UnicastRemoteObject implements Player {
	/**
	 * Serialisierungskonstante
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Referenz auf ein Objekt welches {@link Output} implementiert. Mit diesem Objekt kann lokal
	 * das entfernt stattfindene Spiel mitverfolgt werden.
	 */
	private Output output;

	/**
	 * Das Spielbrett des Spielers.
	 */
	private Board board;

	/**
	 * Ein Viewer auf das Spielbrett des Spielers.
	 */
	private Viewer boardViewer;

	/**
	 * Ein {@link SaveGame}-Objekt, mit welchem der entfernte Spieler das Spiel speichern kann.
	 */
	private SaveGame saveGame = null;

	/**
	 * Referenz auf ein Objekt einer Klasse welche das Interface {@link Player} implementiert.
	 * Diese Referenz wird benutzt, um die Funktionalität des Spielers über das Netzwerk zu sichern.
	 */
	private Player player;

	/**
	 * Konstruktor, welcher zusätzlich zu {@link RemotePlayer#RemotePlayer(Player, Output)} auch
	 * noch eine Referenz auf ein {@link SaveGame}-Objekt zum Speichern des Spielstands.
	 *
	 * @param player
	 * 		Der Spieler, welcher dem Server durch dieses Objekt Züge mitteilen soll.
	 * @param output
	 * 		Das Objekt, auf welchem das aktuelle Spielgeschehen lokal angezeigt wird.
	 * @param saveGame
	 * 		Referenz auf ein {@link SaveGame}-Objekt zum Speichern des Spiels.
	 *
	 * @throws RemoteException
	 * 		Falls während der Netzwerkkommunikation ein Fehler aufgetreten ist.
	 */
	public RemotePlayer(Player player, Output output, SaveGame saveGame) throws RemoteException {
		this(player, output);
		this.saveGame = saveGame;
	}

	/**
	 * Default-Konstruktor, welcher einen neuen übergebenen Spieler als Netzwerkspieler mit einem
	 * bestehenden Objekt einer Klasse, welche das Interface {@link Player} implementiert,
	 * initialisiert.
	 *
	 * @param player
	 * 		Der Spieler, welcher dem Server durch dieses Objekt Züge mitteilen soll.
	 * @param output
	 * 		Das Objekt, auf welchem das aktuelle Spielgeschehen lokal angezeigt wird.
	 *
	 * @throws RemoteException
	 * 		Falls während der Netzwerkkommunikation ein Fehler aufgetreten ist.
	 */
	public RemotePlayer(Player player, Output output) throws RemoteException {
		this.player = player;
		this.output = output;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Move request() throws Exception, RemoteException {
		Move result = null;
		try {
			result = player.request();
		} catch (Exception e) {
			output.showEndMessage(ExitCode.NO_MOVE);
			throw e;
		}
		board.make(result);

		if (saveGame != null) {
			saveGame.add(result);
		}

		output.refresh();
		if (boardViewer.getStatus() != Status.Ok) {
			output.showEndMessage(Convert.statusToText(boardViewer.getStatus()), ExitCode.OK);
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void confirm(Status status) throws Exception, RemoteException {
		try {
			player.confirm(status);
		} catch (Exception e) {
			output.showEndMessage(ExitCode.STATE_INCONSISTENT);
			throw e;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(Move opponentMove, Status status) throws Exception, RemoteException {
		try {
			player.update(opponentMove, status);
		} catch (Exception e) {
			output.showEndMessage(ExitCode.ILLEGAL_MOVE);
			throw e;
		}
		board.make(opponentMove);

		if (saveGame != null) {
			saveGame.add(opponentMove);
		}

		output.refresh();
		if (boardViewer.getStatus() != Status.Ok) {
			output.showEndMessage(Convert.statusToText(boardViewer.getStatus()), ExitCode.OK);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(int boardSize, PlayerColor color) throws Exception, RemoteException {
		board = new MainBoard(boardSize);
		boardViewer = board.viewer();
		output.setViewer(boardViewer);
		output.setSaveGame(new SaveGame(boardSize));

		player.init(boardSize, color);
	}
}
