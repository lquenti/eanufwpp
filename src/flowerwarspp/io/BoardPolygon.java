package flowerwarspp.io;

import flowerwarspp.preset.Position;

import java.awt.*;

public abstract class BoardPolygon extends Polygon {
	/**
	 * Die Quadratwurzel von 3.0. Wird zum Berechnen der Koordinaten der beiden anderen Vertices des
	 * Dreiecks verwendet.
	 */
	private static final double triangleSideHeightFactor = Math.sqrt(3.0) / 2;

	private Color borderColour;
	private Color fillColour;

	/**
	 * Konstruiert ein {@link Polygon}, das für die Darstellung des Spielbretts geeignet ist.
	 *
	 * @param borderColour
	 * Die Farbe des Rands des {@link Polygon}s.
	 *
	 * @param fillColour
	 * Die Füllfarbe des {@link Polygon}s.
	 */
	public BoardPolygon(Color borderColour, Color fillColour) {
		this.borderColour = borderColour;
		this.fillColour = fillColour;
	}

	/**
	 * Zeichnet das {@link Polygon}, indem es dieses Objekt mit der
	 * {@link BoardPolygon#fillColour} füllt und es mit der {@link BoardPolygon#borderColour}
	 * umrandet.
	 *
	 * @param graphics
	 * Das {@link Graphics}-Objekt, auf das dieses {@link Polygon} gezeichnet werden soll.
	 */
	public void drawPolygon(Graphics graphics) {
		if (this.fillColour != null) {
			graphics.setColor(this.fillColour);
			graphics.fillPolygon(this);
		}
		if (this.borderColour != null) {
			graphics.setColor(this.borderColour);
			graphics.drawPolygon(this);
		}
	}

	/**
	 * Setter für die Farbe, mit der der Umriss dieses {@link Polygon}s gezeichnet werden soll.
	 *
	 * @param borderColour
	 * Die Farbe, mit der der Umriss dieses {@link Polygon}s gezeichnet werden soll.
	 */
	public void setBorderColour(Color borderColour) {
		this.borderColour = borderColour;
	}

	/**
	 * Getter für die Farbe, mit der der Umriss dieses {@link Polygon}s gezeichnet werden soll.
	 *
	 * @return
	 * Die Farbe, mit der der Umriss dieses {@link Polygon} gezeichnet werden soll.
	 */
	public Color getBorderColour() {
		return this.borderColour;
	}

	/**
	 * Setter für die Farbe, mit der dieses {@link Polygon} beim Zeichnen gefüllt werden soll.
	 *
	 * @param fillColour
	 * Die Farbe, mit dieses {@link Polygon} gefüllt werden soll.
	 */
	public void setFillColour(Color fillColour) {
		this.fillColour = fillColour;
	}

	/**
	 * Getter für die Farbe, mit der dieses {@link Polygon} beim Zeichnen gefüllt werden soll.
	 *
	 * @return
	 * Die Farbe, mit dieses {@link Polygon} gefüllt werden soll.
	 */
	public Color getFillColour() {
		return fillColour;
	}

	/**
	 * Berechnet die {@link Point}s dieses Polygons relativ zum Koordinatensystem
	 * des Spielbretts.
	 *
	 * @param triangleSideLength
	 * Die Länge der Seiten der Dreiecke auf dem aktuellen Spielbrett.
	 *
	 * @param relativeStart
	 * Der Ort, ab dem das Zeichnen stattfinden soll.
	 */
	public abstract void recalcPoints(int triangleSideLength, Point relativeStart);

	/**
	 * Rechnet eine {@link Position} des Spielbretts in einen {@link Point} auf dem Zeichenbrett um.
	 * Dabei wird die Skalierung des Spielbretts auf dem Zeichenbrett sowie ein Referenzpunkt
	 * beachtet.
	 *
	 * @param position
	 * Die {@link Position} auf dem Spielbrett.
	 *
	 * @param triangleSideLength
	 * Die aktuelle Länge der Seiten der {@link Triangle}s.
	 *
	 * @param referencePoint
	 * Der {@link Point} von wo aus das Element ausgelegt werden soll.
	 *
	 * @return
	 * Der berechnete Punkt, der an der Stelle liegt, an der der Punkt liegen soll.
	 */
	public Point positionToPoint(Position position, int triangleSideLength, Point referencePoint) {
		int column = position.getColumn();
		int row = position.getRow();
		Point point = new Point(referencePoint);
		point.x += (int) (((row - 1) / 2.0) * triangleSideLength) + column * triangleSideLength;
		point.y -= ((row - 1) * (triangleSideLength * triangleSideHeightFactor));
		return point;
	}
}
