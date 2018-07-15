package flowerwarspp.ui.start;

import flowerwarspp.main.GameParameters;

import javax.swing.*;

/**
 * Eine abstrakte Klasse für die GameStart-Panels {@link HostGamePanel} und {@link
 * OfferPlayerPanel}.
 */
public abstract class GameParametersPanel extends JPanel {
	/**
	 * Konstruiert {@link GameParameters} aus dem aktuellen Status der GUI.
	 *
	 * @return Ein {@link GameParameters}-Objekt, aus dem ein Spiel generiert werden soll.
	 *
	 * @throws Exception
	 * 		Falls beim erstellen des {@link GameParameters}-Objekts irgendein Fehler aufgetreten
	 * 		ist.
	 */
	public abstract GameParameters createParameters() throws Exception;
}
