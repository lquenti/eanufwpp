package flowerwarspp.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.border.EmptyBorder;

import flowerwarspp.preset.Status;
import flowerwarspp.util.Convert;
import java.awt.*;
import javax.swing.*;

/**
 * Ein {@link JDialog}, das dem Nutzer mitteilt, dass das Spiel geendet hat.
 */
public class EndPopupFrame extends JDialog {
	/**
	 * Konstruiert einen {@link JDialog}, der den Nutzer über das Ende des Spiels informiert.
	 *
	 * @param parent
	 * Das Elternelement dieses {@link JDialog}.
	 *
	 * @param message
	 * Die anzuzeigende Nachricht.
	 */
	public EndPopupFrame(JFrame parent, String message) {
		super(parent, "Spiel Beendet");

		// Do stuff to *this*; it needs setup.
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setLocationByPlatform(true);

		add(new PopupComponentPane(message));

		pack();
		invalidate();
		repaint();
		setVisible(true);
	}
}

/**
 * Ein {@link JPanel}, das einen Text hält und zentriert anzeigt.
 */
class PopupComponentPane extends JPanel {
	/**
	 * Konsturiert ein {@link JPanel}, das eine Nachricht zentriert anzeigt.
	 *
	 * @param message
	 * Die anzuzeigende Nachricht.
	 */
	PopupComponentPane(String message) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JLabel label = new JLabel(message);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);

		JButton button = new QuitButton();
		button.setAlignmentX(Component.CENTER_ALIGNMENT);

		add(label);
		add(Box.createRigidArea(new Dimension(0, 32)));
		add(button);

		setBorder(new EmptyBorder(8, 16, 8, 16));
	}
}

/**
 * Ein {@link JButton}, der das Programm beendet.
 */
class QuitButton extends JButton implements ActionListener {
	/**
	 * Konsturiert einen {@link JButton}, der auf Klick das Programm beendet.
	 */
	QuitButton() {
		super("Spiel beenden");
		addActionListener(this);
	}

	/**
	 * Handhabt das Auslösen eines {@link JButton}s.
	 * Schließt das Programm.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		System.exit(0);
	}
}
