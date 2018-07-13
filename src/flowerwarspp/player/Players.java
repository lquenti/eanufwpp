package flowerwarspp.player;

import flowerwarspp.preset.*;
import flowerwarspp.util.log.*;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * Diese Klasse ermöglicht das Instanzieren von beiden Spielern, welche am aktuellen Spiel
 * teilnehmen. Außerdem wird in dieser Klasse der Hauptteil der Netzwerkunterstützung
 * implementiert.
 */
public class Players {
	/**
	 * Erstellt einen neuen Spieler gegebenen Typs und weist im ein gegebenes 
	 * {@link Requestable} und ein bestehendes {@link Board} zu.
	 *
	 * @param type  Typ des zu erstellenden Spielers
	 * @param input Das {@link Requestable}, das der Spieler zum Abfragen von Zügen verwenden
	 *              soll
	 * @param board Bestehendes Spielbrett, welches dem Spieler zugewiesen wird
	 * @param url Die URL im Fall eines Remote-Spielers
	 * @return Ein nach den gegebenen Parametern erzeugter Spieler
	 * @throws IllegalArgumentException falls versucht wird, einen Remote-Spieler mit 
	 *                                  vorhandenem, nichtleerem Board zu erzeugen.
	 */
	public static Player createPlayer(PlayerType type, Requestable input, String url, Board board)
			throws IllegalArgumentException {
		// Falls auf dem Brett schon Züge gemacht wurden, geht Netzwerkspiel nicht.
		if (type == PlayerType.REMOTE && board != null && !board.viewer().getFlowers(PlayerColor.Red).isEmpty()) {
			throw new IllegalArgumentException("Spielstände laden wird von Remote-Spielern nicht unterstützt.");
		}

		// Einen neuen Spieler gemäß des geforderten Typs erstellen.
		BasePlayer player = null;
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
				System.err.println("Unbekannter Spielertyp " + type);
				Log.log(LogLevel.ERROR, LogModule.PLAYER,
						"Players.createPlayer: Invalid PlayerType passed: " + type);
				return null;
		}
		// Das gegebene Spielbrett dem Spieler zuweisen.
		player.setBoard(board);
		return player;
	}

	/**
	 * Variante von {@link #createPlayer(PlayerType, Requestable, String, Board)}, welche dem neu erstellten Spieler kein
	 * {@link Board} zuweist.
	 *
	 * @param type  Typ des zu erstellenden Spielers
	 * @param input Das {@link Requestable}, das der Spieler zum Abfragen von Zügen verwenden soll
	 *              verwenden soll
	 * @param url Die URL im Fall eines Remote-Spielers
	 * @return Der erzeugte Spieler
	 */
	public static Player createPlayer(PlayerType type, Requestable input, String url) {
		return createPlayer(type, input, url, null);
	}

	/**
	 * Diese Methode versucht einen im Netzwerk angebotenen entfernten Spieler zu finden und gibt diesen dann zurück.
	 *
	 * @param url Die URL des zu suchenden entfernten Spielers
	 * @return Der im Netzwerk angebotene und gefundene entfernte Spieler
	 */
	public static Player findRemotePlayer(String url) {
		Player result = null;
		try {
			Log.log(LogLevel.DEBUG, LogModule.PLAYER, "Looking up player " + url);
			result = (Player) Naming.lookup("rmi://" + url);
		} catch ( Exception e ) {
			System.out.println("Der angegebene Spieler konnte nicht im Netzwerk gefunden werden.");
			Log.log(LogLevel.ERROR, LogModule.PLAYER, "Unable to find the specified player on the network.");
		}
		return result;
	}

	/**
	 * Bietet einen Netzwerkspieler im Netzwerk an.
	 *
	 * @param player Der im Netzwerk anzubietende Spieler, verpackt als {@link RemotePlayer}.
	 * @param name   Der Name des anzubietenden Netzwerkspielers.
	 * @param port   Der Port des anzubietenden Netzwerkspielers.
	 * @throws RemoteException Falls der Spieler nicht im Netzwerk angeboten werden konnte.
	 */
	public static void offerPlayer(RemotePlayer player, String name, int port) throws RemoteException {
		try {
			Log.log(LogLevel.DEBUG, LogModule.PLAYER, "Trying to offer player with name " + name + " in the " +
					"network on port " + port + ".");
			LocateRegistry.createRegistry(port);
			Naming.rebind(name, player);
		} catch (MalformedURLException e) {
			throw new RemoteException("Der Name des anzubietenden Spielers ist nicht gültig.");
		}

		System.out.println("Der Spieler mit dem Namen " + name + " ist jetzt im Netzwerk auf diesem Host am Port "
				+ port + " verfügbar.");
		Log.log(LogLevel.INFO, LogModule.PLAYER, "Remote player " + name + " has successfully been offered" +
				" on port " + port + " in the network.");
	}
}
