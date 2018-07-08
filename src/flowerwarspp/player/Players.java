package flowerwarspp.player;

import java.rmi.Naming;
import java.util.Scanner;

import flowerwarspp.preset.*;
import flowerwarspp.util.log.Log;
import flowerwarspp.util.log.LogLevel;
import flowerwarspp.util.log.LogModule;

public class Players {
	public static Player createPlayer( final PlayerType type, final Requestable input, final Board board ) {
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
			default:
				System.err.println("Unbekannter Spielertyp " + type);
				Log.log0(LogLevel.ERROR, LogModule.PLAYER,
						"Players.createPlayer: Invalid PlayerType passed: " + type);
				return null;
		}
		player.setBoard(board);
		return player;
	}

	public static Player createPlayer( final PlayerType type, final Requestable input ) {
		return createPlayer(type, input, null);
	}

	public static Player findRemotePlayer() {
		Scanner inputScanner = new Scanner(System.in);

		System.out.print("Adresse des entfernten Spielers: ");
		String host = inputScanner.nextLine();
		Log.log0(LogLevel.INFO, LogModule.PLAYER, "Address of the remote player: " + host);

		System.out.print("Port des entfernten Spielers [1099]: ");
		String port = inputScanner.nextLine();
		if (port.equals("")) {
			port = "1099";
		}
		Log.log0(LogLevel.INFO, LogModule.PLAYER, "Port of the remote player: " + port);

		System.out.print("Name des entfernten Spielers: ");
		String name = inputScanner.nextLine();
		Log.log0(LogLevel.INFO, LogModule.PLAYER, "Name of the remote player: " + name);

		Player result = null;
		try {
			result = (Player) Naming.lookup("rmi://" + host + ":" + port + "/" + name);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return result;
	}

	public static void offerPlayer( RemotePlayer player ) {
		Scanner inputScanner = new Scanner(System.in);
		System.out.print("Name des entfernten Spielers: ");
		String name = inputScanner.nextLine();
		Log.log0(LogLevel.INFO, LogModule.PLAYER, "Name of the remote player: " + name);

		try {
			Naming.rebind(name, player);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}
