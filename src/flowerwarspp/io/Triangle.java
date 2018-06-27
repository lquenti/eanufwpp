package flowerwarspp.io;

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

	/**
	 * Erstellt ein Triangle aus Vertices. Private, da es nur intern genutzt werden soll. Zieht die
	 * Linie zum Schluss zurück zum ersten Vertex.
	 *
	 * @param x1
	 * 		X-Koordinate des ersten Vertex.
	 * @param y1
	 * 		Y-Koordinate des ersten Vertex.
	 * @param x2
	 * 		X-Koordinate des zweiten Vertex.
	 * @param y2
	 * 		Y-Koordinate des zweiten Vertex.
	 * @param x3
	 * 		X-Koordinate des dritten Vertex.
	 * @param y3
	 * 		Y-Koordinate des dritten Vertex.
	 * @param tx1
	 * 		X-Koordinate der ersten {@link Position} auf dem Brett.
	 * @param ty1
	 * 		X-Koordinate der ersten {@link Position} auf dem Brett.
	 * @param tx2
	 * 		X-Koordinate der zweiten {@link Position} auf dem Brett.
	 * @param ty2
	 * 		X-Koordinate der zweiten {@link Position} auf dem Brett.
	 * @param tx3
	 * 		X-Koordinate der dritten {@link Position} auf dem Brett.
	 * @param ty3
	 * 		X-Koordinate der dritten {@link Position} auf dem Brett.
	 * @param size
	 * 		Die Länge der Seiten des Dreiecks.
	 */
	private Triangle(int x1, int y1, int x2, int y2, int x3, int y3,
		             int tx1, int ty1, int tx2, int ty2, int tx3, int ty3,
		             int size) {
		this.addPoint(x1, y1);
		this.addPoint(x2, y2);
		this.addPoint(x3, y3);
		this.addPoint(x1, y1);

		this.edge1 = new Point(x1, y1);
		this.edge2 = new Point(x2, y2);
		this.edge3 = new Point(x3, y3);
		this.triangleEdge1 = new Position(tx1, ty1);
		this.triangleEdge2 = new Position(tx2, ty2);
		this.triangleEdge3 = new Position(tx3, ty3);
		this.size = size;
	}

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
		            int size, boolean flipped) {
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
		try {
			this.triangleEdge1 = new Position(tx1, ty1);
			if (flipped) {
				this.triangleEdge2 = new Position(tx1 - 1, ty1 + 1);
				this.triangleEdge3 = new Position(tx1, ty1 + 1);
			} else {
				this.triangleEdge2 = new Position(tx1, ty1 - 1);
				this.triangleEdge3 = new Position(tx1 + 1, ty1 - 1);
			}
		} catch (Exception e)
		{
			System.out.println(triangleEdge1);
		}
		this.size = size;
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
	 * Konstruiert eine Kopie dieses {@link Triangle}s und gibt ein {@link Triangle} zurück.
	 *
	 * @return Ein {@link Triangle} mit denselben Vertices wie dieses Dreieck.
	 */
	public Triangle cloneTriangle() {
		Triangle clone = new Triangle(
		        this.edge1.x, this.edge1.y,
		        this.edge2.x, this.edge2.y,
		        this.edge3.x, this.edge3.y,
		        this.triangleEdge1.getRow(), triangleEdge1.getColumn(),
		        this.triangleEdge2.getRow(), triangleEdge2.getColumn(),
		        this.triangleEdge3.getRow(), triangleEdge3.getColumn(),
		        this.getSize());

		return clone;
	}

	/**
	 * Konstruiert eine Kopie dieses {@link Triangle}s und gibt ein {@link Triangle} zurück.
	 *
	 * @return Ein {@link Triangle} mit denselben Vertices wie dieses Dreieck.
	 */
	@Override
	public Object clone() {
		return this.cloneTriangle();
	}
}
