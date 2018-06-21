package flowerwarspp.board;

import flowerwarspp.io.BoardViewer;
import flowerwarspp.preset.*;

import java.util.HashSet;

/*
TODO: eigenen besseren Floweralgo benutzen
*/

/**
 * Boardimplementation. Implementiert Board und somit auch Viewable
 *
 * @author Lars Quentin
 * @version 0.1
 */
public class MainBoard implements Board {
	/**
	 * Menge der vom roten Spieler gesetzen Blumen
	 */
	private HashSet<Flower> redFlowers = new HashSet<>();
	/**
	 * Menge der vom blauen Spieler gesetzen Blumen
	 */
	private HashSet<Flower> blueFlowers = new HashSet<>();

	/**
	 * Menge der vom roten Spieler gesetzen Gräben
	 */
	private HashSet<Ditch> redDitches = new HashSet<>();
	/**
	 * Menge der vom blauen Spieler gesetzen Gräben
	 */
	private HashSet<Ditch> blueDitches = new HashSet<>();

	/**
	 * Menge der legalen Züge, die der rote Spieler machen kann.
	 */
	private HashSet<Move> redLegalMoves = new HashSet<>();
	/**
	 * Menge der legalen Züge, die der blaue Spieler machen kann.
	 */
	private HashSet<Move> blueLegalMoves = new HashSet<>();

	/**
	 * Groesse des Boards
	 */
	private final int size;

	/**
	 * Konstruktor. Befuellt Board einer variablen Groesse zwischen [3;30].
	 * Falls Wert invalide wird dieser dem naechsten Element des Intervalls angepasst.
	 *
	 * @param size Groesse des Boardes.
	 */
	public MainBoard(final int size) {
		this.size = (size < 3) ? 3 : ((size > 30) ? 30 : size);

		Flower[] flowers = new Flower[this.size * this.size];
		int insertPosition = 0;
		for (int i = 1; i <= this.size; i++) {
			for (int j = 1; j <= this.size - (i-1); j++) {
				flowers[insertPosition] = (new Flower(
					new Position(i, j),
					new Position(i+1, j),
					new Position(i, j+1)
				));
				insertPosition++;

				if (i + j <= this.size) {
					flowers[insertPosition] = (new Flower(
						new Position(i+1, j+1),
						new Position(i+1, j),
						new Position(i, j+1)
					));
					insertPosition++;
				}
			}
		}
		for (int i = 0; i < flowers.length; i++) {
			for (int j = i+1; j < flowers.length; j++) {
				Move move = new Move(flowers[i], flowers[j]);
				redLegalMoves.add(move);
				blueLegalMoves.add(move);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * Verifiziert Zug und fuehrt diesen dann aus.
	 *
	 * @param move auszufuehrender Zug
	 * @throws IllegalStateException Wenn Zug nicht valide ist.
	 */
	@Override
	public void make(final Move move) throws IllegalStateException {
		boolean turnWasValid;

		// TODO: CATCH NULLPTR
		MoveType moveType = move.getType();
		if (moveType == MoveType.End) {
			// TODO
			return;
		} else if (moveType == MoveType.Surrender) {
			// TODO
			return;
		} else if (moveType == MoveType.Flower) {
			turnWasValid = flowerTurn(move.getFirstFlower(), move.getSecondFlower());
		} else if (moveType == MoveType.Ditch) {
			turnWasValid = ditchTurn(move.getDitch());
		} else {
			turnWasValid = false;
		}

		// Endcheck
		if (!turnWasValid) {
			throw new IllegalStateException("Zug war nicht valide!");
		}
	}

	// TODO: CHECK IF POSITION INSIDE SIZE
	private boolean flowerTurn(final Flower first, final Flower second) throws IllegalStateException {
		return true;
	}

	private boolean ditchTurn(final Ditch ditch) {
		// TODO
		return true;
	}

	/**
	 * Gibt den dazugehoerigen Viewer der Klasse BoardViewer zurueck.
	 *
	 * @return den dazugehoerigen Viewer
	 */
	@Override
	public Viewer viewer() {
		return null;
	}
}
