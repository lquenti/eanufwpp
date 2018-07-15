package flowerwarspp.ui.start;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import flowerwarspp.main.GameParameters;
import flowerwarspp.preset.PlayerColor;
import flowerwarspp.preset.PlayerType;
import flowerwarspp.util.Convert;
import java.awt.*;
import javax.swing.*;

/**
 * Ein {@link JPanel}, das verwendet werden kann, um ein Spiel zu starten.
 */
public class HostGamePanel extends GameParametersPanel {
	/**
	 * Das {@link JLabel}, das dem Nutzer signalisiert, dass das nebenstehende Element
	 * nach der Größe des {@link flowerwarspp.preset.Board}s fragt.
	 */
	private JLabel boardSizeLabel = new JLabel("Board size");
	/**
	 * Das {@link SpinnerModel}, das den Wertebereich für die Spielbrettgröße eingrenzt.
	 * Ist von 3 bis 30 gültig, wobei 3 der Standardwert und 1 die Schrittgröße ist.
	 */
	private SpinnerNumberModel boardSizeSpinnerModel =
		new SpinnerNumberModel(3, 3, 30, 1);
	/**
	 * Der {@link JSpinner}, der den Nutzer nach der Größe des Boards fragt.
	 */
	private JSpinner boardSizeSpinner = new JSpinner(boardSizeSpinnerModel);

	/**
	 * Das {@link JLabel}, das dem Nutzer signalisiert, dass das nebenstehende Element
	 * nach dem Delay fragt, nach dem ein neuer {@link flowerwarspp.preset.Move} erfragt wird.
	 */
	private JLabel delayLabel = new JLabel("Move delay (ms)");
	/**
	 * Das {@link SpinnerModel} für den Delay-Spinner.
	 * Standardmäßig auf 1000 eingestellt, wobei der Wertebereich
	 * von 0 bis {@link Integer#MAX_VALUE} reicht und Schritte von 1ms erlaubt.
	 */
	private SpinnerNumberModel delaySpinnerModel =
		new SpinnerNumberModel(1000, 0, Integer.MAX_VALUE, 1);
	/**
	 * Der {@link JSpinner}, der den Nutzer nach dem Mindestdelay fragt,
	 * nach dem ein neuer {@link flowerwarspp.preset.Move} erfragt wird.
	 */
	private JSpinner delaySpinner = new JSpinner(delaySpinnerModel);

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
		setLayout(new GridLayout(3, 2));

		add(boardSizeLabel);
		add(boardSizeSpinner);

		add(delayLabel);
		add(delaySpinner);

		add(redPlayerDataInput);
		add(bluePlayerDataInput);
	}

	/**
	 * Konstruiert {@link GameParameters} aus dem aktuellen Status der GUI.
	 *
	 * @return
	 * Ein {@link GameParameters}-Objekt, aus dem ein Spiel generiert werden soll.
	 */
	@Override
	public GameParameters createParameters() {
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
		return new GameParameters(boardSize,
			redPlayerType, redPlayerUrl,
			bluePlayerType, bluePlayerUrl,
			moveDelay);
	}
}
