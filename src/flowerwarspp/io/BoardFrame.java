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
	 * Das {@link BoardDisplay}, das auf das {@link Board} schaut.
	 */
	private BoardDisplay boardDisplay;

	/**
	 * Konstruiert das {@link JFrame} und versetzt es in einen nutzbaren Zustand.
	 */
	private BoardFrame() {
		super("Flower Wars");
		setSize(600, 600);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
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
		boardDisplay = new BoardDisplay();
		boardDisplay.setBoardViewer(viewer);
		add(boardDisplay, BorderLayout.CENTER);
		setVisible(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Move request() throws Exception {
		if (boardDisplay == null)
			return null;

		return boardDisplay.awaitMove();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh() {
		if (boardDisplay == null)
			return;

		boardDisplay.refresh();
		repaint();
	}
}
