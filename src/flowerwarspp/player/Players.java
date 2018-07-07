package flowerwarspp.player;

import java.rmi.Naming;
import java.util.Scanner;

import flowerwarspp.preset.*;
import flowerwarspp.util.log.Log;
import flowerwarspp.util.log.LogLevel;
import flowerwarspp.util.log.LogModule;

public class Players {
	public static Player createPlayer( final PlayerType type, final Requestable input ) {
		switch ( type ) {
			case HUMAN:
				return new InteractivePlayer(input);
			case RANDOM_AI:
				return new RandomAI();
			case SIMPLE_AI:
				return new SimpleAI();
			case ADVANCED_AI_1:
				return new AdvancedAI1();
			case REMOTE:
				return findRemotePlayer();
			default:
				System.err.println("Unbekannter Spielertyp " + type);
				Log.log0(LogLevel.ERROR, LogModule.PLAYER,
						"Players.createPlayer: Invalid PlayerType passed");
				return null;
		}
	}

	public static Player findRemotePlayer() {
		Scanner inputScanner = new Scanner(System.in);

		System.out.print("Adresse des entfernten Spielers: ");
		String host = inputScanner.nextLine();
		Log.log0(LogLevel.INFO, LogModule.PLAYER, "Address of the remote player: " + host);

		System.out.print("Name des entfernten Spielers: ");
		String name = inputScanner.nextLine();
		Log.log0(LogLevel.INFO, LogModule.PLAYER, "Name of the remote player: " + name);

		Player result = null;
		try {
			result = (Player) Naming.lookup("rmi://" + host + "/" + name);
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
