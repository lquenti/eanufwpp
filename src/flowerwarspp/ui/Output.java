package flowerwarspp.ui;

import flowerwarspp.preset.Viewer;

/**
 * Ein Output an den menschlichen User.
 */
public interface Output {
	// TODO
	void refresh();

	// TODO
	void setViewer(Viewer viewer);
	void showEndMessage(String message);
}
