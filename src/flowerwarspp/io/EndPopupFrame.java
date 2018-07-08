package flowerwarspp.io;

import flowerwarspp.preset.Status;
import flowerwarspp.util.log.Log;
import flowerwarspp.util.log.LogLevel;
import flowerwarspp.util.log.LogModule;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EndPopupFrame extends JFrame {

	public EndPopupFrame( Status status ) {
		super("Spiel Beendet");

		// Do stuff to *this*; it needs setup.
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(300, 150);

		this.add(new PopupComponentPane(status));

		this.invalidate();
		this.repaint();
		this.setVisible(true);
	}


}

class PopupComponentPane extends JPanel {

	private Status endStatus;
	private JLabel label;
	private JButton button;


	PopupComponentPane( Status status ) {
		endStatus = status;

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		label = new JLabel(getTextFromStatus(this.endStatus));
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

		this.add(Box.createRigidArea(new Dimension(0, 10)));
		this.add(this.label);
		this.add(Box.createRigidArea(new Dimension(0, 50)));
		this.add(this.button);
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
