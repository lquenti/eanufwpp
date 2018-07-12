package flowerwarspp.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.swing.*;

/**
 * Eine Klasse, die einen Ladebildschirm implementiert.
 */
public class LoadingScreen extends JPanel {
	/**
	 * Eine Liste mit "Tipps", von denen zufällig einer im Ladebildschirm angezeigt wird.
	 */
	private static final List<String> loadingMessageStrings =
	    Arrays.asList(
	        "Dreiecke haben drei Ecken.",
	        "Wer anderen einen Graben gräbt, hat einen verbotenen Spielzug gemacht.",
	        "Blumen wurden im Jahre 1734 von Konrad Blume erfunden.",
	        "Wenn Kreise ganz viele Ecken haben, dann haben sie auch mehrere.",
	        // NOTE: The characters are FIRE emoji (U+1F525).
		    // They needn't be escaped because java uses UTF16 for strings.
	        "🔥🔥🔥CHNGS.7Z🔥🔥🔥",
	        "Chinesische Schreibmaschinen sind echt nicht so gut.",
			"Siehe e5e4daf4 (das hier ist kein Ladebildschirmtipp)..."
	    );

	/**
	 * Eine Klasse, die einen Ladebildschirm implementiert.
	 */
	public LoadingScreen() {
		super(new BorderLayout());
		Random random = new Random();
		String message = loadingMessageStrings.get(random.nextInt(loadingMessageStrings.size()));
		add(new JLabel("<html><body>" +
		               "<h1>Spiel wird geladen…</h1>" +
		               "<p>Wussten sie schon?</p>" +
		               "<p>" + message + "</p>" +
		               "</body></html>", JLabel.CENTER));
	}
}
