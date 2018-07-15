package flowerwarspp.ui.component;

import flowerwarspp.preset.*;
import flowerwarspp.ui.GameColors;
import flowerwarspp.ui.geometry.Dot;
import flowerwarspp.ui.geometry.Edge;
import flowerwarspp.ui.geometry.Triangle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Ein {@link JComponent}, welches Nutzereingaben handhabt und eine graphische Ausgabe für ein
 * {@link Board} durch einen {@link Viewer} zur Verfügung stellt.
 */
public class BoardDisplay extends JPanel {
	/**
	 * Der {@link Viewer}, durch den dieses Display auf das {@link Board} schauen soll.
	 */
	private Viewer boardViewer;

	/*
	 * Die folgenden Objekte handhaben Objekte, die mit der Zeichengeometrie zu tun haben.
	 */
	/**
	 * Eine {@link Collection} von {@link Triangle}s, die die {@link Flower}s des betrachteten
	 * {@link Board}s repräsentieren.
	 */
	private Collection<Triangle> mapTriangles = new ArrayList<>();
	/**
	 * Eine {@link Collection} von {@link Edge}s, die die {@link Ditch}es des betrachteten {@link
	 * Board}s repräsentieren.
	 */
	private Collection<Edge> mapEdges = new ArrayList<>();
	/**
	 * Eine {@link Collection} von {@link Dot}s, verwendet für kosmetische Zwecke.
	 */
	private Collection<Dot> mapDots = new ArrayList<>();
	/**
	 * Ein Vergrößerungsfaktor für die Zeichengeometrie.
	 */
	private double zoom = 1.0;
	/**
	 * Handhabt alle {@link MouseEvent}s, die in diesem {@link JPanel} auftreten können.
	 */
	private DisplayMouseHandler displayMouseHandler = new DisplayMouseHandler(this);
	/**
	 * Ein {@link PlayerStatusDisplay}, das den Status des {@link PlayerColor#Red} anzeigt.
	 */
	private PlayerStatusDisplay redStatusDisplay = new PlayerStatusDisplay(GameColors.RED);

	/*
	 * Die folgenden Objekte stellen neben dem Spielbrett selbst andere UI-Elemente zur Verfügung.
	 * Die Toolbar liegt nicht auf diesem Objekt.
	 */
	/**
	 * Ein {@link PlayerStatusDisplay}, das den Status des {@link PlayerColor#Blue} anzeigt.
	 */
	private PlayerStatusDisplay blueStatusDisplay = new PlayerStatusDisplay(GameColors.BLUE);
	/**
	 * Eine Referenz auf die Toolbar am unteren Rand des Bildschirms.
	 */
	private BottomToolbarPanel bottomToolbarPanel;
	/**
	 * Die Größe des {@link Board}s.
	 */
	private int boardSize;


	/*
	 * Diese Objekte halten Informationen über den Spielstand.
	 * Außer boardSize ändern sich alle diese Objekte regelmäßig
	 * (i.d.R. nach jedem Zug).
	 */
	/**
	 * Die {@link Flower}s, die mit einer bestimmten Blume kombinierbar sind.
	 */
	private Collection<Flower> combinableFlowers;
	/**
	 * Eine {@link Collection} möglicher {@link Move}s, die {@link Ditch}es enthalten.
	 */
	private Collection<Move> possibleDitchMoves;
	/**
	 * <code>true</code> genau dann, wenn das Spiel geendet hat.
	 */
	private boolean gameHasEnded = false;
	/**
	 * Konstruiert ein Display für die Darstellung eines {@link Board}s.
	 */
	public BoardDisplay(BottomToolbarPanel bottomToolbarPanel) {
		this.bottomToolbarPanel = bottomToolbarPanel;

		// deriveFont leitet aus dem aktuellen Font einen Font mit anderen Eigenschaften ab.
		// Hier wird die Größe des Fonts geändert.
		Font font = getFont().deriveFont(10F);
		setFont(font);

		// Laut Javadocs muss ein Component setOpaque mit false aufrufen,
		// wenn es nicht die ganze Fläche bezeichnet (d.h. teilweise durchsichtig ist)
		setOpaque(false);

		bottomToolbarPanel.getSurrenderButton().addActionListener(displayMouseHandler);
		bottomToolbarPanel.getEndButton().addActionListener(displayMouseHandler);
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
		gameHasEnded = (boardViewer.getStatus() != Status.Ok);

		redStatusDisplay.updateStatus(boardViewer.getPoints(PlayerColor.Red));
		blueStatusDisplay.updateStatus(boardViewer.getPoints(PlayerColor.Blue));
		bottomToolbarPanel.setSurrenderEnabled(false);
		bottomToolbarPanel.setEndEnabled(false);

		// NOTE: Es ist sehr wichtig, dass die Dreiecke zuerst erstellt werden.
		createTriangles();
		createDitches();
		createDots();

		displayMouseHandler.reset();
		addMouseListener(displayMouseHandler);
		addMouseMotionListener(displayMouseHandler);
		refresh();
	}

	/**
	 * Erstellt {@link Triangle}-Objekte, die die Dreiecke auf dem Spiel repräsentieren.
	 */
	private void createTriangles() {
		// Wenn der Viewer gesetzt wird ist es möglich, dass das Spielbrett eine andere Größe hat.
		mapTriangles.clear();

		// Für jede Blume vom Spielbrett soll ein Triangle erstellt werden.
		Collection<Flower> flowers = boardViewer.getAllFlowers();
		flowers.forEach(f -> mapTriangles.add(new Triangle(f, GameColors.TRIANGLE_DEFAULT)));
	}

	/**
	 * Konstruiert die {@link Edge}s, die {@link Ditch}es repräsentieren werden.
	 */
	private void createDitches() {
		mapEdges.clear();

		// Für jedes Triangle, das nicht auf dem Kopf steht, sollen drei Edges erstellt werden.
		for (Triangle t : mapTriangles) {
			if (! t.isFlipped()) {
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


	/*
	 * Nach dem Setzen des Viewers benötigt das Spielbrettdisplay Initialisierung.
	 * Die folgende Codesektion füllt das Display mit Triangles,
	 * die Flowers repräsentieren. Außerdem werden Edges erstellt,
	 * die Ditches repräsentieren.
	 */

	/**
	 * Konstruiert die {@link Dot}s. Sie dienen als Orientierung für den Spieler und Zuschauer.
	 */
	private void createDots() {
		mapDots.clear();

		// Für jedes Triangle, das nicht auf dem Kopf steht, sollen drei Dots erstellt werden.
		for (Triangle t : mapTriangles) {
			if (! t.isFlipped()) {
				Flower f = t.toFlower();
				Dot leftDot = new Dot(f.getFirst(), GameColors.DOT_DEFAULT);
				Dot topDot = new Dot(f.getSecond(), GameColors.DOT_DEFAULT);
				Dot rightDot = new Dot(f.getThird(), GameColors.DOT_DEFAULT);

				mapDots.add(leftDot);
				mapDots.add(topDot);
				mapDots.add(rightDot);
			}
		}
	}

	/**
	 * Updatet das Display und schedulet ein Repaint.
	 */
	public void refresh() {
		redStatusDisplay.updateStatus(boardViewer.getPoints(PlayerColor.Red));
		blueStatusDisplay.updateStatus(boardViewer.getPoints(PlayerColor.Blue));
		bottomToolbarPanel.setTurnDisplay(boardViewer.getTurn());

		// Wenn das Spiel zuende ist, soll ein Dialog das anzeigen.
		if ((boardViewer.getStatus() != Status.Ok) && (! gameHasEnded)) {
			possibleDitchMoves = null;
			gameHasEnded = true;
		}
	}

	/**
	 * Setzt den Skalierungsfaktor.
	 *
	 * @param zoom
	 * 		Der Skalierungsfaktor, der beim Zeichnen verwenden werden soll.
	 */
	public void setZoom(double zoom) {
		this.zoom = zoom;
		repaint();
	}


	/*
	 * Beim Zeichnen müssen drei wichtige Aktionen passieren:
	 * a) Fordere die BoardPolygons auf, sich zu updaten.
	 *    Sie können die Farbe wechseln oder sich entfärben.
	 * b) Fordere die BoardPolygons auf, ihre Größe zu ändern.
	 *    Sie können größer oder kleiner werden, oder ihre Größe beibehalten.
	 * c) Fordere die Swing-Umgebung auf, alle JComponents neu zu zeichnen.
	 *    Insbesondere wird hierbei der Bildschirm gecleart.
	 * d) Beginne mit dem Zeichnen auf das Panel (d.h. in den Buffer)
	 *
	 * Diese Aktionen passieren im folgenden Codeabteil.
	 */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paintComponent(Graphics g) {
		updateTriangles();
		updateEdges();
		updatePolygonSizes();
		super.paintComponent(g);

		// Antialiasing macht Kanten unpixelig.
		if (g instanceof Graphics2D) {
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		}

		// Die Triangles werden unter allen anderen Polygons gezeichnet.
		mapTriangles.forEach(t -> t.drawPolygon(g));
		mapEdges.forEach(e -> e.drawPolygon(g));
		mapDots.forEach(d -> d.drawPolygon(g));
	}

	// NOTE: Keine der folgenden Methoden sollte außer durch #paintComponent aufgeruft werden.

	/**
	 * Updatet die {@link Triangle}s nach einem Zug.
	 */
	private void updateTriangles() {
		Collection<Flower> redFlowers = boardViewer.getFlowers(PlayerColor.Red);
		Collection<Flower> blueFlowers = boardViewer.getFlowers(PlayerColor.Blue);

		for (Triangle t : mapTriangles) {
			Flower flower = t.toFlower();

			if (redFlowers != null && redFlowers.contains(flower)) {
				// Für den Fall, dass die aktuelle Blume dem roten Spieler gehört...
				if (boardViewer.isGarden(boardViewer.getFlowerBed(flower))) {
					t.setFillColor(GameColors.RED_IN_GARDEN);
				} else {
					t.setFillColor(GameColors.RED);
				}
			} else if (blueFlowers != null && blueFlowers.contains(flower)) {
				// oder dem blauen Spieler gehört, färbe das Dreieck entsprechend.
				if (boardViewer.isGarden(boardViewer.getFlowerBed(flower))) {
					t.setFillColor(GameColors.BLUE_IN_GARDEN);
				} else {
					t.setFillColor(GameColors.BLUE);
				}
			} else if (flower.equals(displayMouseHandler.clickedFlower1)) {
				// Andererseits, überprüfe, ob der Spieler gerade einen Flower-Move macht
				// und diese Blume als erstes ausgewählt hat.
				t.setFillColor(GameColors.TRIANGLE_CLICKED);
			} else if (flower.equals(displayMouseHandler.lastClickedFlower1) ||
					flower.equals(displayMouseHandler.lastClickedFlower2)) {
				if (boardViewer.getTurn() == PlayerColor.Red) {
					t.setFillColor(GameColors.RED);
				} else {
					t.setFillColor(GameColors.BLUE);
				}
			} else if (combinableFlowers != null && combinableFlowers.contains(flower)) {
				t.setFillColor(GameColors.TRIANGLE_COMBINABLE);
			} else {
				// Sonst behält das Triangle die Hintergrundfarbe dieses Displays.
				t.setFillColor(getBackground());
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

			// Färbe die Edge in der Farbe, die den Spieler repräsentiert, dem sie gehört.
			if (redDitches != null && redDitches.contains(ditch)) {
				e.setFillColor(GameColors.RED);
			} else if (blueDitches != null && blueDitches.contains(ditch)) {
				e.setFillColor(GameColors.BLUE);
			} else if ((boardViewer.getTurn() == PlayerColor.Red &&
					ditch.equals(displayMouseHandler.lastClickedDitch))) {
				e.setFillColor(GameColors.RED);
			} else if (boardViewer.getTurn() == PlayerColor.Blue &&
					ditch.equals(displayMouseHandler.lastClickedDitch)) {
				e.setFillColor(GameColors.BLUE);
			} else if (possibleDitchMoves != null && possibleDitchMoves.contains(move)) {
				// Wenn sie niemandem gehört, aber der Spieler gerade spielen soll,
				// färbe sie, um das darzustellen.
				e.setFillColor(GameColors.EDGE_CLICKABLE);
			} else {
				// Sonst, färbe sie in der Standardfarbe.
				e.setFillColor(GameColors.EDGE_DEFAULT);
			}
		}
	}

	/**
	 * Updatet die Größe der {@link Triangle}s. Wird verwendet, um die Dreiecke der aktuellen
	 * {@link Dimension} des Zeichenbretts anzupassen.
	 */
	private void updatePolygonSizes() {
		// Teile die Höhe dieses Dreiecks auf die verschiedenen Triangles auf.
		Dimension displaySize = getParent().getSize();
		displaySize.width = (int) (displaySize.width * zoom);
		displaySize.height = (int) (displaySize.height * zoom);
		setPreferredSize(displaySize);
		revalidate();
		int minimumSize = Math.min(displaySize.width, displaySize.height);

		// Triangles sollen etwas kleiner sein als maximal möglich,
		// um einen gesicherten Abstand zum JFrame-Rand sicherzustellen.
		int sideLength = minimumSize / (boardSize + 1);
		Point drawBegin = new Point();
		drawBegin.x = (displaySize.width / 2) - sideLength * (boardSize + 2) / 2;
		drawBegin.y = sideLength * boardSize;

		mapTriangles.forEach(t -> t.recalcPoints(sideLength, drawBegin));
		mapEdges.forEach(e -> e.recalcPoints(sideLength, drawBegin));
		mapDots.forEach(e -> e.recalcPoints(sideLength, drawBegin));
	}


	/*
	 * Das Display muss sich um Klicks auf sich selbst kümmern.
	 * Dabei:
	 * 1.1) wartet es auf einen Klick (gemeldet von der Swing-Umgebung)
	 * 1.2) evaluiert es den Klick und:
	 *      a) wenn es ein Klick auf ein Triangle ist, wird sichergestellt,
	 *         dass dadurch kein ungültiger Move entsteht.
	 *      b) wenn es ein Klick auf eine Edge ist, wird sichergestellt,
	 *         dass dadurch kein ungültiger Move entsteht.
	 */

	/**
	 * Erwarte einen Move, der von der GUI (d.h. dem menschlichen User) geholt wird.
	 *
	 * @return Ein {@link Move}, der von vom menschlichen User erfragt wird.
	 */
	public Move requestMove() throws InterruptedException {
		// Es ist notwendig, den MouseHandler zurückzusetzen,
		// um sicherzustellen, Klicks, die der Nutzer seit dem letzten request gemacht hat,
		// das Spielverhalten nicht beeinflussen.
		displayMouseHandler.reset();
		displayMouseHandler.isRequesting = true;

		// Wenn das Spiel bereits zuende ist,
		// sollen keine weiteren möglichen Moves mehr angezeigt werden,
		// selbst wenn es noch welche gäbe.
		if ((boardViewer.getStatus() != Status.Ok) && (! gameHasEnded)) {
			possibleDitchMoves = null;
			combinableFlowers = null;
		} else {
			possibleDitchMoves = boardViewer.getPossibleDitchMoves();
			combinableFlowers = boardViewer.getPossibleFlowers();
		}

		// Ändert den Status der Buttons in der Toolbar.
		// Insbesondere setzt es den End-Button auf enabled genau dann,
		// wenn es einen "End"-Move gibt.
		bottomToolbarPanel.setSurrenderEnabled(true);
		bottomToolbarPanel.setEndEnabled(boardViewer.possibleMovesContains(new Move(MoveType
				.End)));

		getParent().repaint();
		// Erwartet einen Move.
		// NOTE: Laut Javadoc ist es wichtig, Object#wait in einer Schleife aufzurufen.
		// Da #awaitMove Object#wait aufruft, muss hier synchronised werden
		Move result = null;
		synchronized (displayMouseHandler.moveAwaitLock) {
			while (result == null) {
				result = awaitMove();
			}
		}

		displayMouseHandler.reset();
		// Wenn der Spieler nicht am Zug ist, soll er auch keinen machen.
		bottomToolbarPanel.setSurrenderEnabled(false);
		bottomToolbarPanel.setEndEnabled(false);
		combinableFlowers = null;
		getParent().repaint();
		return result;
	}

	/**
	 * Erwartet einen Move vom {@link DisplayMouseHandler}.
	 *
	 * @return Einen {@link Move}, oder <code>null</code>, wenn der Spieler seinen Zug noch nicht
	 * beendet hat.
	 *
	 * @throws InterruptedException
	 * 		Wenn {@link Object#wait()} eine {@link InterruptedException} wirft.
	 */
	private Move awaitMove() throws InterruptedException {
		displayMouseHandler.moveAwaitLock.wait();

		Move result;
		switch (displayMouseHandler.moveType) {
			case Surrender:
				// Man darf immer aufgeben, also soll ein Surrender-Move zurückgegeben werden.
				result = new Move(MoveType.Surrender);
				break;
			case End:
				// Man darf das Spiel nicht immer beenden.
				// Es wird ein Move-Objekt benötigt, um prüfen zu können, ob das möglich ist.
				result = new Move(MoveType.End);
				if (! boardViewer.possibleMovesContains(result)) {
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

		return result;
	}

	/**
	 * Ausschließlich intern benutzt. Überprüft den aktuellen Klick auf einen {@link Ditch}-Move.
	 *
	 * @return Einen {@link Move} der den gewählten {@link Ditch} enthält, oder <code>null</code>,
	 * wenn kein gültiger {@link Move} gewählt ist.
	 */
	private Move checkForDitchMove() {
		Ditch ditch = displayMouseHandler.clickedDitch;

		// Wenn der durch den Ditch-Klick resultierende Move ungültig ist,
		// soll der Klick (effektiv) ignoriert werden.
		Move result = new Move(ditch);
		if (boardViewer.possibleMovesContains(result)) {
			displayMouseHandler.lastClickedDitch = ditch;
			return result;
		} else {
			displayMouseHandler.reset();
			displayMouseHandler.isRequesting = true;
		}

		return null;
	}

	/**
	 * Ausschließlich intern benutzt. Überprüft den aktuellen Klick auf einen {@link Flower}-Move.
	 *
	 * @return Einen {@link Move} der die gewählten {@link Flower}s enthält, oder
	 * <code>null</code>, wenn kein gültiger {@link Move} gewählt ist.
	 */
	private Move checkForFlowerMove() {
		Flower flower1 = displayMouseHandler.clickedFlower1;

		if (! boardViewer.possibleMovesContainsMovesContaining(flower1)) {
			// Wenn die Blume in keinem möglichen Zug gültig ist,
			// soll der Klick (effektiv) ignoriert werden.
			displayMouseHandler.reset();
			displayMouseHandler.isRequesting = true;
		} else if (displayMouseHandler.clickedFlower2 == null) {
			// Wenn die geklickte Flower erst die erste war,
			// sollen die möglichen Kombinationen gefärbt werden.
			// Außerdem darf dann kein Ditch-Move mehr gewählt werden.
			combinableFlowers = boardViewer.getFlowersCombinableWith(flower1);
			possibleDitchMoves = null;
		} else {
			// Wenn schon eine zweite Flower geklickt wurde, muss sichergestellt werden,
			// dass die entstehende Kombination gültig ist.
			// Sonst soll der Klick (effektiv) ignoriert werden.
			Flower flower2 = displayMouseHandler.clickedFlower2;
			if (flower1.equals(flower2)) {
				displayMouseHandler.reset();
				displayMouseHandler.isRequesting = true;
				combinableFlowers = boardViewer.getPossibleFlowers();
				possibleDitchMoves = boardViewer.getPossibleDitchMoves();
			} else if (! combinableFlowers.contains(flower2)) {
				displayMouseHandler.clickedFlower2 = null;
			} else {
				displayMouseHandler.lastClickedFlower1 = flower1;
				displayMouseHandler.lastClickedFlower2 = flower2;
				return new Move(flower1, flower2);
			}
		}

		return null;
	}

	/**
	 * Eine private Klasse, die die Mausaktionen für das {@link BoardDisplay} verarbeitet.
	 */
	private class DisplayMouseHandler extends MouseAdapter implements ActionListener {
		private final Object moveAwaitLock = new Object();
		/**
		 * Das {@link BoardDisplay}, zu dem dieser {@link MouseAdapter} gehört.
		 */
		private BoardDisplay boardDisplay;
		private boolean isRequesting = false;
		/**
		 * Der {@link MoveType}, der durch den Klick erzeugt wird. Wichtig ist, dass {@link
		 * MoveType#Flower} nicht impliziert, dass eine {@link Flower} geklickt wurde.
		 */
		private MoveType moveType = MoveType.Flower;
		/**
		 * Wenn {@link Flower}s gesetzt werden, müssen zwei Dreiecke geklickt werden. Wurden zwei
		 * geklickt, so sind sie hierin gespeichert.
		 */
		private Flower clickedFlower1 = null, clickedFlower2 = null;
		/**
		 * Wenn {@link Flower}s gesetzt wurden, werdern sie hier zwischengespeichert, damit es
		 * nicht zu Flackern kommt.
		 */
		private Flower lastClickedFlower1 = null, lastClickedFlower2 = null;
		/**
		 * Wenn ein {@link Ditch} gesetzt werden soll, muss dieser angeklickt werden. Wurde einer
		 * geklickt, so ist er hierin gespeichert.
		 */
		private Ditch clickedDitch = null;
		/**
		 * Wenn ein {@link Ditch} gesetzt wurde, wird er hier zwischengespeichert, damit es
		 * nicht zu Flackern kommt.
		 */
		private Ditch lastClickedDitch = null;

		/**
		 * Konstruiert einen {@link DisplayMouseHandler}, der an ein {@link BoardDisplay} gebunden
		 * ist.
		 *
		 * @param boardDisplay
		 * 		Das {@link BoardDisplay}, an welches dieses Objekt gebunden ist.
		 */
		public DisplayMouseHandler(BoardDisplay boardDisplay) {
			this.boardDisplay = boardDisplay;
		}

		/**
		 * Handelt das Event {@link MouseAdapter#mouseMoved(MouseEvent)}.
		 * <p>
		 * Es wird durch alle gezeichneten {@link Dot}s iteriert. Dann wird überprüft, ob der
		 * Mauszeiger, dessen Position als {@link Point} vom {@link MouseEvent} durchgereicht wird,
		 * in diesem überprüften Dot liegt. Falls dem so ist, wird der ToolTip mit der {@link
		 * String}-Repräsentation der {@link Position} des Dots gesetzt. Liegt der Mauszeiger nicht
		 * im Dot, wird der ToolTip auf <code>null</code> gesetzt.
		 *
		 * @param mouseEvent
		 * 		Das durchgereichte {@link MouseEvent}.
		 */
		@Override
		public void mouseMoved(MouseEvent mouseEvent) {

			for (Dot dot : mapDots) {

				if (dot.contains(mouseEvent.getPoint())) {
					setToolTipText(dot.getPosition().toString());
					return;
				} else {
					setToolTipText(null);
				}
			}

			ToolTipManager.sharedInstance().mouseMoved(mouseEvent);
		}

		/**
		 * {@inheritDoc}
		 */
		public void mouseClicked(MouseEvent mouseEvent) {
			if (! isRequesting) {
				return;
			}

			// processClick verarbeitet den Klick selbst,
			// Sonst soll alles neu gezeichnet und der Wartende benachrichtigt werden,
			// dass er die Zustandsänderung verarbeiten kann.
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
		 * 		Das {@link MouseEvent}, das die Ausführung verursacht hat.
		 */
		private void processClick(MouseEvent mouseEvent) {
			// Versuche einen der Punkte zu finden, die auf dem Brett liegen.
			// Solche Klicks sollen ignoriert werden.
			Point clickPoint = mouseEvent.getPoint();
			Dot dot = findDot(clickPoint);
			if (dot != null) {
				moveType = MoveType.Flower;
				return;
			}

			// Versuche eine Edge zu finden. Edges sollen den Triangles bevorzugt werden,
			// um Ditches anklicken zu können.
			Edge edge = findEdge(clickPoint);
			if (edge != null) {
				onEdgeClick(edge);
				return;
			}

			// Versuche ein Triangle zu finden. Dreiecke sind am größten
			// und werden deswegen zuletzt in Betracht gezogen.
			Triangle triangle = findTriangle(clickPoint);
			if (triangle != null) {
				onTriangleClick(triangle);
			}
		}

		/**
		 * Findet einen {@link Dot} am spezifizierten {@link Point}.
		 *
		 * @param point
		 * 		Der {@link Point}, an dem der {@link Dot} liegt.
		 *
		 * @return Der {@link Dot}, der derzeit an der angegebenen Stelle liegt, oder
		 * <code>null</code>, falls dort keiner liegt.
		 */
		private Dot findDot(Point point) {
			for (Dot d : boardDisplay.mapDots) {
				if (d.contains(point)) {
					return d;
				}
			}

			return null;
		}

		/**
		 * Findet eine {@link Edge} am spezifizierten {@link Point}.
		 *
		 * @param point
		 * 		Der {@link Point}, an dem die Kante liegt.
		 *
		 * @return Die {@link Edge}, das derzeit an der angegebenen Stelle liegt, oder
		 * <code>null</code>, falls dort keine liegt.
		 */
		private Edge findEdge(Point point) {
			for (Edge e : boardDisplay.mapEdges) {
				if (e.contains(point)) {
					return e;
				}
			}

			return null;
		}

		/**
		 * Verarbietet den {@link Edge}-Klick.
		 *
		 * @param edge
		 * 		Die geklickte {@link Edge}. Darf nicht <code>null</code> sein.
		 */
		private void onEdgeClick(Edge edge) {
			if (clickedFlower1 != null) {
				return;
			}

			moveType = MoveType.Ditch;
			clickedDitch = edge.toDitch();
		}

		/**
		 * Findet ein {@link Triangle} am spezifizierten {@link Point}.
		 *
		 * @param point
		 * 		Der {@link Point}, an dem das Dreieck liegt.
		 *
		 * @return Das {@link Triangle}, das derzeit an der angegebenen Stelle liegt, oder
		 * <code>null</code>, falls dort keines liegt.
		 */
		private Triangle findTriangle(Point point) {
			for (Triangle t : boardDisplay.mapTriangles) {
				if (t.contains(point)) {
					return t;
				}
			}

			return null;
		}

		/**
		 * Verarbeitet den {@link Triangle}-Klick.
		 *
		 * @param triangle
		 * 		Das geklickte {@link Triangle}. Darf nicht <code>null</code> sein.
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
		 * {@inheritDoc}
		 */
		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			if (! isRequesting) {
				return;
			}

			// In den beiden folgenden Fällen wird == verwendet.
			// Es soll sichergestellt werden, dass sie dieselben Objekte sind.
			if (actionEvent.getSource() == boardDisplay.bottomToolbarPanel.getSurrenderButton()) {
				moveType = MoveType.Surrender;
			} else if (actionEvent.getSource() == boardDisplay.bottomToolbarPanel.getEndButton()) {
				moveType = MoveType.End;
			}

			boardDisplay.getParent().repaint();
			synchronized (moveAwaitLock) {
				moveAwaitLock.notify();
			}
		}

		/**
		 * Interne Methode, dieses Objekt zurücksetzt. Zurücksetzen bedeutet, dass kein Dreieck
		 * mehr gewählt ist, und kein {@link Move} mehr gehalten wird.
		 */
		private void reset() {
			// Setzt den MouseHandler auf den Ausgangszustand zurück.
			// Insbesondere ist keine Blume gewählt
			// und es wurde kein Move angefragt.
			clickedFlower1 = null;
			clickedFlower2 = null;
			clickedDitch = null;
			isRequesting = false;
		}
	}
}
