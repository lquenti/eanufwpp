package flowerwarspp.io;

import flowerwarspp.preset.*;

import javax.swing.*;
import java.awt.*;

/**
 * Das {@link JFrame}, das das {@link BoardDisplay} enthält.
 */
public class BoardFrame extends JFrame implements Requestable, Output {
	/**
	 * Das {@link BoardDisplay}, das auf das {@link Board} schaut.
	 */
	private BoardDisplay boardDisplay;

	/**
	 * Konstruiert ein JFrame, gegebenenfalls mit einem {@link Viewer}.
	 *
	 * @param viewer
	 * Der {@link Viewer}, durch den auf das {@link Board} geschaut werden soll.
	 */
	public BoardFrame(Viewer viewer) {
		this();
		if (viewer != null)
			setViewer(viewer);
	}

	/**
	 * Konstruiert das {@link JFrame} und versetzt es in einen nutzbaren Zustand.
	 */
	public BoardFrame() {
		super("Flower Wars");
		setSize(600, 600);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
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
