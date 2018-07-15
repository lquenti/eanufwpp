package flowerwarspp.ui.start;

import javax.swing.*;

import flowerwarspp.main.Main;
import flowerwarspp.main.GameParameters;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartupFrame extends JFrame implements ActionListener {
	/**
	 * Der {@link JButton}, der aus dem vom {@link GameStartPanel} generierten
	 * {@link GameParameters}-Objekt ein Spiel initiiert.
	 */
	private JButton startButton = new JButton("Spiel beginnen");
	/**
	 * Der {@link JButton}, der zu einem {@link HostGamePanel} wechselt.
	 */
	private JButton localPlayButton = new JButton("Spiel hosten");
	/**
	 * Der {@link JButton}, der zu einem {@link HostGamePanel} wechselt.
	 */
	private JButton netPlayButton = new JButton("Spieler anbieten");
	/**
	 * Das {@link JPanel}, Kontrollbuttons hält.
	 */
	private JPanel controlButtonPanel = new JPanel();

	/**
	 * Ein {@link HostGamePanel}, mit dem der Nutzer ein Spiel hosten kann.
	 */
	private GameStartPanel hostGamePanel = new HostGamePanel();
	/**
	 * ein {@link OfferPlayerPanel}, mit dem der Nutzer einen {@link flowerwarspp.preset.Player}
	 * anbieten kann.
	 */
	private GameStartPanel offerPlayerPanel = new OfferPlayerPanel();
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

		localPlayButton.addActionListener(this);
		netPlayButton.addActionListener(this);
		startButton.addActionListener(this);
		controlButtonPanel.add(localPlayButton);
		controlButtonPanel.add(startButton);
		controlButtonPanel.add(netPlayButton);

		add(currentGameStartPanel, BorderLayout.CENTER);
		add(controlButtonPanel, BorderLayout.SOUTH);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		remove(currentGameStartPanel);

		// Diese Lösung ist die eleganteste,
		// da die JButtons auch das Frame verändern können müssen.
		if (actionEvent.getSource() == localPlayButton) {
			remove(currentGameStartPanel);
			currentGameStartPanel = hostGamePanel;
			add(currentGameStartPanel, BorderLayout.CENTER);
		} else if (actionEvent.getSource() == netPlayButton) {
			currentGameStartPanel = offerPlayerPanel;
			add(currentGameStartPanel, BorderLayout.CENTER);
		} else {
			// Schließt das Fenster und erstellt einen neuen Thread für das Spiel.
			// NOTE: Hier wird das Spiel in einem Nicht-Main-Thread gestartet
			// (der dann allerdings dem main-Thread in nichts nachsteht).
			dispose();
			GameParameters gameParameters = currentGameStartPanel.createParameters();
			Thread thread = new Thread(() -> Main.startNewGame(gameParameters));
			thread.start();
			return;
		}

		repaint();
		revalidate();
	}
}
