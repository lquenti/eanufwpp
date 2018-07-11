package flowerwarspp.io;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Ein Display, das die Punktzahl der Spieler anzeigt.
 */
public class PlayerStatusDisplay {
	/**
	 * Die Farbe des {@link flowerwarspp.preset.PlayerColor}.
	 */
	private Color playerColour;
	/**
	 * Wenn <code>true</code>, wird das Display auf der linken Hälfte des Bildschirms gezeichnet.
	 */
	private boolean left = false;
	/**
	 * Ein Rechteck das zur Darstellung des Spielers genutzt wird.
	 */
	private Rectangle playerArea = new Rectangle();

	/**
	 * Die aktuelle Punktzahl des {@link flowerwarspp.preset.PlayerColor}.
	 */
	private int playerPoints = 0;

	/**
	 * Konstruiert ein Display für den Status des Spielers.
	 *
	 * @param playerColour
	 * Die Farbe des {@link flowerwarspp.preset.PlayerColor}.
	 */
	public PlayerStatusDisplay(Color playerColour, boolean left) {
		this.playerColour = playerColour;
		this.left = left;
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
		int x = 0;
		int y = 0;

		if (!left) {
			x = currentSize.width - width;
		}

		playerArea.setSize(width, height);
		playerArea.setLocation(x, y);
	}

	/**
	 * Updatet den internen Status des Displays.
	 *
	 * @param playerPoints
	 * Die Anzahl der Punkte, die der {@link flowerwarspp.preset.PlayerColor} hat.
	 */
	public synchronized void updateStatus(int playerPoints) {
		this.playerPoints = playerPoints;
	}

	/**
	 * Zeichnet das Status-Display.
	 *
	 * @param g
	 * Das {@link Graphics}-Objekt, auf das gezeichnet werden soll.
	 */
	public synchronized void draw(Graphics g) {
		g.setColor(playerColour);

		int xLocation = playerArea.x;
		int yLocation = playerArea.y;
		int width = playerArea.width;
		int height = playerArea.height;

		g.fillRoundRect(xLocation, yLocation, width, height, 10, 10);
		g.setColor(Color.BLACK);
		g.drawRoundRect(xLocation, yLocation, width, height, 10, 10);

		String pointCountString = Integer.toString(playerPoints);
		Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(pointCountString, g);
		Point drawPoint = new Point();
		drawPoint.setLocation(playerArea.getCenterX(), playerArea.getCenterY());
		drawPoint.x -= stringBounds.getWidth() / 2;
		drawPoint.y += stringBounds.getHeight() / 2;
		g.setColor(Color.BLACK);
		g.drawString(pointCountString, drawPoint.x, drawPoint.y);
	}
}
