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

	private MainBoardViewer viewer = new MainBoardViewer();


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
			for (int j = 1; j <= this.size - (i - 1); j++) {
				flowers[insertPosition] = (new Flower(
					new Position(i, j),
					new Position(i + 1, j),
					new Position(i, j + 1)
				));
				insertPosition++;

				if (i + j <= this.size) {
					flowers[insertPosition] = (new Flower(
						new Position(i + 1, j + 1),
						new Position(i + 1, j),
						new Position(i, j + 1)
					));
					insertPosition++;
				}
			}
		}
		for (int i = 0; i < flowers.length; i++) {
			for (int j = i + 1; j < flowers.length; j++) {
				Move move = new Move(flowers[i], flowers[j]);
				playerData.get(PlayerColor.Red).legalMoves.add(move);
				playerData.get(PlayerColor.Blue).legalMoves.add(move);
			}
		}
		updateValidMoves();
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
		if (!viewer.getPossibleMoves().contains(move)) {
			throw new IllegalStateException("Illegaler Zug");
		}
		// Ist es best practise nicht null zu checken weil es literally unmoeglich ist?
		switch (move.getType()) {
			case Ditch:
				playerData.get(currentPlayer).ditches.add(move.getDitch());
			case Flower:
				HashSet<Flower> temp = playerData.get(currentPlayer).flowers;
				temp.add(move.getFirstFlower());
				temp.add(move.getSecondFlower());
			case End:
				// TODO
				return;
			case Surrender:
				// TODO
				return;
		}

		updateValidMoves();
	}

	void updateValidMoves() {

	}

	/**
	 * Gibt den dazugehoerigen Viewer der Klasse BoardViewer zurueck.
	 *
	 * @return den dazugehoerigen Viewer
	 */
	@Override
	public Viewer viewer() {
		return new MainBoardViewer();
	}

	/**
	 * Ein Viewer auf das MainBoard.
	 */
	private class MainBoardViewer implements Viewer {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public HashSet<Ditch> getDitches(PlayerColor color) {
			return new HashSet<>(playerData.get(color).ditches);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public HashSet<Flower> getFlowers(PlayerColor color) {
			return new HashSet<>(playerData.get(color).flowers);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getPoints(PlayerColor color) {
			// TODO!
			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public HashSet<Move> getPossibleMoves() {
			return new HashSet<>(playerData.get(currentPlayer).legalMoves);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getSize() {
			return size;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Status getStatus() {
			return currentStatus;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PlayerColor getTurn() {
			return currentPlayer;
		}
	}
}
