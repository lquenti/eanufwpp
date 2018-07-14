package flowerwarspp.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.border.EmptyBorder;

import flowerwarspp.preset.Status;
import flowerwarspp.util.Convert;
import java.awt.*;
import javax.swing.*;

// TODO
public class EndPopupFrame extends JDialog {
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

// TODO
class PopupComponentPane extends JPanel {
	// TODO
	private Status endStatus;
	private JLabel label;
	private JButton button;

	PopupComponentPane(String message) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		label = new JLabel(message);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);

		button = new QuitButton();
		button.setAlignmentX(Component.CENTER_ALIGNMENT);

		add(label);
		add(Box.createRigidArea(new Dimension(0, 32)));
		add(button);

		setBorder(new EmptyBorder(8, 16, 8, 16));
	}
}

class QuitButton extends JButton implements ActionListener {
	QuitButton() {
		super("Spiel beenden");
		addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		System.exit(0);
	}
}
