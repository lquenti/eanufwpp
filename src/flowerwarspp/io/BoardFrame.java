package flowerwarspp.io;

import flowerwarspp.preset.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;

public class BoardFrame extends JFrame implements Requestable, Output {
	private Viewer viewer;

	private BoardDisplay boardDisplay = new BoardDisplay();

	public BoardFrame(Viewer viewer) {
		super("Flower Wars");
		this.viewer = viewer;

		// Do stuff to *this*; it needs setup.
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(600, 600);

		this.add(this.boardDisplay);
		this.setViewer(this.viewer);

		// TODO: Make this obsolete.
		this.boardDisplay.setBorder(new BevelBorder(BevelBorder.LOWERED));

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
