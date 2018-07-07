package flowerwarspp.io;

import flowerwarspp.preset.Position;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Dot extends BoardPolygon {
	private Position position;
	private Point currentLocation;
	private int currentDiameter = 1;

	/**
	 * Konstruiert einen Punkt im Koordinatensystem des {@link flowerwarspp.preset.Board}s.
	 * Wichtig: Der Text, der die Position schreibt wird als Teil des Borders gesehen
	 * (siehe {@link BoardPolygon#getBorderColour()}).
	 *
	 * @param position
	 * Die {@link Position}, an der dieser Punkt liegen soll.
	 */
	public Dot(Position position) {
		super(Color.WHITE, Color.BLACK);
		this.position = position;
		this.currentLocation = new Point();
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
		Point difference = new Point(this.currentLocation);
		difference.x -= x;
		difference.y -= y;

		double distance = Math.sqrt(Math.pow(difference.x, 2.0) + (Math.pow(difference.y, 2.0)));
		return ((this.currentDiameter / 2) > distance);
	}

	@Override
	public void recalcPoints(int triangleSideLength, Point relativeStart) {
		this.currentLocation = positionToPoint(this.position, triangleSideLength, relativeStart);
		this.currentDiameter = triangleSideLength / 2;
	}

	@Override
	public void drawPolygon(Graphics graphics) {
		int drawLocationX = this.currentLocation.x - (this.currentDiameter / 2);
		int drawLocationY = this.currentLocation.y - (this.currentDiameter / 2);
		graphics.setColor(this.getFillColour());
		graphics.fillOval(drawLocationX, drawLocationY, this.currentDiameter, this.currentDiameter);

		String text = this.position.getColumn() + ", " + this.position.getRow();
		Rectangle2D textBounds = graphics.getFontMetrics().getStringBounds(text, graphics);
		drawLocationX = (int) (this.currentLocation.x - textBounds.getWidth() / 2.0);
		drawLocationY = (int) (this.currentLocation.y + textBounds.getHeight() / 2.0);

		graphics.setColor(this.getBorderColour());
		graphics.drawString(text, drawLocationX, drawLocationY);
	}
}
