package flowerwarspp.ui;

import flowerwarspp.main.savegame.SaveGame;
import flowerwarspp.preset.Viewer;

/**
 * Ein Output an den menschlichen User.
 */
public interface Output {
	// TODO
	void refresh();

	// TODO
	void setViewer(Viewer viewer);
	void setSaveGame(SaveGame saveGame);
	void showEndMessage(String message);
}
