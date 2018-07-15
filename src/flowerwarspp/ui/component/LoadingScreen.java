package flowerwarspp.ui.component;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.swing.*;
import javax.swing.border.*;

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
			"Dreiecke wurden im Jahre 2018 von Lars Quentin erfunden.",
			"Wenn Kreise ganz viele Ecken haben, dann haben sie auch mehrere.",
			"🔥🔥🔥CHNGS.7Z🔥🔥🔥",
			"Null ist eindeutig kleiner als Unendlich.",
			"Chinesische Schreibmaschinen sind echt nicht so gut.",
			"Siehe e5e4daf4 (das hier ist kein Ladebildschirmtipp)...",
			"main(s){printf(s=\"main(s){printf(s=%c%s%1$c,34,s);}\",34,s);} ist eine C-Quine",
			"Rosa Pilze sind giftig!",
			"~/🍬/🏠 == \"home/sweet/home\"",
			"Es ist nicht möglich, gegen Adv5 zu gewinnen!"
		);

	/**
	 * Eine Klasse, die einen Ladebildschirm implementiert.
	 */
	public LoadingScreen() {
		super(new BorderLayout());
		Random random = new Random();
		String message = loadingMessageStrings.get(random.nextInt(loadingMessageStrings.size()));
		JLabel content = new JLabel("<html><body>" +
		                            "<h1>Spiel wird geladen…</h1>" +
		                            "<p>Wussten Sie schon?</p>" +
		                            "<p>" + message + "</p>" +
		                            "<br/><br/>" + // In die optische Mitte verschieben
		                            "</body></html>",
		                            JLabel.CENTER);
		content.setBorder(new EmptyBorder(32, 32, 32, 32));
		add(content);
	}
}
