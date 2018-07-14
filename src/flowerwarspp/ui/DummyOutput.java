package flowerwarspp.ui;

import flowerwarspp.preset.Viewer;

public class DummyOutput implements Output {
	@Override
	public void setViewer(Viewer viewer) {}

	@Override
	public void refresh() {}

	@Override
	public void showEndMessage(String message) {}
}
