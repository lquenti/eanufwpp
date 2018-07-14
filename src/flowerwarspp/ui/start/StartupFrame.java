package flowerwarspp.ui.start;

import javax.swing.*;

import flowerwarspp.main.Game;
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

	private GameStartPanel hostGamePanel = new HostGamePanel();
	private GameStartPanel offerPlayerPanel = new OfferPlayerPanel();
	private GameStartPanel currentGameStartPanel = hostGamePanel;

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

		if (actionEvent.getSource() == localPlayButton) {
			remove(currentGameStartPanel);
			currentGameStartPanel = hostGamePanel;
			add(currentGameStartPanel, BorderLayout.CENTER);
		} else if (actionEvent.getSource() == netPlayButton) {
			currentGameStartPanel = offerPlayerPanel;
			add(currentGameStartPanel, BorderLayout.CENTER);
		} else {
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			dispose();
			GameParameters gameParameters = currentGameStartPanel.createParameters();
			Thread thread = new Thread(() -> new Game(gameParameters));
			thread.start();
			return;
		}

		repaint();
		revalidate();
	}
}
