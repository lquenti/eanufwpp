package flowerwarspp.player;

import java.rmi.Naming;
import java.util.Scanner;

import flowerwarspp.player.*;
import flowerwarspp.preset.*;

public class Players {
	public static Player createPlayer(final PlayerType type, final Requestable input) {
		switch (type) {
			case HUMAN: return new InteractivePlayer(input);
			case RANDOM_AI: return new RandomAI();
			case SIMPLE_AI: return new SimpleAI();
			case REMOTE: return findRemotePlayer();
			default: System.err.println("Unbekannter Spielertyp " + type); return null;
		}
	}

	public static Player findRemotePlayer() {
		Scanner inputScanner = new Scanner(System.in);
		System.out.print("Adresse des entfernten Spielers: ");
		String host = inputScanner.nextLine();
		System.out.print("Name des entfernten Spielers: ");
		String name = inputScanner.nextLine();

		Player result = null;
		try {
			result = (Player)Naming.lookup("rmi://" + host + "/" + name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void offerPlayer(Player player) {
		Scanner inputScanner = new Scanner(System.in);
		System.out.print("Name des entfernten Spielers: ");
		String name = inputScanner.nextLine();

		try {
			Naming.rebind(name, new RemotePlayer(player));
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
