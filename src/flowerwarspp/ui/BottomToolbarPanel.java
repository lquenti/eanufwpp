package flowerwarspp.ui;

import flowerwarspp.preset.PlayerColor;
import flowerwarspp.util.Convert;

import java.awt.*;
import javax.swing.border.*;
import javax.swing.*;

/**
 * Die Klasse, die die Toolbar an der unteren Seite des {@link BoardFrame}s hält.
 */
public class BottomToolbarPanel extends JPanel {
	/**
	 * Das {@link JLabel}, das einen Text enthält, der anzeigt, welcher Spieler am Zug ist.
	 */
	private JLabel currentPlayerLabel = new JLabel();
	/**
	 * Der Border der um das {@link #currentPlayerLabel} gelegt wird.
	 */
	// NOTE: Oben, links, unten, rechts.
	private Border playerLabelBorder = new EmptyBorder(0, 10, 0, 10);
	/**
	 * Das {@link JPanel}, das die Buttons containt.
	 */
	private JPanel buttonContainer = new JPanel();
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
	 * Der {@link LayoutManager}, der für das Layout dieses {@link JPanel}s verantwortlich ist.
	 */
	private BorderLayout layoutManager = new BorderLayout();

	public BottomToolbarPanel() {
		setLayout(layoutManager);

		currentPlayerLabel.setBorder(playerLabelBorder);
		add(currentPlayerLabel, BorderLayout.WEST);

		buttonContainer.add(surrenderButton);
		buttonContainer.add(endButton);
		add(buttonContainer, BorderLayout.EAST);
	}

	/**
	 * Enablet oder disablet den {@link #surrenderButton}.
	 *
	 * @param enabled
	 * <code>true</code> oder <code>false</code>, je nachdem, ob der Button aktiv sein soll.
	 */
	public void setSurrenderEnabled(boolean enabled) {
		surrenderButton.setEnabled(enabled);
	}

	/**
	 * Enablet oder disablet den {@link #endButton}.
	 *
	 * @param enabled
	 * <code>true</code> oder <code>false</code>, je nachdem, ob der Button aktiv sein soll.
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
	 * @return
	 * Den {@link #surrenderButton}.
	 */
	JButton getSurrenderButton() {
		return surrenderButton;
	}

	/**
	 * Ein Getter für den {@link #endButton}.
	 *
	 * @return
	 * Den {@link #endButton}.
	 */
	JButton getEndButton() {
		return endButton;
	}
}
