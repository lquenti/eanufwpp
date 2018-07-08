package flowerwarspp.main;

import flowerwarspp.preset.ArgumentParser;
import flowerwarspp.preset.ArgumentParserException;
import flowerwarspp.util.log.Log;
import flowerwarspp.util.log.LogLevel;
import flowerwarspp.util.log.LogModule;

public class Main {

	/**
	 * Dieser Error-Code wird vom Programm zurück gegeben, falls die übergebenen Argumente nicht valide sind.
	 */
	private static final int ERRORCODE_INVALID_ARGS = 1;

	/**
	 * Gibt Informationen zur Verwendendung des Programms auf der Standardausgabe aus und beendet das Programm mit dem
	 * Exit-Code {@link #ERRORCODE_INVALID_ARGS}.
	 */
	public static void quitWithUsage() {
		System.out.println("Verwendung:");
		System.out.println("flowerwarspp.main.Main -size <Spielfeldgröße> -red <Spielertyp> -blue <Spielertyp> "
				+ "-delay <Verzögerung> (optional:) --debug");
		System.out.println();
		System.out.println("Spielfeldgröße: Zahl zwischen 3 und 30");
		System.out.println("Spielertyp:     \"human\", \"remote\", \"random\", \"simple\", oder \"adv1\"");
		System.out.println("Verzögerung:    Zeit zwischen Zügen in Millisekunden");
		System.out.println("Debug:          Zeigt Debug-Information im Game-Log an. Optionaler Flag (hat"
				+ " keine Argumente)");
		System.exit(ERRORCODE_INVALID_ARGS);
	}

	public static void main( String[] args ) {
		new Game(new GameParameters(args));
	}
}
