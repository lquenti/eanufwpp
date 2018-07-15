package flowerwarspp.ui.start;

import flowerwarspp.preset.Player;
import flowerwarspp.preset.PlayerColor;
import flowerwarspp.preset.PlayerType;
import flowerwarspp.util.Convert;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Ein {@link JPanel}, das Elemente hält, um Informationen über einen {@link Player} abzufragen.
 */
public class PlayerDataInput extends JPanel implements ActionListener {

	/**
	 * Eine {@link PlayerTypeComboBox}, über die der Nutzer den Spielertypen eingeben kann.
	 */
	private PlayerTypeComboBox playerTypeComboBox = new PlayerTypeComboBox();

	/**
	 * Ein {@link JLabel}, das dem Nutzer signalisiert, dass ein Hostname einzugeben ist.
	 */
	private JLabel playerHostnameLabel = new JLabel("Host");

	/**
	 * Ein {@link JTextField}, in dem der Nutzer einen Hostnamen angeben soll.
	 */
	private JTextField playerHostnameTextField = new JTextField("localhost");

	/**
	 * Ein {@link JLabel}, das dem Nutzer signalisiert, dass ein Port einzugeben ist.
	 */
	private JLabel playerPortLabel = new JLabel("Port");

	/**
	 * Ein {@link JTextField}, mit dem der Nutzer einen Port auswählen kann.
	 */
	private JTextField playerPortTextField = new JTextField("1099");

	/**
	 * Ein {@link JLabel}, das dem Nutzer signalisiert, dass Nutzername einzugeben ist.
	 */
	private JLabel playerNameLabel = new JLabel("Spielername");

	/**
	 * Ein {@link JTextField}, in welches der Nutzer einen Nutzernamen eingeben kann.
	 */
	private JTextField playerNameTextField = new JTextField("Peter");

	/**
	 * Konstruiert ein {@link PlayerDataInput}, das den Namen anhand der {@link PlayerColor} setzt.
	 *
	 * @param playerColour
	 * 		Die {@link PlayerColor}, die den Namen bestimmt.
	 */
	public PlayerDataInput(PlayerColor playerColour) {
		this(Convert.playerColorToString(playerColour));
	}

	/**
	 * Konstruiert ein {@link PlayerDataInput} mit gegebenen Namen.
	 *
	 * @param name
	 * 		Der Name.
	 */
	public PlayerDataInput(String name) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JLabel nameLabel = new JLabel(name, JLabel.CENTER);
		nameLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		nameLabel.setBorder(new EmptyBorder(0, 0, 8, 0));
		add(nameLabel);

		JPanel settingsPanel = new JPanel(new GridLayout(4, 2));

		JLabel playerTypeLabel = new JLabel("Spielertyp");
		settingsPanel.add(playerTypeLabel);
		playerTypeComboBox.addActionListener(this);
		settingsPanel.add(playerTypeComboBox);

		settingsPanel.add(playerHostnameLabel);
		settingsPanel.add(playerHostnameTextField);
		settingsPanel.add(playerPortLabel);
		settingsPanel.add(playerPortTextField);
		settingsPanel.add(playerNameLabel);
		settingsPanel.add(playerNameTextField);

		setNetworkComponentsEnabled(false);

		add(settingsPanel);
	}

	/**
	 * Ruft {@link Component#setEnabled(boolean)} auf alle Elemente auf, die für
	 * Netzwerkeinstellungen verantwortlich sind.
	 *
	 * @param enabled
	 * 		Ob die {@link Component}s enabled werden sollen oder nicht. Siehe {@link
	 * 		Component#setEnabled(boolean)}}.
	 */
	private void setNetworkComponentsEnabled(boolean enabled) {
		playerHostnameLabel.setEnabled(enabled);
		playerHostnameTextField.setEnabled(enabled);
		playerPortLabel.setEnabled(enabled);
		playerPortTextField.setEnabled(enabled);
		playerNameLabel.setEnabled(enabled);
		playerNameTextField.setEnabled(enabled);
	}

	/**
	 * Getter für den Typen des {@link Player}s.
	 *
	 * @return Der Typ des {@link Player}s.
	 */
	public PlayerType getPlayerType() {
		return playerTypeComboBox.getSelectedItem();
	}

	/**
	 * Konstruiert einen {@link String}, der eine URL repräsentiert, im Muster:
	 * <code>hostname:port/playername</code> Hat explizit <b>kein</b> <code>rmi://</code>
	 *
	 * @return Ein String, der eine URL repräsentiert, mit der ein Nutzer gefunden werden kann.
	 */
	public String getPlayerUrl() {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(playerHostnameTextField.getText());
		urlBuilder.append(':');
		urlBuilder.append(playerPortTextField.getText());
		urlBuilder.append('/');
		urlBuilder.append(playerNameTextField.getText());

		return urlBuilder.toString();
	}

	/**
	 * Graut die Netzwerkeinstellungen aus, wenn kein Netzwerkspieler ausgewählt ist.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == playerTypeComboBox) {
			setNetworkComponentsEnabled(playerTypeComboBox.getSelectedItem() == PlayerType.REMOTE);
		}
	}
}
