package flowerwarspp.util.log;

import flowerwarspp.preset.PlayerColor;
import flowerwarspp.preset.Status;

import static flowerwarspp.preset.PlayerColor.Red;

/**
 * Hilfs-Klasse, welche statische Methoden zum Konvertieren zwischen Datentypen anbietet.
 */
public class Convert {
	/**
	 * Gibt die {@link String-Repräsentation} eines {@link Status} zurück.
	 * @param status Wert des {@link Status} dessen {@link String}-Repräsentation zurpckgegeben werden soll
	 * @return {@link String}-Repräsentation des gegebenen {@link Status}
	 */
	public static String statusToText( Status status ) {
		switch ( status ) {
			case Draw:
				return "Das Spiel endete unentschieden.";
			case RedWin:
				return "Der rote Spieler hat das Spiel gewonnen!";
			case BlueWin:
				return "Der blaue Spieler hat das Spiel gewonnen!";
			case Illegal:
			default:
				Log.log0(LogLevel.ERROR, LogModule.IO, "Invalid status passed to EndPopupFrame");
				return null;
		}
	}

	/**
	 * Gibt die {@link String}-Repräsentation von {@link PlayerColor} zurück.
	 *
	 * @param playerColor Farbe des Spielers als Enum
	 * @return Zum Enum passende {@link String}-Repräsentation
	 */
	public static String playerColorToString( PlayerColor playerColor ) {
		if ( playerColor == Red ) return "Red";
		else return "Blue";
	}


	/**
	 * Gibt die {@link String}-Repräsentation eines gegebenen {@link LogModule} zurück.
	 *
	 * @param module Das {@link LogModule} dessen {@link String}-Repräsentation ausgegeben werden soll
	 * @return {@link String}-Repräsentation des gegebenen Moduls.
	 */
	public static String logModuleToString( LogModule module ) {
		switch ( module ) {
			case ALL:
			default:
				return "(GENERIC)";
			case MAIN:
				return "(MAIN)";
			case BOARD:
				return "(BOARD)";
			case IO:
				return "(IO)";
			case PLAYER:
				return "(PLAYER)";
		}
	}

	/**
	 * Gibt die {@link String}-Repräsentation eines gegebenen {@link LogLevel} zurück.
	 *
	 * @param level Das {@link LogLevel} dessen {@link String}-Repräsentation ausgegeben werden soll
	 * @return {@link String}-Repräsentation des gegebenen Levels.
	 */
	public static String logLevelToString( LogLevel level ) {
		switch ( level ) {
			case NONE:
			default:
				return "[NONE]";

			case DUMP:
				return "[DUMP]";

			case DEBUG:
				return "[DEBUG]";

			case INFO:
				return "[INFO]";

			case WARNING:
				return "[WARNING]";

			case ERROR:
				return "[ERROR]";

			case CRITICAL:
				return "[CRITICAL]";
		}
	}

}
