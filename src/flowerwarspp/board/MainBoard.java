package flowerwarspp.board;

import flowerwarspp.preset.*;

import java.util.*;

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
	 * Der Spieler, welcher aktuell nicht am Zug ist
	 */
	private PlayerColor oppositePlayer = PlayerColor.Blue; // Sonst macht man es x mal redundant beim checken

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
		if (!playerData.get(currentPlayer).legalMoves.contains(move)) {
			throw new IllegalStateException("Illegaler Zug");
		}
		// Ist es best practise nicht null zu checken weil es literally unmoeglich ist?
		// (Falls es nicht so ist Kommentar einfach removen)
		switch (move.getType()) {
			case Ditch:
				playerData.get(currentPlayer).ditches.add(move.getDitch());
				updateValidMoves(move.getDitch());
			case Flower:
				updateValidMoves(new Flower[]{move.getFirstFlower(), move.getSecondFlower()});
			case End:
				// TODO
				return;
			case Surrender:
				// TODO
				return;
		}
		currentPlayer = oppositePlayer;
		oppositePlayer = (currentPlayer == PlayerColor.Red) ? PlayerColor.Blue : PlayerColor.Red;
	}

	// TODO: Am Ende Exception rausnehmen
	private void updateValidMoves(Flower[] fs) {
        /*
        Was aktuell gemacht wird: (als Referenz zum erweitern (Kommentar kommt bei Abgabe raus))
            - Gaertencheck
            - Exkludieren von Gaertenabstaenden
            - Moves fuer andere Farbe exkludieren
            - Grabenerstellung:
                - Gegnerische Graeben entfernen falls geblockt durch eigene Blume
                - Graben erlauben falls direkte Verbindung UND kein existierender Graben teilt (kann das der Fall sein nach Ditchchecks?)
         */
		// TODO: Ist es noetig zu checken ob Flower in valider Spielfeldrange ist?
		// Idee: Gaerten einzeln speichern um Laufzeit zu verbessern da man nur Aussenbereiche testen muss und diese immutable sind
		for (Flower f : fs) {
			// Gesetzte Flowern als valider Zug fuer andere exkludieren
			for (Flower oppositeF : playerData.get(oppositePlayer).flowers) {
				Move move = new Move(f, oppositeF);
				playerData.get(oppositePlayer).legalMoves.remove(move);
			}

			// Gartencheck
			int bedsize = bedSize(f);
			if (bedsize == 4) {
				for (Flower invalid : getAllNeighbours(f)) {
				}

			} else if (bedsize > 4) { // TODO: In Productive entfernen
				System.out.println("DEBUG MESSAGE: BEDSIZEALGO BROKEN");
			}
		}
	}

	private int bedSize(Flower f) {
		int ret = 1;
		ArrayList<Flower> fs = getDirectNeighbours(f);
		for (Flower var : fs) {
			if (playerData.get(currentPlayer).flowers.contains(var)) {
				ret += bedSize(var);
			}
		}
		return ret;
	}

	private ArrayList<Flower> getDirectNeighbours(Flower f) {
		ArrayList<Flower> ret = new ArrayList<>();
		// Da wir keinen positions Array haben und Vererbung der Flowerklasse wohl overkill waere
		Position[] nodes = new Position[]{f.getFirst(), f.getSecond(), f.getThird()};
		Flower temp;
		int n = 2;
		Position third;
		for (int i = 0; i < nodes.length - 2; i++) {
			Position thirdPos;
			if (nodes[i].getColumn() == nodes[(i + 1) % n].getColumn()) {
				Position above = new Position(nodes[i % n].getColumn(), nodes[(i + 1) % n].getRow() + 1);
				third = (nodes[(i + 2) % n] == above) ? new Position(nodes[(i + 1) % n].getColumn(), nodes[(i + 1) % n].getRow() - 1) : above;
			} else {
				Position left = new Position(nodes[(i + 1) % n].getColumn() - 1, nodes[(i + 1) % n].getRow());
				third = (nodes[(i + 2) % n] == left) ? new Position(nodes[i % n].getColumn() + 1, nodes[i % n].getRow()) : left;
			}
			ret.add(new Flower(nodes[i % n], nodes[(i + 1) % n], third));
		}
		return ret;
	}

	private ArrayList<Flower> getAllNeighbours(Flower f) {
		ArrayList<Flower> ret = new ArrayList<>();

		return ret;
	}

	private void updateValidMoves(Ditch d) {
        /*
        Was aktuell gemacht wird:
            - Ueber und unter Graben Flower entvalidieren
            - Andere Graebenmoeglichkeiten entvalidieren falls diese sich eine Position teilen
         */
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
