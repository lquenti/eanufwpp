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
	private Map<PlayerType, JTextArea> templateComponents = new HashMap<>();

	// TODO: Dokumentation
	public PlayerTypeListCellRenderer(Color defaultColor) {
		this.defaultColor = defaultColor;
		templateComponents.put(null, new JTextArea("NULL"));
		for (PlayerType playerType : PlayerType.values())
			templateComponents.put(playerType, new JTextArea(Convert.playerTypeToString(playerType)));
	}

	// TODO: Dokumentation
	@Override
	public Component getListCellRendererComponent(JList<? extends PlayerType> jList, PlayerType playerType, int i, boolean b, boolean b1) {
		JTextArea area = templateComponents.get(playerType);

		if (b)
			area.setBackground(Color.LIGHT_GRAY);
		else if (b1)
			area.setBackground(Color.CYAN);
		else
			area.setBackground(defaultColor);

		return area;
	}
}
