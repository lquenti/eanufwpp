package flowerwarspp.player;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Arrays;
import java.util.Scanner;

import flowerwarspp.preset.*;
import flowerwarspp.util.log.Log;
import flowerwarspp.util.log.LogLevel;
import flowerwarspp.util.log.LogModule;

/**
 * Diese Klasse ermöglicht das Instanzieren von beiden Spielern, welche am aktuellen Spiel teilnehmen.
 * <p>
 * Außerdem wird in dieser Klasse der Hauptteil der Netzwerkunterstützung implementiert.
 *
 * @author Thilo Wischmeyer
 */
public class Players {
	/**
	 * Diese statische Methode erstellt einen neuen Spieler gegebenen Typs und weist im eine gegebene Implementation des
	 * Interfaces {@link Requestable} und ein bestehendes {@link Board} zum Kopieren zu.
	 *
	 * @param type  Typs the zu erstellenden Spielers
	 * @param input Implementation des Interfaces {@link Requestable}, welche der Spieler zum Abfragen von Zügen
	 *              verwenden soll
	 * @param board Bestehendes Spielbrett, welches dem Spieler zugewiesen wird
	 * @return Ein nach den gegebenen Parametern erstellter neuer Spieler
	 * @throes IllegalArgumentException falls versucht wird, einen Remote-Spieler mit vorhandenen
	 * bestimmten Board zu erzeugen.
	 */
	public static Player createPlayer( final PlayerType type, final Requestable input, final Board board ) throws IllegalArgumentException {
		if (type == PlayerType.REMOTE && board != null) {
			throw new IllegalArgumentException("Spielstände laden wird von Remote-Spielern nicht unterstützt.");
		}

		BasePlayer player = null;
		switch ( type ) {
			case REMOTE:
				return findRemotePlayer();
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
		player.setBoard(board);
		return player;
	}

	/**
	 * Variante von {@link #createPlayer(PlayerType, Requestable, Board)}, welche dem neu erstellten Spieler kein {@link
	 * Board} zuweist.
	 *
	 * @param type  Typs the zu erstellenden Spielers
	 * @param input Implementation des Interfaces {@link Requestable}, welche der Spieler zum Abfragen von Zügen
	 *              verwenden soll
	 * @return Der erzeugte Player
	 */
	public static Player createPlayer( final PlayerType type, final Requestable input ) {
		return createPlayer(type, input, null);
	}

	/**
	 * Diese Methode versucht einen im Netzwerk angebotenen entfernten Spieler zu finden und gibt diesen dann zurück.
	 *
	 * @return Der im Netzwerk angebotene und gefundene entfernte Spieler
	 */
	public static Player findRemotePlayer() {

		// TODO: Handle not finding the remote player

		Log.log(LogLevel.DEBUG, LogModule.PLAYER, "Trying to find remote player.");

		Scanner inputScanner = new Scanner(System.in);

		System.out.print("Adresse des entfernten Spielers: ");
		String host = inputScanner.nextLine();
		Log.log(LogLevel.INFO, LogModule.PLAYER, "Address of the remote player: " + host);

		System.out.print("Port des entfernten Spielers [1099]: ");
		String port = inputScanner.nextLine();
		if ( port.equals("") ) {
			port = "1099";
		}
		Log.log(LogLevel.INFO, LogModule.PLAYER, "Port of the remote player: " + port);

		String[] offeredPlayers = null;

		try {
			offeredPlayers = Naming.list("rmi://" + host + ":" + port);
		} catch ( RemoteException | MalformedURLException ignored ) {}

		if ( offeredPlayers != null ) {
			if ( offeredPlayers.length > 0 ) {
				Log.log(LogLevel.INFO, LogModule.PLAYER, "These players could be found on the network: "
						+ Arrays.toString(offeredPlayers));
				System.out.println("Die folgenden Spieler konnten im Netzwerk gefunden werden:");
				for ( String s : offeredPlayers ) {
					System.out.println(s);
				}
			}
		}

		System.out.print("Name des entfernten Spielers: ");
		String name = inputScanner.nextLine();
		Log.log(LogLevel.INFO, LogModule.PLAYER, "Name of the remote player: " + name);

		Player result = null;
		try {
			result = (Player) Naming.lookup("rmi://" + host + ":" + port + "/" + name);
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
	 * @throws RemoteException Falls der Spieler nicht im Netzwerk angeboten werden konnte.
	 */
	public static void offerPlayer( RemotePlayer player ) throws RemoteException {

		Scanner inputScanner = new Scanner(System.in);

		System.out.print("Name des entfernten Spielers: ");
		String name = inputScanner.nextLine();
		Log.log(LogLevel.INFO, LogModule.PLAYER, "Name of the remote player: " + name);

		try {
			Log.log(LogLevel.DEBUG, LogModule.PLAYER, "Trying to offer player with name " + name + " in the " +
					"network.");
			LocateRegistry.createRegistry(1099);
			Naming.rebind(name, player);
		} catch ( MalformedURLException e ) {
			throw new RemoteException("User entered an invalid URL.");
		}

		System.out.println("Der Spieler mit dem Name " + name + " ist jetzt im Netzwerk verfügbar.");
		Log.log(LogLevel.INFO, LogModule.PLAYER, "Player has successfully been offered in the network.");
	}
}
