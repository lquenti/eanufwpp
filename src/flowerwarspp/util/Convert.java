package flowerwarspp.util;

import flowerwarspp.preset.PlayerColor;
import flowerwarspp.preset.Status;
import flowerwarspp.util.log.Log;
import flowerwarspp.util.log.LogLevel;
import flowerwarspp.util.log.LogModule;

/**
 * Hilfs-Klasse, welche statische Methoden zum Konvertieren zwischen Datentypen anbietet.
 */
public class Convert {
	/**
	 * Leerer privater Konstruktor zum Verhindern der Objektinitialisierung
	 */
	private Convert() {}

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
