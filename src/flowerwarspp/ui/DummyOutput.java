package flowerwarspp.ui;

import flowerwarspp.main.ExitCode;
import flowerwarspp.main.savegame.SaveGame;
import flowerwarspp.preset.Viewer;

/**
 * Eine Implementation von {@link Output}, die alle nötigen Methoden überschreibt, aber keine
 * Funktionalität bietet. Nützlich, um die Ausgabe auszuschalten, ohne ständig überprüfen zu müssen,
 * ob ein {@link Output} <code>null</code> ist.
 */
public class DummyOutput implements Output {
	/**
	 * {@inheritDoc} Diese Implementation tut nichts.
	 */
	@Override
	public void setViewer(Viewer viewer) {}

	/**
	 * {@inheritDoc} Diese Implementation tut nichts.
	 */
	@Override
	public void refresh() {}

	/**
	 * {@inheritDoc} Diese Implementation tut nichts.
	 */
	@Override
	public void setSaveGame(SaveGame saveGame) {}

	/**
	 * {@inheritDoc} Beendet das Programm mit dem übergebenen {@link ExitCode}.
	 */
	@Override
	public void showEndMessage(String message, ExitCode exitCode) {
		showEndMessage(exitCode);
	}

	/**
	 * {@inheritDoc} Beendet das Programm mit dem übergebenen {@link ExitCode}.
	 */
	@Override
	public void showEndMessage(ExitCode exitCode) {
		System.exit(exitCode.ordinal());
	}
}
