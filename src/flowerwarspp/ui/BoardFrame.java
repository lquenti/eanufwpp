package flowerwarspp.ui;

import flowerwarspp.main.savegame.SaveGame;
import flowerwarspp.preset.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import flowerwarspp.ui.EndPopupFrame;
/**
 * Das {@link JFrame}, das das {@link BoardDisplay} enthält.
 */
public class BoardFrame extends JFrame implements Requestable, Output, ChangeListener, ActionListener {

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

	// TODO: Dokumentation
	private SaveGame saveGame;

	/**
	 * Konstruiert das {@link JFrame} und versetzt es in einen nutzbaren Zustand.
	 */
	private BoardFrame() {
		super("Flower Wars");

		setMinimumSize(new Dimension(600, 600));
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

		topToolbarPanel.getSaveButton().addActionListener(this);
		topToolbarPanel.getZoomSpinner().addChangeListener(this);
		add(topToolbarPanel, BorderLayout.NORTH);
		add(boardScrollPane, BorderLayout.CENTER);
		add(bottomToolbarPanel, BorderLayout.SOUTH);

		setVisible(true);
	}

	/**
	 * Die {@link ActionListener}-Implementation.
	 * Speichert das Spiel in eine Datei, die wieder geladen werden kann.
	 *
	 * @param actionEvent
	 * Das {@link ActionEvent}, das die Ausführung verursacht hat.
	 */
	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Spielstand speichern");
		add(fc);
		if (fc.showSaveDialog(getParent()) == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			if (file.exists()) {
				fc.approveSelection();
			}
			String filename = fc.getSelectedFile().getAbsolutePath();
			if (!filename.endsWith(".sav")) {
				filename += ".sav";
			}
			try {
				saveGame.save(filename);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println(fc.getSelectedFile());
		}
	}

	/**
	 * Updatet die optische Vergrößerung des Spielfelds.
	 *
	 * @param changeEvent
	 * Das {@link ChangeEvent}, das die Ausführung verursacht hat.
	 */
	@Override
	public void stateChanged(ChangeEvent changeEvent) {
		if (changeEvent.getSource() == topToolbarPanel.getZoomSpinner()) {
			double newZoom = ((Integer) topToolbarPanel.getZoomSpinner().getValue()) / 100.0;
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSaveGame(SaveGame saveGame) {
		this.saveGame = saveGame;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showEndMessage(String message) {
		// NOTE: Es ist wichtig, dass der Konstruktor durch EventQueue aufgerufen wird,
		// da das Programm aufgrund Swings Threading-Struktur sonst blockiert.
		EventQueue.invokeLater(() -> new EndPopupFrame(this, message));
	}
}
