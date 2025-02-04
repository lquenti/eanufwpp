package flowerwarspp.ui.start;

import flowerwarspp.main.GameParameters;
import flowerwarspp.preset.PlayerColor;
import flowerwarspp.preset.PlayerType;

import javax.swing.*;
import java.awt.*;

/**
 * Ein {@link JPanel}, das verwendet werden kann, um ein Spiel zu starten.
 */
public class HostGamePanel extends GameParametersPanel {

	/**
	 * Das {@link SpinnerModel}, das den Wertebereich für die Spielbrettgröße eingrenzt. Ist von 3
	 * bis 30 gültig, wobei 3 der Standardwert und 1 die Schrittgröße ist.
	 */
	private SpinnerNumberModel boardSizeSpinnerModel = new SpinnerNumberModel(3, 3, 30, 1);

	/**
	 * Das {@link SpinnerModel} für den Delay-Spinner. Standardmäßig auf 1000 eingestellt, wobei
	 * der Wertebereich von 0 bis {@link Integer#MAX_VALUE} reicht und Schritte von 1ms erlaubt.
	 */
	private SpinnerNumberModel delaySpinnerModel =
			new SpinnerNumberModel(1000, 0, Integer.MAX_VALUE, 1);

	/**
	 * Ein {@link PlayerDataInput}, das nach Informationen für den {@link PlayerColor#Red} fragt.
	 */
	private PlayerDataInput redPlayerDataInput = new PlayerDataInput(PlayerColor.Red);
	/**
	 * Ein {@link PlayerDataInput}, das nach Informationen für den {@link PlayerColor#Blue} fragt.
	 */
	private PlayerDataInput bluePlayerDataInput = new PlayerDataInput(PlayerColor.Blue);

	/**
	 * Konstruiert ein {@link JPanel}, das die notwendigen {@link JComponent}s hat, um den
	 * menschlichen Spieler nach den notwendigen Variablen zu fragen, um ein Spiel zu starten.
	 */
	public HostGamePanel() {
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;

		c.insets = new Insets(0, 0, 0, 8);
		c.gridy = 0;
		c.gridx = 0;
		JLabel boardSizeLabel = new JLabel("Board size");
		add(boardSizeLabel, c);
		c.insets = new Insets(0, 8, 0, 0);
		c.gridx = 1;
		JSpinner boardSizeSpinner = new JSpinner(boardSizeSpinnerModel);
		add(boardSizeSpinner, c);

		c.insets = new Insets(0, 0, 0, 8);
		c.gridy = 1;
		c.gridx = 0;
		JLabel delayLabel = new JLabel("Move delay (ms)");
		add(delayLabel, c);
		c.insets = new Insets(0, 8, 0, 0);
		c.gridx = 1;
		JSpinner delaySpinner = new JSpinner(delaySpinnerModel);
		add(delaySpinner, c);

		c.insets = new Insets(16, 0, 0, 8);
		c.gridy = 2;
		c.gridx = 0;
		add(redPlayerDataInput, c);
		c.insets = new Insets(16, 8, 0, 0);
		c.gridx = 1;
		add(bluePlayerDataInput, c);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GameParameters createParameters() throws Exception {
		int boardSize = boardSizeSpinnerModel.getNumber().intValue();
		PlayerType redPlayerType = redPlayerDataInput.getPlayerType();
		PlayerType bluePlayerType = bluePlayerDataInput.getPlayerType();
		int moveDelay = delaySpinnerModel.getNumber().intValue();

		String redPlayerUrl = null, bluePlayerUrl = null;
		if (redPlayerType == PlayerType.REMOTE) {
			redPlayerUrl = redPlayerDataInput.getPlayerUrl();
		}
		if (bluePlayerType == PlayerType.REMOTE) {
			bluePlayerUrl = bluePlayerDataInput.getPlayerUrl();
		}

		// Der Konstruktor akzeptiert null als URL genau dann,
		// wenn der dazugehörige PlayerType nicht REMOTE ist.
		return new GameParameters(boardSize, redPlayerType, redPlayerUrl, bluePlayerType,
				bluePlayerUrl, moveDelay);
	}
}
