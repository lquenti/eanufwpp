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
	HashSet<Flower> flowers;
	/**
	 * Die Gräben, die der Spieler gesetzt hat.
	 */
	HashSet<Ditch> ditches;
	/**
	 * Die legalen Züge, die der Spieler noch machen kann.
	 */
	MoveSet legalMoves;
	/**
	 * Der aktuelle Punktestand
	 */
	int currentScore;

	public PlayerData() {
		flowers = new HashSet<>();
		ditches = new HashSet<>();
		legalMoves = new MoveSet();
		currentScore = 0;
	}

	public PlayerData(PlayerData original) {
		flowers = new HashSet<>(original.flowers);
		ditches = new HashSet<>(original.ditches);
		legalMoves = new MoveSet(original.legalMoves);
		currentScore = original.currentScore;
	}
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

	public MainBoard(MainBoard original) {
		size = original.size;
		currentPlayer = original.currentPlayer;
		oppositePlayer = original.oppositePlayer;
		currentStatus = original.currentStatus;

		for (Map.Entry<PlayerColor, PlayerData> entry : original.playerData.entrySet()) {
			playerData.put(entry.getKey(), new PlayerData(entry.getValue()));
		}

		allFlowers = Arrays.copyOf(original.allFlowers, original.allFlowers.length);
	}

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
			if (!isLegalBed(resultingBed, currentPlayer)) {
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

	private HashSet<Ditch> getImportantDitches(final Flower newFlower) {
		HashSet<Ditch> res = new HashSet<>();
		for (Position p : getPositions(newFlower)) {
			res.addAll(getDitchesAround(p));
		}

		Position[] flowerPositions = getPositions(newFlower);
		HashSet<Ditch> tobeRemoved = new HashSet<>(); // Um ConcurrentModificationException zu umgehen
		for (Ditch d : res) {
			if (Arrays.asList(flowerPositions).containsAll(Arrays.asList(d.getFirst(), d.getSecond()))) {
				tobeRemoved.add(d);
			}

			Position p = (Arrays.asList(flowerPositions).contains(d.getFirst())) ? d.getSecond() : d.getFirst();
			if (getFlowersAround(p).stream().noneMatch(f -> getFlowerColor(f) != null) ||
					getDirectNeighbors(d).stream().anyMatch(f -> getFlowerColor(f) != null)) {
				tobeRemoved.add(d);
			}

			/* TODO: Gehoert in Ditch lol (glaube ich)
			*/
		}
		res.removeAll(tobeRemoved);
		return res;
	}

	private void generateNewDitches(final Collection<Ditch> ds) {
		for (Ditch d : ds) {
			playerData.get(currentPlayer).legalMoves.add(new Move(d));
		}
	}

	private Position[] getPositions(Flower f) {
		return new Position[]{f.getFirst(), f.getSecond(), f.getThird()};
	}

	private Position[] getPositions(Ditch d) {
		return new Position[]{d.getFirst(), d.getSecond()};
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
			} catch (IllegalArgumentException e) {
			}
		}
		return result;
	}

	private LinkedList<Flower> getAllNeighbors(final Flower f) {
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
		} catch (IllegalArgumentException e) {
		}
		try {
			Position third = new Position(
					nodes[0].getColumn() - nodes[1].getRow() + nodes[0].getRow(),
					nodes[1].getRow() + nodes[1].getColumn() - nodes[0].getColumn()
			);
			Flower neighbor = new Flower(nodes[0], nodes[1], third);
			if (isOnBoard(neighbor)) {
				result.add(neighbor);
			}
		} catch (IllegalArgumentException e) {
		}
		return result;
	}

	private LinkedList<Position> getPositionsAround(final Position center) {
		LinkedList<Position> result = new LinkedList<>();
		// Im Kreis rumgehen.
		for (int i = 0; i < 6; i++) {
			try {
				Position neighbor = new Position(
					// Folge, die so ein Bisschen wie sin ist aber nicht ganz.
					center.getColumn() + Integer.signum(((i+2)%6-2)%3),
					// Folge, die so ein Bisschen wie -cos ist aber nicht ganz.
					center.getRow() + Integer.signum((i%6-2)%3)
				);
				if (isOnBoard(neighbor)) {
					result.add(neighbor);
				}
			} catch(IllegalArgumentException e) {}
		}
		return result;
	}

	private LinkedList<Flower> getFlowersAround(final Position center) {
		LinkedList<Flower> result = new LinkedList<>();
		Deque<Position> positions = getPositionsAround(center);
		Position previousPosition = positions.getLast();
		for (Position position : positions) {
			if (getPositionsAround(position).contains(previousPosition)) {
				result.add(new Flower(center, previousPosition, position));
			}
			previousPosition = position;
		}
		return result;
	}

	private LinkedList<Ditch> getDitchesAround(final Position center) {
		LinkedList<Ditch> result = new LinkedList<>();
		for (Position position : getPositionsAround(center)) {
			result.add(new Ditch(center, position));
		}
		return result;
	}

	private void updateValidMoves(Ditch d) {
        /*
        Was aktuell gemacht wird:
            - Ueber und unter Graben Flower entvalidieren
            - Andere Graebenmoeglichkeiten entvalidieren falls diese sich eine Position teilen
         */

		// Ueber und unter Graben Flower entvalidieren
		for (Flower f : getDirectNeighbors(d)) {
			for (PlayerData player : playerData.values()) {
				player.legalMoves.removeMovesContaining(f);
			}
		}

		// Andere Grabenmoeglichkeiten entvalidieren falls diese sich eine Position teilen
		for (Position p : getPositions(d)) {
			for (Ditch samePos : getDitchesAround(p)) {
				playerData.get(currentPlayer).legalMoves.remove(new Move(samePos));
			}
		}

		// und zuletzt
		playerData.get(currentPlayer).legalMoves.remove(new Move(d));
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

		@Override
		public HashSet<Flower> getFlowerBed(final Flower flower) {
			return MainBoard.this.getFlowerBed(flower);
		}
	}

	public static void main(String[] args) {
		MainBoard board = new MainBoard(30);
	}
}
