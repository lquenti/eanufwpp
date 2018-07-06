package flowerwarspp.io;

import flowerwarspp.preset.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;

public class BoardFrame extends JFrame implements Requestable, Output {
	private Viewer viewer;

	private BoardDisplay boardDisplay = new BoardDisplay();

	public BoardFrame(Viewer viewer) {
		this();
		if (viewer != null)
			this.setViewer(viewer);
	}

	public BoardFrame() {
		super("Flower Wars");

		// Do stuff to *this*; it needs setup.
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(600, 600);

		this.add(this.boardDisplay);

		this.invalidate();
		this.repaint();
		this.setVisible(true);
	}

	public void setViewer(Viewer viewer) {
		this.boardDisplay.setBoardViewer(viewer);
		this.repaint();
	}

	@Override
	public Move request() throws Exception {
		if (this.boardDisplay == null)
			return null;

		return this.boardDisplay.awaitMove();
	}

	@Override
	public void refresh() {
		this.boardDisplay.refresh();
		this.repaint();
	}
}
