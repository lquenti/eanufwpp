package flowerwarspp.io;

import flowerwarspp.board.MainBoard;
import flowerwarspp.preset.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.util.Collection;
import java.util.Random;
import java.util.stream.Collectors;

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
		this.boardDisplay.updateSize();

		this.boardDisplay.addMouseListener((MouseClickListener) e -> {
			this.setViewer(this.viewer);
			System.out.println("Hallo");
		});

		// TODO: Make this obsolete.
		this.boardDisplay.setBorder(new BevelBorder(BevelBorder.LOWERED));

		this.getContentPane().repaint();
		this.addComponentListener((ComponentResizeListener) e ->
		        this.boardDisplay.updateSize());

		this.invalidate();
		this.repaint();
		this.setVisible(true);
	}

	public void setViewer(Viewer viewer) {
		this.repaint();
		this.boardDisplay.setBoardViewer(viewer);
		this.boardDisplay.updateSize();
	}

	@Override
	public Move request() throws Exception {
		return null;
	}

	@Override
	public void refresh() {
		this.repaint();
	}
}
