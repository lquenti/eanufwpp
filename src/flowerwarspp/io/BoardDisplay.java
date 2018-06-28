package flowerwarspp.io;

import flowerwarspp.preset.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class BoardDisplay extends JPanel {
	/**
	 * Dieses {@link Stroke}-Objekt zeichnet die Dreiecke auf dem Spielbrett mit dicken Linien. Wird
	 * nur verwendet, wenn das {@link Graphics}-Objekt das in {@link #paintComponent(Graphics)}
	 * gegeben wird ein {@link Graphics2D}-Objekt ist.
	 */
	private static final Stroke stroke = new BasicStroke(
	        5.0F,
	        BasicStroke.CAP_ROUND,
	        BasicStroke.JOIN_ROUND);

	private static final Color triangleColour = Color.BLACK;
	private static final Color redColour = Color.RED;
	private static final Color blueColour = Color.CYAN;

	/**
	 * Legt fest, wie groß das Spielbrett in Relation zum Fenster sein soll.
	 * TODO: Dies sollte auch abhängig von der absoluten Größe des Frames sein.
	 */
	private static final int componentSizePercentage = 90;

	private Viewer boardViewer = null;
	private Collection<Triangle> mapTriangles = new ArrayList<>();

	/**
	 * Der {@link Viewer}, mit dem das Spielbrett betrachtet wird.
	 *
	 * @param boardViewer
	 * 		Der {@link Viewer}, der das zubetrachtende Spielbrett betrachtet.
	 */
	public void setBoardViewer(Viewer boardViewer) {
		this.setPreferredSize(new Dimension(500, 500));
		this.setSize(500, 500);
		this.boardViewer = boardViewer;
		this.resizeDisplay();
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

		g.setColor(BoardDisplay.redColour);
		for (Flower f : this.boardViewer.getFlowers(PlayerColor.Red))
		{
			this.mapTriangles.stream()
			    .filter(t -> f.getFirst().equals(t.getTopBoardPosition()))
			    .findFirst()
			    .ifPresent(g::fillPolygon);
		}
		g.setColor(BoardDisplay.blueColour);
		for (Flower f : this.boardViewer.getFlowers(PlayerColor.Red))
		{
			this.mapTriangles.stream()
					.filter(t -> f.getFirst().equals(t.getTopBoardPosition()))
					.findFirst()
					.ifPresent(g::fillPolygon);
		}
		System.out.println("\n\n\n");

		g.setColor(triangleColour);
		// Sets the stroke width if g is a Graphics2D object.
		// I reckon this is basically always the case, but it shouldn't
		// fall apart if it's not.
		if (g instanceof Graphics2D) {
			Graphics2D graphics2D = ((Graphics2D) g);
			graphics2D.setStroke(BoardDisplay.stroke);
		}

		this.mapTriangles.forEach(g::drawPolygon);
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
		int maximumRowCount = (this.boardViewer.getSize() - 1) * 2;
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
}
