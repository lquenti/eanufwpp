package flowerwarspp.ui.start;

import flowerwarspp.preset.PlayerType;
import flowerwarspp.util.Convert;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Ein {@link ListCellRenderer}, der {@link PlayerType}s zeichnet.
 */
public class PlayerTypeListCellRenderer implements ListCellRenderer<PlayerType> {
	/**
	 * Die Standardhintergrundfarbe
	 */
	private Color defaultColour;
	private Color selectedColour = Color.LIGHT_GRAY;

	// TODO: Dokumentation
	private Map<PlayerType, JTextField> templateComponents = new HashMap<>();

	// TODO: Dokumentation
	public PlayerTypeListCellRenderer(Color defaultColor) {
		this.defaultColour = defaultColor;
		templateComponents.put(null, new JTextField("NULL"));
		for (PlayerType playerType : PlayerType.values()) {
			// NOTE: JTextField kann Text-Alignment, aber der Border muss explizit entfernt werden
			JTextField textField = new JTextField(Convert.playerTypeToString(playerType));
			textField.setHorizontalAlignment(JTextField.RIGHT);
			textField.setBorder(null);
			templateComponents.put(playerType, textField);
		}
	}

	/**
	 * Gibt ein {@link JComponent} zurück, das eine graphische Repräsentation
	 * des {@link PlayerType}s in dieser Zelle ist.
	 *
	 * @param elementList
	 * Die {@link JList}, die gezeichnet werden soll.
	 *
	 * @param playerType
	 * Der aktuell gewählte {@link PlayerType}.
	 *
	 * @param cellIndex
	 * Der Index der Zelle.
	 *
	 * @param isSelected
	 * <code>true</code> genau dann, wenn diese Zelle gewählt ist.
	 *
	 * @param hasFocus
	 * Ob diese Zelle fokussiert ist.
	 *
	 * @return
	 * Ein {@link JComponent}, das den Wert dieser Zelle repräsentiert.
	 */
	@Override
	public JComponent getListCellRendererComponent(JList<? extends PlayerType> elementList,
	                                              PlayerType playerType,
	                                              int cellIndex,
	                                              boolean isSelected,
	                                              boolean hasFocus) {
		JTextField textField = templateComponents.get(playerType);

		if (isSelected)
			textField.setBackground(selectedColour);
		else
			textField.setBackground(defaultColour);

		return textField;
	}
}
