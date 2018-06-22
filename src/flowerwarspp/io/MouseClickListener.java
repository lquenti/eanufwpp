package flowerwarspp.io;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public interface MouseClickListener extends MouseListener
{
	@Override
	default void mouseEntered(MouseEvent e) {}
	@Override
	default void mouseExited(MouseEvent e) {}
	@Override
	default void mousePressed(MouseEvent e) {}
	@Override
	default void mouseReleased(MouseEvent e) {}
}
