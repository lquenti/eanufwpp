package flowerwarspp.io;

import flowerwarspp.preset.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class BoardDisplay extends JPanel {
	/**
	 * Eine private Klasse, die die Mausaktionen für das {@link BoardDisplay} verarbeitet.
	 */
	private class BoardDisplayMouseHandler extends MouseAdapter {
		/**
		 * Das {@link BoardDisplay}, zu dem dieser {@link MouseAdapter} gehört.
		 */
		private BoardDisplay boardDisplay = null;
		private boolean isRequesting = false;

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
			if (!this.isRequesting)
				return;

			Triangle triangle = findTriangle(mouseEvent.getPoint());
			if (this.firstClickedTriangle == null) {
				this.firstClickedTriangle = triangle;
				synchronized (this.moveAwaitLock) {
					this.moveAwaitLock.notify();
				}
			} else {
				// The user has actually clicked on a triangle
				if (triangle != null) {
					// If the user clicks on the same triangle again, deselect it.
					if (this.firstClickedTriangle.samePlace(triangle)) {
						Color newColour = this.boardDisplay.getBackground();
						this.firstClickedTriangle.setFillColour(newColour);
						this.firstClickedTriangle = null;
					// Otherwise, try and create a move out of it.
					} else {
						Flower firstFlower = firstClickedTriangle.toFlower();
						Flower secondFlower = triangle.toFlower();
						if (!this.boardDisplay.combinableFlowers.contains(secondFlower)) {
							move = null;
						}
						else {
							this.move = new Move(firstFlower, secondFlower);
						}

						this.firstClickedTriangle = null;
						// The main thread is waiting for a reaction from here, so unlock this
						synchronized (this.moveAwaitLock) {
							this.moveAwaitLock.notify();
						}
					}
				}
			}

			this.boardDisplay.getParent().repaint();
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

	private static final Color triangleDefaultColour = Color.GREEN;
	private static final Color triangleHighlightColour = Color.MAGENTA;
	private static final Color combinableTriangleColour = Color.GREEN;
	private static final Color redColour = Color.RED;
	private static final Color blueColour = Color.CYAN;

	/**
	 * Legt fest, wie groß das Spielbrett in Relation zum Fenster sein soll.
	 * TODO: Dies sollte auch abhängig von der absoluten Größe des Frames sein.
	 */
	private static final int componentSizePercentage = 90;

	private Viewer boardViewer = null;
	private Collection<Triangle> mapTriangles = new ArrayList<>();
	private Collection<Edge> mapEdges = new ArrayList<>();
	private Collection<Dot> mapDots = new ArrayList<>();
	private BoardDisplayMouseHandler boardDisplayMouseHandler = new BoardDisplayMouseHandler(this);

	// Cached information fresh (or stale) from the viewer
	private Collection<Flower> redFlowers;
	private Collection<Flower> blueFlowers;
	private Collection<Flower> combinableFlowers;
	private int boardSize;

	public BoardDisplay() {
		Font font = this.getFont().deriveFont(10F);
		this.setFont(font);
	}

	/**
	 * Der {@link Viewer}, mit dem das Spielbrett betrachtet wird.
	 *
	 * @param boardViewer
	 * 		Der {@link Viewer}, der das zubetrachtende Spielbrett betrachtet.
	 */
	public void setBoardViewer(Viewer boardViewer) {
		this.boardViewer = boardViewer;
		this.boardSize = this.boardViewer.getSize();
		// NOTE: It is very very important that the Triangles be created before the ditches and dots
		this.createTriangles();
		this.createDitches();
		this.createDots();
		this.boardDisplayMouseHandler.reset();
		for (MouseListener mouseListener : this.getMouseListeners())
			this.removeMouseListener(mouseListener);
		this.addMouseListener(this.boardDisplayMouseHandler);
	}

	@Override
	public synchronized void paintComponent(Graphics g) {
		this.updateTriangles();
		this.updatePolygonSizes();
		super.paintComponent(g);

		this.mapTriangles.forEach(t -> t.drawPolygon(g));
		this.mapEdges.forEach(e -> e.drawPolygon(g));
		this.mapDots.forEach(d -> d.drawPolygon(g));
	}

	/**
	 * Updatet die {@link Triangle}s nach einem Zug.
	 */
	private void updateTriangles() {
		for (Triangle t : this.mapTriangles) {
			t.setFillColour(triangleDefaultColour);
			if (t.samePlace(this.boardDisplayMouseHandler.firstClickedTriangle)) {
				t.setFillColour(triangleHighlightColour);
			} else {
				if (this.combinableFlowers != null) {
					Optional<Flower> maybeFlower = this.combinableFlowers.stream()
						.filter(f -> t.samePlace(f))
						.findAny();

					if (maybeFlower.isPresent())
						t.setFillColour(combinableTriangleColour);
					else
						t.setFillColour(this.getBackground());
				}
			}
		}

		if (this.redFlowers != null)
			setTriangleColours(this.redFlowers, redColour);
		if (this.blueFlowers != null)
			setTriangleColours(this.blueFlowers, blueColour);
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
				triangle.setFillColour(color);
		}
	}

	/**
	 * Updatet die Größe der {@link Triangle}s. Wird verwendet, um die Dreiecke der aktuellen
	 * {@link Dimension} des Zeichenbretts anzupassen.
	 */
	private void updatePolygonSizes() {
		Dimension displaySize = this.getSize();
		int minimumSize = Math.min(displaySize.width, displaySize.height);

		// The triangles may not be larger than a fraction percentage of the shortest side.
		int sideLength = minimumSize / (this.boardSize + 1);
		Point drawBegin = new Point();
		drawBegin.x = (displaySize.width / 2) - sideLength * (this.boardSize + 2) / 2;
		drawBegin.y = sideLength * this.boardSize;
		this.mapTriangles.forEach(t -> t.recalcPoints(sideLength, drawBegin));
		this.mapEdges.forEach(e -> e.recalcPoints(sideLength, drawBegin));
		this.mapDots.forEach(e -> e.recalcPoints(sideLength, drawBegin));
	}

	/**
	 * Handhabt eventuelle Größenänderungen des Displays und
	 * skaliert das gezeichnete Spielfeld dementsprechend.
	 */
	private void createTriangles() {
		this.mapTriangles.clear();

		// Create the triangle at the very top of the board.
		// It has the coordinates (1, board size + 1)
		Triangle topTriangle = new Triangle(
		    1, (this.boardSize + 1),
		    false, this.getBackground());

		this.mapTriangles.add(topTriangle);
		createRowTriangles(topTriangle);
	}

	/**
	 * Erstelle die Reihen der Dreiecke.
	 *
	 * @param topTriangle
	 * Das oberste Dreieck des Zeichenbretts.
	 */
	private void createRowTriangles(Triangle topTriangle) {
		int maximumRowCount = (this.boardSize * 2) - 1;
		Triangle currentTriangle = topTriangle;

		for (int triangles = 2; triangles < maximumRowCount; triangles += 2) {
			Position leftPosition = currentTriangle.getLeftBoardPosition();

			currentTriangle = new Triangle(
			    leftPosition.getColumn() - 1, leftPosition.getRow(),
			    false, this.getBackground());

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

		boolean flipped = true;
		for (int i = 0; i < triangleCount; i++)
		{
			Triangle newTriangle = new Triangle(
			    newTopPosition.getColumn() + 1, newTopPosition.getRow(),
			    flipped, this.getBackground());

			this.mapTriangles.add(newTriangle);

			flipped = !flipped;
			newTopPosition = newTriangle.getRightBoardPosition();
		}
	}

	private void createDitches() {
		for (Triangle t : this.mapTriangles) {
			if (!t.isFlipped()) {
				Edge leftDitch = new Edge(t.getLeftBoardPosition(), t.getTopBoardPosition());
				Edge rightDitch = new Edge(t.getRightBoardPosition(), t.getTopBoardPosition());
				Edge bottomDitch = new Edge(t.getLeftBoardPosition(), t.getRightBoardPosition());

				this.mapEdges.add(leftDitch);
				this.mapEdges.add(rightDitch);
				this.mapEdges.add(bottomDitch);
			}
		}
	}

	private void createDots() {
		for (Triangle t : this.mapTriangles) {
			if (!t.isFlipped()) {
				Dot leftDot = new Dot(t.getLeftBoardPosition());
				Dot topDot = new Dot(t.getTopBoardPosition());
				Dot rightDot = new Dot(t.getRightBoardPosition());

				this.mapDots.add(leftDot);
				this.mapDots.add(topDot);
				this.mapDots.add(rightDot);
			}
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
		this.boardDisplayMouseHandler.isRequesting = true;

		// NOTE: We need Thread.sleep to avoid 100% CPU usage.
		while (true) {
			synchronized (this.boardDisplayMouseHandler.moveAwaitLock) {
				this.boardDisplayMouseHandler.moveAwaitLock.wait();
				if (this.boardDisplayMouseHandler.move == null) {
					Triangle triangle = this.boardDisplayMouseHandler.firstClickedTriangle;
					if (triangle != null) {
						Flower flower = triangle.toFlower();
						try {
							this.combinableFlowers = this.boardViewer.getFlowersCombinableWith(flower);
						} catch (NullPointerException e) {
							// FIXME: ¿?
						}
					}
					this.boardDisplayMouseHandler.moveAwaitLock.notifyAll();
				} else {
					Move move = this.boardDisplayMouseHandler.move;
					this.boardDisplayMouseHandler.reset();
					this.boardDisplayMouseHandler.isRequesting = false;
					this.combinableFlowers = Collections.EMPTY_LIST;
					return move;
				}
			}
		}
	}

	public synchronized void refresh() {
		this.redFlowers = this.boardViewer.getFlowers(PlayerColor.Red);
		this.blueFlowers = this.boardViewer.getFlowers(PlayerColor.Blue);
	}
}
