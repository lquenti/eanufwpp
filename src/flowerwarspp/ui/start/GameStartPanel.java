package flowerwarspp.ui.start;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import flowerwarspp.main.GameParameters;
import flowerwarspp.main.Main;
import javax.swing.*;

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
	 * @param parent Der Rahmen, zu dem das Panel gehören soll.
	 * @param parametersPanel Das Einstellungspanel, das in diesem Panel enthalten sein soll.
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
	 * Startet ein neues Spiel mit den Einstellungen von {@link #parametersPanel}, wenn der
	 * {@link #startButton} geklickt wurde.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == startButton) {
			parent.dispose();
			GameParameters parameters = parametersPanel.createParameters();
			Thread thread = new Thread(() -> Main.startNewGame(parameters));
			thread.start();
		}
	}
}
