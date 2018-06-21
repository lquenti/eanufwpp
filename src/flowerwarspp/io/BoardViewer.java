package flowerwarspp.io;

import flowerwarspp.preset.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Eine {@link Viewer}-Implementation, welche die Akquise von Informationen über das Spiel
 * ermöglicht. Dabei wird der Zugriff auf die darunterliegende Datenstruktur verwehrt.
 *
 * @author Fabian Winter
 */
/* TODO: BoardViewer needs to take the actual class instead */
public class BoardViewer implements Viewer {
	/**
	 * Eine Referenz auf das {@link Board} welches durch diesen {@link Viewer} beobachtet werden
	 * soll.
	 */
	private DummyBoard board = null;

	/**
	 * Ein Dummy-Constructor, der nur {@link DummyBoard} animmt.
	 *
	 * @param board
	 * 		Das {@link Board} das durch diesen {@link Viewer} beobachtet werden soll.
	 */
	public BoardViewer(DummyBoard board) {
		this.board = board;
	}

	/* TODO: Implement getTurn in BoardViewer */

	/**
	 * Ein Getter für wer gerade an der Reihe ist.
	 *
	 * @return Die {@link PlayerColor}, die mit dem {@link Player} assoziiert ist der derzeit an der
	 * Reihe ist.
	 */
	@Override
	public PlayerColor getTurn() {
		return null;
	}

	/* TODO: Implement getSize in BoardViewer */

	/**
	 * Ein Getter für die Länge des {@link Board}s in Einheiten.
	 *
	 * @return Die Länge des {@link Board}s in Einheiten. Die Größe ist die Anzahl an Dreiecken, von
	 * denen eine Seite auf der Seite des Spielbretts liegt, deren Länge betrachtet werden soll.
	 */
	@Override
	public int getSize() {
		return this.board.getSize();
	}

	/* TODO: Implement getStatus in BoardViewer */

	/**
	 * Ein Getter für den {@link Status} des Spiels.
	 *
	 * @return Der aktuelle {@link Status} des Spiels.
	 */
	@Override
	public Status getStatus() {
		return this.board.getStatus();
	}

	/* TODO: Implement getFlowers in BoardViewer */

	/**
	 * Ein Getter für die Menge von {@link Flower}s die ein {@link Player} auf dem Spielbrett hat.
	 *
	 * @param colour
	 * 		Die Farbe des {@link Player}s, dessen {@link Flower}s zurückgegeben werden sollen.
	 *
	 * @return Eine {@link Collection}&lt;{@link Flower}&gt; die alle Blumen für den Spieler
	 * enthält.
	 */
	@Override
	public Collection<Flower> getFlowers(PlayerColor colour) {
		Collection<Flower> flowers = this.board.getFlowers(colour);
		return new ArrayList<>(flowers);
	}

	/* TODO: Implement getDitches in BoardViewer */

	/**
	 * Ein Getter für die Menge von {@link Ditch}es die auf dem Brett von einem Spieler gesetzt
	 * wurden.
	 *
	 * @param colour
	 * 		Die Farbe des {@link Player}s, dessen {@link Ditch}es zurückgegeben werden sollen.
	 *
	 * @return Eine {@link Collection}&lt;{@link Ditch}&gt; die alle Gräben für den Spieler enthält.
	 */
	@Override
	public Collection<Ditch> getDitches(PlayerColor colour) {
		Collection<Ditch> ditches = this.board.getDitches(colour);
		return new ArrayList<>(ditches);
	}

	/* TODO: Implement getPossibleMoves in BoardViewer */

	/**
	 * Getter für eine {@link Collection} von {@link Move}s, welche derzeit möglich sind.
	 *
	 * @return Eine {@link Collection}&lt;{@link Move}&gt; derzeit möglicher {@link Move}s
	 */
	@Override
	public Collection<Move> getPossibleMoves() {
		Collection<Move> possibleMoves = this.board.getPossibleMoves();
		return new ArrayList<>(possibleMoves);
	}

	/* TODO: Implement getPoints in BoardViewer */

	/**
	 * Ein Getter für die Anzahl von Punkten eines bestimmten {@link Player}s, spezifiziert durch
	 * die {@link PlayerColor}.
	 *
	 * @param playerColour
	 * 		Die {link PlayerColor} des {@link Player}s, dessen Punktzahl zurückgegeben werden soll.
	 *
	 * @return Die Anzahl an Punkten des {@link Player}s der mit der {@link PlayerColor} assoziiert
	 * ist.
	 */
	@Override
	public int getPoints(PlayerColor playerColour) {
		return this.board.getPoints(playerColour);
	}
}
