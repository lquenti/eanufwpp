package flowerwarspp.ui.start;

import flowerwarspp.preset.PlayerType;
import flowerwarspp.util.Convert;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PlayerTypeListCellRenderer implements ListCellRenderer<PlayerType> {
	private Color defaultColor = Color.WHITE;
	private Map<PlayerType, JTextField> templateComponents = new HashMap<>();

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
