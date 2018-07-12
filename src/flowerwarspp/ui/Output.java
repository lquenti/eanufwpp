package flowerwarspp.ui;

import flowerwarspp.preset.Viewer;

/**
 * Ein Output an den menschlichen User.
 */
public interface Output {
	void refresh();
	void setViewer(Viewer viewer);
}
