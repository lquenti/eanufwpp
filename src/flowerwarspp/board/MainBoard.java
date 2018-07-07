package flowerwarspp.board;

import flowerwarspp.preset.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
	MoveSet legalMoves = new MoveSet();
	/**
	 * Der aktuelle Punktestand
	 */
	int currentScore = 0;
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
		this.size = size;

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

		Move surrenderMove = new Move(MoveType.Surrender);
		playerData.get(PlayerColor.Red).legalMoves.add(surrenderMove);
		playerData.get(PlayerColor.Blue).legalMoves.add(surrenderMove);
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
		// TODO: Score aktualisieren
		if (currentStatus != Status.Ok) {
			throw new IllegalStateException("Das Spielbrett kann keine Züge mehr annehmen!");
		}
		if (!playerData.get(currentPlayer).legalMoves.contains(move)) {
			currentStatus = Status.Illegal;
			return;
		}
		switch (move.getType()) {
			case Ditch:
				playerData.get(currentPlayer).ditches.add(move.getDitch());
				updateValidMoves(move.getDitch());
				break;
			case Flower:
				playerData.get(currentPlayer).flowers.add(move.getFirstFlower());
				playerData.get(currentPlayer).flowers.add(move.getSecondFlower());
				updateValidMoves(new Flower[]{move.getFirstFlower(), move.getSecondFlower()});
				break;
			case End:
				// TODO: Ueberpruefen
				int r = playerData.get(PlayerColor.Red).currentScore, b = playerData.get(PlayerColor.Blue).currentScore;
				currentStatus = r > b ? Status.RedWin : r < b ? Status.BlueWin : Status.Draw;
				return;
			case Surrender:
				// TODO: Ueberpruefen
				currentStatus = (currentPlayer == PlayerColor.Red) ? Status.BlueWin : Status.RedWin;
				return;
		}

		for (PlayerData player : playerData.values()) {
			if (player.legalMoves.getFlowerMoves().isEmpty() && !player.legalMoves.getDitchMoves().isEmpty()) {
				player.legalMoves.add(new Move(MoveType.End));
			}
		}

		currentPlayer = oppositePlayer;
		oppositePlayer = (currentPlayer == PlayerColor.Red) ? PlayerColor.Blue : PlayerColor.Red;
	}

	// TODO: Am Ende Exception rausnehmen
	private void updateValidMoves(final Flower[] fs) {
		for (Flower f : fs) {
			// Gesetzte Flowers für alle verbieten
			for (PlayerData player : playerData.values()) {
				player.legalMoves.removeMovesContaining(f);
			}

			// Gartencheck
			for (Collection<Flower> bed : getBedsNear(f, 4, currentPlayer)) {
				updateValidMovesForBed(bed);
			}

			// Ditchchecks:
			//  - Ditches halt checken
			//  - Diese duerfen nicht an Blumen anliegen
			//  - Ditch liegt noch nicht auf
			generateNewDitches(getImportantDitches(f));
		}
	}

	// TODO: Checken ob performant
	private HashSet<Collection<Flower>> getBedsNear(Flower flower, int radius, PlayerColor player) {
		HashSet<Collection<Flower>> result = new HashSet<>();
		if (playerData.get(currentPlayer).flowers.contains(flower)) {
			result.add(getFlowerBed(flower));
		}
		if (radius != 0) {
			for (Flower neighbor : getDirectNeighbors(flower)) {
				result.addAll(getBedsNear(neighbor, radius - 1, player));
			}
		}
		return result;
	}

	private void updateValidMovesForBed(final Collection<Flower> bed) {
		if (bed.size() == 4) { // Fuer aktuelles Bed
			for (Flower bedNeighbor : getAllNeighbors(bed)) {
				playerData.get(currentPlayer).legalMoves.removeMovesContaining(bedNeighbor);
			}
			return;
		}
		for (Flower bedNeighbor : getDirectNeighbors(bed)) {
			// Dann bereits bearbeitet
			if (!playerData.get(currentPlayer).legalMoves.containsMovesContaining(bedNeighbor)) {
				continue;
			}
			playerData.get(currentPlayer).flowers.add(bedNeighbor);
			Collection<Flower> resultingBed = getFlowerBed(bedNeighbor);
			if (!isLegalBed(resultingBed, currentPlayer)) { // Neue Kombination gefaehrlich
				playerData.get(currentPlayer).legalMoves.removeMovesContaining(bedNeighbor);
			} else if (resultingBed.size() == 4) { // ILLEGAL, Bed raushauen TODO: exkludieren in eigene Fkt
				for (Flower secondBedNeighbor : getAllNeighbors(resultingBed)) {
					playerData.get(currentPlayer).legalMoves.remove(
							new Move(bedNeighbor, secondBedNeighbor)
					);
				}
			} else {
				for (Flower secondBedNeighbor : getDirectNeighbors(resultingBed)) {
					if (!playerData.get(currentPlayer).legalMoves.containsMovesContaining(secondBedNeighbor)) {
						continue;
					}
					playerData.get(currentPlayer).flowers.add(secondBedNeighbor);
					if (!isLegalBed(getFlowerBed(secondBedNeighbor), currentPlayer)) {
						playerData.get(currentPlayer).legalMoves.remove(
								new Move(bedNeighbor, secondBedNeighbor)
						);
					}
					playerData.get(currentPlayer).flowers.remove(secondBedNeighbor);
				}
			}
			playerData.get(currentPlayer).flowers.remove(bedNeighbor);
		}
	}

	// TODO: Refactor
	private LinkedList<Ditch> getImportantDitches(final Flower newFlower) {
		LinkedList<Ditch> res = new LinkedList<>();
		LinkedList<Flower> neighbors = getAllNeighbors(newFlower);
		for (Flower f : neighbors) {
			Position[] ps = getPositions(f);
			int n = ps.length;
			for (int i=0; i<=n; i++) {
				if (getPositionNeighbors(ps[i%n]).stream().anyMatch(neighbors::contains) &&
						Arrays.asList(getPositions(newFlower)).contains(ps[(i+1)%n])
						||
						getPositionNeighbors(ps[(i+1)%n]).stream().anyMatch(neighbors::contains) &&
								Arrays.asList(getPositions(newFlower)).contains(ps[i%n])) {
					res.add(new Ditch(ps[i%n], ps[(i+1)%n]));
				}
			}
		}
		return res;
	}

	private void generateNewDitches(final Collection<Ditch> ds) {
		//if (getDirectNeighbors(d).stream().noneMatch(df -> getFlowerColor(df) != null)) {
		for (Ditch d : ds) {
			if (getDirectNeighbors(d).stream().anyMatch(df -> getFlowerColor(df) != null) ||
					getDitchColor(d) != null) {
				continue;
			}
			playerData.get(currentPlayer).legalMoves.add(new Move(d));
		}
	}

	private Position[] getPositions(Flower f) {
		return new Position[]{f.getFirst(), f.getSecond(), f.getThird()};
	}

	private HashSet<Flower> getPositionNeighbors(Position p) {
		// TODO: Durch Vektormagie replacen
		return Arrays.stream(allFlowers)
				.filter(f -> (Arrays.asList(getPositions(f)).contains(p)))
				.collect(Collectors.toCollection(HashSet::new));

	}

	private boolean isLegalBed(final Collection<Flower> bed, final PlayerColor player) {
		return bed.size() < 4
				|| bed.size() == 4
				&& Collections.disjoint(getAllNeighbors(bed), playerData.get(player).flowers);
	}

	private PlayerColor getDitchColor(final Ditch d) {
		for (Map.Entry<PlayerColor, PlayerData> entry : playerData.entrySet()) {
			if (entry.getValue().ditches.contains(d)) {
				return entry.getKey();
			}
		}
		return null;
	}

	private PlayerColor getFlowerColor(final Flower f) {
		for (Map.Entry<PlayerColor, PlayerData> entry : playerData.entrySet()) {
			if (entry.getValue().flowers.contains(f)) {
				return entry.getKey();
			}
		}
		return null;
	}


	private HashSet<Flower> getFlowerBed(final Flower f) {
		PlayerColor flowerColor = getFlowerColor(f);
		if (flowerColor == null) {
			return null;
		}

		HashSet<Flower> result = new HashSet<>();
		Stack<Flower> toVisit = new Stack<>();
		toVisit.add(f);

		while (!toVisit.empty()) {
			Flower visiting = toVisit.pop();
			for (Flower neighbor : getDirectNeighbors(visiting)) {
				if (!result.contains(neighbor) && playerData.get(flowerColor).flowers.contains(neighbor)) {
					toVisit.add(neighbor);
				}
				result.add(visiting);
			}
		}

		return result;
	}

	private LinkedList<Flower> getDirectNeighbors(final Flower f) {
		LinkedList<Flower> result = new LinkedList<>();
		Position[] nodes = getPositions(f);
		for (int i = 0; i < 3; i++) {
			try {
				Position third = new Position(
					nodes[i % 3].getColumn() + nodes[(i + 1) % 3].getColumn() - nodes[(i + 2) % 3].getColumn(),
					nodes[i % 3].getRow() + nodes[(i + 1) % 3].getRow() - nodes[(i + 2) % 3].getRow()
				);
				Flower neighbor = new Flower(nodes[i % 3], nodes[(i + 1) % 3], third);
				if (isOnBoard(neighbor)) {
					result.add(neighbor);
				}
			} catch (IllegalArgumentException e) {}
		}
		return result;
	}

	private LinkedList<Flower> getAllNeighbors(final Flower f) { //  n := distance
		LinkedList<Flower> result = getDirectNeighbors(f);
		Position[] nodes = getPositions(f);
		Position lastPoint = null;
		// Über die Positionen iterieren, die das Dreieck umgeben.
		for (int i = 0; i <= 9; i++) {
			try {
				Position point = new Position(
					nodes[i / 3 % 3].getColumn() + nodes[(i + 1) / 3 % 3].getColumn() - nodes[((i + 2) / 3 + 1) % 3].getColumn(),
					nodes[i / 3 % 3].getRow() + nodes[(i + 1) / 3 % 3].getRow() - nodes[((i + 2) / 3 + 1) % 3].getRow()
				);
				if (lastPoint != null) {
					Flower neighbor = new Flower(nodes[i / 3 % 3], lastPoint, point);
					if (isOnBoard(neighbor)) {
						result.add(neighbor);
					}
				}
				lastPoint = point;
			} catch (IllegalArgumentException e) {
				lastPoint = null;
			}
		}
		return result;
	}

	private HashSet<Flower> getDirectNeighbors(final Collection<Flower> bed) {
		HashSet<Flower> result = new HashSet<>();
		for (Flower flower : bed) {
			for (Flower neighbor : getDirectNeighbors(flower)) {
				if (!bed.contains(neighbor)) {
					result.add(neighbor);
				}
			}
		}
		return result;
	}

	private HashSet<Flower> getAllNeighbors(final Collection<Flower> bed) {
		HashSet<Flower> result = new HashSet<>();
		for (Flower flower : bed) {
			for (Flower neighbor : getAllNeighbors(flower)) {
				if (!bed.contains(neighbor)) {
					result.add(neighbor);
				}
			}
		}
		return result;
	}

	// TODO: Refactor (was eigentlich für alles gilt)
	private LinkedList<Flower> getDirectNeighbors(final Ditch ditch) {
		LinkedList<Flower> result = new LinkedList<>();
		Position[] nodes = {ditch.getFirst(), ditch.getSecond()};
		try {
			Position third = new Position(
				nodes[1].getColumn() + nodes[1].getRow() - nodes[0].getRow(),
				nodes[0].getRow() - nodes[1].getColumn() + nodes[0].getColumn()
				);
			Flower neighbor = new Flower(nodes[0], nodes[1], third);
			if (isOnBoard(neighbor)) {
				result.add(neighbor);
			}
		} catch (IllegalArgumentException e) {}
		try {
			Position third = new Position(
				nodes[0].getColumn() - nodes[1].getRow() + nodes[0].getRow(),
				nodes[1].getRow() + nodes[1].getColumn() - nodes[0].getColumn()
				);
			Flower neighbor = new Flower(nodes[0], nodes[1], third);
			if (isOnBoard(neighbor)) {
				result.add(neighbor);
			}
		} catch (IllegalArgumentException e) {}
		return result;
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
		for (Flower f : invalids) {
			for (PlayerData player : playerData.values()) {
				player.legalMoves.removeMovesContaining(f);
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
		return position != null
				&& position.getColumn() > 0 && position.getRow() > 0
				&& position.getColumn() + position.getRow() < size + 3;
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
		public Set<Ditch> getDitches(PlayerColor color) {
			return Collections.unmodifiableSet(playerData.get(color).ditches);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Set<Flower> getFlowers(PlayerColor color) {
			return Collections.unmodifiableSet(playerData.get(color).flowers);
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
		public Set<Move> getPossibleMoves() {
			return Collections.unmodifiableSet(playerData.get(currentPlayer).legalMoves);
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

		// TODO: REFACTOR
		@Override
		public LinkedList<Flower> getDirectNeighbors(Flower f) {
			return MainBoard.this.getDirectNeighbors(f);
		}

		@Override
		public LinkedList<Flower> getAllNeighbors(Flower f) {
			return MainBoard.this.getAllNeighbors(f);
		}

		@Override
		public boolean possibleMovesContains(Move move) {
			return playerData.get(currentPlayer).legalMoves.contains(move);
		}

		@Override
		public boolean possibleMovesContainsMovesContaining(Flower flower) {
			return playerData.get(currentPlayer).legalMoves.containsMovesContaining(flower);
		}

		@Override
		public Collection<Move> getPossibleFlowerMoves() {
			return playerData.get(currentPlayer).legalMoves.getFlowerMoves();
		}

		@Override
		public Collection<Flower> getPossibleFlowers() {
			return playerData.get(currentPlayer).legalMoves.getFlowers();
		}

		@Override
		public Collection<Flower> getFlowersCombinableWith(Flower flower) {
			return playerData.get(currentPlayer).legalMoves.getFlowersCombinableWith(flower);
		}

		@Override
		public Collection<Move> getPossibleMovesContaining(Flower flower) {
			return playerData.get(currentPlayer).legalMoves.getMovesContaining(flower);
		}

		@Override
		public Collection<Move> getPossibleDitchMoves() {
			return playerData.get(currentPlayer).legalMoves.getDitchMoves();
		}

		@Override
		public PlayerColor getFlowerColor(Flower flower) {
			return MainBoard.this.getFlowerColor(flower);
		}
	}

	public static void main(String[] args) {
		MainBoard board = new MainBoard(30);
	}
}
