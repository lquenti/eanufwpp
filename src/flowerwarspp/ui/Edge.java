package flowerwarspp.ui;

import flowerwarspp.preset.Ditch;
import flowerwarspp.preset.Position;

import java.awt.*;

// Welcome to Microsoft Windows 10

/**
 * Eine Klasse, die Kanten darstellt.
 * Diese Kanten stellen die {@link Ditch}es des Spielbretts dar.
 */
public class Edge extends BoardPolygon {
	/**
	 * Der Faktor, durch den die Länge des perpendikularen Vektoren geteilt wird.
	 * Ausschließlich für optische Zwecke benutzt.
	 */
	private static final int divisionFactor = 15;

	/**
	 * Die "erste" Position (siehe {@link Position#compareTo(Position)}).
	 */
	private Position position1;
	/**
	 * Die "zweite" Position (siehe {@link Position#compareTo(Position)}).
	 */
	private Position position2;

	/**
	 * Erstelle ein Edge-Objekt, das einen {@link Ditch} repräsentiert.
	 *
	 * @param position1
	 * Eine der {@link Position}en auf dem Brett. Die Reihenfolge der beiden Positionen
	 * ist in diesem Konstruktor egal.
	 *
	 * @param position2
	 * Eine der {@link Position}en auf dem Brett. Die Reihenfolge der beiden Positionen
	 * ist in diesem Konstruktor egal.
	 */
	public Edge(Position position1, Position position2) {
		super(Color.BLACK, GameColors.EDGE_DEFAULT);

		this.position1 = position1;
		this.position2 = position2;
	}

	/**
	 * Erstellt ein {@link Ditch}-Objekt, das auf dem Spielbrett an der Stelle liegt,
	 * die dieses Polygon repräsentiert.
	 *
	 * @return
	 * Ein neues {@link Ditch}-Objekt, das die Koordinaten diese Edge auf dem Spielbrett hat.
	 */
	public Ditch toDitch() {
		return new Ditch(position1, position2);
	}

	// TODO
	@Override
	public void recalcPoints(int triangleSideLength, Point relativeStart) {
		reset();

		Point edge1 = positionToPoint(position1, triangleSideLength, relativeStart);
		Point edge2 = positionToPoint(position1, triangleSideLength, relativeStart);
		Point edge3 = positionToPoint(position2, triangleSideLength, relativeStart);
		Point edge4 = positionToPoint(position2, triangleSideLength, relativeStart);

		// Ziehe einen Vektor von edge1 nach edge2, da es einfach ist,
		// dazu einen perpendikularen Vektor zu finden.
		Point vector = new Point(edge1);
		vector.x -= edge3.x;
		vector.y -= edge3.y;

		// Hier wird der Vektor gekürzt.
		Point perpendicularVector = new Point(vector.y, -vector.x);
		perpendicularVector.x /= divisionFactor;
		perpendicularVector.y /= divisionFactor;

		// Der kurze, perpendikulare Vektor wird von der Kante aus in jede Richtung addiert.
		// Damit wird ein Rechteck konstruiert, wobei die Seiten genau gleich weit
		// von der Mitte entfernt sind.
		edge1.x += perpendicularVector.x;
		edge1.y += perpendicularVector.y;
		edge2.x -= perpendicularVector.x;
		edge2.y -= perpendicularVector.y;

		edge3.x -= perpendicularVector.x;
		edge3.y -= perpendicularVector.y;
		edge4.x += perpendicularVector.x;
		edge4.y += perpendicularVector.y;

		addPoint(edge1.x, edge1.y);
		addPoint(edge2.x, edge2.y);
		addPoint(edge3.x, edge3.y);
		addPoint(edge4.x, edge4.y);
	}
}
