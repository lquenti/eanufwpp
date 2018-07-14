package flowerwarspp.main;

import java.util.Arrays;

import flowerwarspp.preset.ArgumentParser;
import flowerwarspp.preset.ArgumentParserException;
import flowerwarspp.preset.PlayerType;
import flowerwarspp.util.log.*;

/**
 * Ein Daten-Konstrukt um gesammelt Parameter an die Spiel-Klassen weiterleiten zu können.
 */
public class GameParameters {

	/**
	 * Die Größe des Spielbretts.
	 */
	private int boardSize = 0;

	/**
	 * {@link PlayerType} des roten Spielers.
	 */
	private PlayerType redType = null;

	/**
	 * Url des roten Spielers im Fall eines Netzwerspiels.
	 */
	private String redUrl = null;

	/**
	 * {@link PlayerType} des blauen Spielers.
	 */
	private PlayerType blueType = null;

	/**
	 * Url des blauen Spielers im Fall eines Netzwerspiels.
	 */
	private String blueUrl = null;

	/**
	 * {@link PlayerType} des im Netzwerk anzubietenden Spielers.
	 */
	private PlayerType offerType = null;

	/**
	 * Url des angebotenen Spielers im Fall, dass ein Netzwerkspieler angeboten wird.
	 */
	private String offerUrl = null;

	/**
	 * Name des im Netzwerk anzubietenden Spielers.
	 */
	private String offerName = null;

	/**
	 * Port, unter dem der Spieler im Netzwerk angeboten werden soll.
	 */
	private int offerPort = 1099;

	/**
	 * Verzögerung zwischen Zügen in Millisekunden.
	 */
	private int delay = 0;

	/**
	 * Ob Debug-Informationen im {@link Log} angezeigt werden sollen, oder nicht.
	 */
	private boolean debug = false;

	/**
	 * Ob die Texteingabe verwendet werden soll.
	 */
	private boolean text = false;

	/**
	 * Ob der Spielverlauf ausgegeben werden soll.
	 */
	private boolean quiet = false;

	/**
	 * Anzahl der Spiele, die gespielt werden sollen.
	 */
	private int numberOfGames = 1;

	/**
	 * Name des zu ladenden Spielstands (falls geladen werden soll).
	 */
	private String saveGameName = null;

	/**
	 * <code>true</code> falls ein Spielstand geladen werden soll, <code>false</code> andererseits.
	 */
	private boolean loadGame = false;

	/**
	 * Die Zeit in MS zwischen Zügen beim Replay eines geladenen Spielstands.
	 */
	private long replaySpeed = - 1;

	/**
	 * Erzeugt ein neues Objekt basierend auf den angegebenen Kommandozeilenparametern und versucht, diese zu parsen.
	 *
	 * @param args Die Kommandozeilenparameter
	 */
	public GameParameters(String[] args) {
		try {
			// Einen ArgumentParser instanziieren, damit Kommandozeilenargumente geparsed werden können.
			ArgumentParser argumentParser = new ArgumentParser(args);

			// Help-Schalter überprüfen
			if (argumentParser.isSet("help"))
				Main.quitWithUsage();

			// Debug-Schalter überprüfen
			debug = argumentParser.isSet("debug");

			// Text-Schalter überprüfen
			text = argumentParser.isSet("text");

			// Quiet-Schalter überprüfen
			quiet = argumentParser.isSet("quiet");

			// Wenn ein Spieler im Netzwerk angeboten werden soll, werden die notwendigen Einstellungen geparsed und
			// dann die Methode verlassen.
			if (argumentParser.isSet("offer")) {
				System.out.println("Biete Netzwerkspieler an, andere Argumente werden ignoriert.");
				offerType = argumentParser.getOffer();
				offerName = argumentParser.getOfferName();
				if (argumentParser.isSet("port")) {
					offerPort = argumentParser.getOfferPort();
				}
				if (offerType == PlayerType.REMOTE) {
					offerUrl = argumentParser.getOfferUrl();
				}
				return;
			}

			// Games-Einstellung überprüfen
			if (argumentParser.isSet("games"))
				numberOfGames = argumentParser.getNumberOfGames();

			if (argumentParser.isSet("delay")) {
				delay = argumentParser.getDelay();
			}

			redType = argumentParser.getRed();
			if (redType == PlayerType.REMOTE) {
				redUrl = argumentParser.getRedUrl();
			}

			blueType = argumentParser.getBlue();
			if (blueType == PlayerType.REMOTE) {
				blueUrl = argumentParser.getBlueUrl();
			}

			if (argumentParser.isSet("replay")) {
				replaySpeed = argumentParser.getReplay();
			}

			if (argumentParser.isSet("load")) {
				saveGameName = argumentParser.getLoad();
				loadGame = true;
				return;
			}

			boardSize = argumentParser.getSize();

			// Validate board size
			if (getBoardSize() < 3
					|| getBoardSize() > 30
					|| getBoardSize() < 0) {
				throw new ArgumentParserException("Groeße des Spielfelds ist nicht gueltig.");
			}

		} catch (ArgumentParserException e) {
			Log.log(LogLevel.ERROR, LogModule.MAIN, "Invalid arguments passed: " + Arrays.toString(args));
			Main.quitWithUsage();
		}
	}

	public GameParameters(int boardSize,
	                      PlayerType redType,
	                      PlayerType blueType,
	                      int delay) {
		this.boardSize = boardSize;
		this.redType = redType;
		this.blueType = blueType;
		this.delay = delay;
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
	 * Gibt {@link #redUrl} zurück.
	 *
	 * @return Wert von {@link #redUrl}
	 */
	String getRedUrl() {
		return redUrl;
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
	 * Gibt {@link #blueUrl} zurück.
	 *
	 * @return Wert von {@link #blueUrl}
	 */
	String getBlueUrl() {
		return blueUrl;
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
	 * Gibt {@link #offerUrl} zurück.
	 *
	 * @return Wert von {@link #offerUrl}
	 */
	String getOfferUrl() {
		return offerUrl;
	}

	/**
	 * Gibt {@link #offerName} zurück.
	 *
	 * @return Wert von {@link #offerName}
	 */
	String getOfferName() {
		return offerName;
	}

	/**
	 * Gibt {@link #offerPort} zurück.
	 *
	 * @return Wert von {@link #offerPort}
	 */
	int getOfferPort() {
		return offerPort;
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
	 * Gibt {@link #quiet} zurück.
	 *
	 * @return Wert von {@link #quiet}
	 */
	boolean getQuiet() {
		return quiet;
	}

	/**
	 * Gibt {@link #numberOfGames} zurück.
	 *
	 * @return Wert von {@link #numberOfGames}
	 */
	int getNumberOfGames() {
		return numberOfGames;
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


	/**
	 * Gibt {@link #loadGame} zurück.
	 *
	 * @return Wert von {@link #loadGame()}
	 */
	public boolean loadGame() {
		return loadGame;
	}
}
