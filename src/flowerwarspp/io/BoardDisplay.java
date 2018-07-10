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
		private BoardDisplay boardDisplay;
		private boolean isRequesting = false;

		private final Object moveAwaitLock = new Object();
		/**
		 * Der {@link MoveType}, der durch den Klick erzeugt wird.
		 * Nur genau dann nicht {@link MoveType#Flower}, wenn entweder
		 * der Surrender- oder der End-Button geklickt wurde.
		 */
		private MoveType moveType = MoveType.Flower;
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
			if (!isRequesting)
				return;

			processClick(mouseEvent);
			boardDisplay.getParent().repaint();
			synchronized (moveAwaitLock) {
				moveAwaitLock.notify();
			}
		}

		/**
		 * Reagiert auf den Klick selbst.
		 *
		 * @param mouseEvent
		 * Das {@link MouseEvent}, das die Ausführung verursacht hat.
		 */
		private void processClick(MouseEvent mouseEvent) {
			// Yes, both of these are supposed to be "==".
			// We want to make sure they are the same object.
			if (mouseEvent.getComponent() == boardDisplay.surrenderButton) {
				moveType = MoveType.Surrender;
				return;
			}
			if (mouseEvent.getComponent() == boardDisplay.endButton) {
				moveType = MoveType.End;
				return;
			}

			Point clickPoint = mouseEvent.getPoint();
			Dot dot = findDot(clickPoint);
			if (dot != null)
				return;

			Edge edge = findEdge(clickPoint);
			if (edge != null) {
				onEdgeClick(edge);
				return;
			}

			Triangle triangle = findTriangle(clickPoint);
			if (triangle != null) {
				onTriangleClick(triangle);
			}
		}

		/**
		 * Verarbietet den {@link Edge}-Klick.
		 *
		 * @param edge
		 * Die geklickte {@link Edge}. Darf nicht <code>null</code> sein.
		 */
		private void onEdgeClick(Edge edge) {
			if (clickedFlower1 != null)
				return;

			Ditch ditch = edge.toDitch();
			clickedDitch = ditch;
		}

		/**
		 * Verarbeitet den {@link Triangle}-Klick.
		 *
		 * @param triangle
		 * Das geklickte {@link Triangle}. Darf nicht <code>null</code> sein.
		 */
		private void onTriangleClick(Triangle triangle) {
			if (triangle != null) {
				if (clickedFlower1 == null) {
					clickedFlower1 = triangle.toFlower();
				} else {
					clickedFlower2 = triangle.toFlower();
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
			for (Triangle t : boardDisplay.mapTriangles) {
				if (t.contains(point))
					return t;
			}

			return null;
		}

		private Edge findEdge(Point point) {
			for (Edge e : boardDisplay.mapEdges) {
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
			for (Dot d : boardDisplay.mapDots) {
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
			clickedFlower1 = null;
			clickedFlower2 = null;
			clickedDitch = null;
			isRequesting = false;
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
	 * Die Farbe die eine {@link Edge} hat,
	 * wenn es einen gültigen {@link Move} gibt, der den repräsentierten {@link Ditch} enthält.
	 */
	private static final Color ditchClickableColour = Color.GREEN;
	/**
	 * Die Farbe der Dreiecke, die dem {@link PlayerColor#Red} gehören.
	 */
	private static final Color redColour = Color.RED;
	/**
	 * Die Farbe der Dreiecke, die dem {@link PlayerColor#Blue} gehören.
	 */
	private static final Color blueColour = Color.CYAN;
	private static final int buttonHeight = 20;
	private static final int buttonWidth = 200;

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
	 * <code>true</code> genau dann, wenn das Spiel geendet hat.
	 */
	private boolean gameEnd = false;

	private JButton surrenderButton = new JButton("Surrender");
	private JButton endButton = new JButton("End");

	/**
	 * Konstruiert ein Display für die Darstellung eines {@link Board}s.
	 */
	public BoardDisplay() {
		Font font = getFont().deriveFont(10F);
		setFont(font);
		setOpaque(false);
		setLayout(null);
		surrenderButton.addMouseListener(displayMouseHandler);
		endButton.addMouseListener(displayMouseHandler);
		add(surrenderButton);
		add(endButton);
	}

	/**
	 * Der {@link Viewer}, mit dem das Spielbrett betrachtet wird.
	 *
	 * @param boardViewer
	 * 		Der {@link Viewer}, der das zubetrachtende Spielbrett betrachtet.
	 */
	public void setBoardViewer(Viewer boardViewer) {
		this.boardViewer = boardViewer;
		redFlowers = boardViewer.getFlowers(PlayerColor.Red);
		blueFlowers = boardViewer.getFlowers(PlayerColor.Blue);
		redDitches = boardViewer.getDitches(PlayerColor.Red);
		blueDitches = boardViewer.getDitches(PlayerColor.Blue);
		boardSize = boardViewer.getSize();

		statusDisplay.updateStatus(boardViewer.getPoints(PlayerColor.Red),
		                           boardViewer.getPoints(PlayerColor.Blue));
		surrenderButton.setEnabled(false);
		// This sets the endButton enabled if and only if there is an "End" move available.
		endButton.setEnabled(boardViewer.possibleMovesContains(new Move(MoveType.End)));
		// NOTE: It is very very important that the Triangles be created before the ditches and dots

		createTriangles();
		createDitches();
		createDots();

		displayMouseHandler.reset();
		addMouseListener(displayMouseHandler);
	}


	/*
	 * Post-viewersetting, the board display requires initialisation.
	 * The following code section fills the display with Triangles
	 * representing Flowers, with Edges representing Ditches and
	 * other Polynomiæ.
	 */


	/**
	 * Erstellt {@link Triangle}-Objekte, die die Dreiecke auf dem Spiel repräsentieren.
	 */
	private void createTriangles() {
		mapTriangles.clear();

		// Create the triangle at the very top of the board.
		// It has the coordinates (1, board size + 1)
		Triangle topTriangle = new Triangle(1, (boardSize + 1),false, getBackground());

		mapTriangles.add(topTriangle);
		createRowTriangles(topTriangle);
	}

	/**
	 * Erstellt die Reihen von Dreiecken
	 *
	 * @param topTriangle
	 * Das oberste Dreieck des Zeichenbretts.
	 */
	private void createRowTriangles(Triangle topTriangle) {
		int maximumRowCount = (boardSize * 2) - 1;
		Triangle currentTriangle = topTriangle;

		for (int triangles = 2; triangles < maximumRowCount; triangles += 2) {
			Position leftPosition = currentTriangle.getLeftBoardPosition();

			int column = leftPosition.getColumn() - 1;
			int row = leftPosition.getRow();
			currentTriangle = new Triangle(column, row, false, getBackground());

			mapTriangles.add(currentTriangle);

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
			int column = newTopPosition.getColumn() + 1;
			int row = newTopPosition.getRow();
			Triangle newTriangle = new Triangle(column, row, flipped, getBackground());

			mapTriangles.add(newTriangle);

			flipped = !flipped;
			newTopPosition = newTriangle.getRightBoardPosition();
		}
	}

	/**
	 * Konstruiert die {@link Edge}s, die {@link Ditch}es repräsentieren werden.
	 */
	private void createDitches() {
		// for each non-flipped triangle, create the three ditches around it.
		for (Triangle t : mapTriangles) {
			if (!t.isFlipped()) {
				Edge leftDitch = new Edge(t.getLeftBoardPosition(), t.getTopBoardPosition());
				Edge rightDitch = new Edge(t.getRightBoardPosition(), t.getTopBoardPosition());
				Edge bottomDitch = new Edge(t.getLeftBoardPosition(), t.getRightBoardPosition());

				mapEdges.add(leftDitch);
				mapEdges.add(rightDitch);
				mapEdges.add(bottomDitch);
			}
		}
	}

	/**
	 * Konstruiert die {@link Dot}s. Sie dienen als Orientierung für den Spieler und Zuschaue
	 */
	private void createDots() {
		// For each non-flipped triangle, create three dots for its edges.
		for (Triangle t : mapTriangles) {
			if (!t.isFlipped()) {
				Dot leftDot = new Dot(t.getLeftBoardPosition());
				Dot topDot = new Dot(t.getTopBoardPosition());
				Dot rightDot = new Dot(t.getRightBoardPosition());

				mapDots.add(leftDot);
				mapDots.add(topDot);
				mapDots.add(rightDot);
			}
		}
	}


	/*
	 * When drawing, three substantial actions need to be taken:
	 * a) Ask the crucial BoardPolygons to update themselves.
	 *    They may colouring, recolouring or de-colouring.
	 * b) Ask the crucial BoardPolygons to update their sizes.
	 *    They may get smaller or bigger, they may not resize at all.
	 * c) Ask the Swing components to update themselves.
	 * d) Ask the Swing environment to cover general drawage.
	 *    That will take care of drawing the Swing components
	 *    and the clear the canvas for us
	 * e) Begin drawing onto the panel (that is, into the buffer).
	 *
	 * All of these actions take place in the following section of code.
	 */


	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void paintComponent(Graphics g) {
		updatePolygons();
		updatePolygonSizes();
		updateComponents();
		super.paintComponent(g);

		// Antialiasing makes things look good.
		if (g instanceof Graphics2D) {
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			                                  RenderingHints.VALUE_ANTIALIAS_ON);
		}

		mapTriangles.forEach(t -> t.drawPolygon(g));
		mapEdges.forEach(e -> e.drawPolygon(g));
		mapDots.forEach(d -> d.drawPolygon(g));
		statusDisplay.draw(g);
	}

	/**
	 * Updatet die {@link BoardPolygon}s nach einem Zug, die ein Update benötigen.
	 */
	private void updatePolygons() {
		updateTriangles();
		updateEdges();
	}

	/**
	 * Updatet die {@link Triangle}s nach einem Zug.
	 */
	private void updateTriangles() {
		for (Triangle t : mapTriangles) {
			t.setFillColour(getBackground());
			Flower flower = t.toFlower();
			if ((redFlowers != null) && redFlowers.contains(flower))
				t.setFillColour(redColour);
			else if ((blueFlowers != null) && blueFlowers.contains(flower))
				t.setFillColour(blueColour);
			else {
				if (displayMouseHandler.clickedFlower1 != null) {
					if (flower.equals(displayMouseHandler.clickedFlower1)) {
						t.setFillColour(triangleClickedColour);
					} else if ((combinableFlowers != null) && (combinableFlowers.contains(flower))) {
						t.setFillColour(triangleCombinableColour);
					}
				} else if ((combinableFlowers != null) && combinableFlowers.contains(flower)) {
					t.setFillColour(triangleCombinableColour);
				}
			}
		}
	}

	/**
	 * Updatet die {@link Edge}s nach einem Zug.
	 */
	private void updateEdges() {
		for (Edge e : mapEdges) {
			Ditch ditch = e.toDitch();
			Move move = new Move(ditch);

			if ((redDitches != null) && (redDitches.contains(ditch))) {
				e.setFillColour(redColour);
			} else if ((blueDitches != null) && (blueDitches.contains(ditch))) {
				e.setFillColour(blueColour);
			} else if ((possibleDitchMoves != null) && possibleDitchMoves.contains(move)) {
				e.setFillColour(ditchClickableColour);
			} else {
				e.setFillColour(Color.BLACK);
			}
		}
	}

	/**
	 * Updatet die Größe der {@link Triangle}s. Wird verwendet, um die Dreiecke der aktuellen
	 * {@link Dimension} des Zeichenbretts anzupassen.
	 */
	private void updatePolygonSizes() {
		Dimension displaySize = getParent().getSize();
		displaySize.height -= buttonHeight;
		setPreferredSize(displaySize);
		int minimumSize = Math.min(displaySize.width, displaySize.height);

		// The triangles may not be larger than a fraction percentage of the shortest side.
		int sideLength = minimumSize / (boardSize + 1);
		Point drawBegin = new Point();
		drawBegin.x = (displaySize.width / 2) - sideLength * (boardSize + 2) / 2;
		drawBegin.y = sideLength * boardSize;

		mapTriangles.forEach(t -> t.recalcPoints(sideLength, drawBegin));
		mapEdges.forEach(e -> e.recalcPoints(sideLength, drawBegin));
		mapDots.forEach(e -> e.recalcPoints(sideLength, drawBegin));
		statusDisplay.updateRectangleSizes(displaySize);
	}

	/**
	 * Updatet die {@link JComponent}s (Buttons etc).
	 */
	private void updateComponents() {
		Dimension size = getSize();
		surrenderButton.setSize(buttonWidth, buttonHeight);
		endButton.setSize(buttonWidth, buttonHeight);

		surrenderButton.setLocation((size.width / 2) - buttonWidth, size.height - buttonHeight);
		endButton.setLocation(size.width / 2, size.height - buttonHeight);
	}


	/*
	 * The display needs to take care of general clickage.
	 * The following portion of code:
	 * 1.1) waits for clicks from the Swing environment.
	 * 1.2) evaluates the click that has happened:
	 *      a) If it is a click on a triangle, eligibility for flowerage must be ensured.
	 *      b) If it is a click on an edge, eligibility for ditcherage must be ensured.
	 */


	/**
	 * Erwarte einen Move, der von der GUI (d.h. dem menschlichen User) geholt wird.
	 *
	 * @return
	 * Ein {@link Move}, der von vom menschlichen User erfragt wird.
	 */
	public Move awaitMove() throws InterruptedException {
		displayMouseHandler.reset();
		displayMouseHandler.isRequesting = true;

		// If the game has ended already, no moves should be drawn
		// even if there might still be moves available.
		if ((boardViewer.getStatus() != Status.Ok) && (!gameEnd)) {
			possibleDitchMoves = null;
			combinableFlowers = null;
		} else {
			possibleDitchMoves = boardViewer.getPossibleDitchMoves();
			combinableFlowers = boardViewer.getPossibleFlowers();
		}

		// This sets the endButton enabled if and only if there is an "End" move available.
		surrenderButton.setEnabled(true);
		endButton.setEnabled(boardViewer.possibleMovesContains(new Move(MoveType.End)));

		Move result = null;

		// Actually await the move itself.
		while (result == null) {
			synchronized (displayMouseHandler.moveAwaitLock) {
				getParent().repaint();
				displayMouseHandler.moveAwaitLock.wait();

				if (displayMouseHandler.moveType == MoveType.Surrender) {
					result = new Move(MoveType.Surrender);
				} else if (displayMouseHandler.moveType == MoveType.End) {
					result = new Move(MoveType.End);
					if (!boardViewer.possibleMovesContains(result)) {
						result = null;
						displayMouseHandler.reset();
						displayMouseHandler.isRequesting = true;
					}
				} else if (displayMouseHandler.clickedFlower1 != null) {
					result = checkForFlowerMove();
					possibleDitchMoves = null;
				} else {
					result = checkForDitchMove();
				}
			}
		}

		displayMouseHandler.reset();
		// This sets the endButton enabled if and only if there is an "End" move available.
		surrenderButton.setEnabled(false);
		endButton.setEnabled(false);
		combinableFlowers = null;
		getParent().repaint();
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
		Flower flower1 = displayMouseHandler.clickedFlower1;
		if (!boardViewer.possibleMovesContainsMovesContaining(flower1)) {
			displayMouseHandler.reset();
			displayMouseHandler.isRequesting = true;
		} else if (displayMouseHandler.clickedFlower2 == null) {
			combinableFlowers = boardViewer.getFlowersCombinableWith(flower1);
		} else {
			Flower flower2 = displayMouseHandler.clickedFlower2;
			if (!combinableFlowers.contains(flower2)) {
				displayMouseHandler.clickedFlower2 = null;
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
		Ditch ditch = displayMouseHandler.clickedDitch;
		if (ditch == null) {
			displayMouseHandler.reset();
			displayMouseHandler.isRequesting = true;
		} else {
			if (((redDitches != null) && redDitches.contains(ditch) ||
				blueDitches != null && blueDitches.contains(ditch)))
			{
				displayMouseHandler.reset();
				displayMouseHandler.isRequesting = true;
			}

			Move result = new Move(ditch);
			if ((possibleDitchMoves != null) && possibleDitchMoves.contains(result))
				return result;
		}

		return null;
	}

	/**
	 * Updatet das Display und schedulet ein Repaint.
	 */
	public synchronized void refresh() {
		int redPlayerPoints = boardViewer.getPoints(PlayerColor.Red);
		int bluePlayerPoints = boardViewer.getPoints(PlayerColor.Blue);
		statusDisplay.updateStatus(redPlayerPoints, bluePlayerPoints);

		if ((boardViewer.getStatus() != Status.Ok) && (!gameEnd)) {
			possibleDitchMoves = null;

			// NOTE: This is necessary to be invoked by EventQueue.
			// Due to Swing's Threading structure, the program stalls otherwise.
			EventQueue.invokeLater(() -> new EndPopupFrame(boardViewer.getStatus()));
			gameEnd = true;
		}

		getParent().repaint();
	}
}
