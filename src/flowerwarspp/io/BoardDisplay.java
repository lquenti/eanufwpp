package flowerwarspp.io;

import flowerwarspp.preset.Move;
import flowerwarspp.preset.PlayerColor;
import flowerwarspp.preset.Status;
import flowerwarspp.preset.Viewer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

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

		// Sets the stroke width if g is a Graphics2D object.
		// I reckon this is basically always the case, but it shouldn't
		// fall apart if it's not.
		if (g instanceof Graphics2D) {
			Graphics2D graphics2D = ((Graphics2D) g);
			graphics2D.setStroke(BoardDisplay.stroke);
		}

		if (this.boardViewer != null) {
			this.mapTriangles.forEach(g::drawPolygon);
		}
	}

	/**
	 * Setzt die Größe dieses Elements auf 60% der Größe des Elternelements.
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

		Dimension size = this.getSize();
		int minimumSize = (size.width < size.height) ?
		        size.width : size.height;

		/*
		 * If one side is 0px wide, skip to the end.
		 * Nothing will be visible anyway.
		 */
		if ((minimumSize == 0) || (this.boardViewer == null))
			return;

		boolean flipped = false;
		int triangleHeight = minimumSize / (this.boardViewer.getSize() + 1);
		Triangle topTriangle = new Triangle(size.width / 2,
		        10,
		        triangleHeight,
		        flipped);
		this.mapTriangles.add(topTriangle);

		Triangle currentCentralTriangle = topTriangle;
		int maxTriangleCount = this.boardViewer.getSize();

		// TODO: Make the maths work out here.
		for (int triangles = 1; triangles < maxTriangleCount; triangles++) {
			flipped = !flipped;
			Point newTopPosition = currentCentralTriangle.getTopEdge();
			if (flipped)
				newTopPosition.y += 2 * currentCentralTriangle.getHeight();

			currentCentralTriangle = new Triangle(newTopPosition.x,
			        newTopPosition.y,
			        currentCentralTriangle.getSize(),
			        flipped);

			this.mapTriangles.add(currentCentralTriangle);

			propagateRow(currentCentralTriangle, triangles, flipped);
		}
	}

	/**
	 * Fülle eine Reihe mit Dreiecken. Es wird die Annahme getroffen, dass das erste Dreieck auf dem
	 * Kopf steht (das erste Dreieck unter dem obersten Dreieck).
	 *
	 * @param centralTriangle
	 * 		Referenz auf das Dreieck das in der Mitte liegt. Da jede Reihe eine ungerade Anzahl an
	 * 		Dreiecken hat, liegt dieses Dreieck immer genau in der Mitte.
	 * @param count
	 * 		Die Anzahl an Dreiecken in jede Richtung. Die Gesamtzahl der Dreiecken in der Reihe
	 * 		entspricht dann (2*count)+1.
	 * @param flipped
	 * 		Ob das erste Dreieck schon auf dem Kopf steht.
	 */
	private void propagateRow(Triangle centralTriangle, int count, boolean flipped) {
		// Propagate the row to the left
		putTriangles(centralTriangle, count, true, flipped);
		putTriangles(centralTriangle, count, false, flipped);
	}

	/**
	 * Erstellt Dreiecke und fügt sie dem Display hinzu.
	 *
	 * @param centralTriangle
	 * 		Eine Referenz auf das {@link Triangle}, das in der Mitte liegt.
	 * 		Die anderen {@link Triangle}s werden um dieses Dreieck herum gelegt.
	 *
	 * @param count
	 * 		Die Anzahl an Dreiecken, die auf jede Seite des zentralen Dreiecks gelegt
	 *		sollen.
	 *
	 * @param left
	 * 		Ob die Dreiecke auf die linke Seite gelegt werden sollen.
	 *
	 * @param flipped
	 * 		Ob das zentrale Dreieck auf dem Kopf steht.
	 */
	private void putTriangles(Triangle centralTriangle, int count, boolean left, boolean flipped)
	{
		boolean flipCurrent = !flipped;

		Triangle lastTriangle = centralTriangle;
		for (int x = 0; x < count; x++)
		{
			Point topEdge = null;
			if (left)
				topEdge = lastTriangle.getLeftEdge();
			else
				topEdge = lastTriangle.getRightEdge();

			lastTriangle = new Triangle(topEdge.x,
			        topEdge.y,
			        centralTriangle.getSize(),
					flipCurrent);

			this.mapTriangles.add(lastTriangle);
			flipCurrent = !flipCurrent;
		}
	}
}
