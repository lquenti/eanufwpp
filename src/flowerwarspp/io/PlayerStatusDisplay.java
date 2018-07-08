package flowerwarspp.io;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Ein Display, das die Punktzahl der Spieler anzeigt.
 */
public class PlayerStatusDisplay {
	/**
	 * Die Farbe des {@link flowerwarspp.preset.PlayerColor#Red}.
	 */
	private Color redColour;
	/**
	 * Die Farbe des {@link flowerwarspp.preset.PlayerColor#Blue}.
	 */
	private Color blueColour;
	/**
	 * Ein Rechteck das zur Darstellung des roten Spielers genutzt wird.
	 */
	private Rectangle redPlayerArea = new Rectangle();
	/**
	 * Ein Rechteck das zur Darstellung des blauen Spielers genutzt wird.
	 */
	private Rectangle bluePlayerArea = new Rectangle();

	/**
	 * Die aktuelle Punktzahl des {@link flowerwarspp.preset.PlayerColor#Red}.
	 */
	private int redPlayerPoints = 0;
	/**
	 * Die aktuelle Punktzahl des {@link flowerwarspp.preset.PlayerColor#Blue}.
	 */
	private int bluePlayerPoints = 0;

	/**
	 * Konstruiert ein Display für die Stati der Spieler.
	 *
	 * @param redColour
	 * Die {@link Color} des {@link flowerwarspp.preset.PlayerColor#Red}.
	 *
	 * @param blueColour
	 * Die {@link Color} des {@link flowerwarspp.preset.PlayerColor#Blue}.
	 */
	public PlayerStatusDisplay(Color redColour, Color blueColour) {
		this.redColour = redColour;
		this.blueColour = blueColour;
	}

	/**
	 * Updatet die Größe der Rechtecke, die die Punkte der Spieler anzeigt.
	 *
	 * @param currentSize
	 * Die Größe des Zeichenbretts.
	 */
	public synchronized void updateRectangleSizes(Dimension currentSize) {
		int minimumSize = Math.min(currentSize.width, currentSize.height);
		int width = minimumSize / 5;
		int height = width * 9 / 16;
		int x = currentSize.width - width;
		int y = 0;

		redPlayerArea.setSize(width, height);
		bluePlayerArea.setSize(width, height);

		redPlayerArea.setLocation(x, y);
		bluePlayerArea.setLocation(x, y + height);
	}

	/**
	 * Updatet den internen Status des Displays.
	 *
	 * @param redPlayerPoints
	 * Die Punkte des {@link flowerwarspp.preset.PlayerColor#Red}.
	 *
	 * @param bluePlayerPoints
	 * Die Punkte des {@link flowerwarspp.preset.PlayerColor#Blue}.
	 */
	public synchronized void updateStatus(int redPlayerPoints, int bluePlayerPoints) {
		this.redPlayerPoints = redPlayerPoints;
		this.bluePlayerPoints = bluePlayerPoints;
	}

	/**
	 * Zeichnet das Status-Display.
	 *
	 * @param g
	 * Das {@link Graphics}-Objekt, auf das gezeichnet werden soll.
	 */
	public synchronized void draw(Graphics g) {
		drawRectangle(g, redPlayerArea, redColour, redPlayerPoints);
		drawRectangle(g, bluePlayerArea, blueColour, bluePlayerPoints);
	}

	/**
	 * Zeichnet ein Dreick für einen Spieler.
	 *
	 * @param g
	 * Die {@link Graphics}, auf das gezeichnet werden soll.
	 *
	 * @param rectangle
	 * Das {@link Rectangle}, das gezeichet werden soll.
	 *
	 * @param colour
	 * Die {@link Color}, die zum Spieler gehört.
	 *
	 * @param points
	 * Die Zahl an Punkten, die zum Spieler gehören.
	 */
	private void drawRectangle(Graphics g, Rectangle rectangle, Color colour, int points) {
		g.setColor(colour);
		g.fillRoundRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height, 10, 10);
		g.setColor(Color.BLACK);
		g.drawRoundRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height, 10, 10);

		String pointCountString = Integer.toString(points);
		Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(pointCountString, g);
		Point drawPoint = new Point();
		drawPoint.setLocation(rectangle.getCenterX(), rectangle.getCenterY());
		drawPoint.x -= stringBounds.getWidth() / 2;
		drawPoint.y += stringBounds.getHeight() / 2;
		g.setColor(Color.BLACK);
		g.drawString(pointCountString, drawPoint.x, drawPoint.y);
	}
}
