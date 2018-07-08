package flowerwarspp.main;

import flowerwarspp.preset.PlayerType;
import flowerwarspp.util.log.Log;

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
	 * Gibt {@link #boardSize} zurück.
	 *
	 * @return Wert von {@link #boardSize}
	 */
	int getBoardSize() {
		return boardSize;
	}

	/**
	 * Setzt einen neuen Wert für {@link #boardSize}.
	 *
	 * @param boardSize Neuer Wert für {@link #boardSize}
	 */
	void setBoardSize( int boardSize ) {
		this.boardSize = boardSize;
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
	 * Setzt einen neuen Wert für {@link #redType}.
	 *
	 * @param redType Neuer Wert für {@link #redType}
	 */
	void setRedType( PlayerType redType ) {
		this.redType = redType;
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
	 * Setzt einen neuen Wert für {@link #blueType}.
	 *
	 * @param blueType Neuer Wert für {@link #blueType}
	 */
	void setBlueType( PlayerType blueType ) {
		this.blueType = blueType;
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
	 * Setzt einen neuen Wert für {@link #offerType}.
	 *
	 * @param offerType Neuer Wert für {@link #offerType}
	 */
	void setOfferType( PlayerType offerType ) {
		this.offerType = offerType;
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
	 * Setzt einen neuen Wert für {@link #delay}.
	 *
	 * @param delay Neuer Wert für {@link #delay}
	 */
	void setDelay( int delay ) {
		this.delay = delay;
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
	 * Setzt einen neuen Wert für {@link #debug}.
	 *
	 * @param debug Neuer Wert für {@link #debug}
	 */
	void setDebug( boolean debug ) {
		this.debug = debug;
	}
}
