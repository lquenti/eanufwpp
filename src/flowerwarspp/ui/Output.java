package flowerwarspp.ui;

import flowerwarspp.main.ExitCode;
import flowerwarspp.main.savegame.SaveGame;
import flowerwarspp.preset.Viewer;

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
	 * @param viewer
	 * 		Der Viewer
	 */
	void setViewer(Viewer viewer);

	/**
	 * Setzt das {@link SaveGame}, das über die Ausgabe gespeichert werden kann.
	 *
	 * @param saveGame
	 * 		Das SaveGame
	 */
	void setSaveGame(SaveGame saveGame);

	/**
	 * Zeigt in der Ausgabe eine Meldung an, die dem Nutzer die Möglichkeit bietet, das Programm zu
	 * beenden. Das Programm wird mit dem übergebenen {@link ExitCode} beendet.
	 *
	 * @param message
	 * 		Die Nachricht, die angezeigt werden soll
	 * @param exitCode
	 * 		{@link ExitCode} welcher dem Betriebssystem mit {@link System#exit(int)} mitgeteilt
	 * 		wird.
	 */
	void showEndMessage(String message, ExitCode exitCode);

	/**
	 * Zeigt in der Ausgabe eine Meldung an, die dem Nutzer die Möglichkeit bietet, das Programm zu
	 * beenden. Das Programm wird mit dem übergebenen {@link ExitCode} beendet. Die anzuzeigende
	 * Nachricht wird dabei vom übergebenen {@link ExitCode} übernommen.
	 *
	 * @param exitCode
	 * 		{@link ExitCode} welcher dem Betriebssystem mit {@link System#exit(int)} mitgeteilt
	 * 		wird.
	 */
	void showEndMessage(ExitCode exitCode);
}
