package flowerwarspp.ui;

import flowerwarspp.main.savegame.SaveGame;
import flowerwarspp.preset.Viewer;

// TODO
public class DummyOutput implements Output {
	// TODO
	@Override
	public void setViewer(Viewer viewer) {}

	// TODO
	@Override
	public void refresh() {}

	@Override
	public void setSaveGame(SaveGame saveGame) {}
	
	public void showEndMessage(String message) {}
}
