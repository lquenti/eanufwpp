package flowerwarspp.ui.start;

import flowerwarspp.main.GameParameters;
import flowerwarspp.main.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Ein Panel, das einen Einstellungbereich und einen Button, mit dem das Spiel gestartet werden
 * kann, enthält.
 */
public class GameStartPanel extends JPanel implements ActionListener {
	/**
	 * Eine Referenz auf den {@link JFrame}, der beim Klicken des Startknopfes geschlossen wird.
	 */
	private JFrame parent;

	/**
	 * Ein Panel, in dem die Einstellungen verändert werden können.
	 */
	private GameParametersPanel parametersPanel;

	/**
	 * Ein Button, der das Spiel startet.
	 */
	private JButton startButton = new JButton("Spiel starten");

	/**
	 * Konstruktor, der neues Panel erzeugt.
	 *
	 * @param parent
	 * 		Der Rahmen, zu dem das Panel gehören soll.
	 * @param parametersPanel
	 * 		Das Einstellungspanel, das in diesem Panel enthalten sein soll.
	 */
	public GameStartPanel(JFrame parent, GameParametersPanel parametersPanel) {
		this.parent = parent;
		this.parametersPanel = parametersPanel;

		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		startButton.addActionListener(this);
		bottomPanel.add(startButton);

		setLayout(new BorderLayout());
		add(bottomPanel, BorderLayout.SOUTH);
		add(parametersPanel, BorderLayout.CENTER);
	}

	/**
	 * Startet ein neues Spiel mit den Einstellungen von {@link #parametersPanel}, wenn der {@link
	 * #startButton} geklickt wurde.
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == startButton) {
			try {
				GameParameters parameters = parametersPanel.createParameters();
				parent.dispose();
				Thread thread = new Thread(() -> Main.startNewGame(parameters));
				thread.start();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Die Einstellungen sind ungültig", "Fehler",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
