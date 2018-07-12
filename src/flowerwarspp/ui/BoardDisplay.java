package flowerwarspp.ui;

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
		 * Wichtig ist, dass {@link MoveType#Flower} nicht impliziert,
		 * dass eine {@link Flower} geklickt wurde.
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
			if (mouseEvent.getComponent() == boardDisplay.bottomToolbarPanel.getSurrenderButton()) {
				moveType = MoveType.Surrender;
				return;
			}
			if (mouseEvent.getComponent() == boardDisplay.bottomToolbarPanel.getEndButton()) {
				moveType = MoveType.End;
				return;
			}

			Point clickPoint = mouseEvent.getPoint();
			Dot dot = findDot(clickPoint);
			if (dot != null) {
				moveType = MoveType.Flower;
				return;
			}

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

			moveType = MoveType.Ditch;
			clickedDitch = edge.toDitch();
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

				moveType = MoveType.Flower;
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

	/*
	 * These Color-Objects are basically constant.
	 * Nowhere in the code should there be any colour equivalent to those
	 * referred to on the right side of these declarations.
	 */

	/**
	 * Die Farbe, die ein {@link Triangle} standardmäßig hat.
	 */
	private static final Color triangleDefaultColour = Color.LIGHT_GRAY;
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
	 * Die Farbe die eine {@link Edge} normalerweise hat.
	 */
	private static final Color edgeDefaultColour = Color.BLACK;
	/**
	 * Die Farbe die eine {@link Edge} hat,
	 * wenn es einen gültigen {@link Move} gibt, der den repräsentierten {@link Ditch} enthält.
	 */
	private static final Color edgeClickableColour = Color.GREEN;
	/**
	 * Die Farbe der Dreiecke, die dem {@link PlayerColor#Red} gehören.
	 */
	private static final Color redColour = Color.RED;
	/**
	 * Eine Farbe, die eine Blume symbolisiert, die in einem roten Garten ist.
	 */
	private static final Color redInGardenColour = redColour.darker();
	/**
	 * Die Farbe der Dreiecke, die dem {@link PlayerColor#Blue} gehören.
	 */
	private static final Color blueColour = Color.CYAN;
	/**
	 * Eine Farbe, die eine Blume symbolisiert, die in einem roten Garten ist.
	 */
	private static final Color blueInGardenColour = blueColour.darker();

	/*
	 * These objects mainly handle geometry and the like.
	 */

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
	 * Ein {@link PlayerStatusDisplay}, das den Status des {@link PlayerColor#Red} anzeigt.
	 */
	private PlayerStatusDisplay redStatusDisplay = new PlayerStatusDisplay(redColour, true);
	/**
	 * Ein {@link PlayerStatusDisplay}, das den Status des {@link PlayerColor#Blue} anzeigt.
	 */
	private PlayerStatusDisplay blueStatusDisplay = new PlayerStatusDisplay(blueColour, false);
	/**
	 * Eine Referenz auf die Toolbar am unteren Rand des Bildschirms.
	 */
	private BottomToolbarPanel bottomToolbarPanel;

	/*
	 * These objects basically only hold information about the game.
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

	/**
	 * Konstruiert ein Display für die Darstellung eines {@link Board}s.
	 */
	public BoardDisplay(BottomToolbarPanel bottomToolbarPanel) {
		this.bottomToolbarPanel = bottomToolbarPanel;

		Font font = getFont().deriveFont(10F);
		setFont(font);
		setOpaque(false);
		setLayout(null);

		bottomToolbarPanel.setButtonClickListener(displayMouseHandler);
	}

	/**
	 * Der {@link Viewer}, mit dem das Spielbrett betrachtet wird.
	 *
	 * @param boardViewer
	 * 		Der {@link Viewer}, der das zubetrachtende Spielbrett betrachtet.
	 */
	public void setBoardViewer(Viewer boardViewer) {
		this.boardViewer = boardViewer;
		boardSize = boardViewer.getSize();

		redStatusDisplay.updateStatus(boardViewer.getPoints(PlayerColor.Red));
		redStatusDisplay.updateStatus(boardViewer.getPoints(PlayerColor.Blue));
		bottomToolbarPanel.setSurrenderEnabled(false);
		// This sets the endButton enabled if and only if there is an "End" move available.
		bottomToolbarPanel.setEndEnabled(boardViewer.possibleMovesContains(new Move(MoveType.End)));

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
		Collection<Flower> flowers = boardViewer.getAllFlowers();
		flowers.forEach(f -> mapTriangles.add(new Triangle(f, triangleDefaultColour)));
	}

	/**
	 * Konstruiert die {@link Edge}s, die {@link Ditch}es repräsentieren werden.
	 */
	private void createDitches() {
		// for each non-flipped triangle, create the three ditches around it.
		for (Triangle t : mapTriangles) {
			if (!t.isFlipped()) {
				Flower f = t.toFlower();
				Edge leftDitch = new Edge(f.getFirst(), f.getSecond());
				Edge rightDitch = new Edge(f.getFirst(), f.getThird());
				Edge bottomDitch = new Edge(f.getSecond(), f.getThird());

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
				Flower f = t.toFlower();
				Dot leftDot = new Dot(f.getFirst());
				Dot topDot = new Dot(f.getSecond());
				Dot rightDot = new Dot(f.getThird());

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
	public void paintComponent(Graphics g) {
		updatePolygons();
		updatePolygonSizes();
		super.paintComponent(g);

		// Antialiasing makes things look good.
		if (g instanceof Graphics2D) {
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			                                  RenderingHints.VALUE_ANTIALIAS_ON);
		}

		mapTriangles.forEach(t -> t.drawPolygon(g));
		mapEdges.forEach(e -> e.drawPolygon(g));
		mapDots.forEach(d -> d.drawPolygon(g));
		redStatusDisplay.draw(g);
		blueStatusDisplay.draw(g);
	}

	// NOTE: NONE of the following methods may be called by anything but #paintComponent

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
		Collection<Flower> redFlowers = boardViewer.getFlowers(PlayerColor.Red);
		Collection<Flower> blueFlowers = boardViewer.getFlowers(PlayerColor.Blue);

		for (Triangle t : mapTriangles) {
			t.setFillColour(getBackground());
			Flower flower = t.toFlower();
			if ((redFlowers != null) && redFlowers.contains(flower)) {
				if (boardViewer.getFlowerBed(flower).size() > 3)
					t.setFillColour(redInGardenColour);
				else
					t.setFillColour(redColour);
			}
			else if ((blueFlowers != null) && blueFlowers.contains(flower)) {
				if (boardViewer.getFlowerBed(flower).size() > 3)
					t.setFillColour(blueInGardenColour);
				else
					t.setFillColour(blueColour);
			} else {
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
		Collection<Ditch> redDitches = boardViewer.getDitches(PlayerColor.Red);
		Collection<Ditch> blueDitches = boardViewer.getDitches(PlayerColor.Blue);

		for (Edge e : mapEdges) {
			Ditch ditch = e.toDitch();
			Move move = new Move(ditch);

			if ((redDitches != null) && (redDitches.contains(ditch))) {
				e.setFillColour(redColour);
			} else if ((blueDitches != null) && (blueDitches.contains(ditch))) {
				e.setFillColour(blueColour);
			} else if ((possibleDitchMoves != null) && possibleDitchMoves.contains(move)) {
				e.setFillColour(edgeClickableColour);
			} else {
				e.setFillColour(edgeDefaultColour);
			}
		}
	}

	/**
	 * Updatet die Größe der {@link Triangle}s. Wird verwendet, um die Dreiecke der aktuellen
	 * {@link Dimension} des Zeichenbretts anzupassen.
	 */
	private void updatePolygonSizes() {
		Dimension displaySize = getSize();
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
		redStatusDisplay.updateRectangleSizes(displaySize);
		blueStatusDisplay.updateRectangleSizes(displaySize);
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
	public Move requestMove() throws InterruptedException {
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
		bottomToolbarPanel.setSurrenderEnabled(true);
		bottomToolbarPanel.setEndEnabled(boardViewer.possibleMovesContains(new Move(MoveType.End)));
		bottomToolbarPanel.setLabelText(boardViewer.getTurn() + " ist am Zug.");


		// NOTE: This is actually the most crucial method call.
		Move result = null;
		while (result == null) {
			result = awaitMove();
		}


		displayMouseHandler.reset();
		// This sets the endButton enabled if and only if there is an "End" move available.
		bottomToolbarPanel.setSurrenderEnabled(false);
		bottomToolbarPanel.setEndEnabled(false);
		combinableFlowers = null;
		getParent().repaint();
		return result;
	}

	private Move awaitMove() throws InterruptedException {
		Move result;

		synchronized (displayMouseHandler.moveAwaitLock) {
			getParent().repaint();
			displayMouseHandler.moveAwaitLock.wait();

			switch (displayMouseHandler.moveType) {
				case Surrender:
					result = new Move(MoveType.Surrender);
					break;
				case End:
					result = new Move(MoveType.End);
					if (!boardViewer.possibleMovesContains(result)) {
						result = null;
						displayMouseHandler.reset();
						displayMouseHandler.isRequesting = true;
					}
					break;
				case Ditch:
					result = checkForDitchMove();
					break;
				default:
					result = checkForFlowerMove();
					break;
			}
		}

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
			possibleDitchMoves = null;
		} else {
			Flower flower2 = displayMouseHandler.clickedFlower2;
			if (flower1.equals(flower2)) {
				displayMouseHandler.reset();
				displayMouseHandler.isRequesting = true;
				combinableFlowers = boardViewer.getPossibleFlowers();
				possibleDitchMoves = boardViewer.getPossibleDitchMoves();
			} else if (!combinableFlowers.contains(flower2)) {
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
			Move result = new Move(ditch);
			if (boardViewer.possibleMovesContains(result))
				return result;
			else {
				displayMouseHandler.reset();
				displayMouseHandler.isRequesting = true;
			}
		}

		return null;
	}

	/**
	 * Updatet das Display und schedulet ein Repaint.
	 */
	public void refresh() {
		redStatusDisplay.updateStatus(boardViewer.getPoints(PlayerColor.Red));
		blueStatusDisplay.updateStatus(boardViewer.getPoints(PlayerColor.Blue));

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
