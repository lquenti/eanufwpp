package flowerwarspp.ui;

import flowerwarspp.preset.Status;
import flowerwarspp.util.Convert;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// TODO
public class EndPopupFrame extends JDialog {
	// TODO
	public EndPopupFrame( Status status ) {
		super(BoardFrame.getInstance(), "Spiel Beendet");

		// Do stuff to *this*; it needs setup.
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setLocationByPlatform(true);
		setSize(300, 150);

		add(new PopupComponentPane(status));

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

	// TODO
	PopupComponentPane( Status status ) {
		endStatus = status;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		label = new JLabel(Convert.statusToText(endStatus));
		label.setAlignmentX(Component.CENTER_ALIGNMENT);

		button = new JButton("Spiel Beenden");
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		button.addMouseListener(new MouseAdapter() {
			/**
			 * {@inheritDoc}
			 *
			 * @param e
			 */
			@Override
			public void mouseClicked( MouseEvent e ) {
				System.exit(0);
			}
		});

		add(Box.createRigidArea(new Dimension(0, 10)));
		add(label);
		add(Box.createRigidArea(new Dimension(0, 50)));
		add(button);
	}

}
