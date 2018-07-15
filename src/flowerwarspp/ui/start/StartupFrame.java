package flowerwarspp.ui.start;

import javax.swing.*;

import flowerwarspp.main.Main;
import flowerwarspp.main.GameParameters;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartupFrame extends JFrame {
	/**
	 * Ein {@link HostGamePanel}, mit dem der Nutzer ein Spiel hosten kann.
	 */
	private GameStartPanel hostGamePanel = new GameStartPanel(this, new HostGamePanel());
	/**
	 * ein {@link OfferPlayerPanel}, mit dem der Nutzer einen {@link flowerwarspp.preset.Player}
	 * anbieten kann.
	 */
	private GameStartPanel offerPlayerPanel = new GameStartPanel(this, new OfferPlayerPanel());
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
		setMinimumSize(new Dimension(500, 300));

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Spiel hosten", hostGamePanel);
		tabbedPane.addTab("Spieler anbieten", offerPlayerPanel);
		add(tabbedPane, BorderLayout.CENTER);

		setVisible(true);
	}
}
