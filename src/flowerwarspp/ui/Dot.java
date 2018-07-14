package flowerwarspp.ui;

import flowerwarspp.preset.Position;

import java.awt.*;
import java.awt.geom.Rectangle2D;

// TODO: Dokumentation
public class Dot extends BoardPolygon {
	/**
	 * Die {@link Position} dieses Punkts auf dem Spielbrett.
	 */
	private Position position;

	/**
	 * Die Position dieses Points auf dem Swing-Zeichenbrett.
	 */
	private Point currentLocation;
	/**
	 * Der aktuelle Durchmesser in Pixeln.
	 */
	private int currentDiameter = 1;

	/**
	 * Gibt den Wert von {@link #position} zurück.
	 * @return Wert von {@link #position}.
	 */
	public Position getPosition() {
		return position;
	}

	/**
	 * Konstruiert einen Punkt im Koordinatensystem des {@link flowerwarspp.preset.Board}s.
	 * Wichtig: Der Text, der die Position schreibt wird als Teil des Borders gesehen
	 * (siehe {@link BoardPolygon#getBorderColor()}).
	 *
	 * @param position
	 * Die {@link Position}, an der dieser Punkt liegen soll.
	 */
	public Dot(Position position, Color fillColor) {
		super(Color.WHITE, fillColor);
		this.position = position;
		currentLocation = new Point();
	}

	/**
	 * Überprüft, ob sich ein Punkt in diesem {@link Dot} befindet.
	 *
	 * @param x
	 * Die x-Koordinate.
	 *
	 * @param y
	 * Die y-Koordinate.
	 *
	 * @return
	 * <code>true</code> genau dann, wenn der Abstand des Punkts
	 * zum Mittelpunkt dieses {@link Dot}s kleiner ist als der Radius
	 * dieses {@link Dot}s.
	 */
	@Override
	public boolean contains(int x, int y) {
		Point difference = new Point(currentLocation);
		difference.x -= x;
		difference.y -= y;

		double distance = Math.sqrt(Math.pow(difference.x, 2.0) + (Math.pow(difference.y, 2.0)));
		return ((currentDiameter / 2) > distance);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void recalcPoints(int triangleSideLength, Point relativeStart) {
		currentLocation = positionToPoint(position, triangleSideLength, relativeStart);
		currentDiameter = triangleSideLength / 2;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void drawPolygon(Graphics graphics) {
		int drawLocationX = currentLocation.x - (currentDiameter / 2);
		int drawLocationY = currentLocation.y - (currentDiameter / 2);
		graphics.setColor(getFillColor());
		graphics.fillOval(drawLocationX, drawLocationY, currentDiameter, currentDiameter);

		if (currentDiameter > 30) {
			String text = position.getColumn() + ", " + position.getRow();
			Rectangle2D textBounds = graphics.getFontMetrics().getStringBounds(text, graphics);
			drawLocationX = (int) (currentLocation.x - textBounds.getWidth() / 2.0);
			drawLocationY = (int) (currentLocation.y + textBounds.getHeight() / 2.0);

			graphics.setColor(getBorderColor());
			graphics.drawString(text, drawLocationX, drawLocationY);
		}
	}
}
