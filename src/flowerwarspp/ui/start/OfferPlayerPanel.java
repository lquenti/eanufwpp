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
	 * Ein {@link JLabel}, das dem Nutzer signalisiert, dass Nutzername einzugeben ist.
	 */
	private JLabel playerNameLabel = new JLabel("Spielername");

	/**
	 * Ein {@link JTextField}, in welches der Nutzer einen Nutzernamen eingeben kann.
	 */
	private JTextField playerNameTextField = new JTextField("Peter");

	/**
	 * Ein {@link JLabel}, das dem Nutzer signalisiert, dass ein Port einzugeben ist.
	 */
	private JLabel playerPortLabel = new JLabel("Port");

	/**
	 * Ein {@link JTextField}, mit dem der Nutzer einen Port auswählen kann.
	 */
	private JTextField playerPortTextField = new JTextField("1099");

	/**
	 * Ein {@link PlayerDataInput}, mit dem Informationen zum geofferten Spieler
	 * vom Nutzer abgefragt werden sollen.
	 */
	private PlayerDataInput playerDataInput = new PlayerDataInput("Angebotener Spieler");

	/**
	 * Konsturiert ein {@link OfferPlayerPanel}, das genutzt werden kann,
	 * um den Nutzer nach den nötigen Parametern fragen kann,
	 * einen {@link Player} anzubieten.
	 */
	public OfferPlayerPanel() {
		add(playerNameLabel);
		add(playerNameTextField);
		add(playerPortLabel);
		add(playerPortTextField);
		add(playerDataInput);
	}

	/**
	 * Konstruiert ein {@link GameParameters}-Objekt, mit dem ein Spielzustand erstellt werden kann,
	 * das einen {@link Player} anbietet.
	 *
	 * @return
	 * Das konstruierte {@link GameParameters}-Objekt.
	 */
	@Override
	public GameParameters createParameters() {
		PlayerType playerType = playerDataInput.getPlayerType();
		String playerName = playerNameTextField.getText();
		int playerPort = Integer.parseInt(playerPortTextField.getText());
		String playerUrl = playerDataInput.getPlayerUrl();
		return new GameParameters(playerType, playerName, playerPort, playerUrl);
	}
}
