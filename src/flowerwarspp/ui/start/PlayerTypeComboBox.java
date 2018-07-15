package flowerwarspp.ui.start;

import java.awt.Component;
import java.util.Collection;

import flowerwarspp.player.Players;
import flowerwarspp.preset.PlayerType;
import flowerwarspp.util.Convert;
import javax.swing.*;

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
	 * {@link Players#AVAILABLE_PLAYER_TYPES} auswählen lassen.
	 */
	public PlayerTypeComboBox() {
		this(Players.AVAILABLE_PLAYER_TYPES);
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
