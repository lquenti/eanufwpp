package flowerwarspp.ui;

import flowerwarspp.preset.Position;

import java.awt.*;

/**
 * Handhabt die Umrechnung von {@link flowerwarspp.preset.Board}-Koordinaten nach
 * Swing-Koordinaten für die Darstellung von {@link flowerwarspp.preset.Flower}s
 * oder {@link flowerwarspp.preset.Ditch}es.
 */
public abstract class BoardPolygon extends Polygon {
	/**
	 * Die Quadratwurzel von 3.0. Wird zum Berechnen der Koordinaten der beiden anderen Vertices des
	 * Dreiecks verwendet.
	 */
	private static final double triangleSideHeightFactor = Math.sqrt(3.0) / 2;

	/**
	 * Die Farbe, die die Seiten dieses Polygons haben.
	 */
	private Color borderColor;
	/**
	 * Die Farbe, mit der dieses Polygon gefüllt wird.
	 */
	private Color fillColor;

	/**
	 * Konstruiert ein {@link Polygon}, das für die Darstellung des Spielbretts geeignet ist.
	 *
	 * @param borderColor
	 * Die Farbe des Rands des {@link Polygon}s.
	 *
	 * @param fillColor
	 * Die Füllfarbe des {@link Polygon}s.
	 */
	public BoardPolygon(Color borderColor, Color fillColor) {
		this.borderColor = borderColor;
		this.fillColor = fillColor;
	}

	/**
	 * Zeichnet das {@link Polygon}, indem es dieses Objekt mit der
	 * {@link BoardPolygon#fillColor} füllt und es mit der {@link BoardPolygon#borderColor}
	 * umrandet.
	 *
	 * @param graphics
	 * Das {@link Graphics}-Objekt, auf das dieses {@link Polygon} gezeichnet werden soll.
	 */
	public void drawPolygon(Graphics graphics) {
		if (fillColor != null) {
			graphics.setColor(fillColor);
			graphics.fillPolygon(this);
		}
		if (borderColor != null) {
			graphics.setColor(borderColor);
			graphics.drawPolygon(this);
		}
	}

	/**
	 * Setter für die Farbe, mit der der Umriss dieses {@link Polygon}s gezeichnet werden soll.
	 *
	 * @param borderColor
	 * Die Farbe, mit der der Umriss dieses {@link Polygon}s gezeichnet werden soll.
	 */
	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	/**
	 * Getter für die Farbe, mit der der Umriss dieses {@link Polygon}s gezeichnet werden soll.
	 *
	 * @return
	 * Die Farbe, mit der der Umriss dieses {@link Polygon} gezeichnet werden soll.
	 */
	public Color getBorderColor() {
		return borderColor;
	}

	/**
	 * Setter für die Farbe, mit der dieses {@link Polygon} beim Zeichnen gefüllt werden soll.
	 *
	 * @param fillColor
	 * Die Farbe, mit dieses {@link Polygon} gefüllt werden soll.
	 */
	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}

	/**
	 * Getter für die Farbe, mit der dieses {@link Polygon} beim Zeichnen gefüllt werden soll.
	 *
	 * @return
	 * Die Farbe, mit dieses {@link Polygon} gefüllt werden soll.
	 */
	public Color getFillColor() {
		return fillColor;
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

		// Geometrie
		Point point = new Point(referencePoint);
		point.x += (int) (((row - 1) / 2.0) * triangleSideLength) + column * triangleSideLength;
		point.y -= ((row - 1) * (triangleSideLength * triangleSideHeightFactor));

		return point;
	}
}
