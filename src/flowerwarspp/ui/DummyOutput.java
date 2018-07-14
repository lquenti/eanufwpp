package flowerwarspp.ui;

import flowerwarspp.main.savegame.SaveGame;
import flowerwarspp.preset.Viewer;

public class DummyOutput implements Output {
	@Override
	public void setViewer(Viewer viewer) {}

	@Override
	public void refresh() {}

	@Override
	public void setSaveGame(SaveGame saveGame) {};
}
