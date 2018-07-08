package flowerwarspp.io;

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
	 * Ecke "top" dieses Dreiecks.
	 */
	private Position triangleEdge1;
	/**
	 * Ecke "left" dieses Dreiecks.
	 */
	private Position triangleEdge2;
	/**
	 * Ecke "right" dieses Dreiecks.
	 */
	private Position triangleEdge3;

	private int size = -1;
	private boolean flipped;

	/**
	 * Konstruiert ein dreieckiges {@link Polygon}.
	 *
	 * @param tx1
	 * 		Die x-Koordinate der {@link Position} der Spitze dieses Dreiecks.
	 * @param ty1
	 * 		Die y-Koordinate der {@link Position} der Spitze dieses Dreiecks.
	 * @param flipped
	 * 		<code>true</code> bedeutet, dass das Dreieck auf dem Kopf steht. <code>false></code>
	 * 		bedeutet, dass die Spitze nach oben zeigt ("normales Dreieck", wie Δ)
	 */
	public Triangle(int tx1, int ty1, boolean flipped, Color currentColour) {
		super(Color.BLACK, currentColour);
		this.flipped = flipped;

		triangleEdge1 = new Position(tx1, ty1);
		// Dependent upon whether this triangle is on its head or not,
		// the coordinates are either above or below the other two coordinates
		if (flipped) {
			triangleEdge2 = new Position(tx1 - 1, ty1 + 1);
			triangleEdge3 = new Position(tx1, ty1 + 1);
		} else {
			triangleEdge2 = new Position(tx1, ty1 - 1);
			triangleEdge3 = new Position(tx1 + 1, ty1 - 1);
		}
	}

	/**
	 * Getter für die Position die die Spitze dieses Dreiecks auf dem Brett hat.
	 *
	 * @return
	 * Die Spielbrettposition der Spitze dieses Dreiecks.
	 */
	public Position getTopBoardPosition()
	{
		return new Position(triangleEdge1.getColumn(), triangleEdge1.getRow());
	}

	/**
	 * Getter für die Position die die linke Ecke dieses Dreiecks auf dem Brett hat.
	 *
	 * @return
	 * Die Spielbrettposition der linken Ecke dieses Dreiecks.
	 */
	public Position getLeftBoardPosition()
	{
		return new Position(triangleEdge3.getColumn(), triangleEdge3.getRow());
	}

	/**
	 * Getter für die Position die die rechte Ecke dieses Dreiecks auf dem Brett hat.
	 *
	 * @return
	 * Die Spielbrettposition der rechten Ecke dieses Dreiecks.
	 */
	public Position getRightBoardPosition()
	{
		return new Position(triangleEdge2.getColumn(), triangleEdge2.getRow());
	}

	/**
	 * Ein Getter für die Umgedrehtheit dieses {@link Triangle}s.
	 *
	 * @return
	 * <code>true</code> genau dann, wenn dieses {@link Triangle} auf dem Kopf steht,
	 * das heißt die Spitz zeigt nach unten.
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
		return new Flower(triangleEdge1, triangleEdge2, triangleEdge3);
	}

	/**
	 * Gibt an, ob eine {@link Flower} am selben Ort wie <code>this</code> ist, das heißt,
	 * ob dieses {@link Triangle} diese {@link Flower} repräsentiert.
	 *
	 * @param thatFlower
	 * Die {@link Flower}, für die geprüft werden soll, ob sie auf diesem {@link Triangle} liegt.
	 *
	 * @return
	 * <code>true</code> genau dann, wenn eine {@link Flower}, die dieselbe Position auf dem
	 * Spielbrett hat wie dieses {@link Triangle} an derselben Stelle liegt wie
	 * <code>thatFlower</code> (siehe {@link Flower#equals(Object)}.
	 */
	public boolean samePlace(Flower thatFlower) {
		if (thatFlower == null)
			return false;

		Flower thisFlower = new Flower(triangleEdge1, triangleEdge2, triangleEdge3);
		return thatFlower.equals(thisFlower);
	}

	@Override
	public void recalcPoints(int triangleSideLength, Point relativeStart) {
		reset();

		Point edge1, edge2, edge3;
		edge1 = positionToPoint(triangleEdge1, triangleSideLength, relativeStart);
		edge2 = positionToPoint(triangleEdge2, triangleSideLength, relativeStart);
		edge3 = positionToPoint(triangleEdge3, triangleSideLength, relativeStart);
		addPoint(edge1.x, edge1.y);
		addPoint(edge2.x, edge2.y);
		addPoint(edge3.x, edge3.y);
	}
}
