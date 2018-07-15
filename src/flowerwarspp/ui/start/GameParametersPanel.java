package flowerwarspp.ui.start;

import flowerwarspp.main.GameParameters;
import javax.swing.*;

/**
 * Eine abstrakte Klasse für die GameStart-Panels
 * {@link HostGamePanel} und {@link OfferPlayerPanel}.
 */
public abstract class GameParametersPanel extends JPanel {
	/**
	 * Erstellt eine {@link GameParameters}-Instanz,
	 * aus der ein gültiger Spielzustand geneiert werden kann.
	 *
	 * @return
	 * Eine {@link GameParameters}-Instanz.
	 */
	public abstract GameParameters createParameters();
}
