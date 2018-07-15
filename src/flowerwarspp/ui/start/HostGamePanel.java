package flowerwarspp.ui.start;

import flowerwarspp.main.GameParameters;
import flowerwarspp.preset.PlayerColor;
import flowerwarspp.preset.PlayerType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Ein {@link JPanel}, das verwendet werden kann, um ein Spiel zu starten.
 */
public class HostGamePanel extends GameStartPanel implements ActionListener {
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
	private PlayerTypeComboBox redPlayerTypeSelector = new PlayerTypeComboBox();

	/**
	 * Das {@link JLabel}, das dem Nutzer signalisiert, dass das nebenstehende Element
	 * nach dem {@link PlayerType} des blauen Spielers fragt.
	 */
	private JLabel bluePlayerTypeLabel = new JLabel("Blue player type");
	/**
	 * Eine {@link JComboBox}, das den Nutzer nach dem {@link PlayerType}
	 * von {@link flowerwarspp.preset.PlayerColor#Blue} fragt
	 */
	private PlayerTypeComboBox bluePlayerTypeSelector = new PlayerTypeComboBox();

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
	 * Ein {@link RemotePlayerDataInput},
	 * das nach Verbindungsinformationen für den {@link PlayerColor#Red} fragt.
	 */
	private RemotePlayerDataInput redPlayerDataInput = new RemotePlayerDataInput(PlayerColor.Red);
	/**
	 * Ein {@link RemotePlayerDataInput},
	 * das nach Verbindungsinformationen für den {@link PlayerColor#Blue} fragt.
	 */
	private RemotePlayerDataInput bluePlayerDataInput = new RemotePlayerDataInput(PlayerColor.Blue);

	/**
	 * Das {@link JComponent}, das derzeit am weitesten unten links liegt.
	 */
	private JComponent southernmostLeftComponent = boardSizeLabel;
	/**
	 * Das {@link JComponent}, das derzeit am weitesten unten rechts liegt.
	 */
	private JComponent southernmostRightComponent = boardSizeSpinner;

	/**
	 * Konstruiert ein {@link JPanel}, das die notwendigen {@link JComponent}s hat, um den
	 * menschlichen Spieler nach den notwendigen Variablen zu fragen, um ein Spiel zu starten.
	 */
	public HostGamePanel() {
		setLayout(springLayout);
		setSize(400, 400);
		setMinimumSize(getSize());

		add(boardSizeLabel);
		add(boardSizeSpinner);

		// Die Renderer stellen den ausgewählten Wert graphisch dar
		redPlayerTypeSelector.setRenderer(new PlayerTypeListCellRenderer(getBackground()));
		add(redPlayerTypeLabel);
		add(redPlayerTypeSelector);

		bluePlayerTypeSelector.setRenderer(new PlayerTypeListCellRenderer(getBackground()));
		add(bluePlayerTypeLabel);
		add(bluePlayerTypeSelector);

		add(delayLabel);
		add(delaySpinner);

		redPlayerDataInput.setComponentsEnabled(false);
		redPlayerTypeSelector.addActionListener(this);
		bluePlayerDataInput.setComponentsEnabled(false);
		bluePlayerTypeSelector.addActionListener(this);
		add(redPlayerDataInput);
		add(bluePlayerDataInput);

		setupConstraints();
		setVisible(true);
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
		PlayerType redPlayerType = redPlayerTypeSelector.getSelectedItem();
		PlayerType bluePlayerType = bluePlayerTypeSelector.getSelectedItem();
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

		appendToSouth(redPlayerTypeLabel, redPlayerTypeSelector);
		appendToSouth(bluePlayerTypeLabel, bluePlayerTypeSelector);
		appendToSouth(delayLabel, delaySpinner);
		appendToSouth(redPlayerDataInput, bluePlayerDataInput);
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		// Beide hiervon sollen "==" sein, da Instanzen gleich sein sollen
		if (actionEvent.getSource() == redPlayerTypeSelector) {
			boolean shouldEnable = redPlayerTypeSelector.getSelectedItem() == PlayerType.REMOTE;
			redPlayerDataInput.setComponentsEnabled(shouldEnable);
		}
		if (actionEvent.getSource() == bluePlayerTypeSelector) {
			boolean shouldEnable = bluePlayerTypeSelector.getSelectedItem() == PlayerType.REMOTE;
			bluePlayerDataInput.setComponentsEnabled(shouldEnable);
		}
	}

	/**
	 * Hängt zwei {@link JComponent}s an das Ende dieses {@link JPanel}s.
	 * Die beiden Elemente werden so ausgelegt, dass
	 * <list>
	 *     <li>das linke und rechte Element in einer Reihe mit dem Element darüber ist</li>
	 *     <li>das linke und das rechte Element auf einer Höhe liegen</li>
	 * </list>
	 *
	 * @param leftComponent
	 * Das {@link JComponent}, das auf der linken Seite liegen soll.
	 *
	 * @param rightComponent
	 * Das {@link JComponent}, das auf der rechten Seite liegen soll.
	 */
	private void appendToSouth(JComponent leftComponent, JComponent rightComponent) {
		// Die West- und Ostseite sollen mit dem Element darüber übereinstimmen
		springLayout.putConstraint(SpringLayout.WEST, rightComponent, 0,
		                           SpringLayout.WEST, southernmostRightComponent);
		springLayout.putConstraint(SpringLayout.EAST, rightComponent, 0,
		                           SpringLayout.EAST, southernmostRightComponent);
		// Nordseite soll 5px unter der Südseite des darüberliegenden Elements liegen
		springLayout.putConstraint(SpringLayout.NORTH, rightComponent, 5,
		                           SpringLayout.SOUTH, southernmostRightComponent);

		// Die West- und Ostseite sollen mit dem Element darüber übereinstimmen
		springLayout.putConstraint(SpringLayout.WEST, leftComponent, 0,
		                           SpringLayout.WEST, southernmostLeftComponent);
		springLayout.putConstraint(SpringLayout.EAST, leftComponent, 0,
		                           SpringLayout.EAST, southernmostLeftComponent);
		// Die Baselines des linken und des rechten Components sollen übereinstimmen
		springLayout.putConstraint(SpringLayout.BASELINE, leftComponent, 0,
		                           SpringLayout.BASELINE, rightComponent);

		southernmostLeftComponent = leftComponent;
		southernmostRightComponent = rightComponent;
	}
}
