package flowerwarspp.io;

import flowerwarspp.preset.Flower;
import flowerwarspp.preset.Position;

import java.awt.*;

public class Triangle extends BoardPolygon {
	private Position triangleEdge1;
	private Position triangleEdge2;
	private Position triangleEdge3;

	private Point edge1;
	private Point edge2;
	private Point edge3;

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
	public Triangle(int tx1, int ty1, boolean flipped,
	                Color currentColour) {
		super(Color.WHITE, Color.BLACK);
		this.flipped = flipped;

		this.triangleEdge1 = new Position(tx1, ty1);
		if (flipped) {
			this.triangleEdge2 = new Position(tx1 - 1, ty1 + 1);
			this.triangleEdge3 = new Position(tx1, ty1 + 1);
		} else {
			this.triangleEdge2 = new Position(tx1, ty1 - 1);
			this.triangleEdge3 = new Position(tx1 + 1, ty1 - 1);
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
		return new Position(this.triangleEdge1.getColumn(), this.triangleEdge1.getRow());
	}

	/**
	 * Getter für die Position die die linke Ecke dieses Dreiecks auf dem Brett hat.
	 *
	 * @return
	 * Die Spielbrettposition der linken Ecke dieses Dreiecks.
	 */
	public Position getLeftBoardPosition()
	{
		return new Position(this.triangleEdge3.getColumn(), this.triangleEdge3.getRow());
	}

	/**
	 * Getter für die Position die die rechte Ecke dieses Dreiecks auf dem Brett hat.
	 *
	 * @return
	 * Die Spielbrettposition der rechten Ecke dieses Dreiecks.
	 */
	public Position getRightBoardPosition()
	{
		return new Position(this.triangleEdge2.getColumn(), this.triangleEdge2.getRow());
	}

	/**
	 * Getter für die Größe des Dreiecks, die verwendet wurde, um das Dreieck zu konstruieren (d.h.
	 * die Länge aller Seiten).
	 *
	 * @return Die Größe des Dreiecks (d.h. die Länge aller Seiten).
	 */
	public int getSize() {
		return this.size;
	}

	/**
	 * Zeichnet dieses Dreieck und gegebenenfalls andere Informationen über
	 * die Blume an dieser Stelle.
	 *
	 * @param graphics
	 * Das {@link Graphics}-Element auf das die Blume, welche dieses Dreieck
	 * repräsentiert, gezeichnet werden soll.
	 */
	@Override
	public void drawPolygon(Graphics graphics)
	{
		super.drawPolygon(graphics);
		String text = this.triangleEdge2.getColumn() + ", " + this.triangleEdge2.getRow();
		graphics.drawString(text, this.edge2.x, this.edge2.y);
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
	 * Eine {@link Flower}, die an der Stelle liegt,
	 * welche dieses {@link Triangle} repräsentiert.
	 */
	public Flower toFlower() {
		return new Flower(this.triangleEdge1, this.triangleEdge2, this.triangleEdge3);
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
		Flower thisFlower = new Flower(this.triangleEdge1, this.triangleEdge2, this.triangleEdge3);
		return thatFlower.equals(thisFlower);
	}

	/**
	 * Gibt an, ob ein {@link Triangle} auf dem Spielbrett
	 * am selben Ort liegt wie <code>this</code>.
	 *
	 * @param other
	 * Das andere {@link Triangle}.
	 *
	 * @return
	 * <code>true</code> genau dann, wenn die Spielbrettkoordinaten des anderen Dreiecks den
	 * Spielbrettkoordinaten dieses {@link Triangle}s übereinstimmen.
	 */
	public boolean samePlace(Triangle other) {
		if (other == null)
			return false;

		return (this.getTopBoardPosition().equals(other.getTopBoardPosition()) &&
		        this.getRightBoardPosition().equals(other.getRightBoardPosition()) &&
		        this.getLeftBoardPosition().equals(other.getLeftBoardPosition()));
	}

	@Override
	public void recalcPoints(int triangleSideLength, Point relativeStart) {
		this.reset();

		this.edge1 = positionToPoint(this.triangleEdge1, triangleSideLength, relativeStart);
		this.edge2 = positionToPoint(this.triangleEdge2, triangleSideLength, relativeStart);
		this.edge3 = positionToPoint(this.triangleEdge3, triangleSideLength, relativeStart);
		this.addPoint(this.edge1.x, this.edge1.y);
		this.addPoint(this.edge2.x, this.edge2.y);
		this.addPoint(this.edge3.x, this.edge3.y);
	}
}
