package flowerwarspp.main;

import flowerwarspp.preset.PlayerType;
import flowerwarspp.util.Convert;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PlayerTypeListCellRenderer implements ListCellRenderer<PlayerType> {
	private Color defaultColour = Color.WHITE;
	private Map<PlayerType, JTextArea> templateComponents = new HashMap<>();

	public PlayerTypeListCellRenderer(Color defaultColour) {
		this.defaultColour = defaultColour;
		templateComponents.put(null, new JTextArea("NULL"));
		for (PlayerType playerType : PlayerType.values())
			templateComponents.put(playerType, new JTextArea(Convert.playerTypeToString(playerType)));
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends PlayerType> jList, PlayerType playerType, int i, boolean b, boolean b1) {
		JTextArea area = templateComponents.get(playerType);

		if (b)
			area.setBackground(Color.LIGHT_GRAY);
		else if (b1)
			area.setBackground(Color.CYAN);
		else
			area.setBackground(defaultColour);

		return area;
	}
}
