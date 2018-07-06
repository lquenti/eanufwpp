package flowerwarspp.io;

import flowerwarspp.preset.Position;

import java.awt.*;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;

public class Dot extends BoardPolygon {
	private Position position;
	private Point currentLocation;
	private int currentRadius = 1;

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
	}

	@Override
	public void recalcPoints(int triangleSideLength, Point relativeStart) {
		this.currentLocation = positionToPoint(this.position, triangleSideLength, relativeStart);
		this.currentRadius = triangleSideLength / 2;
	}

	@Override
	public void drawPolygon(Graphics graphics) {
		int drawLocationX = this.currentLocation.x - (this.currentRadius / 2);
		int drawLocationY = this.currentLocation.y - (this.currentRadius / 2);
		graphics.setColor(this.getFillColour());
		graphics.fillOval(drawLocationX, drawLocationY, this.currentRadius, this.currentRadius);

		String text = this.position.getColumn() + ", " + this.position.getRow();
		Rectangle2D textBounds = graphics.getFontMetrics().getStringBounds(text, graphics);
		drawLocationX = (int) (this.currentLocation.x - textBounds.getWidth() / 2.0);
		drawLocationY = (int) (this.currentLocation.y + textBounds.getHeight() / 2.0);

		graphics.setColor(this.getBorderColour());
		graphics.drawString(text, drawLocationX, drawLocationY);
	}
}
