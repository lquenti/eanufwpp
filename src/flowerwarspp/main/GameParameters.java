package flowerwarspp.main;

import java.util.Arrays;

import flowerwarspp.preset.ArgumentParser;
import flowerwarspp.preset.ArgumentParserException;
import flowerwarspp.preset.PlayerType;
import flowerwarspp.util.log.*;

/**
 * Ein Daten-Konstrukt um gesammelt Parameter an die Spiel-Klassen weiterleiten zu können.
 *
 * @author Michael Merse
 */
class GameParameters {

	/**
	 * Die Größe des Spielbretts.
	 */
	private int boardSize;

	/**
	 * {@link PlayerType} des roten Spielers.
	 */
	private PlayerType redType;

	/**
	 * {@link PlayerType} des blauen Spielers.
	 */
	private PlayerType blueType;

	/**
	 * {@link PlayerType} des im Netzwerk anzubietenden Spielers.
	 */
	private PlayerType offerType;

	/**
	 * Verzögerung zwischen Zügen in Millisekunden.
	 */
	private int delay;

	/**
	 * Ob Debug-Informationen im {@link Log} angezeigt werden sollen, oder nicht.
	 */
	private boolean debug;

	/**
	 * Ob die Texteingabe verwendet werden soll.
	 */
	private boolean text;

	/**
	 * Name des zu ladenden Spielstands (falls geladen werden soll).
	 */
	private String saveGameName;

	private long replaySpeed;

	/**
	 * Erzeugt ein neues Objekt basierend auf den angegebenen Kommandozeilenparametern und versucht, diese zu parsen.
	 *
	 * @param args Die Kommandozeilenparameter
	 */
	public GameParameters(String[] args) {
		try {
			// set up
			ArgumentParser argumentParser = new ArgumentParser(args);

			// If we want to offer the player, set that variable and return
			try {
				offerType = argumentParser.getOffer();
				return;
			} catch ( ArgumentParserException e ) {
				offerType = null;
			}

			try {
				saveGameName = argumentParser.getLoad();
			} catch ( ArgumentParserException e ) {
				saveGameName = null;
			}

			try {
				replaySpeed = argumentParser.getReplay();
			} catch ( ArgumentParserException e ) {
				replaySpeed = -1;
			}

			boardSize = argumentParser.getSize();
			redType = argumentParser.getRed();
			blueType = argumentParser.getBlue();

			try {
				delay = argumentParser.getDelay();
			} catch ( ArgumentParserException e ) {
				delay = 0;
			}

			// Validate board size
			if ( getBoardSize() < 3
					|| getBoardSize() > 30
					|| getBoardSize() < 0 ) {
				throw new ArgumentParserException("Groeße des Spielfelds ist nicht gueltig.");
					}

			try {
				debug = argumentParser.isDebug();
			} catch ( ArgumentParserException e ) {
				debug = false;
			}

			try {
				text = argumentParser.isText();
			} catch ( ArgumentParserException e ) {
				text = false;
			}
		} catch ( ArgumentParserException e ) {
			Log.log0(LogLevel.ERROR, LogModule.MAIN, "Invalid arguments passed: " + Arrays.toString(args));
			Main.quitWithUsage();
		}
	}

	/**
	 * Gibt {@link #boardSize} zurück.
	 *
	 * @return Wert von {@link #boardSize}
	 */
	int getBoardSize() {
		return boardSize;
	}

	/**
	 * Gibt {@link #redType} zurück.
	 *
	 * @return Wert von {@link #redType}
	 */
	PlayerType getRedType() {
		return redType;
	}

	/**
	 * Gibt {@link #blueType} zurück.
	 *
	 * @return Wert von {@link #blueType}
	 */
	PlayerType getBlueType() {
		return blueType;
	}

	/**
	 * Gibt {@link #offerType} zurück.
	 *
	 * @return Wert von {@link #offerType}
	 */
	PlayerType getOfferType() {
		return offerType;
	}

	/**
	 * Gibt {@link #delay} zurück.
	 *
	 * @return Wert von {@link #delay}
	 */
	int getDelay() {
		return delay;
	}

	/**
	 * Gibt {@link #debug} zurück.
	 *
	 * @return Wert von {@link #debug}
	 */
	boolean getDebug() {
		return debug;
	}

	/**
	 * Gibt {@link #text} zurück.
	 *
	 * @return Wert von {@link #text}
	 */
	boolean getText() {
		return text;
	}

	/**
	 * Gibt {@link #saveGameName} zurück.
	 *
	 * @return Wert von {@link #saveGameName}
	 */
	public String getSaveGameName() {
		return saveGameName;
	}

	/**
	 * Gibt {@link #replaySpeed} zurück.
	 *
	 * @return Wert von {@link #replaySpeed}
	 */
	public long getReplaySpeed() {
		return replaySpeed;
	}

}
