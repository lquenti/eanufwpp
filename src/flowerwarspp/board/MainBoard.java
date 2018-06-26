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
	 * Liste mit allen möglichen Blumen.
	 */
	private final Flower[] allFlowers;

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

		allFlowers = new Flower[this.size * this.size];
		int insertPosition = 0;
		for (int i = 1; i <= this.size; i++) {
			for (int j = 1; j <= this.size - (i - 1); j++) {
				allFlowers[insertPosition] = (new Flower(
						new Position(i, j),
						new Position(i + 1, j),
						new Position(i, j + 1)
				));
				insertPosition++;

				if (i + j <= this.size) {
					allFlowers[insertPosition] = (new Flower(
							new Position(i + 1, j + 1),
							new Position(i + 1, j),
							new Position(i, j + 1)
					));
					insertPosition++;
				}
			}
		}

		for (int i = 0; i < allFlowers.length; i++) {
			for (int j = i + 1; j < allFlowers.length; j++) {
				Move move = new Move(allFlowers[i], allFlowers[j]);
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
				break;
			case Flower:
				updateValidMoves(new Flower[]{move.getFirstFlower(), move.getSecondFlower()});
				break;
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
		for (Flower f : fs) { // TODO: Extern redandant function
			// Gesetzte Flowern als valider Zug fuer andere exkludieren
			for (Flower oppositeF : playerData.get(oppositePlayer).flowers) {
				Move move = new Move(f, oppositeF);
				playerData.get(oppositePlayer).legalMoves.remove(move);
			}

			// Gartencheck
			int bedsize = getFlowerBed(f).size();
			if (bedsize == 4) {
				for (Flower invalid : getAllNeighbours(f)) {
				}

			} else if (bedsize > 4) { // TODO: In Productive entfernen
				System.out.println("DEBUG MESSAGE: BEDSIZEALGO BROKEN");
			}

			// finally
			playerData.get(currentPlayer).flowers.add(f);
		}
	}

	private PlayerColor getFlowerColor(Flower f) {
		for (Map.Entry<PlayerColor, PlayerData> entry: playerData.entrySet()) {
			if (entry.getValue().flowers.contains(f)) {
				return entry.getKey();
			}
		}
		return null;
	}

	private HashSet<Flower> getFlowerBed(Flower f) {
		PlayerColor flowerColor = getFlowerColor(f);
		if (flowerColor == null) {
			return null;
		}

		HashSet<Flower> result = new HashSet<>();
		Stack<Flower> toVisit = new Stack<>();
		toVisit.add(f);

		while (!toVisit.empty()) {
			Flower visiting = toVisit.pop();
			for (Flower neighbour : getDirectNeighbours(visiting)) {
				if (!result.contains(neighbour) && playerData.get(flowerColor).flowers.contains(neighbour)) {
					toVisit.add(neighbour);
				}
				result.add(visiting);
			}
		}

		return result;
	}

	private HashSet<Flower> getDirectNeighbours(Flower f) {
		HashSet<Flower> ret = new HashSet<>();
		Position[] nodes = {f.getFirst(), f.getSecond(), f.getThird()};
		for (int i = 0; i < 3; i++) {
			Position third = new Position(
				nodes[i%3].getColumn() + nodes[(i+1)%3].getColumn() - nodes[(i+2)%3].getColumn(),
				nodes[i%3].getRow() + nodes[(i+1)%3].getRow() - nodes[(i+2)%3].getRow()
			);
			Flower neighbour = new Flower(nodes[i % 3], nodes[(i + 1) % 3], third);
			if (isOnBoard(neighbour)) {
				ret.add(neighbour);
			}
		}
		return ret;
	}

	private HashSet<Flower> getAllNeighbours(Flower f) {
		HashSet<Flower> ret = getDirectNeighbours(f);
		// Über die Positionen iterieren, die das Dreieck umgeben.
		Position[] nodes = {f.getFirst(), f.getSecond(), f.getThird()};
		Position last = new Position(
			nodes[2].getColumn() + nodes[0].getColumn() - nodes[1].getColumn(),
			nodes[2].getRow() + nodes[0].getRow() - nodes[1].getRow()
		);
		for (int i = 0; i < 9; i++) {
			Position third = new Position(
				nodes[i/3].getColumn() + nodes[(i+1)/3%3].getColumn() - nodes[((i+2)/3+1)%3].getColumn(),
				nodes[i/3].getRow() + nodes[(i+1)/3%3].getRow() - nodes[((i+2)/3+1)%3].getRow()
			);
			Flower neighbour = new Flower(nodes[i/3], last, third);
			if (isOnBoard(neighbour)) {
				ret.add(neighbour);
			}
			last = third;
		}
		return ret;
	}

	// TODO: IDEE, Array aus {column, row} und dann einfach Ein if
	private void updateValidMoves(Ditch d) {
        /*
        Was aktuell gemacht wird:
            - Ueber und unter Graben Flower entvalidieren
            - Andere Graebenmoeglichkeiten entvalidieren falls diese sich eine Position teilen
         */

		// Ueber und unter Graben Flower entvalidieren
		Flower[] invalids = new Flower[2];
		if (d.getFirst().getRow() == d.getSecond().getRow()) { // Horizontal
			invalids[0] = new Flower(
					d.getFirst(),
					d.getSecond(),
					new Position(d.getFirst().getColumn(), d.getFirst().getRow() + 1)
			);
			invalids[1] = new Flower(
					d.getFirst(),
					d.getSecond(),
					new Position(d.getSecond().getColumn(), d.getSecond().getRow() - 1)
			);
		} else { // Vertikal
			invalids[0] = new Flower(
					d.getFirst(),
					d.getSecond(),
					new Position(d.getSecond().getColumn() - 1, d.getSecond().getRow())
			);
			invalids[1] = new Flower(
					d.getFirst(),
					d.getSecond(),
					new Position(d.getFirst().getColumn() + 1, d.getSecond().getRow())
			);
		}
		for (Flower f : invalids) { // TODO: Extern redundant function
			for (Flower newInvalid : playerData.get(currentPlayer).flowers) {
				Move move = new Move(f, newInvalid);
				playerData.get(currentPlayer).legalMoves.remove(move);
			}
		}

		// Andere Grabenmoeglichkeiten entvalidieren falls diese sich eine Position teilen
		Ditch[] invalidD = new Ditch[4];
		if (d.getFirst().getRow() == d.getSecond().getRow()) { // Horizontal
			Position above = new Position(d.getFirst().getColumn(), d.getFirst().getRow() + 1);
			Position below = new Position(d.getSecond().getColumn(), d.getSecond().getRow() - 1);
			invalidD[0] = new Ditch(d.getFirst(), above);
			invalidD[1] = new Ditch(d.getSecond(), above);
			invalidD[2] = new Ditch(d.getFirst(), below);
			invalidD[3] = new Ditch(d.getSecond(), below);
		} else { // Vertikal
			Position left = new Position(d.getSecond().getColumn() - 1, d.getSecond().getRow());
			Position right = new Position(d.getFirst().getColumn() + 1, d.getFirst().getRow());
			invalidD[0] = new Ditch(d.getFirst(), left);
			invalidD[1] = new Ditch(d.getSecond(), left);
			invalidD[2] = new Ditch(d.getFirst(), right);
			invalidD[3] = new Ditch(d.getSecond(), right);
		}
		for (Ditch var : invalidD) {
			// Egal ob das drin ist oder nicht
			playerData.get(currentPlayer).legalMoves.remove(new Move(var));
		}
		// und zuletzt
		playerData.get(currentPlayer).ditches.add(d);
	}

	/*
	 * Prüft, ob eine Position sich auf diesem Board befindet.
	 * @return ob die Position auf dem Board ist
	 */
	private boolean isOnBoard(Position position) {
		return position != null && position.getColumn() + position.getRow() < size + 3;
	}

	/*
	 * Prüft, ob eine Flower sich auf diesem Board befindet.
	 * @return ob die Flower auf dem Board ist
	 */
	private boolean isOnBoard(Flower flower) {
		return flower != null && isOnBoard(flower.getThird());
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
