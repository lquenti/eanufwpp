package flowerwarspp.ui.start;

import flowerwarspp.preset.Player;
import flowerwarspp.preset.PlayerColor;
import flowerwarspp.util.Convert;

import javax.swing.*;
import java.awt.*;

/**
 * Ein {@link JPanel}, das Elemente hält, um Informationen über einen {@link Player} abzufragen,
 * der als Netzwerkspieler fungieren soll.
 */
public class RemotePlayerDataInput extends JPanel {

	/**
	 * Das {@link GridLayout}, das für das Layout dieses Containers zuständig ist.
	 */
	private GridLayout gridLayout = new GridLayout(4, 2);

	/**
	 * Ein {@link JLabel}, das dem Nutzer signalisiert, dass ein Hostname einzugeben ist.
	 */
	private JLabel playerHostnameLabel = new JLabel();
	/**
	 * Ein {@link JTextField}, in dem der Nutzer einen Hostnamen angeben soll.
	 */
	private JTextField playerHostnameTextField = new JTextField("localhost");

	/**
	 * Ein {@link JLabel}, das dem Nutzer signalisiert, dass ein Port einzugeben ist.
	 */
	private JLabel playerPortLabel = new JLabel();
	/**
	 * Ein {@link SpinnerNumberModel}, das sich im Wertebereich beschränken lässt.
	 */
	private SpinnerNumberModel playerPortSpinnerNumberModel = new SpinnerNumberModel();
	/**
	 * Ein {@link JSpinner}, mit dem der Nutzer einen Port auswählen kann.
	 */
	private JSpinner playerPortSpinner = new JSpinner(playerPortSpinnerNumberModel);

	/**
	 * Ein {@link JLabel}, das dem Nutzer signalisiert, dass Nutzername einzugeben ist.
	 */
	private JLabel playerNameLabel = new JLabel();
	/**
	 * Ein {@link JTextField}, in welches der Nutzer einen Nutzernamen eingeben kann.
	 */
	private JTextField playerNameTextField = new JTextField("Peter");

	public RemotePlayerDataInput(String name) {
		setLayout(gridLayout);

		setName(name);

		playerPortSpinnerNumberModel.setMinimum(0);
		playerPortSpinnerNumberModel.setMaximum(65535);
		playerPortSpinnerNumberModel.setValue(1099);

		// Wenn die Reihenfolge dieser Aufrufe geändert wird,
		// wird auch die Reihenfolge der Elemente verändert.
		add(playerHostnameLabel);
		add(playerHostnameTextField);
		add(playerPortLabel);
		add(playerPortSpinner);
		add(playerNameLabel);
		add(playerNameTextField);
	}

	/**
	 * Konstruiert ein {@link RemotePlayerDataInput}, das den Namen
	 * anhand der {@link PlayerColor} setzt.
	 *
	 * @param playerColour
	 * Die {@link PlayerColor}, die den Namen bestimmt.
	 */
	public RemotePlayerDataInput(PlayerColor playerColour) {
		this(Convert.playerColorToString(playerColour));
	}

	/**
	 * Ändert den angezeigten Namen.
	 *
	 * @param name
	 * Der neue Name.
	 */
	public void setName(String name) {
		playerHostnameLabel.setText("Hostname (" + name + ")");
		playerPortLabel.setText("Port (" + name + ")");
		playerNameLabel.setText("Name (" + name + ")");
	}

	/**
	 * Getter für den Nutzernamen des {@link Player}s.
	 *
	 * @return
	 * Der Nutzername des {@link Player}s.
	 */
	public String getPlayerName() {
		return playerNameTextField.getText();
	}

	/**
	 * Konstruiert einen {@link String}, der eine URL repräsentiert,
	 * im Muster: <code>hostname:port/playername</code>
	 * Hat explizit <b>kein</b> <code>rmi://</code>
	 *
	 * @return
	 * Ein String, der eine URL repräsentiert, mit der ein Nutzer gefunden werden kann.
	 */
	public String getPlayerUrl() {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(playerHostnameTextField.getText());
		urlBuilder.append(':');
		urlBuilder.append(playerPortSpinnerNumberModel.getNumber().intValue());
		urlBuilder.append('/');
		urlBuilder.append(playerNameTextField.getText());
		return urlBuilder.toString();
	}

	/**
	 * Ruft {@link Component#setEnabled(boolean)} auf alle Elemente auf,
	 * die in diesem Panel sind.
	 *
	 * @param enabled
	 * Ob die {@link Component}s enabled werden sollen oder nicht.
	 * Siehe {@link Component#setEnabled(boolean)}}.
	 */
	public void setComponentsEnabled(boolean enabled) {
		for (Component c : this.getComponents()) {
			c.setEnabled(enabled);
		}
	}
}
