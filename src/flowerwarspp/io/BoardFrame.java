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
			this.setViewer(viewer);
	}

	/**
	 * Konstruiert das {@link JFrame} und versetzt es in einen nutzbaren Zustand.
	 */
	public BoardFrame() {
		super("Flower Wars");
		this.setSize(600, 600);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
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
