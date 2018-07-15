package flowerwarspp.ui;

import flowerwarspp.main.savegame.SaveGame;
import flowerwarspp.preset.Viewer;
import flowerwarspp.preset.Board;

/**
 * Ein Output an den menschlichen User.
 */
public interface Output {
	/**
	 * Aktualisiert die Ausgabe.
	 */
	void refresh();

	/**
	 * Setzt den {@link Viewer}, den die Ausgabe benutzt, um Informationen über das Board
	 * abzufragen.
	 *
	 * @param viewer Der Viewer
	 */
	void setViewer(Viewer viewer);

	/**
	 * Setzt das {@link SaveGame}, das über die Ausgabe gespeichert werden kann.
	 *
	 * @param saveGame Das SaveGame
	 */
	void setSaveGame(SaveGame saveGame);

	/**
	 * Zeigt in der Ausgabe eine Meldung an, die dem Nutzer die Möglichkeit bietet, das Programm
	 * zu beenden.
	 *
	 * @param message Die Nachricht, die angezeigt werden soll
	 */
	void showEndMessage(String message);
}
