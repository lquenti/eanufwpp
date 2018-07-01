package flowerwarspp.io;

import flowerwarspp.preset.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collection;

public class BoardDisplay extends JPanel implements ActionListener {
	/**
	 * Eine private Klasse, die die Mausaktionen für das {@link BoardDisplay} verarbeitet.
	 */
	private class BoardDisplayMouseHandler extends MouseAdapter {
		/**
		 * Das {@link BoardDisplay}, zu dem dieser {@link MouseAdapter} gehört.
		 */
		private BoardDisplay boardDisplay = null;

		private final Object moveAwaitLock = new Object();
		/**
		 * Wenn Blumen gesetzt werden, müssen zwei Dreiecke geklickt werden.
		 * Wurde bereits ein Dreieck geklickt worden, so ist jenes hierin gespeichert.
		 */
		private Triangle firstClickedTriangle = null;
		private Move move = null;

		/**
		 * Konstruiert einen {@link BoardDisplayMouseHandler}, der an ein {@link BoardDisplay}
		 * gebunden ist.
		 *
		 * @param boardDisplay
		 * Das {@link BoardDisplay}, an welches dieses Objekt gebunden ist.
		 */
		public BoardDisplayMouseHandler(BoardDisplay boardDisplay) {
			this.boardDisplay = boardDisplay;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseClicked(MouseEvent mouseEvent) {
			Triangle triangle = findTriangle(mouseEvent.getPoint());
			Collection<Move> moves = this.boardDisplay.boardViewer.getPossibleMoves();
			if (this.firstClickedTriangle == null) {
				this.firstClickedTriangle = triangle;
			} else {
				if (triangle != null) {
					if (this.firstClickedTriangle.samePlace(triangle)) {
						this.firstClickedTriangle = null;
					} else {
						this.move = this.getFlowerMove(this.firstClickedTriangle, triangle, moves);
						this.firstClickedTriangle = null;
						synchronized (this.moveAwaitLock) {
							this.moveAwaitLock.notifyAll();
						}
					}
				}
			}
		}

		/**
		 * Findet ein {@link Triangle} am spezifizierten {@link Point}.
		 *
		 * @param point
		 * Der {@link Point}, an dem das Dreieck liegt.
		 *
		 * @return
		 * Das {@link Triangle}, das derzeit an der angegebenen Stelle liegt,
		 * oder <code>null</code>, falls dort keines liegt.
		 */
		private Triangle findTriangle(Point point) {
			for (Triangle t : this.boardDisplay.mapTriangles) {
				if (t.contains(point))
					return t;
			}

			return null;
		}

		/**
		 * Findet einen {@link Move} in der Liste möglicher Moves, der die gewählten
		 * Blumen enthält.
		 *
		 * @param firstFlowerTriangle
		 * Die erste Blume. Die Reihenfolge der Blumen ist in dieser Methode unwichtig.
		 *
		 * @param secondFlowerTriangle
		 * Die zweite Blume. Die Reihenfolge der Blumen ist in dieser Methode unwichtig.
		 *
		 * @param possibleMoves
		 * Eine {@link Collection} möglicher {@link Move}s.
		 *
		 * @return
		 */
		private Move getFlowerMove(Triangle firstFlowerTriangle,
		                           Triangle secondFlowerTriangle,
		                           Collection<Move> possibleMoves) {
			Flower firstFlower = firstFlowerTriangle.toFlower();
			Flower secondFlower = secondFlowerTriangle.toFlower();
			Move compareMove = new Move(firstFlower, secondFlower);

			for (Move move : possibleMoves) {
				if (move.equals(compareMove))
					return move;
			}

			return null;
		}

		/**
		 * Interne Methode, dieses Objekt zurücksetzt.
		 * Zurücksetzen bedeutet, dass kein Dreieck mehr gewählt ist,
		 * und kein {@link Move} mehr gehalten wird.
		 */
		private void reset() {
			this.firstClickedTriangle = null;
			this.move = null;
		}
	}

	/**
	 * Dieses {@link Stroke}-Objekt zeichnet die Dreiecke auf dem Spielbrett mit dicken Linien. Wird
	 * nur verwendet, wenn das {@link Graphics}-Objekt das in {@link #paintComponent(Graphics)}
	 * gegeben wird ein {@link Graphics2D}-Objekt ist.
	 */
	private static final Stroke stroke = new BasicStroke(
	        5.0F,
	        BasicStroke.CAP_ROUND,
	        BasicStroke.JOIN_ROUND);

	private static final Color triangleBorderColour = Color.BLACK;
	private static final Color triangleDefaultColour = Color.GREEN;
	private static final Color triangleHighlightColour = Color.MAGENTA;
	private static final Color redColour = Color.RED;
	private static final Color blueColour = Color.CYAN;

	/**
	 * Legt fest, wie groß das Spielbrett in Relation zum Fenster sein soll.
	 * TODO: Dies sollte auch abhängig von der absoluten Größe des Frames sein.
	 */
	private static final int componentSizePercentage = 90;

	private Viewer boardViewer = null;
	private Dimension lastDrawingDimension = new Dimension(500, 500);
	private Collection<Triangle> mapTriangles = new ArrayList<>();
	private BoardDisplayMouseHandler boardDisplayMouseHandler = new BoardDisplayMouseHandler(this);
	private Timer renderTimer = new Timer(50, this);

	/**
	 * Der {@link Viewer}, mit dem das Spielbrett betrachtet wird.
	 *
	 * @param boardViewer
	 * 		Der {@link Viewer}, der das zubetrachtende Spielbrett betrachtet.
	 */
	public void setBoardViewer(Viewer boardViewer) {
		this.setPreferredSize(this.lastDrawingDimension);
		this.setSize(this.lastDrawingDimension);
		this.boardViewer = boardViewer;
		this.boardDisplayMouseHandler.reset();
		for (MouseListener mouseListener : this.getMouseListeners())
			this.removeMouseListener(mouseListener);
		this.addMouseListener(this.boardDisplayMouseHandler);
		this.resizeDisplay();
		this.renderTimer.restart();
		this.refresh();
	}

	public void update(Move move) {

	}

	public void showStatus(Status status) {

	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (this.boardViewer == null)
			return;

		Collection<Flower> redFlowers = this.boardViewer.getFlowers(PlayerColor.Red);
		Collection<Flower> blueFlowers = this.boardViewer.getFlowers(PlayerColor.Blue);
		for (Triangle triangle : this.mapTriangles) {
			if (triangle.samePlace(this.boardDisplayMouseHandler.firstClickedTriangle)) {
				Color triangleColour = triangle.getFlowerColour();
				Color newColour = new Color(0xFF - triangleColour.getRed(),
					0xFF - triangleColour.getGreen(),
					0xFF - triangleColour.getBlue());
				triangle.setFlowerColour(newColour);
			}

			triangle.drawTriangle(g);
		}

		this.lastDrawingDimension = this.getSize();
	}

	/**
	 * Diese Methode berechnet gegebenenfalls die Dreiecke neu {@link #updateSize()},
	 * und scheduledtdtddttt ein Redrawing.
	 *
	 * @param actionEvent
	 * Ignoriert.
	 */
	public void actionPerformed(ActionEvent actionEvent) {
			this.updateSize();
			this.repaint();
	}

	/**
	 * Setzt die Größe dieses Elements auf
	 * einen festgelegten Anteil der Größe des Elternelements.
	 */
	public void updateSize() {
		Dimension newSize = new Dimension(
		    this.getParent().getWidth() * componentSizePercentage / 100,
		    this.getParent().getHeight() * componentSizePercentage / 100);
		this.setSize(newSize);
		this.setPreferredSize(newSize);
		this.resizeDisplay();

		this.mapTriangles.stream().forEach(t -> t.setFlowerColour(triangleDefaultColour));
		setTriangleColours(this.boardViewer.getFlowers(PlayerColor.Red), redColour);
		setTriangleColours(this.boardViewer.getFlowers(PlayerColor.Blue), blueColour);
	}

	/**
	 * Färbt Dreiecke basierend auf der Blume, an der sie liegen.
	 *
	 * @param flowers
	 * Eine Sammlung von Blumen.
	 *
	 * @param color
	 * Die Farbe, die den Dreiecken gegeben werden soll, deren Position mit
	 * der Position einer der Blumen übereinstimmt.
	 */
	private void setTriangleColours(Collection<Flower> flowers, Color color) {
		for (Triangle triangle : this.mapTriangles) {
			Flower flower = new Flower(triangle.getTopBoardPosition(),
			                           triangle.getLeftBoardPosition(),
			                           triangle.getRightBoardPosition());

			if (flowers.contains(flower))
				triangle.setFlowerColour(color);
		}
	}

	/**
	 * Handhabt eventuelle Größenänderungen des Displays und
	 * skaliert das gezeichnete Spielfeld dementsprechend.
	 */
	public void resizeDisplay() {
		this.mapTriangles.clear();

		Dimension displaySize = this.getSize();
		int minimumSize = Math.min(displaySize.width, displaySize.height);

		/*
		 * If one side is 0px wide, skip to the end.
		 * Nothing will be visible anyway.
		 */
		if ((minimumSize == 0) || (this.boardViewer == null))
			return;

		// The triangles may not be larger than a fraction percentage of the shortest side.
		int triangleHeight = minimumSize / (this.boardViewer.getSize() + 1);

		// Create the triangle at the very top of the board.
		// It has the coordinates (1, board size + 1)
		Triangle topTriangle = new Triangle(
		    displaySize.width / 2, 10,
		    1, (this.boardViewer.getSize() + 1),
		    triangleHeight,
		    false);
		this.mapTriangles.add(topTriangle);

		recalculateTriangles(topTriangle);
	}

	/**
	 * Berechne die Dreiecke der graphischen Oberfläche neu.
	 *
	 * @param topTriangle
	 * Das oberste Dreieck des Zeichenbretts.
	 */
	private void recalculateTriangles(Triangle topTriangle) {
		int maximumRowCount = (this.boardViewer.getSize() * 2) - 1;
		Triangle currentTriangle = topTriangle;

		for (int triangles = 2; triangles < maximumRowCount; triangles += 2) {
			currentTriangle = new Triangle(
			    currentTriangle.getLeftEdge().x,
			    currentTriangle.getLeftEdge().y,
			    currentTriangle.getLeftBoardPosition().getColumn() - 1,
			    currentTriangle.getLeftBoardPosition().getRow(),
			    topTriangle.getSize(), false);

			this.mapTriangles.add(currentTriangle);
			fillRow(currentTriangle, triangles);
		}
	}

	/**
	 * Fülle eine Reihe mit Dreiecken. Es wird die Annahme getroffen, dass das erste Dreieck auf dem
	 * Kopf steht (das erste Dreieck unter dem obersten Dreieck).
	 *
	 * @param leftTriangle
	 * 		Referenz auf das Dreieck ganz links.
	 * 		Die Reihe wird nach rechts aufgefüllt mit einer Anzahl an Dreiecken,
	 * @param triangleCount
	 * 		Die Anzahl an Dreiecken, die in der Reihe aufgefüllt werden müssen.
	 */
	private void fillRow(Triangle leftTriangle, int triangleCount) {
		Position newTopPosition = leftTriangle.getRightBoardPosition();
		Point newTopPoint = leftTriangle.getRightEdge();

		boolean flipped = true;
		for (int i = 0; i < triangleCount; i++)
		{
			Triangle newTriangle = new Triangle(
			    newTopPoint.x, newTopPoint.y,
			    newTopPosition.getColumn() + 1, newTopPosition.getRow(),
			    leftTriangle.getSize(),
			    flipped);

			this.mapTriangles.add(newTriangle);
			flipped = !flipped;
			newTopPosition = newTriangle.getRightBoardPosition();
			newTopPoint = newTriangle.getRightEdge();
		}
	}

	/**
	 * Erwarte einen Move, der von der GUI (d.h. dem menschlichen User) geholt wird.
	 *
	 * @return
	 * Ein {@link Move}, der von vom menschlichen User erfragt wird.
	 */
	public Move awaitMove() throws InterruptedException {
		this.boardDisplayMouseHandler.reset();

		// NOTE: We need Thread.sleep to avoid 100% CPU usage.
		while (true) {
			synchronized (this.boardDisplayMouseHandler.moveAwaitLock) {
				this.boardDisplayMouseHandler.moveAwaitLock.wait();
				if (this.boardDisplayMouseHandler.move == null) {
					this.boardDisplayMouseHandler.moveAwaitLock.notifyAll();
				} else {
					Move move = this.boardDisplayMouseHandler.move;
					this.boardDisplayMouseHandler.reset();
					return move;
				}
			}
		}
	}

	public void refresh() {
		this.updateSize();
	}
}
