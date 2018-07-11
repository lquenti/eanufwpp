package flowerwarspp.io;

import javax.swing.*;
import java.awt.event.MouseAdapter;

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
	private JButton surrenderButton = new JButton("Surrender");
	/**
	 * Der {@link JButton}, der geklickt werden kann, wenn der Spieler das Spiel beenden möchte
	 * (siehe Spielregeln, was ein Endzug ist).
	 */
	private JButton endButton = new JButton("End");

	public BottomToolbarPanel() {
		add(currentPlayerLabel);
		add(surrenderButton);
		add(endButton);
	}

	/**
	 * Setzt den {@link MouseAdapter} der Buttons. Muss in der Lage sein,
	 * die beiden {@link JButton}s auseinander zu halten! {@link #getSurrenderButton()}.
	 *
	 * @param mouseAdapter
	 * Der {@link MouseAdapter}, der die Buttons handhaben soll.
	 */
	void setButtonClickListener(MouseAdapter mouseAdapter) {
		surrenderButton.addMouseListener(mouseAdapter);
		endButton.addMouseListener(mouseAdapter);
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
	public void setLabelText(String text) {
		currentPlayerLabel.setText(text);
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
