package flowerwarspp.ui.component;

import flowerwarspp.preset.PlayerColor;
import flowerwarspp.util.Convert;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Die Klasse, die die Toolbar an der unteren Seite des {@link BoardFrame}s hält.
 */
public class BottomToolbarPanel extends JPanel {
	/**
	 * Das {@link JLabel}, das einen Text enthält, der anzeigt, welcher Spieler am Zug ist.
	 */
	private JLabel currentPlayerLabel = new JLabel();

	/**
	 * Der {@link JButton}, der geklickt werden kann, wenn der Spieler aufgeben möchte.
	 */
	private JButton surrenderButton = new JButton("Aufgeben");
	/**
	 * Der {@link JButton}, der geklickt werden kann, wenn der Spieler das Spiel beenden möchte
	 * (siehe Spielregeln, was ein Endzug ist).
	 */
	private JButton endButton = new JButton("Spiel beenden");

	/**
	 * Konstruiert ein {@link JPanel}, das eine Toolbar ist, die für das Spielen verwendet wird.
	 */
	public BottomToolbarPanel() {
		BorderLayout layoutManager = new BorderLayout();
		setLayout(layoutManager);

		Border playerLabelBorder = new EmptyBorder(0, 10, 0, 10);
		currentPlayerLabel.setBorder(playerLabelBorder);
		add(currentPlayerLabel, BorderLayout.WEST);

		JPanel buttonContainer = new JPanel();
		buttonContainer.add(surrenderButton);
		buttonContainer.add(endButton);
		add(buttonContainer, BorderLayout.EAST);
	}

	/**
	 * Enabled oder disabled den {@link #surrenderButton}.
	 *
	 * @param enabled
	 * 		<code>true</code> oder <code>false</code>, je nachdem, ob der Button aktiv sein soll.
	 */
	public void setSurrenderEnabled(boolean enabled) {
		surrenderButton.setEnabled(enabled);
	}

	/**
	 * Enabled oder disabled den {@link #endButton}.
	 *
	 * @param enabled
	 * 		<code>true</code> oder <code>false</code>, je nachdem, ob der Button aktiv sein soll.
	 */
	public void setEndEnabled(boolean enabled) {
		endButton.setEnabled(enabled);
	}

	/**
	 * Setzt den Text des {@link #currentPlayerLabel}s.
	 */
	public void setTurnDisplay(PlayerColor color) {
		currentPlayerLabel.setText(Convert.playerColorToString(color) + " ist am Zug");
	}

	/**
	 * Ein Getter für den {@link #surrenderButton}.
	 *
	 * @return Den {@link #surrenderButton}.
	 */
	JButton getSurrenderButton() {
		return surrenderButton;
	}

	/**
	 * Ein Getter für den {@link #endButton}.
	 *
	 * @return Den {@link #endButton}.
	 */
	JButton getEndButton() {
		return endButton;
	}
}
