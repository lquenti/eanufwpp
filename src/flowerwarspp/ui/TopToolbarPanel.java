package flowerwarspp.ui;

import flowerwarspp.preset.PlayerColor;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.EnumMap;

public class TopToolbarPanel extends JPanel implements ChangeListener {

	/**
	 * Eine {@link java.util.Map}, die für jede {@link PlayerColor} ein {@link PlayerStatusDisplay}
	 * hält.
	 */
	private EnumMap<PlayerColor, PlayerStatusDisplay> playerStatusDisplays;

	/**
	 * Ein {@link JButton}, der das Speichern ermöglichen soll.
	 */
	private JButton saveButton = new JButton("\uD83D\uDCBESAVE!");

	/**
	 * Ein {@link JSlider}, der den Skalierungsfaktor der graphischen Oberfläche bestimmt.
	 * In Prozent.
	 */
	private JSlider zoomSlider = new JSlider(50, 250, 99);

	/**
	 * Ein {@link JLabel}, das den Skalierungsfaktor der graphischen Oberfläche anzeigt.
	 */
	private JLabel zoomLabel = new JLabel();

	public TopToolbarPanel() {
		playerStatusDisplays = new EnumMap<>(PlayerColor.class);
		for (PlayerColor playerColor : PlayerColor.values()) {
			Color playerDrawColor = GameColors.getColorForPlayerColor(playerColor);
			PlayerStatusDisplay playerStatusDisplay = new PlayerStatusDisplay(playerDrawColor);
			playerStatusDisplays.put(playerColor, playerStatusDisplay);
			add(playerStatusDisplay);
		}

		int idx = 1;
		add(saveButton, idx);
		idx++;
		add(zoomSlider, idx);

		zoomSlider.addChangeListener(this);
		zoomSlider.setValue(100);
		idx++;
		add(zoomLabel, idx);
	}

	/**
	 * Ein Getter für den {@link JSlider}, der den Zoom bestimmt.
	 *
	 * @return Der Slider, der den Zoom bestimmt.
	 */
	JSlider getZoomSlider() {
		return zoomSlider;
	}

	@Override
	public void stateChanged(ChangeEvent changeEvent) {
		if (changeEvent.getSource() == zoomSlider)
			zoomLabel.setText(Double.toString(zoomSlider.getValue() / 100.0) + "x ZOOMIES");
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
