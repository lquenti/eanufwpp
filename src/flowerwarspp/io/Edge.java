package flowerwarspp.io;

import flowerwarspp.preset.Ditch;
import flowerwarspp.preset.Position;

import java.awt.*;

public class Edge extends BoardPolygon {

	private static final Color defaultDitchColor = new Color(0x88, 0x55, 0x22);

	/**
	 * Die "erste" Position (siehe {@link Position#compareTo(Position)}).
	 */
	private Position position1;
	/**
	 * Die "zweite" Position (siehe {@link Position#compareTo(Position)}).
	 */
	private Position position2;

	private Point edge1;
	private Point edge2;
	private Point edge3;
	private Point edge4;

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
		super(Color.BLACK, defaultDitchColor);

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
		return new Ditch(this.position1, this.position2);
	}

	@Override
	public void recalcPoints(int triangleSideLength, Point relativeStart) {
		this.reset();

		this.edge1 = positionToPoint(this.position1, triangleSideLength, relativeStart);
		this.edge1.x += 2;
		this.edge1.y += 2;

		this.edge2 = positionToPoint(this.position1, triangleSideLength, relativeStart);
		this.edge2.x -= 2;
		this.edge2.y -= 2;

		this.edge3 = positionToPoint(this.position2, triangleSideLength, relativeStart);
		this.edge3.x -= 2;
		this.edge3.y -= 2;

		this.edge4 = positionToPoint(this.position2, triangleSideLength, relativeStart);
		this.edge4.x += 2;
		this.edge4.y += 2;

		this.addPoint(this.edge1.x, this.edge1.y);
		this.addPoint(this.edge2.x, this.edge2.y);
		this.addPoint(this.edge3.x, this.edge3.y);
		this.addPoint(this.edge4.x, this.edge4.y);
	}
}
