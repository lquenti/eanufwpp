package flowerwarspp.ui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.EmptyBorder;

import flowerwarspp.main.ExitCode;
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
	 *
	 * @param exitCode
	 * {@link ExitCode} welcher dem Betriebssystem mit {@link System#exit(int)} mitgeteilt wird.
	 */
	public EndPopupFrame(JFrame parent, String message, ExitCode exitCode) {
		super(parent, "Spiel Beendet");

		// Do stuff to *this*; it needs setup.
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLocationByPlatform(true);

		add(new PopupComponentPane(message, exitCode));

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
	 *
	 * @param exitCode
	 * {@link ExitCode} welcher dem Betriebssystem mit {@link System#exit(int)} mitgeteilt wird.
	 */
	PopupComponentPane(String message, ExitCode exitCode) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JLabel label = new JLabel(message);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);

		JButton button = new QuitButton(exitCode);
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
	 * Der Exit-Code welcher dem Betriebssystem beim Beenden des Programms zurück gegeben wird.
	 */
	private int exitCode;

	/**
	 * Konsturiert einen {@link JButton}, der auf Klick das Programm beendet.
	 *
	 * @param exitCode
	 * {@link ExitCode} welcher dem Betriebssystem mit {@link System#exit(int)} mitgeteilt wird.
	 */
	QuitButton(ExitCode exitCode) {
		super("Spiel beenden");
		this.exitCode = exitCode.ordinal();
		addActionListener(this);
	}

	/**
	 * Handhabt das Auslösen eines {@link JButton}s.
	 * Schließt das Programm.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		System.exit(exitCode);
	}
}
