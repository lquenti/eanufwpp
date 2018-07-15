package flowerwarspp.util;

import flowerwarspp.preset.PlayerColor;
import flowerwarspp.preset.PlayerType;
import flowerwarspp.preset.Status;
import flowerwarspp.util.log.Log;
import flowerwarspp.util.log.LogLevel;
import flowerwarspp.util.log.LogModule;

/**
 * Hilfs-Klasse, welche statische Methoden zum Konvertieren zwischen Datentypen anbietet.
 */
public class Convert {
	/**
	 * Gibt die {@link String}-Repräsentation eines {@link Status} zurück.
	 *
	 * @param status
	 * 		Wert des {@link Status} dessen {@link String}-Repräsentation zurpckgegeben werden soll
	 *
	 * @return {@link String}-Repräsentation des gegebenen {@link Status}
	 */
	public static String statusToText(Status status) {
		switch (status) {
			case Draw:
				return "Das Spiel endete unentschieden.";
			case RedWin:
				return "Der rote Spieler hat das Spiel gewonnen!";
			case BlueWin:
				return "Der blaue Spieler hat das Spiel gewonnen!";
			case Illegal:
				return "Ein Spieler hat einen verbotenen Zug gemacht.";
			default:
				Log.log(LogLevel.ERROR, LogModule.UI, "Invalid status passed to statusToText");
				return null;
		}
	}

	/**
	 * Gibt einen formatierten, lesbaren {@link String} für einen gegebenen {@link PlayerType}
	 * zurück.
	 *
	 * @param playerType
	 * 		Der {@link PlayerType} der als {@link String} zurück gegeben werden soll.
	 *
	 * @return Typ des Spielers als {@link String}.
	 */
	public static String playerTypeToString(PlayerType playerType) {
		switch (playerType) {
			case HUMAN:
				return "Mensch";
			case RANDOM_AI:
				return "Zufälliger Computerspieler";
			case SIMPLE_AI:
				return "Einfacher Computerspieler";
			case ADVANCED_AI_1:
				return "Fortgeschrittener Computerspieler 1";
			case ADVANCED_AI_2:
				return "Fortgeschrittener Computerspieler 2";
			case ADVANCED_AI_3:
				return "Fortgeschrittener Computerspieler 3";
			case ADVANCED_AI_4:
				return "Fortgeschrittener Computerspieler 4";
			case ADVANCED_AI_5:
				return "Fortgeschrittener Computerspieler 5";
			case REMOTE:
				return "Netzwerkspieler";
			default:
				return "ILLEGAL VALUE";
		}
	}

	/**
	 * Gibt eine übersetzte {@link String}-Repräsentation einer {@link PlayerColor} zurück.
	 *
	 * @param color
	 * 		Zu übersetzende Spieler-Farbe.
	 *
	 * @return Übersetzte {@link String}-Repräsentation der Farbe
	 */
	public static String playerColorToString(PlayerColor color) {
		switch (color) {
			case Red:
				return "Rot";
			case Blue:
				return "Blau";
		}
		return null;
	}
}
