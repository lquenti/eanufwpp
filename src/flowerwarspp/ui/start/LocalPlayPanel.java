package flowerwarspp.ui.start;

import flowerwarspp.main.GameParameters;
import flowerwarspp.preset.PlayerType;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collection;

/**
 * Ein {@link JPanel}, das verwendet werden kann, um ein Spiel zu starten.
 */
public class LocalPlayPanel extends GameStartPanel {
	// TODO: Dokumentation
	private static final Collection<PlayerType> availablePlayerTypes =
		Arrays.asList(PlayerType.HUMAN,
			PlayerType.RANDOM_AI,
			PlayerType.SIMPLE_AI,
			PlayerType.ADVANCED_AI_1,
			PlayerType.ADVANCED_AI_2);

	/**
	 * Der {@link LayoutManager} dieses {@link JPanel}s.
	 */
	private final SpringLayout springLayout = new SpringLayout();

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
	 * nach dem {@link PlayerType} des roten Spielers fragt.
	 */
	private JLabel redPlayerTypeLabel = new JLabel("Red player type");
	/**
	 * Eine {@link JComboBox}, das den Nutzer nach dem {@link PlayerType}
	 * von {@link flowerwarspp.preset.PlayerColor#Red} fragt
	 */
	private JComboBox<PlayerType> redPlayerTypeSelector = new JComboBox<>();

	/**
	 * Das {@link JLabel}, das dem Nutzer signalisiert, dass das nebenstehende Element
	 * nach dem {@link PlayerType} des blauen Spielers fragt.
	 */
	private JLabel bluePlayerTypeLabel = new JLabel("Blue player type");
	/**
	 * Eine {@link JComboBox}, das den Nutzer nach dem {@link PlayerType}
	 * von {@link flowerwarspp.preset.PlayerColor#Blue} fragt
	 */
	private JComboBox<PlayerType> bluePlayerTypeSelector = new JComboBox<>();

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
	 * Konstruiert ein {@link JPanel}, das die notwendigen {@link JComponent}s hat, um den
	 * menschlichen Spieler nach den notwendigen Variablen zu fragen, um ein Spiel zu starten.
	 */
	public LocalPlayPanel() {
		setLayout(springLayout);
		setSize(400, 400);
		setMinimumSize(getSize());

		add(boardSizeLabel);
		add(boardSizeSpinner);

		for (PlayerType playerType : availablePlayerTypes) {
			redPlayerTypeSelector.addItem(playerType);
			bluePlayerTypeSelector.addItem(playerType);
		}

		redPlayerTypeSelector.setRenderer(new PlayerTypeListCellRenderer(getBackground()));
		add(redPlayerTypeLabel);
		add(redPlayerTypeSelector);

		bluePlayerTypeSelector.setRenderer(new PlayerTypeListCellRenderer(getBackground()));
		add(bluePlayerTypeLabel);
		add(bluePlayerTypeSelector);

		add(delayLabel);
		add(delaySpinner);

		setupConstraints();
		setVisible(true);
	}

	@Override
	public GameParameters createParameters() {
		int boardSize = boardSizeSpinnerModel.getNumber().intValue();
		PlayerType redPlayerType = (PlayerType) redPlayerTypeSelector.getSelectedItem();
		PlayerType bluePlayerType = (PlayerType) bluePlayerTypeSelector.getSelectedItem();
		int moveDelay = delaySpinnerModel.getNumber().intValue();
		return new GameParameters(boardSize, redPlayerType, bluePlayerType, moveDelay);
	}

	/**
	 * Erstellen der Constraints, d.h. der Vorgaben für das Layout der {@link JComponent}s
	 * dieses {@link JFrame}s.
	 */
	private void setupConstraints() {
		// Auslegen des BoardSize-Spinners und -Labels
		springLayout.putConstraint(SpringLayout.WEST, boardSizeSpinner, 5,
		                           SpringLayout.HORIZONTAL_CENTER, this);
		springLayout.putConstraint(SpringLayout.EAST, boardSizeSpinner, 0,
		                           SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.NORTH, boardSizeSpinner, 0,
		                           SpringLayout.NORTH, this);

		springLayout.putConstraint(SpringLayout.WEST, boardSizeLabel, 0,
		                           SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, boardSizeLabel, -5,
		                           SpringLayout.HORIZONTAL_CENTER, this);
		springLayout.putConstraint(SpringLayout.BASELINE, boardSizeLabel, 0,
		                           SpringLayout.BASELINE, boardSizeSpinner);

		// Auslegen des RedPlayerType-Spinners und -Labels
		springLayout.putConstraint(SpringLayout.WEST, redPlayerTypeSelector, 0,
		                           SpringLayout.WEST, boardSizeSpinner);
		springLayout.putConstraint(SpringLayout.EAST, redPlayerTypeSelector, 0,
		                           SpringLayout.EAST, boardSizeSpinner);
		springLayout.putConstraint(SpringLayout.NORTH, redPlayerTypeSelector, 5,
		                           SpringLayout.SOUTH, boardSizeSpinner);

		springLayout.putConstraint(SpringLayout.WEST, redPlayerTypeLabel, 0,
		                           SpringLayout.WEST, boardSizeLabel);
		springLayout.putConstraint(SpringLayout.EAST, redPlayerTypeLabel, 0,
		                           SpringLayout.EAST, boardSizeLabel);
		springLayout.putConstraint(SpringLayout.BASELINE, redPlayerTypeLabel, 0,
		                           SpringLayout.BASELINE, redPlayerTypeSelector);

		// AUslegen des BluePlayerType-Spinners und -Labels
		springLayout.putConstraint(SpringLayout.WEST, bluePlayerTypeSelector, 0,
		                           SpringLayout.WEST, redPlayerTypeSelector);
		springLayout.putConstraint(SpringLayout.EAST, bluePlayerTypeSelector, 0,
		                           SpringLayout.EAST, redPlayerTypeSelector);
		springLayout.putConstraint(SpringLayout.NORTH, bluePlayerTypeSelector, 5,
		                           SpringLayout.SOUTH, redPlayerTypeSelector);

		springLayout.putConstraint(SpringLayout.WEST, bluePlayerTypeLabel, 0,
		                           SpringLayout.WEST, redPlayerTypeLabel);
		springLayout.putConstraint(SpringLayout.EAST, bluePlayerTypeLabel, 0,
		                           SpringLayout.EAST, redPlayerTypeLabel);
		springLayout.putConstraint(SpringLayout.BASELINE, bluePlayerTypeLabel, 0,
		                           SpringLayout.BASELINE, bluePlayerTypeSelector);

		// Auslegen des Move-Delay-Spinners und -Labels
		springLayout.putConstraint(SpringLayout.WEST, delaySpinner, 0,
		                           SpringLayout.WEST, bluePlayerTypeSelector);
		springLayout.putConstraint(SpringLayout.EAST, delaySpinner, 0,
		                           SpringLayout.EAST, bluePlayerTypeSelector);
		springLayout.putConstraint(SpringLayout.NORTH, delaySpinner, 5,
		                           SpringLayout.SOUTH, bluePlayerTypeSelector);

		springLayout.putConstraint(SpringLayout.WEST, delayLabel, 0,
		                           SpringLayout.WEST, bluePlayerTypeLabel);
		springLayout.putConstraint(SpringLayout.EAST, delayLabel, 0,
		                           SpringLayout.EAST, bluePlayerTypeLabel);
		springLayout.putConstraint(SpringLayout.BASELINE, delayLabel, 0,
		                           SpringLayout.BASELINE, delaySpinner);
	}

}
