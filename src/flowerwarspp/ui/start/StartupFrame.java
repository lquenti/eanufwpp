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
	 * Der {@link JButton}, der zu einem {@link LocalPlayPanel} wechselt.
	 */
	private JButton localPlayButton = new JButton("Lokales Spiel");
	/**
	 * Der {@link JButton}, der zu einem {@link LocalPlayPanel} wechselt.
	 */
	private JButton netPlayButton = new JButton("Netzwerkspiel");
	/**
	 * Das {@link JPanel}, Kontrollbuttons hält.
	 */
	private JPanel controlButtonPanel = new JPanel();

	private GameStartPanel localPlayPanel = new LocalPlayPanel();
	private GameStartPanel netPlayPanel = new NetPlayPanel();
	private GameStartPanel currentGameStartPanel = localPlayPanel;

	public StartupFrame() {
		super("eanufwpp");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(450, 300));

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
			currentGameStartPanel = localPlayPanel;
			add(currentGameStartPanel, BorderLayout.CENTER);
		} else if (actionEvent.getSource() == netPlayButton) {
			currentGameStartPanel = netPlayPanel;
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
	}
}
