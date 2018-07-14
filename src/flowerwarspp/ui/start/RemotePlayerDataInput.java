package flowerwarspp.ui.start;

import flowerwarspp.preset.PlayerColor;
import flowerwarspp.util.Convert;

import javax.swing.*;
import java.awt.*;

public class RemotePlayerDataInput extends JPanel {

	private GridLayout gridLayout = new GridLayout(4, 2);

	private JLabel playerUrlLabel = new JLabel();
	private JTextField playerUrlTextField = new JTextField("localhost");

	private JLabel playerPortLabel = new JLabel();
	private SpinnerNumberModel playerPortSpinnerNumberModel = new SpinnerNumberModel();
	private JSpinner playerPortSpinner = new JSpinner(playerPortSpinnerNumberModel);

	private JLabel playerNameLabel = new JLabel();
	private JTextField playerNameTextField = new JTextField();

	public RemotePlayerDataInput(String name) {
		setLayout(gridLayout);

		setName(name);

		playerPortSpinnerNumberModel.setMinimum(0);
		playerPortSpinnerNumberModel.setMaximum(65535);
		playerPortSpinnerNumberModel.setValue(1099);

		add(playerUrlLabel);
		add(playerUrlTextField);
		add(playerPortLabel);
		add(playerPortSpinner);
		add(playerNameLabel);
		add(playerNameTextField);
	}

	public RemotePlayerDataInput(PlayerColor playerColour) {
		this(Convert.playerColorToString(playerColour));
	}

	public void setName(String name) {
		playerUrlLabel.setText("URL (" + name + ")");
		playerPortLabel.setText("Port (" + name + ")");
		playerNameLabel.setText("Name (" + name + ")");
	}

	public String getPlayerName() {
		return playerNameTextField.getText();
	}

	public int getPlayerPort() {
		return playerPortSpinnerNumberModel.getNumber().intValue();
	}

	public String getPlayerUrl() {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(playerUrlTextField.getText());
		urlBuilder.append(':');
		urlBuilder.append(playerPortSpinnerNumberModel.getNumber().intValue());
		urlBuilder.append('/');
		urlBuilder.append(playerNameTextField.getText());
		return urlBuilder.toString();
	}

	public void setComponentsEnabled(boolean enabled) {
		for (Component c : this.getComponents()) {
			c.setEnabled(enabled);
		}
	}
}
