package flowerwarspp.ui;

import flowerwarspp.preset.Flower;
import flowerwarspp.preset.Position;

import java.awt.*;

/**
 * Klasse, die dreieckige Polygone auf dem Zeichenbrett zeichnet.
 * Objekte dieser Klasse repräsentieren {@link Flower}s auf dem Spielbrett.
 * Sie bestehen aus einer Spitze ("top") und zwei weiteren Ecken ("left", "right").
 *
 * Die Spitze ist die Ecke des Dreiecks, die gegenüber der Seite liegt,
 * die parallel zur x-Achse des Zeichenbretts verläuft.
 *
 * Die linke Ecke ist immer die Ecke, die von der Spitze aus in Richtung
 * des Ursprungs der x-Achse des Zeichenbretts liegt.
 *
 * Die rechte Ecke ist immer die Ecke, die von der Spitze aus auf der
 * gegenüberliegenden Seite zur linken Ecke liegt (d.h. in Richtung
 * Unendlich der x-Achse des Zeichenbretts).
 */
public class Triangle extends BoardPolygon {

	/**
	 * Die {@link Flower}, die dieses {@link Triangle} repräsentiert.
	 */
	private Flower flower;

	/**
	 * <code>true</code> genau dann, wenn die erste {@link Position} der {@link Flower},
	 * die dieses {@link Triangle} repräsentiert auf derselben Spalte liegt,
	 * wie die zweite {@link Position}.
	 */
	private boolean flipped;

	/**
	 * Konstruiert ein dreieckiges {@link Polygon}.
	 *
	 * @param flower
	 * Die {@link Flower}, die dieses {@link Triangle} repräsentiert.
	 *
	 * @param currentColor
	 * Die {@link Color}, die dieses {@link Triangle} derzeit hat.
	 */
	public Triangle(Flower flower, Color currentColor) {
		super(Color.BLACK, currentColor);
		this.flower = flower;
		flipped = (flower.getFirst().getColumn() == flower.getSecond().getColumn());
	}

	/**
	 * Ein Getter für die Umgedrehtheit dieses {@link Triangle}s.
	 *
	 * @return
	 * <code>true</code> genau dann, wenn dieses {@link Triangle} auf dem Kopf steht,
	 * das heißt die Spitze zeigt nach unten.
	 */
	public boolean isFlipped() {
		return flipped;
	}

	/**
	 * Erstellt eine {@link Flower} aus diesem {@link Triangle}.
	 *
	 * @return
	 * Eine {@link Flower}, die an der Stelle liegt, welche dieses {@link Triangle} repräsentiert.
	 */
	public Flower toFlower() {
		return flower;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void recalcPoints(int triangleSideLength, Point relativeStart) {
		reset();

		Point edge1, edge2, edge3;
		edge1 = positionToPoint(flower.getFirst(), triangleSideLength, relativeStart);
		edge2 = positionToPoint(flower.getSecond(), triangleSideLength, relativeStart);
		edge3 = positionToPoint(flower.getThird(), triangleSideLength, relativeStart);
		addPoint(edge1.x, edge1.y);
		addPoint(edge2.x, edge2.y);
		addPoint(edge3.x, edge3.y);
	}
}
