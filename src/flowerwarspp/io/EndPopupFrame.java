package flowerwarspp.io;

import flowerwarspp.preset.Status;
import flowerwarspp.util.log.Log;
import flowerwarspp.util.log.LogLevel;
import flowerwarspp.util.log.LogModule;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EndPopupFrame extends JDialog {

	public EndPopupFrame( Status status ) {
		super(BoardFrame.getInstance(), "Spielende.");

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

class PopupComponentPane extends JPanel {

	private Status endStatus;
	private JLabel label;
	private JButton button;


	PopupComponentPane( Status status ) {
		endStatus = status;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		label = new JLabel(getTextFromStatus(endStatus));
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

	private static String getTextFromStatus( Status status ) {
		switch ( status ) {
			case Draw:
				return "Das Spiel endete unentschieden.";
			case RedWin:
				return "Der rote Spieler hat das Spiel gewonnen!";
			case BlueWin:
				return "Der blaue Spieler hat das Spiel gewonnen!";
			case Illegal:
			default:
				Log.log0(LogLevel.ERROR, LogModule.IO, "Invalid status passed to EndPopupFrame");
				return null;
		}
	}

}
