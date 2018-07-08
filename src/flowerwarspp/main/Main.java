package flowerwarspp.main;

import flowerwarspp.preset.ArgumentParser;
import flowerwarspp.preset.ArgumentParserException;
import flowerwarspp.util.log.Log;
import flowerwarspp.util.log.LogLevel;
import flowerwarspp.util.log.LogModule;

import java.util.Arrays;

public class Main {

	/**
	 * Dieser Error-Code wird vom Programm zurück gegeben, falls die übergebenen Argumente nicht valide sind.
	 */
	private static final int ERRORCODE_INVALID_ARGS = 1;

	/**
	 * Gibt Informationen zur Verwendendung des Programms auf der Standardausgabe aus und beendet das Programm mit dem
	 * Exit-Code {@link #ERRORCODE_INVALID_ARGS}.
	 */
	private static void quitWithUsage() {
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

	/**
	 * Verarbeitet die übergebenen Argumente und setzt die gewünschten Parameter.
	 *
	 * @param args Die Argumente, die von der Kommandozeile übergeben worden sind
	 */
	private static GameParameters parseArguments( String[] args ) {

		GameParameters gameParameters = new GameParameters();

		try {
			// set up
			ArgumentParser argumentParser = new ArgumentParser(args);

			// If we want to offer the player, set that variable and return
			try {
				gameParameters.setOfferType(argumentParser.getOffer());
				return gameParameters;
			} catch ( ArgumentParserException e ) {
				gameParameters.setOfferType(null);
			}

			gameParameters.setBoardSize(argumentParser.getSize());
			gameParameters.setRedType(argumentParser.getRed());
			gameParameters.setBlueType(argumentParser.getBlue());
			gameParameters.setDelay(argumentParser.getDelay());

			// Validate board size
			if ( gameParameters.getBoardSize() < 3
					|| gameParameters.getBoardSize() > 30
					|| gameParameters.getBoardSize() < 0 ) {
				throw new ArgumentParserException("Groeße des Spielfelds ist nicht gueltig.");
			}

			try {
				gameParameters.setDebug(argumentParser.isDebug());
			} catch ( ArgumentParserException e ) {
				gameParameters.setDebug(false);
			}

		} catch ( ArgumentParserException e ) {

			Log.log0(LogLevel.ERROR, LogModule.MAIN, "Invalid arguments passed: " + Arrays.toString(args));
			quitWithUsage();
		}

		return gameParameters;
	}

	public static void main( String[] args ) {

		new Game(parseArguments(args));
	}
}
