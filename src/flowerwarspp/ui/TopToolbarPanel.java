package flowerwarspp.ui;

import java.util.EnumMap;
import javax.swing.border.EmptyBorder;

import flowerwarspp.preset.PlayerColor;
import java.awt.*;
import javax.swing.*;

/**
 * Ein {@link JPanel}, das {@link JComponent}s für allgemeine Spielinformationen enthält.
 */
public class TopToolbarPanel extends JPanel {
	/**
	 * Eine {@link java.util.Map}, die für jede {@link PlayerColor} ein {@link PlayerStatusDisplay}
	 * hält.
	 */
	private EnumMap<PlayerColor, PlayerStatusDisplay> playerStatusDisplays;

	/**
	 * Ein {@link JButton}, der das Speichern ermöglichen soll.
	 */
	private JButton saveButton = new JButton("Spielstand speichern…");

	/**
	 * Das NumberModel für {@link #zoomSpinner}.
	 */
	private SpinnerNumberModel zoomSpinnerNumberModel = new SpinnerNumberModel(100, 100, 300, 25);

	/**
	 * Ein {@link JSlider}, der den Skalierungsfaktor der graphischen Oberfläche bestimmt.
	 * In Prozent.
	 */
	private JSpinner zoomSpinner = new JSpinner(zoomSpinnerNumberModel);

	/**
	 * Konstruiert ein {@link TopToolbarPanel} und fügt notwendige {@link JComponent}s hinzu.
	 */
	public TopToolbarPanel() {
		setLayout(new GridLayout(1, 3));

		// Mehrere Panels ineinander, damit das vertikal zentriert ist.
		JPanel leftPanel = new JPanel(new GridBagLayout());
		leftPanel.add(saveButton);
		JPanel leftWrapper = new JPanel(new BorderLayout());
		leftWrapper.setBorder(new EmptyBorder(0, 5, 0, 0));
		leftWrapper.add(leftPanel, BorderLayout.WEST);
		add(leftWrapper);

		JPanel centerPanel = new JPanel();
		playerStatusDisplays = new EnumMap<>(PlayerColor.class);
		for (PlayerColor playerColor : PlayerColor.values()) {
			Color playerDrawColor = GameColors.getColorForPlayerColor(playerColor);
			PlayerStatusDisplay playerStatusDisplay = new PlayerStatusDisplay(playerDrawColor);
			playerStatusDisplays.put(playerColor, playerStatusDisplay);
			centerPanel.add(playerStatusDisplay);
		}
		add(centerPanel);

		// Mehrere Panels ineinander, damit das vertikal zentriert ist.
		JPanel rightPanel = new JPanel(new GridBagLayout());
		rightPanel.add(zoomSpinner);
		rightPanel.add(new JLabel("% Zoom"));
		JPanel rightWrapper = new JPanel(new BorderLayout());
		rightWrapper.setBorder(new EmptyBorder(0, 0, 0, 5));
		rightWrapper.add(rightPanel, BorderLayout.EAST);
		add(rightWrapper);
	}

	/**
	 * Ein Getter fuer den {@link #saveButton}.
	 */
	JButton getSaveButton() {
		return saveButton;
	}

	/**
	 * Ein Getter für den {@link JSlider}, der den Zoom bestimmt.
	 *
	 * @return Der Spinner, der den Zoom bestimmt.
	 */
	JSpinner getZoomSpinner() {
		return zoomSpinner;
	}

	/**
	 * Ein Getter für das {@link SpinnerNumberModel}, das den Zoom bestimmt.
	 *
	 * @return Das {@link SpinnerNumberModel}, das den Zoom bestimmt.
	 */
	SpinnerNumberModel getZoomSpinnerNumberModel() {
		return zoomSpinnerNumberModel;
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
