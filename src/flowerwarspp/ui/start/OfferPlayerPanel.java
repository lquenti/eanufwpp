package flowerwarspp.ui.start;

import flowerwarspp.main.GameParameters;
import flowerwarspp.preset.Player;
import flowerwarspp.preset.PlayerType;

import javax.swing.*;
import java.awt.*;

/**
 * Ein {@link GameStartPanel}, mit dem ein {@link flowerwarspp.preset.Player} geoffert werden kann.
 */
public class OfferPlayerPanel extends GameStartPanel {
	/**
	 * Der {@link LayoutManager} dieses {@link JPanel}s.
	 * Layt sachen out.
	 */
	private SpringLayout springLayout = new SpringLayout();
	/**
	 * Ein {@link RemotePlayerDataInput}, mit dem Informationen zum geofferten Spieler
	 * vom Nutzer abgefragt werden sollen.
	 */
	private RemotePlayerDataInput remotePlayerDataInput = new RemotePlayerDataInput("");
	/**
	 * Eine {@link PlayerTypeComboBox}, mit dem der {@link PlayerType}
	 * des anzubietenden {@link Player} determiniert werden kann.
	 */
	private PlayerTypeComboBox playerTypeSelector = new PlayerTypeComboBox();

	/**
	 * Konsturiert ein {@link OfferPlayerPanel}, das genutzt werden kann,
	 * um den Nutzer nach den nötigen Parametern fragen kann,
	 * einen {@link Player} anzubieten.
	 */
	public OfferPlayerPanel() {
		setLayout(springLayout);
		add(remotePlayerDataInput);
		add(playerTypeSelector);
		setupConstraints();
	}

	private void setupConstraints() {
		// Legt das remotePlayerDataInput nach oben und über das ganze Panel aus.
		springLayout.putConstraint(SpringLayout.NORTH, remotePlayerDataInput, 0,
		                           SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, remotePlayerDataInput, 0,
		                           SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, remotePlayerDataInput, 0,
		                           SpringLayout.EAST, this);

		// Die playerTypeSelector-Box soll hauptsächlich zentriert sein,
		// und unter dem remotePlayerDataInput liegen.
		springLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, playerTypeSelector, 0,
		                           SpringLayout.HORIZONTAL_CENTER, this);
		springLayout.putConstraint(SpringLayout.NORTH, playerTypeSelector, 0,
		                           SpringLayout.SOUTH, remotePlayerDataInput);
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
		PlayerType playerType = playerTypeSelector.getItemAt(playerTypeSelector.getSelectedIndex());
		String playerName = remotePlayerDataInput.getPlayerName();
		String playerUrl = "rmi://" + remotePlayerDataInput.getPlayerUrl();
		// TODO: Port einlesen
		return new GameParameters(playerType, playerName, 1099, playerUrl);
	}
}
