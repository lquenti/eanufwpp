package flowerwarspp.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.border.EmptyBorder;

import flowerwarspp.preset.Status;
import flowerwarspp.util.Convert;
import java.awt.*;
import javax.swing.*;

// TODO: Dokumentation
public class EndPopupFrame extends JDialog {
	// TODO: Dokumentation
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

// TODO: Dokumentation
class PopupComponentPane extends JPanel {
	// TODO: Dokumentation
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

// TODO: Dokumentation
class QuitButton extends JButton implements ActionListener {
	// TODO: Dokumentation
	QuitButton() {
		super("Spiel beenden");
		addActionListener(this);
	}

	// TODO: Dokumentation
	public void actionPerformed(ActionEvent e) {
		System.exit(0);
	}
}
