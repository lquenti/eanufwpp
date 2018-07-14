package flowerwarspp.ui;

import flowerwarspp.preset.PlayerColor;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.EnumMap;
import java.util.Hashtable;

// TODO
public class TopToolbarPanel extends JPanel {

	/**
	 * Eine {@link java.util.Map}, die für jede {@link PlayerColor} ein {@link PlayerStatusDisplay}
	 * hält.
	 */
	private EnumMap<PlayerColor, PlayerStatusDisplay> playerStatusDisplays;

	/**
	 * Ein {@link JButton}, der das Speichern ermöglichen soll.
	 */
	private JButton saveButton = new JButton("Spielstand speichern");

	/**
	 * Ein {@link JSlider}, der den Skalierungsfaktor der graphischen Oberfläche bestimmt.
	 * In Prozent.
	 */
	private JSpinner zoomSpinner = new JSpinner(new SpinnerNumberModel(100, 100, 300, 25));

	// TODO
	public TopToolbarPanel() {
		playerStatusDisplays = new EnumMap<>(PlayerColor.class);
		for (PlayerColor playerColor : PlayerColor.values()) {
			Color playerDrawColor = GameColors.getColorForPlayerColor(playerColor);
			PlayerStatusDisplay playerStatusDisplay = new PlayerStatusDisplay(playerDrawColor);
			playerStatusDisplays.put(playerColor, playerStatusDisplay);
			add(playerStatusDisplay);
		}

		add(saveButton, 1);
		add(zoomSpinner, 2);
	}

	/**
	 * Ein Getter für den {@link JSlider}, der den Zoom bestimmt.
	 *
	 * @return Der Slider, der den Zoom bestimmt.
	 */
	JSpinner getZoomSpinner() {
		return zoomSpinner;
	}

	/**
	 * Setzt die Anzahl der Punkte  für einen Spieler.
	 *
	 * @param playerColor
	 * Die {@link PlayerColor} des Spielers, deren Punktzahl geändert werden soll.
	 *
	 * @param playerPoints
	 * Die Anzahl der Punkte, die angezeigt werden soll.
	 */
	public void updatePlayerStatus(PlayerColor playerColor, int playerPoints) {
		playerStatusDisplays.get(playerColor).updateStatus(playerPoints);
	}
}
