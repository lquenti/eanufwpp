package flowerwarspp.io;

import flowerwarspp.preset.*;

import javax.swing.*;
import java.awt.*;

/**
 * Das {@link JFrame}, das das {@link BoardDisplay} enthält.
 */
public class BoardFrame extends JFrame implements Requestable, Output {
	/**
	 * Private Referenz auf die singuläre Instanz dieser Klasse.
	 */
	private static BoardFrame instance;

	/**
	 * Der {@link Viewer}, durch den dieses {@link JFrame} auf das {@link Board} schaut.
	 */
	private Viewer viewer;

	/**
	 * Der Container für das {@link BoardDisplay}.
	 */
	private JPanel boardContainer = new JPanel();
	/**
	 * Das {@link BoardDisplay}, das auf das {@link Board} schaut.
	 */
	private BoardDisplay boardDisplay;

	private JPanel buttonContainer = new JPanel();
	/**
	 * Der {@link JButton}, der das Aufgeben ermöglicht.
	 */
	private JButton surrenderButton = new JButton("Surrender");
	/**
	 * Der {@link JButton}, der das Beenden des Spiels ermöglicht,
	 * wenn dies den Spielregeln zufolge möglich ist.
	 */
	private JButton endButton = new JButton("End");

	/**
	 * Konstruiert das {@link JFrame} und versetzt es in einen nutzbaren Zustand.
	 */
	private BoardFrame() {
		super("Flower Wars");
		this.setSize(600, 600);

		// Do stuff to *this*; it needs setup.
		this.buttonContainer.add(this.surrenderButton);
		this.buttonContainer.add(this.endButton);
		this.add(this.buttonContainer, BorderLayout.SOUTH);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	/**
	 * Gibt einen Verweis auf die singuläre Instanz dieser Singleton-Klasse zurück. Falls noch keine Instanz existiert,
	 * wird eine neue erzeugt.
	 *
	 * @return Die singuläre Instanz des Frames
	 */
	public synchronized static BoardFrame getInstance() {
		if (instance == null) {
			instance = new BoardFrame();
		}
		return instance;
	}

	/**
	 * Setzt den {@link Viewer}, durch den gerade geschaut wird und erstellt ein
	 * zugehöriges {@link BoardDisplay}.
	 *
	 * @param viewer
	 * Der {@link Viewer}, durch den auf das Spielbrett geschaut wird.
	 */
	public void setViewer(Viewer viewer) {
		this.boardDisplay = new BoardDisplay();
		this.boardDisplay.setBoardViewer(viewer);
		this.add(this.boardDisplay, BorderLayout.CENTER);
		this.setVisible(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Move request() throws Exception {
		if (this.boardDisplay == null)
			return null;

		return this.boardDisplay.awaitMove();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh() {
		if (this.boardDisplay == null)
			return;

		this.boardDisplay.refresh();
		this.repaint();
	}
}
