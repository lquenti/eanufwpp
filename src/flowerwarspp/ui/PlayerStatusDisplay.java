package flowerwarspp.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Ein Display, das die Punktzahl der Spieler anzeigt.
 */
public class PlayerStatusDisplay extends JPanel {
	/**
	 * Die aktuelle Punktzahl des {@link flowerwarspp.preset.PlayerColor}.
	 */
	private int playerPoints = 0;

	/**
	 * Konstruiert ein Display für den Status des Spielers.
	 *
	 * @param playerColor
	 * Die Farbe des {@link flowerwarspp.preset.PlayerColor}.
	 */
	public PlayerStatusDisplay(Color playerColor) {
		setBackground(playerColor);
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
	@Override
	public synchronized void paintComponent(Graphics g) {
		g.setFont(new Font("sans", Font.BOLD, 24));

		String pointCountString = Integer.toString(playerPoints);
		Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(pointCountString, g);

		Dimension dimension = new Dimension();
		int dimensionHeight = (int) (stringBounds.getHeight() * 1.5) ;
		int dimensionWidth = (int) (stringBounds.getHeight() / 6 * 16); // (h * 1.5) / (9.0 * 16)
		dimension.setSize(dimensionWidth, dimensionHeight);
		setPreferredSize(dimension);
		revalidate();

		Rectangle drawRect = g.getClipBounds();
		int x = drawRect.x + 2;
		int y = drawRect.y + 2;
		int width = dimension.width - 4;
		int height = dimension.height - 4;

		g.setColor(getBackground());
		g.fillRoundRect(x, y, width, height, 5, 5);
		g.setColor(Color.BLACK);
		g.drawRoundRect(x, y, width, height,5, 5);

		g.setColor(Color.BLACK);
		Point drawPoint = new Point();
		drawPoint.setLocation(drawRect.getCenterX(), drawRect.getCenterY());
		drawPoint.x -= stringBounds.getWidth() / 2 - 2;
		drawPoint.y += stringBounds.getHeight() / 2 - stringBounds.getHeight() / 6;
		g.drawString(pointCountString, drawPoint.x, drawPoint.y);
	}
}
