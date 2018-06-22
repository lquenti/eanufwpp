package flowerwarspp.player;

import flowerwarspp.preset.*;

/**
 * Klasse, die einen menschlichen Spieler repräsentiert.
 */
public class HumanPlayer extends BasePlayer {
	/**
	 * Eingabemethode für den Spieler.
	 */
	private Requestable input;

	/**
	 * {@inheritDoc} Der Spieler wird aufgefordert, seinen Zug einzugeben und der eingegebene
	 * Zug wird zurückgeliefert.
	 */
	@Override
	public Move request() throws Exception {
		return input.request();
	}
}
