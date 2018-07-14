package flowerwarspp.ui.start;

import flowerwarspp.main.GameParameters;
import flowerwarspp.preset.PlayerType;

import javax.swing.*;

public class OfferPlayerPanel extends GameStartPanel {
	private JComboBox<PlayerType> playerTypeSelector = new JComboBox<>();
	private RemotePlayerDataInput remotePlayerDataInput = new RemotePlayerDataInput("");

	public OfferPlayerPanel() {
		add(remotePlayerDataInput);

		for (PlayerType playerType : GameStartPanel.availablePlayerTypes) {
			playerTypeSelector.addItem(playerType);
		}
		add(playerTypeSelector);
	}

	@Override
	public GameParameters createParameters() {
		PlayerType playerType = playerTypeSelector.getItemAt(playerTypeSelector.getSelectedIndex());
		String playerName = remotePlayerDataInput.getPlayerName();
		String playerUrl = "rmi://" + remotePlayerDataInput.getPlayerUrl();
		return new GameParameters(playerType, playerUrl, playerName);
	}
}
