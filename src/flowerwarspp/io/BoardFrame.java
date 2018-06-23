package flowerwarspp.io;

import flowerwarspp.board.MainBoard;
import flowerwarspp.preset.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.util.Collection;
import java.util.Random;
import java.util.stream.Collectors;

public class BoardFrame extends JFrame implements Requestable {
	public static void main(String[] args) throws Exception {
		BoardFrame boardFrame = new BoardFrame();
		boardFrame.setVisible(true);
	}

	private MainBoard mainBoard = new MainBoard(10);
	private Viewer viewer = mainBoard.viewer();

	private SpringLayout springLayout = new SpringLayout();
	private JPanel container = new JPanel(this.springLayout);
	private JButton testFlowerMove = new JButton("Random flower");
	private JButton testDitchMove = new JButton("Random ditch");
	private BoardDisplay boardDisplay = new BoardDisplay();

	public BoardFrame() {
		// Do stuff to *this*; it needs setup.
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(600, 600);

		this.add(this.container);
		this.container.add(this.boardDisplay);
		this.container.add(this.testFlowerMove);
		this.container.add(this.testDitchMove);
		this.boardDisplay.updateSize();

		this.boardDisplay.addMouseListener((MouseClickListener) e -> {
			this.setBoard(mainBoard);
			System.out.println("Hallo");
		});

		this.testFlowerMove.addMouseListener((MouseClickListener) e -> {
			Collection<Move> moves = this.viewer.getPossibleMoves()
			        .stream()
			        .filter(t -> t.getType() == MoveType.Flower)
			        .collect(Collectors.toList());

			Random random = new Random();
			int idx = random.nextInt(moves.size());
			int i = 0;
			for (Move m : moves) {
				if (i == idx) {
					System.out.println(m.getType());
					this.mainBoard.make(m);
					break;
				}

				i++;
			}
		});

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
		// Lays out the display itself
		this.springLayout.putConstraint(
		        SpringLayout.HORIZONTAL_CENTER, this.boardDisplay, 0,
		        SpringLayout.HORIZONTAL_CENTER, this.container);
		this.springLayout.putConstraint(
		        SpringLayout.VERTICAL_CENTER, this.boardDisplay, 0,
		        SpringLayout.VERTICAL_CENTER, this.container);

		// Lays out the flower move test button
		this.springLayout.putConstraint(
		        SpringLayout.SOUTH, this.testFlowerMove, 0,
				SpringLayout.SOUTH, this.container);
		this.springLayout.putConstraint(
		        SpringLayout.WEST, this.testFlowerMove, 0,
		        SpringLayout.WEST, this.container);

		// Lays out the ditch move test button
		this.springLayout.putConstraint(
		        SpringLayout.SOUTH, this.testDitchMove, 0,
		        SpringLayout.SOUTH, this.container);
		this.springLayout.putConstraint(
		        SpringLayout.WEST, this.testDitchMove, 0,
		        SpringLayout.EAST, this.testFlowerMove);
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
