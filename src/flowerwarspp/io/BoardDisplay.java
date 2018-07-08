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
	private class DisplayMouseHandler extends MouseAdapter {
		/**
		 * Das {@link BoardDisplay}, zu dem dieser {@link MouseAdapter} gehört.
		 */
		private BoardDisplay boardDisplay = null;
		private boolean isRequesting = false;

		private final Object moveAwaitLock = new Object();
		/**
		 * Wenn {@link Flower}s gesetzt werden, müssen zwei Dreiecke geklickt werden.
		 * Wurden zwei geklickt, so sind sie hierin gespeichert.
		 */
		private Flower clickedFlower1 = null, clickedFlower2 = null;
		/**
		 * Wenn ein {@link Ditch} gesetzt werden soll, muss dieser angeklickt werden.
		 * Wurde einer geklickt, so ist er hierin gespeichert.
		 */
		private Ditch clickedDitch = null;

		/**
		 * Konstruiert einen {@link DisplayMouseHandler}, der an ein {@link BoardDisplay}
		 * gebunden ist.
		 *
		 * @param boardDisplay
		 * Das {@link BoardDisplay}, an welches dieses Objekt gebunden ist.
		 */
		public DisplayMouseHandler(BoardDisplay boardDisplay) {
			this.boardDisplay = boardDisplay;
		}

		/**
		 * {@inheritDoc}
		 */
		public void mouseClicked(MouseEvent mouseEvent) {
			if (!this.isRequesting)
				return;

			this.processClick(mouseEvent);
			this.boardDisplay.getParent().repaint();
			synchronized (this.moveAwaitLock) {
				this.moveAwaitLock.notify();
			}
		}

		/**
		 * Reagiert auf den Klick selbst.
		 *
		 * @param mouseEvent
		 * Das {@link MouseEvent}, das die Ausführung verursacht hat.
		 */
		private void processClick(MouseEvent mouseEvent) {
			Point clickPoint = mouseEvent.getPoint();
			Dot dot = findDot(clickPoint);
			if (dot != null)
				return;

			Edge edge = findEdge(clickPoint);
			if (edge != null) {
				this.onEdgeClick(edge);
				return;
			}

			Triangle triangle = findTriangle(clickPoint);
			if (triangle != null) {
				this.onTriangleClick(triangle);
			}
		}

		/**
		 * Verarbietet den {@link Edge}-Klick.
		 *
		 * @param edge
		 * Die geklickte {@link Edge}. Darf nicht <code>null</code> sein.
		 */
		private void onEdgeClick(Edge edge) {
			if (this.clickedFlower1 != null)
				return;

			Ditch ditch = edge.toDitch();
			this.clickedDitch = ditch;
		}

		/**
		 * Verarbeitet den {@link Triangle}-Klick.
		 *
		 * @param triangle
		 * Das geklickte {@link Triangle}. Darf nicht <code>null</code> sein.
		 */
		private void onTriangleClick(Triangle triangle) {
			if (triangle != null) {
				if (this.clickedFlower1 == null) {
					this.clickedFlower1 = triangle.toFlower();
				} else {
					this.clickedFlower2 = triangle.toFlower();
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

		private Edge findEdge(Point point) {
			for (Edge e : this.boardDisplay.mapEdges) {
				if (e.contains(point))
					return e;
			}

			return null;
		}

		/**
		 * Findet einen {@link Dot} am spezifizierten {@link Point}.
		 *
		 * @param point
		 * Der {@link Point}, an dem der {@link Dot} liegt.
		 *
		 * @return
		 * Der {@link Dot}, der derzeit an der angegebenen Stelle liegt,
		 * oder <code>null</code>, falls dort keiner liegt.
		 */
		private Dot findDot(Point point) {
			for (Dot d : this.boardDisplay.mapDots) {
				if (d.contains(point))
					return d;
			}

			return null;
		}

		/**
		 * Interne Methode, dieses Objekt zurücksetzt.
		 * Zurücksetzen bedeutet, dass kein Dreieck mehr gewählt ist,
		 * und kein {@link Move} mehr gehalten wird.
		 */
		private void reset() {
			this.clickedFlower1 = null;
			this.clickedFlower2 = null;
			this.clickedDitch = null;
			this.isRequesting = false;
		}
	}

	/**
	 * Die Farbe, die ein angewähltes {@link Triangle} hat,
	 * bevor ein zweites für einen {@link Move} gewählt wurde.
	 */
	private static final Color triangleClickedColour = Color.MAGENTA;
	/**
	 * Die Farbe, die ein {@link Triangle} hat,
	 * wenn es mit dem aktuell angewählten kombinierbar ist.
	 */
	private static final Color triangleCombinableColour = Color.GREEN;
	/**
	 * Die Farbe der Dreiecke, die dem {@link PlayerColor#Red} gehören.
	 */
	private static final Color redColour = Color.RED;
	/**
	 * Die Farbe der Dreiecke, die dem {@link PlayerColor#Blue} gehören.
	 */
	private static final Color blueColour = Color.CYAN;

	/**
	 * Der {@link Viewer}, durch den dieses Display auf das {@link Board} schauen soll.
	 */
	private Viewer boardViewer;
	/**
	 * Eine {@link Collection} von {@link Triangle}s, die die {@link Flower}s
	 * des betrachteten {@link Board}s repräsentieren.
	 */
	private Collection<Triangle> mapTriangles = new ArrayList<>();
	/**
	 * Eine {@link Collection} von {@link Edge}s, die die {@link Ditch}es
	 * des betrachteten {@link Board}s repräsentieren.
	 */
	private Collection<Edge> mapEdges = new ArrayList<>();
	/**
	 * Eine {@link Collection} von {@link Dot}s, verwendet für kosmetische Zwecke.
	 */
	private Collection<Dot> mapDots = new ArrayList<>();
	/**
	 * Handhabt alle {@link MouseEvent}s, die in diesem {@link JPanel} auftreten können.
	 */
	private DisplayMouseHandler displayMouseHandler = new DisplayMouseHandler(this);
	/**
	 * Ein {@link PlayerStatusDisplay}, das den Status der Spieler anzeigt.
	 */
	private PlayerStatusDisplay statusDisplay = new PlayerStatusDisplay(redColour, blueColour);

	// Cached information fresh (or stale) from the viewer
	/**
	 * Eine {@link Collection} von {@link Flower}s, die dem {@link PlayerColor#Red} gehören.
	 */
	private Collection<Flower> redFlowers;
	/**
	 * Eine {@link Collection} von {@link Flower}s, die dem {@link PlayerColor#Blue} gehören.
	 */
	private Collection<Flower> blueFlowers;
	/**
	 * Eine {@link Collection} von {@link Ditch}es, die dem {@link PlayerColor#Red} gehören.
	 */
	private Collection<Ditch> redDitches;
	/**
	 * Eine {@link Collection} von {@link Ditch}es, die dem {@link PlayerColor#Blue} gehören.
	 */
	private Collection<Ditch> blueDitches;
	/**
	 * Eine {@link Collection} von {@link Flower}s,
	 * die mit der aktuell angewählten {@link Flower} kombinierbar sind.
	 */
	private Collection<Flower> combinableFlowers;
	/**
	 * Eine {@link Collection} möglicher {@link Move}s, die {@link Ditch}es enthalten.
	 */
	private Collection<Move> possibleDitchMoves;
	/**
	 * Die Größe des {@link Board}s.
	 */
	private int boardSize;
	/**
	 * Die {@link PlayerColor} des Spielers, der gerade am Zug ist.
	 */
	private PlayerColor currentPlayer;
	/**
	 * <code>true</code> genau dann, wenn das Spiel geendet hat.
	 */
	private boolean gameEnd = false;

	/**
	 * Konstruiert ein Display für die Darstellung eines {@link Board}s.
	 */
	public BoardDisplay() {
		Font font = this.getFont().deriveFont(10F);
		this.setFont(font);
		this.setOpaque(false);
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
		this.statusDisplay.updateStatus(0, 0);
		// NOTE: It is very very important that the Triangles be created before the ditches and dots
		this.createTriangles();
		this.createDitches();
		this.createDots();
		this.displayMouseHandler.reset();
		this.addMouseListener(this.displayMouseHandler);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void paintComponent(Graphics g) {
		this.updateTriangles();
		this.updatePolygonSizes();
		super.paintComponent(g);

		this.mapTriangles.forEach(t -> t.drawPolygon(g));
		this.mapEdges.forEach(e -> e.drawPolygon(g));
		this.mapDots.forEach(d -> d.drawPolygon(g));
		this.statusDisplay.draw(g);
	}

	/**
	 * Updatet die {@link Triangle}s nach einem Zug.
	 */
	private void updateTriangles() {
		for (Triangle t : this.mapTriangles) {
			Flower f = t.toFlower();
			if ((this.redFlowers != null) && this.redFlowers.contains(f))
				t.setFillColour(redColour);
			else if ((this.blueFlowers != null) && this.blueFlowers.contains(f))
				t.setFillColour(blueColour);
			else {
				if (this.displayMouseHandler.clickedFlower1 == null) {
					if ((this.combinableFlowers != null) && (this.combinableFlowers.contains(f))) {
						t.setFillColour(triangleCombinableColour);
					} else {
						t.setFillColour(this.getBackground());
					}
				} else if (t.samePlace(this.displayMouseHandler.clickedFlower1)) {
						t.setFillColour(triangleClickedColour);
				}
			}
		}

		if (this.redDitches != null)
			setEdgeColours(this.redDitches, redColour);
		if (this.blueDitches != null)
			setEdgeColours(this.blueDitches, blueColour);
	}

	/**
	 * Setzt die Farbe der Edges zur Farbe der Spieler, dem sie gehören.
	 *
	 * @param ditches
	 * Eine {@link Collection} von {@link Ditch}es.
	 *
	 * @param colour
	 * Eine {@link Color}, die den Spieler repräsentiert, dem die {@link Ditch}es gehören.
	 */
	private void setEdgeColours(Collection<Ditch> ditches, Color colour) {
		for (Edge edge : this.mapEdges) {
			Ditch ditch = edge.toDitch();

			if (ditches.contains(ditch)) {
				edge.setFillColour(colour);
			}
		}
	}

	/**
	 * Updatet die Größe der {@link Triangle}s. Wird verwendet, um die Dreiecke der aktuellen
	 * {@link Dimension} des Zeichenbretts anzupassen.
	 */
	private void updatePolygonSizes() {
		Dimension displaySize = this.getParent().getSize();
		displaySize.height -= 20;
		this.setPreferredSize(displaySize);
		int minimumSize = Math.min(displaySize.width, displaySize.height);

		// The triangles may not be larger than a fraction percentage of the shortest side.
		int sideLength = minimumSize / (this.boardSize + 1);
		Point drawBegin = new Point();
		drawBegin.x = (displaySize.width / 2) - sideLength * (this.boardSize + 2) / 2;
		drawBegin.y = sideLength * this.boardSize;

		this.mapTriangles.forEach(t -> t.recalcPoints(sideLength, drawBegin));
		this.mapEdges.forEach(e -> e.recalcPoints(sideLength, drawBegin));
		this.mapDots.forEach(e -> e.recalcPoints(sideLength, drawBegin));
		this.statusDisplay.updateRectangleSizes(displaySize);
	}

	/**
	 * Erstellt {@link Triangle}-Objekte, die die Dreiecke auf dem Spiel repräsentieren.
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
	 * Erstellt die Reihen von Dreiecken
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

	/**
	 * Konstruiert die {@link Edge}s, die {@link Ditch}es repräsentieren werden.
	 */
	private void createDitches() {
		// for each non-flipped triangle, create the three ditches around it.
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

	/**
	 * Konstruiert die {@link Dot}s. Sie dienen als Orientierung für den Spieler und Zuschaue
	 */
	private void createDots() {
		// For each non-flipped triangle, create three dots for its edges.
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
		this.displayMouseHandler.reset();
		this.displayMouseHandler.isRequesting = true;
		Move result = null;

		while (result == null) {
			this.combinableFlowers = this.boardViewer.getPossibleFlowers();
			this.repaint();
			synchronized (this.displayMouseHandler.moveAwaitLock) {
				this.displayMouseHandler.moveAwaitLock.wait();

				if (this.displayMouseHandler.clickedFlower1 != null) {
					result = this.checkForFlowerMove();
				} else {
					result = this.checkForDitchMove();
				}
			}
		}

		this.displayMouseHandler.reset();
		this.repaint();
		return result;
	}

	/**
	 * Ausschließlich intern benutzt.
	 * Überprüft den aktuellen Klick auf einen {@link Flower}-Move.
	 *
	 * @return
	 * Einen {@link Move} der die gewählten {@link Flower}s enthält,
	 * oder <code>null</code>, wenn kein gültiger {@link Move} gewählt ist.
	 */
	private Move checkForFlowerMove() {
		Flower flower1 = this.displayMouseHandler.clickedFlower1;
		if (!this.boardViewer.possibleMovesContainsMovesContaining(flower1)) {
			this.displayMouseHandler.reset();
			this.displayMouseHandler.isRequesting = true;
		} else if (this.displayMouseHandler.clickedFlower2 == null) {
			this.combinableFlowers = this.boardViewer.getFlowersCombinableWith(flower1);
		} else {
			Flower flower2 = this.displayMouseHandler.clickedFlower2;
			if (!this.combinableFlowers.contains(flower2)) {
				this.displayMouseHandler.clickedFlower2 = null;
			} else {
				return new Move(flower1, flower2);
			}
		}

		return null;
	}

	/**
	 * Ausschließlich intern benutzt.
	 * Überprüft den aktuellen Klick auf einen {@link Ditch}-Move.
	 *
	 * @return
	 * Einen {@link Move} der den gewählten {@link Ditch} enthält,
	 * oder <code>null</code>, wenn kein gültiger {@link Move} gewählt ist.
	 */
	private Move checkForDitchMove() {
		Ditch ditch = this.displayMouseHandler.clickedDitch;
		if (ditch == null) {
			this.displayMouseHandler.reset();
			this.displayMouseHandler.isRequesting = true;
		} else {
			if (((this.redDitches != null) && this.redDitches.contains(ditch) ||
				this.blueDitches != null && this.blueDitches.contains(ditch)))
			{
				this.displayMouseHandler.reset();
				this.displayMouseHandler.isRequesting = true;
			}

			Move result = new Move(ditch);
			if ((this.possibleDitchMoves != null) && this.possibleDitchMoves.contains(result))
				return result;
		}

		return null;
	}

	/**
	 * Updatet das interne Cache des Spielbretts dieses Displays.
	 */
	public synchronized void refresh() {
		if (this.gameEnd)
			return;

		this.redFlowers = this.boardViewer.getFlowers(PlayerColor.Red);
		this.blueFlowers = this.boardViewer.getFlowers(PlayerColor.Blue);
		this.redDitches = this.boardViewer.getDitches(PlayerColor.Red);
		this.blueDitches = this.boardViewer.getDitches(PlayerColor.Blue);
		this.possibleDitchMoves = this.boardViewer.getPossibleDitchMoves();
		this.currentPlayer = this.boardViewer.getTurn();

		int redPlayerPoints = this.boardViewer.getPoints(PlayerColor.Red);
		int bluePlayerPoints = this.boardViewer.getPoints(PlayerColor.Blue);
		this.statusDisplay.updateStatus(redPlayerPoints, bluePlayerPoints);

		if (this.boardViewer.getStatus() != Status.Ok) {
			new EndPopupFrame(this.boardViewer.getStatus());
			this.gameEnd = true;
		}
	}
}
