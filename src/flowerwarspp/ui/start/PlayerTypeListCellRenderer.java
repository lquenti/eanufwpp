package flowerwarspp.ui.start;

import flowerwarspp.preset.PlayerType;
import flowerwarspp.util.Convert;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

// TODO: Dokumentation
public class PlayerTypeListCellRenderer implements ListCellRenderer<PlayerType> {
	// TODO: Dokumentation
	private Color defaultColor = Color.WHITE;

	// TODO: Dokumentation
	private Map<PlayerType, JTextField> templateComponents = new HashMap<>();

	// TODO: Dokumentation
	public PlayerTypeListCellRenderer(Color defaultColor) {
		this.defaultColor = defaultColor;
		templateComponents.put(null, new JTextField("NULL"));
		for (PlayerType playerType : PlayerType.values()) {
			// NOTE: JTextField kann Text-Alignment, aber der Border muss explizit entfernt werden
			JTextField textField = new JTextField(Convert.playerTypeToString(playerType));
			textField.setHorizontalAlignment(JTextField.RIGHT);
			textField.setBorder(null);
			templateComponents.put(playerType, textField);
		}
	}

	// TODO: Dokumentation
	@Override
	public Component getListCellRendererComponent(JList<? extends PlayerType> jList, PlayerType playerType, int i, boolean b, boolean b1) {
		JTextField textField = templateComponents.get(playerType);

		if (b)
			textField.setBackground(Color.LIGHT_GRAY);
		else if (b1)
			textField.setBackground(Color.CYAN);
		else
			textField.setBackground(defaultColor);

		return textField;
	}
}
