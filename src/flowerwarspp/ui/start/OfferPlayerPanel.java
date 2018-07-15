package flowerwarspp.ui.start;

import flowerwarspp.main.GameParameters;
import flowerwarspp.preset.Player;
import flowerwarspp.preset.PlayerType;

import javax.swing.*;
import java.awt.*;

/**
 * Ein {@link GameStartPanel}, mit dem ein {@link flowerwarspp.preset.Player} geoffert werden kann.
 */
public class OfferPlayerPanel extends GameParametersPanel {

	/**
	 * Ein {@link JTextField}, in welches der Nutzer einen Nutzernamen eingeben kann.
	 */
	private JTextField playerNameTextField = new JTextField("Peter");

	/**
	 * Ein {@link JTextField}, mit dem der Nutzer einen Port auswählen kann.
	 */
	private JTextField playerPortTextField = new JTextField("1099");

	/**
	 * Ein {@link PlayerDataInput}, mit dem Informationen zum geofferten Spieler vom Nutzer
	 * abgefragt werden sollen.
	 */
	private PlayerDataInput playerDataInput = new PlayerDataInput("Angebotener Spieler");

	/**
	 * Konsturiert ein {@link OfferPlayerPanel}, das genutzt werden kann, um den Nutzer nach den
	 * nötigen Parametern fragen kann, einen {@link Player} anzubieten.
	 */
	public OfferPlayerPanel() {
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;

		c.insets = new Insets(0, 0, 0, 8);
		c.gridy = 0;
		c.gridx = 0;
		JLabel playerNameLabel = new JLabel("Spielername");
		add(playerNameLabel, c);
		c.insets = new Insets(0, 8, 0, 0);
		c.gridx = 1;
		add(playerNameTextField, c);

		c.insets = new Insets(0, 0, 0, 8);
		c.gridy = 1;
		c.gridx = 0;
		JLabel playerPortLabel = new JLabel("Port");
		add(playerPortLabel, c);
		c.insets = new Insets(0, 8, 0, 0);
		c.gridx = 1;
		add(playerPortTextField, c);

		c.insets = new Insets(16, 0, 0, 0);
		c.gridy = 2;
		c.gridx = 0;
		c.gridwidth = 2;
		add(playerDataInput, c);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GameParameters createParameters() throws Exception {
		PlayerType playerType = playerDataInput.getPlayerType();
		String playerName = playerNameTextField.getText();
		int playerPort = Integer.parseInt(playerPortTextField.getText());
		String playerUrl = playerDataInput.getPlayerUrl();
		return new GameParameters(playerType, playerName, playerPort, playerUrl);
	}
}
