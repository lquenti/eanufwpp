package flowerwarspp.board;

import flowerwarspp.io.BoardViewer;
import flowerwarspp.preset.*;

import java.util.EnumMap;
import java.util.HashSet;

/*
TODO: eigenen besseren Floweralgo benutzen
*/

/**
 * Verwaltungsklasse, die Daten über die gemachten und noch möglichen Züge
 * eines Spielers Speichert
 */
class PlayerData {
	/**
	 * Die Blumen, die der Spieler gesetzt hat.
	 */
	HashSet<Flower> flowers = new HashSet<>();
	/**
	 * Die Gräben, die der Spieler gesetzt hat.
	 */
	HashSet<Ditch> ditches = new HashSet<>();
	/**
	 * Die legalen Züge, die der Spieler noch machen kann.
	 */
	HashSet<Move> legalMoves = new HashSet<>();
}

/**
 * Boardimplementation. Implementiert Board und somit auch Viewable
 *
 * @author Lars Quentin
 * @version 0.1
 */
public class MainBoard implements Board {
	/**
	 * Groesse des Boards
	 */
	private final int size;

	/**
	 * Der Spieler, der aktuell am Zug ist.
	 */
	private PlayerColor currentPlayer = PlayerColor.Red;

	/**
	 * Der Aktuelle Status des Spielbretts.
	 */
	private Status currentStatus = Status.Ok;

	/**
	 * Daten über die Spieler.
	 */
	private EnumMap<PlayerColor, PlayerData> playerData = new EnumMap<>(PlayerColor.class);


	/**
	 * Konstruktor. Befuellt Board einer variablen Groesse zwischen [3;30].
	 * Falls Wert invalide wird dieser dem naechsten Element des Intervalls angepasst.
	 *
	 * @param size Groesse des Boardes.
	 */
	public MainBoard(final int size) {
		this.size = (size < 3) ? 3 : ((size > 30) ? 30 : size);

		playerData.put(PlayerColor.Red, new PlayerData());
		playerData.put(PlayerColor.Blue, new PlayerData());

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
				playerData.get(PlayerColor.Red).legalMoves.add(move);
				playerData.get(PlayerColor.Blue).legalMoves.add(move);
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
