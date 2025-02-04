package flowerwarspp.ui.start;

import javax.swing.*;
import java.awt.*;

/**
 * Ein {@link JFrame} über den ein Spiel mit grafischer Ausgabe gestartet werden kann.
 */
public class StartupFrame extends JFrame {
	/**
	 * Ein {@link HostGamePanel}, mit dem der Nutzer ein Spiel hosten kann.
	 */
	private GameStartPanel hostGamePanel = new GameStartPanel(this, new HostGamePanel());

	/**
	 * Eine Referenz auf das aktuell angezeigt Panel.
	 */
	private GameStartPanel currentGameStartPanel = hostGamePanel;

	/**
	 * Konstruiert ein Fenster, mit dem der Nutzer ein Spiel starten kann.
	 */
	public StartupFrame() {
		super("eanufwpp");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(600, 400));

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Spiel hosten", hostGamePanel);
		GameStartPanel offerPlayerPanel = new GameStartPanel(this, new OfferPlayerPanel());
		tabbedPane.addTab("Spieler anbieten", offerPlayerPanel);
		add(tabbedPane, BorderLayout.CENTER);

		setVisible(true);
	}
}
