package flowerwarspp.ui.start;

import flowerwarspp.main.GameParameters;
import flowerwarspp.preset.PlayerType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.Arrays;
import java.util.Collection;

/**
 * Eine abstrakte Klasse für die GameStart-Panels
 * {@link HostGamePanel} und
 */
public abstract class GameStartPanel extends JPanel {
	static final Collection<PlayerType> availablePlayerTypes =
		Arrays.asList(PlayerType.HUMAN,
			PlayerType.RANDOM_AI,
			PlayerType.SIMPLE_AI,
			PlayerType.ADVANCED_AI_1,
			PlayerType.ADVANCED_AI_2,
			PlayerType.REMOTE);

	public GameStartPanel() {
		setBorder(new EmptyBorder(5, 5, 5, 5));
	}

	/**
	 * Erstellt eine {@link GameParameters}-Instanz,
	 * aus der ein gültiger Spielzustand geneiert werden kann.
	 *
	 * @return
	 * Eine {@link GameParameters}-Instanz.
	 */
	public abstract GameParameters createParameters();
}
