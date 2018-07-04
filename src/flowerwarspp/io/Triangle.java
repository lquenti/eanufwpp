package flowerwarspp.io;

import flowerwarspp.preset.Flower;
import flowerwarspp.preset.Position;

import java.awt.*;

public class Triangle extends Polygon implements Cloneable {
	/**
	 * Die Quadratwurzel von 3.0. Wird zum Berechnen der Koordinaten der beiden anderen Vertices des
	 * Dreiecks verwendet.
	 */
	private static final double squareRootThree = Math.sqrt(3.0);

	private Position triangleEdge1 = null;
	private Position triangleEdge2 = null;
	private Position triangleEdge3 = null;

	private Point edge1 = null;
	private Point edge2 = null;
	private Point edge3 = null;

	private int size = -1;
	private Color flowerColour;

	/**
	 * Konstruiert ein dreieckiges {@link Polygon}.
	 *
	 * @param x1
	 * 		Die x-Koordinate der Spitze, d.h. der oberen, mittleren Ecke (bzw. der unteren, mittleren
	 * 		Ecke, falls das Dreieck auf dem Kopf steht).
	 * @param y1
	 * 		Die y-Koordinate der Spitze, d.h. der oberen, mittleren Ecke (bzw. der unteren, mittleren
	 * 		Ecke, falls das Dreieck auf dem Kopf steht).
	 * @param tx1
	 * 		Die x-Koordinate der {@link Position} der Spitze dieses Dreiecks.
	 * @param ty1
	 * 		Die y-Koordinate der {@link Position} der Spitze dieses Dreiecks.
	 * @param size
	 * 		Der Abstand der anderen Ecken zur Spitze in Pixel. Der Abstand ist die Länge der
	 * 		Hypothenuse des halben äquilateralen Dreiecks.
	 * @param flipped
	 * 		<code>true</code> bedeutet, dass das Dreieck auf dem Kopf steht. <code>false></code>
	 * 		bedeutet, dass die Spitze nach oben zeigt ("normales Dreieck", wie Δ)
	 */
	public Triangle(int x1, int y1,
		            int tx1, int ty1,
		            int size, boolean flipped,
	                Color currentColour) {
		int sign = (flipped) ? -1 : 1;
		int xDistance = (size / 2);
		int yDistance = sign * ((int) (xDistance * squareRootThree));
		// NOTE: (x2, y2) is the point on the *right*!
		int x2 = x1 + xDistance;
		int y2 = y1 + yDistance;
		// NOTE: (x3, y3) is the point on the left
		int x3 = x1 - xDistance;
		int y3 = y1 + yDistance;

		this.addPoint(x1, y1);
		this.addPoint(x2, y2);
		this.addPoint(x3, y3);
		this.addPoint(x1, y1);

		this.edge1 = new Point(x1, y1);
		this.edge2 = new Point(x2, y2);
		this.edge3 = new Point(x3, y3);
		this.triangleEdge1 = new Position(tx1, ty1);
		if (flipped) {
			this.triangleEdge2 = new Position(tx1 - 1, ty1 + 1);
			this.triangleEdge3 = new Position(tx1, ty1 + 1);
		} else {
			this.triangleEdge2 = new Position(tx1, ty1 - 1);
			this.triangleEdge3 = new Position(tx1 + 1, ty1 - 1);
		}
		this.size = size;
		this.flowerColour = currentColour;
	}

	/**
	 * Getter für die Spitze des Dreiecks.
	 *
	 * @return Ein {@link Point}-Objekt das die Spitze des Dreiecks kennzeichnet. Da eine Seite des
	 * Dreiecks an der Horizontalen ausgerichtet ist, ist die Spitze immer die dieser Seite
	 * gegenüberliegende Ecke.
	 */
	public Point getTopEdge() {
		return new Point(this.edge1);
	}

	/**
	 * Getter für die linke Ecke des Dreiecks.
	 *
	 * @return Ein {@link Point}-Objekt das die linke Ecke des Dreiecks kennzeichnet. Da eine Seite
	 * des Dreiecks an der Horizontalen ausgerichtet ist, ist die linke Ecke die Ecke links von der
	 * Mitte des Dreiecks.
	 */
	public Point getLeftEdge() {
		return new Point(this.edge3);
	}

	/**
	 * Getter für die rechte Seite des Dreiecks.
	 *
	 * @return Ein {@link Point}-Objekt das die rechte Ecke des Dreiecks kennzeichnet. Da eine Seite
	 * des Dreiecks an der Horizontalen ausgerichtet ist, ist die rechte Ecke die Ecke rechts von
	 * der Mitte des Dreiecks.
	 */
	public Point getRightEdge() {
		return new Point(this.edge2);
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
	 * Getter für die Höhe des Dreiecks, d.h. der Abstand zwischen der Spitze des Dreiecks und der
	 * Mitte der Basislinie des Dreiecks.
	 *
	 * @return
	 */
	public int getHeight() {
		return (int) ((size / 2) * squareRootThree);
	}

	/**
	 * Ändert die Farbe dieses {@link Triangle}s.
	 *
	 * @param newFlowerColour
	 * Die Farbe, die dieses Dreieck beim nächsten Zeichnen annehmen soll.
	 */
	public void setFlowerColour(Color newFlowerColour) {
		this.flowerColour = newFlowerColour;
	}

	/**
	 * Ein Getter für die Farbe dieses {@link Triangle}s.
	 *
	 * @return
	 * Die {@link Color}, mit der dieses {@link Polygon} beim nächsten Zeichnen gefüllt wird.
	 */
	public Color getFlowerColour() {
		return this.flowerColour;
	}

	/**
	 * Zeichnet dieses Dreieck und gegebenenfalls andere Informationen über
	 * die Blume an dieser Stelle.
	 *
	 * @param graphics
	 * Das {@link Graphics}-Element auf das die Blume, welche dieses Dreieck
	 * repräsentiert, gezeichnet werden soll.
	 */
	public void drawTriangle(Graphics graphics)
	{
		graphics.setColor(this.flowerColour);
		graphics.fillPolygon(this);
		graphics.setColor(Color.BLACK);
		graphics.drawPolygon(this);
		String text = this.triangleEdge2.getColumn() + ", " + this.triangleEdge2.getRow();
		graphics.drawString(text, this.edge3.x, this.edge3.y);
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

	public boolean samePlace(Flower thatFlower) {
		Flower thisFlower = new Flower(this.triangleEdge1, this.triangleEdge2, this.triangleEdge3);
		return thatFlower.equals(thisFlower);
	}

	public boolean samePlace(Position p1, Position p2, Position p3) {
		Flower thatFlower = new Flower(p1, p2, p3);
		return samePlace(thatFlower);
	}

	public boolean samePlace(Triangle other) {
		if (other == null)
			return false;

		return (this.getTopBoardPosition().equals(other.getTopBoardPosition()) &&
		        this.getRightBoardPosition().equals(other.getRightBoardPosition()) &&
		        this.getLeftBoardPosition().equals(other.getLeftBoardPosition()));
	}
}
