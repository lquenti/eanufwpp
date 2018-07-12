package flowerwarspp.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Eine Klasse, die einen Ladebildschirm implementiert.
 */
public class LoadingScreen extends JPanel {

	/**
	 * Eine Klasse, die in regelmäßigen Intervallen den Ladebildschirmtipp ändert.
	 */
	private class TextUpdater implements ActionListener {
		private Random random = new Random();
		private int lastIndex = 0;

		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			int index = random.nextInt(loadingMessageStrings.size());
			while (lastIndex == index)
				index = random.nextInt(loadingMessageStrings.size());
			String string = loadingMessageStrings.get(index);
			loadingMessageLabel.setText(string);
			lastIndex = index;
		}
	}

	private static final List<String> loadingMessageStrings =
	    Arrays.asList(
	        "Dreiecke haben drei Ecken.",
	        "Wer anderen einen Graben gräbt, hat einen verbotenen Spielzug gemacht.",
	        "Blumen wurden im Jahre 1734 von Konrad Blume erfunden",
	        "Wenn Kreise ganz viele Ecken haben, dann haben sie auch mehrere.",
	        // NOTE: The characters are FIRE emoji (U+1F525).
		    // They needn't be escaped because java uses UTF16 for strings.
	        "🔥🔥🔥CHNGS.7Z🔥🔥🔥",
	        "Chinesische Schreibmaschinen sind echt nicht so gut."
	    );

	/**
	 * Die Zeit in Millisekunden, nach der eine Änderung des Ladebildschirmtipps geschehen soll.
	 */
	private static final int refreshTimeMilliseconds = 1000;

	/**
	 * Ein Objekt, das regelmäßig den Ladebildschirmtext ändert.
	 */
	private TextUpdater textUpdater = new TextUpdater();

	/**
	 * Ein {@link Timer}, der in regelmäßigen Abständen den {@link #textUpdater} aufruft.
	 */
	private Timer timer = new Timer(refreshTimeMilliseconds, textUpdater);

	/**
	 * Ein {@link JLabel}, das Ladebildschirmtipps anzeigt.
	 */
	private JLabel loadingMessageLabel = new JLabel(loadingMessageStrings.get(0));

	/**
	 * Eine Klasse, die einen Ladebildschirm implementiert.
	 */
	public LoadingScreen() {
		// The timer runs on a special thread,
		// but the events are dispatched in the default EventDispatcher thread.
		timer.setRepeats(true);
		timer.start();

		add(loadingMessageLabel);
	}

	public void stop() {
		timer.setRepeats(false);
	}
}
