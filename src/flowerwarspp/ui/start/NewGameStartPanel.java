package flowerwarspp.ui.start;

import flowerwarspp.preset.PlayerType;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collection;

/**
 * Ein {@link JPanel}, das verwendet werden kann, um ein Spiel zu starten.
 */
// TODO: ¿Finish?
public class NewGameStartPanel extends JPanel {

	private static final Collection<PlayerType> availablePlayerTypes =
	    Arrays.asList(PlayerType.HUMAN,
	                  PlayerType.RANDOM_AI,
	                  PlayerType.SIMPLE_AI,
	                  PlayerType.ADVANCED_AI_1,
	                  PlayerType.ADVANCED_AI_2,
	                  PlayerType.REMOTE);

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
	private SpinnerModel boardSizeSpinnerModel = new SpinnerNumberModel(3, 3, 30, 1);
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
	private JComboBox<PlayerType> redPlayerType = new JComboBox<>();

	/**
	 * Das {@link JLabel}, das dem Nutzer signalisiert, dass das nebenstehende Element
	 * nach dem {@link PlayerType} des blauen Spielers fragt.
	 */
	private JLabel bluePlayerTypeLabel = new JLabel("Blue player type");
	/**
	 * Eine {@link JComboBox}, das den Nutzer nach dem {@link PlayerType}
	 * von {@link flowerwarspp.preset.PlayerColor#Blue} fragt
	 */
	private JComboBox<PlayerType> bluePlayerType = new JComboBox<>();

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
	private SpinnerModel delaySpinnerModel = new SpinnerNumberModel(1000, 0, Integer.MAX_VALUE, 1);
	/**
	 * Der {@link JSpinner}, der den Nutzer nach dem Mindestdelay fragt,
	 * nach dem ein neuer {@link flowerwarspp.preset.Move} erfragt wird.
	 */
	private JSpinner delaySpinner = new JSpinner(delaySpinnerModel);

	/**
	 * Der {@link JButton}, der das Spiel startet.
	 */
	// TODO: Implement start button
	private JButton startButton = new JButton("Start game");

	/**
	 * Konstruiert ein {@link JPanel}, das die notwendigen {@link JComponent}s hat, um den
	 * menschlichen Spieler nach den notwendigen Variablen zu fragen, um ein Spiel zu starten.
	 */
	public NewGameStartPanel() {
		setLayout(springLayout);
		setSize(400, 400);
		setMinimumSize(getSize());

		add(boardSizeLabel);
		add(boardSizeSpinner);

		// For each playerType we have add an item to the spinner for the red player
		availablePlayerTypes.forEach(redPlayerType::addItem);
		redPlayerType.setRenderer(new PlayerTypeListCellRenderer(getBackground()));
		add(redPlayerTypeLabel);
		add(redPlayerType);

		// For each playerType we have add an item to the spinner for the blue player
		availablePlayerTypes.forEach(bluePlayerType::addItem);
		bluePlayerType.setRenderer(new PlayerTypeListCellRenderer(getBackground()));
		add(bluePlayerTypeLabel);
		add(bluePlayerType);

		add(delayLabel);
		add(delaySpinner);

		add(startButton);

		int maximumSelectorWidth = 0;
		for (Component c : getComponents()) {
			if (c.getPreferredSize().width > maximumSelectorWidth)
				maximumSelectorWidth = c.getPreferredSize().width;
		}

		setupConstraints(maximumSelectorWidth);
		setVisible(true);
	}

	/**
	 * Erstellen der Constraints, d.h. der Vorgaben für das Layout der {@link JComponent}s
	 * dieses {@link JFrame}s.
	 *
	 * @param distanceFromCentre
	 * Der Abstand zwischen der Mitte des {@link JFrame}s und der {@link SpringLayout#WEST}-Seite
	 * der Eingabe-{@link JComponent}s.
	 */
	private void setupConstraints(int distanceFromCentre) {
		// Lay out the board size label and spinner
		springLayout.putConstraint(SpringLayout.WEST, boardSizeLabel, -distanceFromCentre,
		                           SpringLayout.HORIZONTAL_CENTER, this);
		springLayout.putConstraint(SpringLayout.NORTH, boardSizeLabel, 10,
		                           SpringLayout.NORTH, this);

		springLayout.putConstraint(SpringLayout.WEST, boardSizeSpinner, 0,
		                           SpringLayout.HORIZONTAL_CENTER, this);
		springLayout.putConstraint(SpringLayout.BASELINE, boardSizeSpinner, 0,
		                           SpringLayout.BASELINE, boardSizeLabel);

		// Lay out the red player type combobox and label
		springLayout.putConstraint(SpringLayout.NORTH, redPlayerTypeLabel, 10,
		                           SpringLayout.SOUTH, boardSizeLabel);
		springLayout.putConstraint(SpringLayout.WEST, redPlayerTypeLabel, 0,
		                           SpringLayout.WEST, boardSizeLabel);

		springLayout.putConstraint(SpringLayout.WEST, redPlayerType, 0,
		                           SpringLayout.WEST, boardSizeSpinner);
		springLayout.putConstraint(SpringLayout.BASELINE, redPlayerType, 0,
		                           SpringLayout.BASELINE, redPlayerTypeLabel);

		// Lay out the blue player type combobox and label
		springLayout.putConstraint(SpringLayout.NORTH, bluePlayerTypeLabel, 10,
		                           SpringLayout.SOUTH, redPlayerTypeLabel);
		springLayout.putConstraint(SpringLayout.WEST, bluePlayerTypeLabel, 0,
		                           SpringLayout.WEST, redPlayerTypeLabel);

		springLayout.putConstraint(SpringLayout.WEST, bluePlayerType, 0,
		                           SpringLayout.WEST, redPlayerType);
		springLayout.putConstraint(SpringLayout.BASELINE, bluePlayerType, 0,
		                           SpringLayout.BASELINE, bluePlayerTypeLabel);

		// Lay out the AI delay spinner and label
		springLayout.putConstraint(SpringLayout.NORTH, delayLabel, 10,
		                           SpringLayout.SOUTH, bluePlayerTypeLabel);
		springLayout.putConstraint(SpringLayout.WEST, delayLabel, 0,
		                           SpringLayout.WEST, bluePlayerTypeLabel);

		springLayout.putConstraint(SpringLayout.WEST, delaySpinner, 0,
		                           SpringLayout.WEST, bluePlayerType);
		springLayout.putConstraint(SpringLayout.BASELINE, delaySpinner, 0,
		                           SpringLayout.BASELINE, delayLabel);

		// Lay out the start button
		springLayout.putConstraint(SpringLayout.SOUTH, startButton, -10,
		                           SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, startButton, 0,
		                           SpringLayout.HORIZONTAL_CENTER, this);

		// Lay out the selectors so they are equal-sized.
		springLayout.putConstraint(SpringLayout.EAST, boardSizeSpinner, 0,
		                           SpringLayout.EAST, delaySpinner);
		springLayout.putConstraint(SpringLayout.EAST, redPlayerType, 0,
		                           SpringLayout.EAST, delaySpinner);
		springLayout.putConstraint(SpringLayout.EAST, bluePlayerType, 0,
		                           SpringLayout.EAST, delaySpinner);
	}
}
