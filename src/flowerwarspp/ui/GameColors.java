package flowerwarspp.ui;

import flowerwarspp.preset.Ditch;
import flowerwarspp.preset.Move;
import flowerwarspp.preset.PlayerColor;

import java.awt.*;

/**
 * Eine Klasse von Konstanten, die Farben betreffen.
 * Neben {@link Color}s für die graphische Ausgabe sind hier auch
 * Strings definiert, die eine farbige Ausgabe auf der Konsole ermöglichen.
 */
public final class GameColors {
	/*
	 * Die folgenden Color-Objekte sind konstant.
	 * Es soll keine Farbe, die auf der rechten Seite der folgenden Deklarationen steht
	 * irgenwdo im Code referenziert werden, außer durch diese Deklarationen.
	 */

	/**
	 * Die Farbe, die ein {@link Triangle} standardmäßig hat.
	 */
	public static final Color TRIANGLE_DEFAULT = Color.LIGHT_GRAY;
	/**
	 * Die Farbe, die ein angewähltes {@link Triangle} hat,
	 * bevor ein zweites für einen {@link Move} gewählt wurde.
	 */
	public static final Color TRIANGLE_CLICKED = new Color(0x966BAF);
	/**
	 * Die Farbe, die ein {@link Triangle} hat,
	 * wenn es mit dem aktuell angewählten kombinierbar ist.
	 */
	public static final Color TRIANGLE_COMBINABLE = new Color(0xB9EEA0);
	/**
	 * Die Farbe die eine {@link Edge} normalerweise hat.
	 */
	public static final Color EDGE_DEFAULT = Color.BLACK;
	/**
	 * Die Farbe die eine {@link Edge} hat,
	 * wenn es einen gültigen {@link Move} gibt, der den repräsentierten {@link Ditch} enthält.
	 */
	public static final Color EDGE_CLICKABLE = TRIANGLE_COMBINABLE;
	/**
	 * Die Farbe, die die {@link Dot}s standardmäßig haben.
	 */
	public static final Color DOT_DEFAULT = Color.BLACK;
	/**
	 * Die Farbe der Dreiecke, die dem {@link PlayerColor#Red} gehören.
	 */
	public static final Color RED = new Color(0xFF5255);
	/**
	 * Eine Farbe, die eine Blume symbolisiert, die in einem roten Garten ist.
	 */
	public static final Color RED_IN_GARDEN = new Color(0xC94143);
	/**
	 * Die Farbe der Dreiecke, die dem {@link PlayerColor#Blue} gehören.
	 */
	public static final Color BLUE = new Color(0x00DDFF);
	/**
	 * Eine Farbe, die eine Blume symbolisiert, die in einem roten Garten ist.
	 */
	public static final Color BLUE_IN_GARDEN = new Color(0x42B7C9);

	/**
	 * Ein ANSI-Escape-Farbcode, der für die Gräben des roten Spielers verwendet wird.
	 */
	public static final String ANSI_DITCH_RED = "\u001B[91m";

	/**
	 * Ein ANSI-Escape-Farbcode, der für die Blumen des roten Spielers verwendet wird.
	 */
	public static final String ANSI_FLOWER_RED = "\u001B[41m";

	/**
	 * Ein ANSI-Escape-Farbcode, der für die Gräben des blauen Spielers verwendet wird.
	 */
	public static final String ANSI_DITCH_BLUE = "\u001B[94m";

	/**
	 * Ein ANSI-Escape-Farbcode, der für die Blumen des blauen Spielers verwendet wird.
	 */
	public static final String ANSI_FLOWER_BLUE = "\u001B[44m";

	/**
	 * Ein ANSI-Escape-Farbcode, der für die Beschriftungen der Textausgabe verwendet wird.
	 */
	public static final String ANSI_GRID = "\u001B[90m";

	/**
	 * Ein ANSI-Escape-Code, der die Farben auf die Standardeinstellungen zurücksetzt.
	 */
	public static final String ANSI_RESET = "\u001B[0m";

	/**
	 * Gibt für jede {@link PlayerColor} das entsprechende {@link Color}-Objekt zurück.
	 *
	 * @param playerColor
	 * Die {@link PlayerColor}, für die die {@link Color} zurückgegeben werden soll.
	 *
	 * @return
	 * Die zur {@link PlayerColor} passende {@link Color} im "normalen" Zustand."
	 */
	public static Color getColorForPlayerColor(PlayerColor playerColor) {
		switch (playerColor) {
			case Blue:
				return BLUE;
			case Red:
				return RED;
			default:
				return Color.WHITE;
		}
	}

	/**
	 * Gibt den ANSI-Escape-Code für die Blumenfarbe für eine {@link PlayerColor} zurück.
	 *
	 * @param color
	 * Die {@link PlayerColor}, für die der Farbstring zurückgegeben werden soll.
	 *
	 * @return
	 * Der zur {@link PlayerColor} gehörende ANSI-Escape-Code.
	 */
	public static String getAnsiFlowerColor(PlayerColor color) {
		if (color == null) {
			return "";
		}
		switch(color) {
			case Blue: return ANSI_FLOWER_BLUE;
			case Red: return ANSI_FLOWER_RED;
		}
		return "";
	}

	/**
	 * Gibt den ANSI-Escape-Code für die Grabenfarbe für eine {@link PlayerColor} zurück.
	 *
	 * @param color
	 * Die {@link PlayerColor}, für die der Farbstring zurückgegeben werden soll.
	 *
	 * @return
	 * Der zur {@link PlayerColor} gehörende ANSI-Escape-Code.
	 */
	public static String getAnsiDitchColor(PlayerColor color) {
		if (color == null) {
			return "";
		}
		switch(color) {
			case Blue: return ANSI_DITCH_BLUE;
			case Red: return ANSI_DITCH_RED;
		}
		return "";
	}
}
