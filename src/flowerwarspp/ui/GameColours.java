package flowerwarspp.ui;

import flowerwarspp.preset.Ditch;
import flowerwarspp.preset.Move;
import flowerwarspp.preset.PlayerColor;

import java.awt.*;

public final class GameColours {
	/*
	 * Die folgenden Color-Objekte sind konstant.
	 * Es soll keine Farbe, die auf der rechten Seite der folgenden Deklarationen steht
	 * irgenwdo im Code referenziert werden, außer durch diese Deklarationen.
	 */

	/**
	 * Die Farbe, die ein {@link Triangle} standardmäßig hat.
	 */
	public static final Color triangleDefaultColour = Color.LIGHT_GRAY;
	/**
	 * Die Farbe, die ein angewähltes {@link Triangle} hat,
	 * bevor ein zweites für einen {@link Move} gewählt wurde.
	 */
	public static final Color triangleClickedColour = new Color(0x966BAF);
	/**
	 * Die Farbe, die ein {@link Triangle} hat,
	 * wenn es mit dem aktuell angewählten kombinierbar ist.
	 */
	public static final Color triangleCombinableColour = new Color(0xB9EEA0);
	/**
	 * Die Farbe die eine {@link Edge} normalerweise hat.
	 */
	public static final Color edgeDefaultColour = Color.BLACK;
	/**
	 * Die Farbe die eine {@link Edge} hat,
	 * wenn es einen gültigen {@link Move} gibt, der den repräsentierten {@link Ditch} enthält.
	 */
	public static final Color edgeClickableColour = triangleCombinableColour;
	/**
	 * Die Farbe, die die {@link Dot}s standardmäßig haben.
	 */
	public static final Color dotDefaultColor = Color.BLACK;
	/**
	 * Die Farbe der Dreiecke, die dem {@link PlayerColor#Red} gehören.
	 */
	public static final Color redColour = new Color(0xFF5255);
	/**
	 * Eine Farbe, die eine Blume symbolisiert, die in einem roten Garten ist.
	 */
	public static final Color redInGardenColour = new Color(0xC94143);
	/**
	 * Die Farbe der Dreiecke, die dem {@link PlayerColor#Blue} gehören.
	 */
	public static final Color blueColour = new Color(0x00DDFF);
	/**
	 * Eine Farbe, die eine Blume symbolisiert, die in einem roten Garten ist.
	 */
	public static final Color blueInGardenColour = new Color(0x42B7C9);

	/**
	 * Gibt für jede {@link PlayerColor} das entsprechende {@link Color}-Objekt zurück.
	 *
	 * @param playerColor
	 * Die {@link PlayerColor}, für die die {@link Color} zurückgegeben werden soll.
	 *
	 * @return
	 * Die zur {@link PlayerColor} passende {@link Color} im "normalen" Zustand."
	 */
	public static Color getColourForPlayerColor(PlayerColor playerColor) {
		switch (playerColor) {
			case Blue:
				return blueColour;
			case Red:
				return redColour;
			default:
				return Color.WHITE;
		}
	}
}
