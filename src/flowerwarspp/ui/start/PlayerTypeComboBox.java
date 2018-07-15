package flowerwarspp.ui.start;

import flowerwarspp.preset.PlayerType;

import javax.swing.*;
import java.util.Collection;

/**
 * Eine {@link JComboBox} für {@link PlayerType}s.
 */
public class PlayerTypeComboBox extends JComboBox<PlayerType> {
	/**
	 * Konstruiert eine {@link JComboBox} von einer {@link Collection},
	 * die {@link PlayerType}s hält.
	 *
	 * @param playerTypes
	 * Die {@link Collection}%lt;{@link PlayerType}%gt;,
	 * die in dieser {@link JComboBox} auswählbar sein sollen.
	 */
	public PlayerTypeComboBox(Collection<PlayerType> playerTypes) {
		for (PlayerType playerType : playerTypes) {
			addItem(playerType);
		}
	}

	/**
	 * Konstruiert eine {@link JComboBox}, mit der sich alle Elemente aus
	 * {@link GameStartPanel#availablePlayerTypes} auswählen lassen.
	 */
	public PlayerTypeComboBox() {
		this(GameStartPanel.availablePlayerTypes);
	}

	// NOTE: Das heißt "Covariant Return Type"

	/**
	 * Gibt den aktuell gewählten {@link PlayerType} zurück.
	 *
	 * @return
	 * Den aktuell gewählten {@link PlayerType}.
	 */
	@Override
	public PlayerType getSelectedItem() {
		return getItemAt(getSelectedIndex());
	}
}
