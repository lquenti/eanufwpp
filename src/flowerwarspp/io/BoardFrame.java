package flowerwarspp.io;

import flowerwarspp.preset.Board;
import flowerwarspp.preset.Move;
import flowerwarspp.preset.Requestable;

import javax.swing.*;
import javax.swing.border.BevelBorder;

public class BoardFrame extends JFrame implements Requestable {
	public static void main(String[] args) throws Exception {
		DummyBoard dummyBoard = new DummyBoard(6);
		BoardFrame boardFrame = new BoardFrame();
		boardFrame.setVisible(true);
		boardFrame.addMouseListener((MouseClickListener) e ->
		        boardFrame.setBoard(dummyBoard));
	}

	private SpringLayout springLayout = new SpringLayout();
	private JPanel container = new JPanel(this.springLayout);
	private BoardDisplay boardDisplay = new BoardDisplay();

	public BoardFrame() {
		// Do stuff to *this*; it needs setup.
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(600, 600);

		this.add(this.container);
		this.container.add(this.boardDisplay);
		this.boardDisplay.updateSize();

		// Call after adding all the components to the container.
		// Sets up the layout of everything.
		this.setupConstraints();

		// TODO: Make this obsolete.
		this.boardDisplay.setBorder(new BevelBorder(BevelBorder.LOWERED));

		this.getContentPane().repaint();
		this.addComponentListener((ComponentResizeListener) e ->
		        this.boardDisplay.updateSize());

		this.invalidate();
		this.repaint();
	}

	/**
	 * Erstelle {@link SpringLayout.Constraints} für die einzelnen Elemente des {@link JFrame}s.
	 * Diese Constraints legen fest, wie die Elemente in Relation zum Fenster bzw. zueinander liegen
	 * sollen.
	 */
	private void setupConstraints() {
		this.springLayout.putConstraint(
		        SpringLayout.HORIZONTAL_CENTER, this.boardDisplay, 0,
		        SpringLayout.HORIZONTAL_CENTER, this.container);
		this.springLayout.putConstraint(
		        SpringLayout.VERTICAL_CENTER, this.boardDisplay, 0,
		        SpringLayout.VERTICAL_CENTER, this.container);
	}

	public void setBoard(Board board) {
		this.repaint();
		this.boardDisplay.setBoardViewer(board.viewer());
		this.boardDisplay.updateSize();
	}

	@Override
	public Move request() throws Exception {
		return null;
	}
}
