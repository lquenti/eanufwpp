package flowerwarspp.ui;

import flowerwarspp.preset.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Das {@link JFrame}, das das {@link BoardDisplay} enthält.
 */
public class BoardFrame extends JFrame implements Requestable, Output, ChangeListener {

	/**
	 * Private Referenz auf die singuläre Instanz dieser Klasse.
	 */
	private static BoardFrame instance;
	/**
	 * Der {@link Viewer}, durch den auf das {@link Board} geschaut wird.
	 */
	private Viewer viewer;

	/**
	 * Das {@link LoadingScreen}-Objekt, das den Ladebildschirm darstellt.
	 */
	private LoadingScreen loadingScreen = new LoadingScreen();

	/**
	 * Das {@link JPanel}, das die obere Toolbar hält.
	 */
	private TopToolbarPanel topToolbarPanel = new TopToolbarPanel();

	/**
	 * Das {@link BoardDisplay}, das auf das {@link Board} schaut.
	 */
	private BoardDisplay boardDisplay;
	/**
	 * Ein {@link JScrollPane}, das das Scrollen über die Spielbrettanzeige ermöglicht.
	 */
	private JScrollPane boardScrollPane = new JScrollPane();

	/**
	 * Das {@link BottomToolbarPanel}, das die Toolbar am unteren Bildschirmrand darstellt.
	 */
	private BottomToolbarPanel bottomToolbarPanel = new BottomToolbarPanel();

	/**
	 * Konstruiert das {@link JFrame} und versetzt es in einen nutzbaren Zustand.
	 */
	private BoardFrame() {
		super("Flower Wars");

		setMinimumSize(new Dimension(400, 400));
		setSize(600, 600);

		add(loadingScreen, BorderLayout.CENTER);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	// NOTE: synchronized, da ggf. ein Singleton gesetzt wird.
	/**
	 * Gibt einen Verweis auf die singuläre Instanz dieser Singleton-Klasse zurück. Falls noch keine Instanz existiert,
	 * wird eine neue erzeugt.
	 *
	 * @return Die singuläre Instanz des Frames
	 */
	public synchronized static BoardFrame getInstance() {
		if (instance == null) {
			instance = new BoardFrame();
		}
		return instance;
	}

	/**
	 * Setzt den {@link Viewer}, durch den gerade geschaut wird und erstellt ein
	 * zugehöriges {@link BoardDisplay}.
	 *
	 * @param viewer
	 * Der {@link Viewer}, durch den auf das Spielbrett geschaut wird.
	 */
	public void setViewer(Viewer viewer) {
		this.viewer = viewer;

		boardDisplay = new BoardDisplay(bottomToolbarPanel);
		boardDisplay.setBoardViewer(viewer);
		boardScrollPane.setViewportView(boardDisplay);

		remove(loadingScreen);

		topToolbarPanel.getZoomSpinner().addChangeListener(this);
		add(topToolbarPanel, BorderLayout.NORTH);
		add(boardScrollPane, BorderLayout.CENTER);
		add(bottomToolbarPanel, BorderLayout.SOUTH);

		setVisible(true);
	}

	@Override
	public void stateChanged(ChangeEvent changeEvent) {
		if (changeEvent.getSource() == topToolbarPanel.getZoomSpinner()) {
			double newZoom = (Integer)topToolbarPanel.getZoomSpinner().getValue() / 100.0;
			boardDisplay.setZoom(newZoom);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Move request() throws Exception {
		if (boardDisplay == null)
			return null;

		return boardDisplay.requestMove();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh() {
		if (boardDisplay == null)
			return;

		for (PlayerColor playerColor : PlayerColor.values()) {
			topToolbarPanel.updatePlayerStatus(playerColor, viewer.getPoints(playerColor));
		}

		boardDisplay.refresh();
		repaint();
	}
}
