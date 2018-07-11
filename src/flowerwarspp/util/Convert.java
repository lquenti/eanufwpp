package flowerwarspp.util;

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
				Log.log(LogLevel.ERROR, LogModule.IO, "Invalid status passed to EndPopupFrame");
				return null;
		}
	}
}
