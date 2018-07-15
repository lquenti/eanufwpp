package flowerwarspp.player;

import flowerwarspp.preset.*;
import flowerwarspp.util.log.Log;
import flowerwarspp.util.log.LogLevel;
import flowerwarspp.util.log.LogModule;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Arrays;
import java.util.Collection;

/**
 * Diese Klasse ermöglicht das Instanzieren von beiden Spielern, welche am aktuellen Spiel
 * teilnehmen. Außerdem wird in dieser Klasse der Hauptteil der Netzwerkunterstützung
 * implementiert.
 */
public class Players {
	/**
	 * Eine {@link Collection} von verfügbaren {@link PlayerType}s.
	 */
	public static final Collection<PlayerType> AVAILABLE_PLAYER_TYPES =
			Arrays.asList(PlayerType.HUMAN, PlayerType.RANDOM_AI, PlayerType.SIMPLE_AI,
					PlayerType.ADVANCED_AI_1, PlayerType.ADVANCED_AI_2, PlayerType.REMOTE);

	/**
	 * Variante von {@link #createPlayer(PlayerType, Requestable, String, Board)}, welche dem neu
	 * erstellten Spieler kein {@link Board} zuweist.
	 *
	 * @param type
	 * 		Typ des zu erstellenden Spielers
	 * @param input
	 * 		Das {@link Requestable}, das der Spieler zum Abfragen von Zügen verwenden soll
	 * 		verwenden
	 * 		soll
	 * @param url
	 * 		Die URL im Fall eines Remote-Spielers
	 *
	 * @return Der erzeugte Spieler
	 *
	 * @throws NetworkException
	 * 		Falls an der gegebenen URL kein entfernter Spieler gefunden werden konnte.
	 */
	public static Player createPlayer(PlayerType type, Requestable input, String url)
			throws NetworkException {
		return createPlayer(type, input, url, null);
	}

	/**
	 * Erstellt einen neuen Spieler gegebenen Typs und weist im ein gegebenes {@link Requestable}
	 * und ein bestehendes {@link Board} zu.
	 *
	 * @param type
	 * 		Typ des zu erstellenden Spielers
	 * @param input
	 * 		Das {@link Requestable}, das der Spieler zum Abfragen von Zügen verwenden soll
	 * @param board
	 * 		Bestehendes Spielbrett, welches dem Spieler zugewiesen wird
	 * @param url
	 * 		Die URL im Fall eines Remote-Spielers
	 *
	 * @return Ein nach den gegebenen Parametern erzeugter Spieler
	 *
	 * @throws IllegalArgumentException
	 * 		falls versucht wird, einen Remote-Spieler mit vorhandenem, nichtleerem Board zu
	 * 		erzeugen.
	 * @throws NetworkException
	 * 		Falls an der gegebenen URL kein entfernter Spieler gefunden werden konnte.
	 */
	public static Player createPlayer(PlayerType type, Requestable input, String url, Board board)
			throws IllegalArgumentException, NetworkException {
		// Falls auf dem Brett schon Züge gemacht wurden, geht Netzwerkspiel nicht.
		if (type == PlayerType.REMOTE && board != null &&
				! board.viewer().getFlowers(PlayerColor.Red).isEmpty()) {
			throw new IllegalArgumentException(
					"Spielstände laden wird von Remote-Spielern nicht unterstützt.");
		}

		// Einen neuen Spieler gemäß des geforderten Typs erstellen.
		AbstractPlayer player = null;
		switch (type) {
			case REMOTE:
				return findRemotePlayer(url);
			case HUMAN:
				player = new InteractivePlayer(input);
				break;
			case RANDOM_AI:
				player = new RandomAI();
				break;
			case SIMPLE_AI:
				player = new SimpleAI();
				break;
			case ADVANCED_AI_1:
				player = new AdvancedAI1();
				break;
			case ADVANCED_AI_2:
				player = new AdvancedAI2();
				break;
			default:
				Log.log(LogLevel.ERROR, LogModule.PLAYER,
						"Players.createPlayer: Invalid PlayerType passed: " + type);
				return null;
		}
		// Das gegebene Spielbrett dem Spieler zuweisen.
		player.setBoard(board);
		return player;
	}

	/**
	 * Diese Methode versucht einen im Netzwerk angebotenen entfernten Spieler zu finden und gibt
	 * diesen dann zurück.
	 *
	 * @param url
	 * 		Die URL des zu suchenden entfernten Spielers
	 *
	 * @return Der im Netzwerk angebotene und gefundene entfernte Spieler
	 *
	 * @throws NetworkException
	 * 		Falls an der angegebenen URL kein Spieler gefunden werden konnte.
	 */
	public static Player findRemotePlayer(String url) throws NetworkException {
		Player result = null;

		Log.log(LogLevel.DEBUG, LogModule.PLAYER, "Looking up player " + url);
		try {
			result = (Player) Naming.lookup("rmi://" + url);
		} catch (Exception e) {
			Log.log(LogLevel.ERROR, LogModule.PLAYER,
					"Unable to find the specified player on the network.");
			throw new NetworkException();
		}

		return result;
	}

	/**
	 * Bietet einen Netzwerkspieler im Netzwerk an.
	 *
	 * @param player
	 * 		Der im Netzwerk anzubietende Spieler, verpackt als {@link RemotePlayer}.
	 * @param name
	 * 		Der Name des anzubietenden Netzwerkspielers.
	 * @param port
	 * 		Der Port des anzubietenden Netzwerkspielers.
	 *
	 * @throws RemoteException
	 * 		Falls der Spieler nicht im Netzwerk angeboten werden konnte.
	 */
	public static void offerPlayer(RemotePlayer player, String name, int port)
			throws RemoteException {
		try {
			Log.log(LogLevel.DEBUG, LogModule.PLAYER,
					"Trying to offer player with name " + name + " in the " + "network on port " +
							port + ".");
			LocateRegistry.createRegistry(port);
			Naming.rebind(name, player);
		} catch (MalformedURLException e) {
			throw new RemoteException("Der Name des anzubietenden Spielers ist nicht gültig.");
		}

		Log.log(LogLevel.INFO, LogModule.PLAYER,
				"Remote player " + name + " has successfully been offered" + " on port " + port +
						" in the network.");
	}
}
