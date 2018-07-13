package flowerwarspp.board;

import flowerwarspp.preset.*;
import flowerwarspp.util.log.Log;
import flowerwarspp.util.log.LogLevel;
import flowerwarspp.util.log.LogModule;

import java.util.*;

/**
 * Verwaltungsklasse, die Daten über die gemachten und noch möglichen Züge
 * eines Spielers speichert.
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
	 * Der aktuelle Punktestand des Spielers.
	 */
	int currentScore;

	/**
	 * Konstruktor. Initialisiert alle Objektattribute.
	 */
	PlayerData() {
		flowers = new HashSet<>();
		ditches = new HashSet<>();
		legalMoves = new MoveSet();
		currentScore = 0;
	}

	/**
	 * Kopierkonstruktor. Übernimmt alle Werte des uebergebenen PlayerData-Objekts.
	 * @param original PlayerData-Objekt von dem die Werte übernommen werden.
	 */
	PlayerData(PlayerData original) {
		flowers = new HashSet<>(original.flowers);
		ditches = new HashSet<>(original.ditches);
		legalMoves = new MoveSet(original.legalMoves);
		currentScore = original.currentScore;
	}
}

/**
 * Implementation des Spielbretts.
 */
public class MainBoard implements Board {
	/**
	 * Größe des Boards.
	 */
	private final int size;

	/**
	 * Der Spieler, der aktuell am Zug ist.
	 */
	private PlayerColor currentPlayer = PlayerColor.Red;

	/**
	 * Der Spieler, der aktuell nicht am Zug ist.
	 */
	private PlayerColor oppositePlayer = PlayerColor.Blue;

	/**
	 * Der aktuelle Status des Spielbretts.
	 */
	private Status currentStatus = Status.Ok;

	/**
	 * Daten über die Spieler.
	 */
	private EnumMap<PlayerColor, PlayerData> playerDataSet = new EnumMap<>(PlayerColor.class);

	/**
	 * Liste mit allen möglichen Blumen.
	 */
	private final Flower[] allFlowers;

	/**
	 * Erzeugt ein neues Spielbrett mit der angegebenen Größe.
	 *
	 * @param size Größe des Boardes.
	 */
	public MainBoard(final int size) {
		this.size = size;
		allFlowers = new Flower[this.size * this.size];
		initBoard();
	}

	/**
	 * Erzeugt eine Kopie eines vorhandenen MainBoards.
	 *
	 * @param original Das Brett, das kopiert werden soll.
	 */
	public MainBoard(final MainBoard original) {
		size = original.size;
		currentPlayer = original.currentPlayer;
		oppositePlayer = original.oppositePlayer;
		currentStatus = original.currentStatus;

		for (Map.Entry<PlayerColor, PlayerData> entry : original.playerDataSet.entrySet()) {
			playerDataSet.put(entry.getKey(), new PlayerData(entry.getValue()));
		}

		allFlowers = Arrays.copyOf(original.allFlowers, original.allFlowers.length);
	}

	/**
	 * Initalisiert das Board.
	 */
	private void initBoard() {
		// Spielerdaten anlegen
		playerDataSet.put(PlayerColor.Red, new PlayerData());
		playerDataSet.put(PlayerColor.Blue, new PlayerData());

		// Alle Blumen erzeugen.
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

		// Züge für alle möglichen Kombinationen von Blumen erzeugen und für alle Spieler erlauben.
		for (int i = 0; i < allFlowers.length; i++) {
			for (int j = i + 1; j < allFlowers.length; j++) {
				Move move = new Move(allFlowers[i], allFlowers[j]);
				playerDataSet.get(PlayerColor.Red).legalMoves.add(move);
				playerDataSet.get(PlayerColor.Blue).legalMoves.add(move);
			}
		}

		// Aufgeben für beide Spieler erlauben.
		Move surrenderMove = new Move(MoveType.Surrender);
		playerDataSet.get(PlayerColor.Red).legalMoves.add(surrenderMove);
		playerDataSet.get(PlayerColor.Blue).legalMoves.add(surrenderMove);
	}

	/**
	 * {@inheritDoc}
	 * Verifiziert Zug, führt diesen aus und berechnet die Punktzahl.
	 *
	 * @param move Auszuführender Zug
	 * @throws IllegalStateException falls der Zug nicht erlaubt ist.
	 */
	@Override
	public void make(final Move move) throws IllegalStateException {
		Log.log(LogLevel.DEBUG, LogModule.BOARD, "Status at beginning of make: " + currentStatus);
		if (currentStatus != Status.Ok) {
			throw new IllegalStateException("Das Spielbrett kann keine Züge mehr annehmen!");
		}
		if (!playerDataSet.get(currentPlayer).legalMoves.contains(move)) {
			currentStatus = Status.Illegal;
			return;
		}
		switch (move.getType()) {
			case Ditch:
				playerDataSet.get(currentPlayer).ditches.add(move.getDitch());
				updateAfterMove(move.getDitch());
				break;
			case Flower:
				playerDataSet.get(currentPlayer).flowers.add(move.getFirstFlower());
				playerDataSet.get(currentPlayer).flowers.add(move.getSecondFlower());
				updateAfterMove(new Flower[]{move.getFirstFlower(), move.getSecondFlower()});
				break;
			case End:
				endGame();
				return;
			case Surrender:
				endGame(oppositePlayer);
				return;
		}

		// Überprüfen, ob ein Spieler das Spiel beenden kann.
		for (PlayerData playerData : playerDataSet.values()) {
			if (playerData.legalMoves.getFlowerMoves().isEmpty()) {
				playerData.legalMoves.add(new Move(MoveType.End));
			}
		}

		// Überprüfen, ob das Spiel vorbei ist.
		if (playerDataSet.get(oppositePlayer).legalMoves.getFlowerMoves().isEmpty() &&
		    playerDataSet.get(oppositePlayer).legalMoves.getDitchMoves().isEmpty()) {
			Log.log(LogLevel.DEBUG, LogModule.BOARD, "Ending game because next Player can't make more moves");
			endGame();
			return;
		}

		PlayerColor t = currentPlayer;
		currentPlayer = oppositePlayer;
		oppositePlayer = t;
	}

	/**
	 * Beendet das Spiel. Unentschieden wenn Parameter null ist.
	 *
	 * @param winner Die Farbe des Gewinners.
	 */
	private void endGame(final PlayerColor winner) {
		if (winner == null) {
			currentStatus = Status.Draw;
			return;
		}
		switch (winner) {
			case Red: currentStatus = Status.RedWin; break;
			case Blue: currentStatus = Status.BlueWin; break;
			default: currentStatus = Status.Draw;
		}
	}

	/**
	 * Beendet das Spiel und bestimmt den Gewinner sofern keine Züge mehr möglich sind.
	 */
	private void endGame() {
		int redPoints = playerDataSet.get(PlayerColor.Red).currentScore;
		Log.log(LogLevel.DEBUG, LogModule.BOARD, "Red player has " + redPoints + "points at end of game.");
		int bluePoints = playerDataSet.get(PlayerColor.Blue).currentScore;
		Log.log(LogLevel.DEBUG, LogModule.BOARD, "Blue player has " + bluePoints + "points at end of game.");

		if (redPoints > bluePoints) {
			endGame(PlayerColor.Red);
		} else if (bluePoints > redPoints) {
			endGame(PlayerColor.Blue);
		} else {
			endGame(null);
		}
	}


	/**
	 * Wertet das Spielbrett nach gesetzten Blumenzug aus.
	 * Hierzu gehören das Aktualisieren der möglichen Blumen, Gräben und der aktuellen Punktzahl
	 *
	 * @param flowers die gesetzten Blumen
	 */
	private void updateAfterMove(final Flower[] flowers) {
		for (Flower flower : flowers) {
			// Gesetzte Blumen für alle verbieten
			for (PlayerData playerData: playerDataSet.values()) {
				playerData.legalMoves.removeMovesContaining(flower);
			}

			// Durch diese Blume ungültig gewordene Blumenzüge verbieten
			for (Collection<Flower> bed : getBedsNear(flower, 4, currentPlayer)) {
				updateValidMovesForBed(bed);
			}

			// Gegebenenfalls möglich gewordene Grabenzüge erlauben
			generateNewDitches(flower);

			// Verbieten der Gräben, die eine Kante mit der Blume gemeinsam haben.
			for (Ditch edgeDitch : getEdgeDitches(flower)) {
				for (PlayerData playerData : playerDataSet.values()) {
					playerData.legalMoves.remove(new Move(edgeDitch));
				}
			}
		}

		// Punktestand aktualisieren
		playerDataSet.get(currentPlayer).currentScore += updateScore(flowers[0]);
		if (!getBedChain(flowers[0]).contains(getFlowerBed(flowers[1]))) {
			playerDataSet.get(currentPlayer).currentScore += updateScore(flowers[1]);
		}
	}

	/**
	 * Gibt die Gräben zurück, welche die Blume bilden.
	 *
	 * @param flower Blume wessen Gräben zurückgegeben werden.
	 * @return Die Gräben aus welchen die Blume besteht.
	 */
	private Ditch[] getEdgeDitches(final Flower flower) {
		Ditch[] result = new Ditch[3];
		Position[] positions = getPositions(flower);
		for ( int i = 0; i < positions.length; i++ ) {
			result[i] = new Ditch(positions[i], positions[(i+1)%positions.length]);
		}
		return result;
	}

	/**
	 * Gibt die Beete eines Spielers innerhalb einer Reichweite an Blumenabständen zurück.
	 * Hierbei ist die Reichweite nur durch direkte Verbindungen definiert.
	 *
	 * @param flower Blume von welcher aus gesucht wird.
	 * @param radius Radius in welchen von der Blume aus gesucht wird.
	 * @param player Spieler wessen Beeten zurückgegeben werden.
	 * @return Menge an Beeten des Spielers innerhalb des Radius.
	 */
	private HashSet<Collection<Flower>> getBedsNear(final Flower flower, final int radius, final PlayerColor player) {
		HashSet<Collection<Flower>> result = new HashSet<>();
		if (playerDataSet.get(player).flowers.contains(flower)) {
			result.add(getFlowerBed(flower));
		}
		if (radius != 0) {
			for (Flower neighbor : getDirectNeighbors(flower)) {
				result.addAll(getBedsNear(neighbor, radius - 1, player));
			}
		}
		return result;
	}

	/**
	 * Aktualisiert die validen Züge des Spielers.
	 * Hierbei werden sich alle möglichen Zuege nahe dem modifizierten Beet angeguckt.
	 *
	 * @param bed Das Beet zu welchen die neu gesetzte Blume gehört.
	 */
	private void updateValidMovesForBed(final Collection<Flower> bed) {
		// Wenn die Größe des Beetes 4 beträgt müssen alle Nachbarn verboten werden.
		if (bed.size() == 4) {
			for (Flower bedNeighbor : getAllNeighbors(bed)) {
				playerDataSet.get(currentPlayer).legalMoves.removeMovesContaining(bedNeighbor);
			}
			return;
		}
		// Ansonsten nehmen wir uns einen direkten Beetnachbarn.
		for (Flower bedNeighbor : getDirectNeighbors(bed)) {
			// Wenn dieser bereits verboten ist nichts mehr zu tun.
			if (!playerDataSet.get(currentPlayer).legalMoves.containsMovesContaining(bedNeighbor)) {
				continue;
			}
			// Wir platzieren den Nachbarn testweise auf dem Brett.
			playerDataSet.get(currentPlayer).flowers.add(bedNeighbor);
			Collection<Flower> resultingBed = getFlowerBed(bedNeighbor);
			if (!isLegalBed(resultingBed, currentPlayer)) {
				// Wenn mit diesem Nachbarn das Beet ungültig wird, müssen alle Züge mit dieser Flower verboten werden
				playerDataSet.get(currentPlayer).legalMoves.removeMovesContaining(bedNeighbor);
			} else if (resultingBed.size() == 4) {
				/*
				 * Wenn mit diesen Nachbarn das Beet Größe 4 hat, müssen alle Züge verboten werden, die die Größe des
				 * Beets noch weiter erhöhen würden.
				 */
				for (Flower secondBedNeighbor : getAllNeighbors(resultingBed)) {
					playerDataSet.get(currentPlayer).legalMoves.remove(
						new Move(bedNeighbor, secondBedNeighbor)
					);
				}
			} else {
				/*
				 * Ansonsten probieren wir alle Züge aus diesem Nachbarn und den Nachbarn des durch das Platzieren
				 * dieses Nachbars entstehenden Beetes entstehen aus und verbieten sie, wenn dadurch ein ungültiges
				 * Beet entsteht.
				 */
				for (Flower secondBedNeighbor : getDirectNeighbors(resultingBed)) {
					if (!playerDataSet.get(currentPlayer).legalMoves.containsMovesContaining(secondBedNeighbor)) {
						continue;
					}
					// Wir platzieren den Nachbarn testweise auf dem Brett.
					playerDataSet.get(currentPlayer).flowers.add(secondBedNeighbor);
					if (!isLegalBed(getFlowerBed(secondBedNeighbor), currentPlayer)) {
						playerDataSet.get(currentPlayer).legalMoves.remove(
							new Move(bedNeighbor, secondBedNeighbor)
						);
					}
					// Testweise platzierte Blume wieder wegnehmen.
					playerDataSet.get(currentPlayer).flowers.remove(secondBedNeighbor);
				}
			}
			// Testweise platzierte Blume wieder wegnehmen.
			playerDataSet.get(currentPlayer).flowers.remove(bedNeighbor);
		}
	}

	/**
	 * Gibt alle Gräben zurück, die eine Position mit einer gegebenen Blume gemeinsam haben.
	 *
	 * @param flower Blume, für die die Gräben zurückgegeben werden sollen
	 * @return Alle Gräben, die eine Position mit dieser Blume gemeinsam haben
	 */
	private HashSet<Ditch> getAdjacentDitches(final Flower flower) {
		HashSet<Ditch> res = new HashSet<>();
		for (Position pos : getPositions(flower)) {
			res.addAll(getDitchesAround(pos));
		}
		return res;
	}

	/**
	 * Gibt alle für den aktuellen Spieler platzierbare Gräben zurück, die eine Position mit einer gegebenen Blume
	 * gemeinsam haben.
	 *
	 * @param flower Blume, für die die Gräben zurückgegeben werden sollen
	 * @return Alle für den aktuellen Spieler platzierbare Gräben, die eine Position mit einer gegebenen Blume
	 * gemeinsam haben
	 */
	private HashSet<Ditch> getPossibleDitches(final Flower flower) {
		HashSet<Ditch> result = getAdjacentDitches(flower);
		Position[] flowerPositions = getPositions(flower);
		for (Iterator<Ditch> it = result.iterator(); it.hasNext(); ) {
			Ditch ditch = it.next();
			// Prüfen, ob auf der anderen Seite des Grabens eine Blume des aktuellen Spielers ist
			Position pos = (Arrays.asList(flowerPositions).contains(ditch.getFirst()))
				? ditch.getSecond() : ditch.getFirst();
			boolean noFlowerConnectedToDitch = getFlowersAround(pos).stream().
				noneMatch(f -> playerDataSet.get(currentPlayer).flowers.contains(f));
			if (noFlowerConnectedToDitch) {
				it.remove();
				continue;
			}

			// Prüfen, ob die an den Graben angrenzenden Blumen einem Spieler gehören
			boolean ditchBlockedByFlowerNeighbors = getDirectNeighbors(ditch).stream()
				.anyMatch(f -> getFlowerColor(f) != null);
			if (ditchBlockedByFlowerNeighbors) {
				it.remove();
				continue;
			}

			// Prüfen, ob eine der Positionen des Grabens schon durch einen anderen Graben besetzt ist.
			boolean otherDitchContainsSamePositon = Arrays.stream(getPositions(ditch))
				.map(this::getDitchesAround)
				.flatMap(Collection::stream)
				.anyMatch(ditchContainingPos -> getDitchColor(ditchContainingPos) != null);
			if (otherDitchContainsSamePositon) {
				it.remove();
			}
		}
		return result;
	}

	/**
	 * Erlaubt alle neuen legalen Grabenzüge an einer Blume.
	 *
	 * @param flower Blume, die mit den Gräben verbunden ist.
	 */
	private void generateNewDitches(final Flower flower) {
		HashSet<Ditch> possibleDitches = getPossibleDitches(flower);
		Log.log(LogLevel.DUMP, LogModule.BOARD, "Allowing ditches: " + possibleDitches);
		for (Ditch ditch : possibleDitches) {
			if (getDitchColor(ditch) == null) {
				playerDataSet.get(currentPlayer).legalMoves.add(new Move(ditch));
			}
		}
	}

	/**
	 * Berechnet die Punktzahl nachdem ein neuer Garten entstanden ist.
	 *
	 * @param flower Blume welche gesetzt wurde.
	 * @return Um wieviel sich die Punktzahl erhöht hat.
	 */
	private int updateScore(final Flower flower) {
		if (getFlowerBed(flower).size() != 4) {
			return 0;
		}
		return getBedChainScore(flower);
	}

	/**
	 * Aktualisiert den Punktestand eines Spielers nach dem Setzen eines Grabens.
	 *
	 * @param ditch Der Graben, der gesetzt wurde
	 */
	private void updateScore(final Ditch ditch) {
		// Temporäres entfernen des Grabens
		LinkedList<Integer> scores = new LinkedList<>();
		playerDataSet.get(currentPlayer).ditches.remove(ditch);

		HashSet<HashSet<Flower>> visitedBeds = new HashSet<>();

		for (Position pos : getPositions(ditch)) {
			int score = 0;
			for (Flower flowerConnectedToPos : getFlowersAround(pos)) {
				if (playerDataSet.get(currentPlayer).flowers.contains(flowerConnectedToPos) &&
						!visitedBeds.contains(getFlowerBed(flowerConnectedToPos))) {
					score += getBedChainScore(flowerConnectedToPos);
					// Damit Ketten nicht doppelt gezaehlt werden
					visitedBeds.addAll(getBedChain(flowerConnectedToPos));
				}
			}
		scores.add(score);
		}

		// Hier muss dann jeweils der Score der einzelnen Pfade entfernt werden und dann die Summe der Summe
		// aller Pfäden hinzugefügt werden
		for (int score : scores) {
			// Gaußsche Summenformel
			playerDataSet.get(currentPlayer).currentScore -= (score*score+score)/2;
		}
		int newScore = 0;
		for (int score : scores) {
			newScore += score;
		}
		playerDataSet.get(currentPlayer).currentScore += (newScore*newScore+newScore)/2;

		// Wieder hinzufügen
		playerDataSet.get(currentPlayer).ditches.add(ditch);
	}

	/**
	 * Gibt die Kette von verbundenen Beeten zurück, zu der eine Blume gehört.
	 *
	 * @param flower Blume, die zu einer Kette gehört
	 * @return Die Kette der verbundenen Beete
	 */
	private HashSet<HashSet<Flower>> getBedChain(final Flower flower) {
		HashSet<HashSet<Flower>> bedChain = new HashSet<>();
		LinkedList<HashSet<Flower>> queue = new LinkedList<>();
		queue.add(getFlowerBed(flower));
		while (!queue.isEmpty()) {
			HashSet<Flower> currentBed = queue.pop();
			bedChain.add(currentBed);
			getBedsConnectedToBed(currentBed).stream().filter(bed -> !bedChain.contains(bed)).forEach(queue::add);
		}
		return bedChain;
	}

	/**
	 * Gibt den Wert einer Beetkette zurück.
	 *
	 * @param flower Blume, die zur Beetenkette gehört
	 * @return Der Wert
	 */
	private int getBedChainScore(final Flower flower) {
		HashSet<HashSet<Flower>> bedChain = getBedChain(flower);
		int score = 0;
		for (HashSet<Flower> bed : bedChain) {
			if (bed.size() == 4) {
				score += 1;
			}
		}
		return score;
	}

	/**
	 * Gibt die Positionen einer Blume zurueck.
	 *
	 * @param flower Blume, deren Positionen zurückgegeben werden
	 * @return Positionen der Blume
	 */
	private Position[] getPositions(final Flower flower) {
		return new Position[]{flower.getFirst(), flower.getSecond(), flower.getThird()};
	}

	/**
	 * Gibt die Positionen eines Grabens zurueck.
	 *
	 * @param ditch Graben, dessen Positionen zurückgegeben werden
	 * @return Position des Grabens
	 */
	private Position[] getPositions(final Ditch ditch) {
		return new Position[]{ditch.getFirst(), ditch.getSecond()};
	}

	/**
	 * Überprüft, ob ein Beet erlaubt ist.
	 *
	 * @param bed Beet, das überprüft werden soll
	 * @param player Farbe des Spielers, dem das Beet gehört
	 * @return Ob das Beet erlaubt ist.
	 */
	private boolean isLegalBed(final Collection<Flower> bed, final PlayerColor player) {
		return bed.size() < 4
		    || bed.size() == 4
		    && Collections.disjoint(getAllNeighbors(bed), playerDataSet.get(player).flowers);
	}

	/**
	 * Gibt die Spielerfarbe eines Grabens zurück. Gibt null zurück, falls der Graben noch nicht gesetzt wurde.
	 *
	 * @param ditch Graben, der überprüft werden soll
	 * @return Farbe des Grabens
	 */
	private PlayerColor getDitchColor(final Ditch ditch) {
		for (Map.Entry<PlayerColor, PlayerData> entry : playerDataSet.entrySet()) {
			if (entry.getValue().ditches.contains(ditch)) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * Gibt die Spielerfarbe einer Blume zurück. Gibt null zurück, falls die Blume noch nicht gesetzt wurde.
	 *
	 * @param flower Blume, die überprüft werden soll
	 * @return Farbe der Blume
	 */
	private PlayerColor getFlowerColor(final Flower flower) {
		for (Map.Entry<PlayerColor, PlayerData> entry : playerDataSet.entrySet()) {
			if (entry.getValue().flowers.contains(flower)) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * Gibt das Beet zurück, zu dem eine Blume gehört.
	 *
	 * @param flower Blume, die zu einem Beet gehört
	 * @return Das Beet oder null, falls die Blume zu keinem Beet gehört
	 */
	private HashSet<Flower> getFlowerBed(final Flower flower) {
		PlayerColor flowerColor = getFlowerColor(flower);
		if (flowerColor == null) {
			return null;
		}

		HashSet<Flower> result = new HashSet<>();
		Stack<Flower> toVisit = new Stack<>();
		toVisit.add(flower);

		while (!toVisit.empty()) {
			Flower visiting = toVisit.pop();
			for (Flower neighbor : getDirectNeighbors(visiting)) {
				if (!result.contains(neighbor) && playerDataSet.get(flowerColor).flowers.contains(neighbor)) {
					toVisit.add(neighbor);
				}
				result.add(visiting);
			}
		}
		return result;
	}

	/**
	 * Gibt die Blumen zurück, die mit einer gegebenen Blume eine Kante gemeinsam haben.
	 *
	 * @param center Die Blume, dessen Nachbarn zurück gegeben werden sollen.
	 * @return Die direkten Nachbarn
	 */
	private LinkedList<Flower> getDirectNeighbors(final Flower center) {
		LinkedList<Flower> result = new LinkedList<>();
		Position[] nodes = getPositions(center);

		/* 
		 * Die Fehlenden Punkte lassen sich als Kombinationen der Eckpunte des Gegebenen Dreiecks
		 * darstellen:
		 *
		 *                    p2+p0-p1    p2    p1+p2-p0
		 *                       x        x        x
		 *                               / \
		 *                              /   \
		 *                             /     \
		 *                            x-------x
		 *                           p0       p1
		 *
		 *
		 *                                x
		 *                             p0+p1-p2
		 *
		 *
		 * Die for-Schleife iteriert über diese Punkte.
		 */
		for (int i = 0; i < 3; i++) {
			try {
				Position third = new Position(
					nodes[i].getColumn() + nodes[(i+1)%3].getColumn() - nodes[(i+2)%3].getColumn(),
					nodes[i].getRow() + nodes[(i+1)%3].getRow() - nodes[(i+2)%3].getRow()
				);
				Flower neighbor = new Flower(nodes[i], nodes[(i+1)%3], third);
				if (isOnBoard(neighbor)) {
					result.add(neighbor);
				}
			} catch (IllegalArgumentException e) {
			}
		}
		return result;
	}

	/**
	 * Gibt die Blumen zurück, die mit einer gegebenen Blume eine Ecke gemeinsam haben.
	 *
	 * @param center Die Blume, dessen Nachbarn zurück gegeben werden sollen.
	 * @return Die Nachbarn
	 */
	private LinkedList<Flower> getAllNeighbors(final Flower center) {
		// Die Blumen, die eine Kante gemeinsam haben holen wir uns von der vorhandenen Methode.
		LinkedList<Flower> result = getDirectNeighbors(center);
		Position[] nodes = getPositions(center);
		Position lastPoint = null;

		/* 
		 * Die Fehlenden Punkte lassen sich als Kombinationen der Eckpunte des Gegebenen Dreiecks
		 * darstellen:
		 *
		 *                           x        x
		 *                       p2+p2-p1   p2+p2-p0
		 * 
		 *
		 *                   p2+p0-p1     p2    p1+p2-p0
		 *                       x        x        x
		 *                               / \
		 *                              /   \
		 *                p0+p0-p1     /     \      p1+p1-p0
		 *                    x       x-------x        x
		 *                           p0       p1
		 *
		 *                    p0+p0-p2         p1+p1-p2
		 *                        x       x       x
		 *                             p0+p1-p2
		 *
		 *
		 * Die for-Schleife iteriert über diese Punkte.
		 */
		for (int i = 0; i <= 9; i++) {
			try {
				Position point = new Position(
					nodes[i/3%3].getColumn() + nodes[(i+1)/3%3].getColumn() - nodes[((i+2)/3+1)%3].getColumn(),
					nodes[i/3%3].getRow() + nodes[(i+1)/3%3].getRow() - nodes[((i+2)/3+1)%3].getRow()
				);
				// Erst eine Blume erzeugen, wenn wir 2 Punkte für die äußere Kante haben.
				if (lastPoint != null) {
					Flower neighbor = new Flower(nodes[i/3%3], lastPoint, point);
					if (isOnBoard(neighbor)) {
						result.add(neighbor);
					}
				}
				lastPoint = point;
			} catch (IllegalArgumentException e) {
				/*
				 * Falls die Position ungültig war, müssen wir erst wieder zwei Positionen sammeln,
				 * bevor wir die nächste Blume erzeugen.
				 */
				lastPoint = null;
			}
		}
		return result;
	}

	/**
	 * Gibt die Blumen zurück, die direkt an einem Beet anliegen.
	 *
	 * @param bed Beet, dessen direkten Nachbarn gesucht werden
	 * @return Die direkten Nachbarn des Beetes
	 */
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

	/**
	 * Gibt alle Blumen zurück, die sich mindestens eine Position mit einem Beet teilen.
	 *
	 * @param bed Beet, dessen Nachbarn gesucht werden
	 * @return Die Nachbarn des Beetes
	 */
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

	/**
	 * Gibt die Blumen zurück, die mit einen gegebenen Graben eine Kante gemeinsam haben.
	 *
	 * @param ditch Der Graben, dessen Nachbarn zurück gegeben werden sollen.
	 * @return Die direkten Nachbarn
	 */
	private LinkedList<Flower> getDirectNeighbors(final Ditch ditch) {
		LinkedList<Flower> result = new LinkedList<>();
		Position[] nodes = {ditch.getFirst(), ditch.getSecond()};
		try {
			result.add(new Flower(
				nodes[0],
				nodes[1],
				new Position(
					nodes[1].getColumn() + nodes[1].getRow() - nodes[0].getRow(),
					nodes[0].getRow() - nodes[1].getColumn() + nodes[0].getColumn()
				)
			));
		} catch (IllegalArgumentException e) {}
		try {
			result.add(new Flower(
				nodes[0],
				nodes[1],
				new Position(
					nodes[0].getColumn() - nodes[1].getRow() + nodes[0].getRow(),
					nodes[1].getRow() + nodes[1].getColumn() - nodes[0].getColumn()
				)
			));
		} catch (IllegalArgumentException e) {}
		result.removeIf(f -> !isOnBoard(f));
		return result;
	}

	/**
	 * Gibt alle Positionen zurück, die von der gegebenen Position genau eine Position entfernt
	 * sind. Das Ergebnis ist im Uhrzeigersinn geordnet und beginnt mit dem unteren linken
	 * Nachbarn.
	 *
	 * @param center Die Position in der Mitte
	 * @return Die benachbarten Positionen
	 */
	private LinkedList<Position> getPositionsAround(final Position center) {
		LinkedList<Position> result = new LinkedList<>();
		/*
		 * Wir verwenden die periodische Folge a_i := sgn((i%6-2)%3) und eine nach links
		 * verschobene Version davon, um im Kreis über die Nachbarn zu iterieren (ähnlich wie
		 * mit Sinus und Kosinus am Einheitskreis).
		 */
		for (int i = 0; i < 6; i++) {
			try {
				Position neighbor = new Position(
					center.getColumn() + Integer.signum(((i+2)%6-2)%3),
					center.getRow() + Integer.signum((i%6-2)%3)
				);
				if (isOnBoard(neighbor)) {
					result.add(neighbor);
				}
			} catch(IllegalArgumentException e) {}
		}
		return result;
	}

	/**
	 * Gibt alle Blumen zurück, die an eine gegebene Position angrenzen.
	 *
	 * @param center Position welche sich in jeder Blume befindet
	 * @return Alle Blumen mit dieser Position
	 */
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

	/**
	 * Gibt alle Gräben zurück, die von einer gegebenen Position ausgehen.
	 *
	 * @param center Position welche sich in jedem Graben befindet
	 * @return Alle Gräben mit dieser Position
	 */
	private LinkedList<Ditch> getDitchesAround(final Position center) {
		LinkedList<Ditch> result = new LinkedList<>();
		for (Position position : getPositionsAround(center)) {
			result.add(new Ditch(center, position));
		}
		return result;
	}

	/**
	 * Gibt alle Beete zurück, die mit einem Beet über Gräben verbunden sind.
	 *
	 * @param bed Beet, welches ueberprüft wird.
	 * @return Die Menge aller verbundenen Beete.
	 */
	private HashSet<HashSet<Flower>> getBedsConnectedToBed(final HashSet<Flower> bed) {
		HashSet<HashSet<Flower>> bedsConnectedToBed = new HashSet<>();
		for (Flower bedFlower : bed) {
			HashSet<Ditch> flowerDitches = getAdjacentDitches(bedFlower);

			for (Ditch d : flowerDitches) {
				if (!playerDataSet.get(currentPlayer).ditches.contains(d)) {
					continue;
				}
				// Nun muessen wir herausfinden welche Seite zum neuen Beet gehoert.
				Position p = (Arrays.asList(getPositions(bedFlower)).contains(d.getFirst())) ? d.getSecond() : d.getFirst();

				LinkedList<Flower> nearby = getFlowersAround(p);
				for (Flower nearbyFlower : nearby) {
					if (playerDataSet.get(currentPlayer).flowers.contains(nearbyFlower)) {
						bedsConnectedToBed.add(getFlowerBed(nearbyFlower));
					}
				}
			}
		}
		return bedsConnectedToBed;
	}

	/**
	 * Wertet das Spielbrett nach einem Grabenzug aus.
	 * Hierzu gehören das Aktualisieren der möglichen Gräben und der aktuellen Punktzahl.
	 *
	 * @param ditch der Graben der gesetzt wurde.
	 */
	private void updateAfterMove(final Ditch ditch) {
		// Setzen von Blumen auf Felder, die an den Graben angrenzen, verbieten
		for (Flower ditchNeighbor : getDirectNeighbors(ditch)) {
			for (PlayerData playerData : playerDataSet.values()) {
				playerData.legalMoves.removeMovesContaining(ditchNeighbor);
			}
		}

		// Andere Grabenmöglichkeiten verbieten, falls diese sich eine Position teilen
		for (Position pos : getPositions(ditch)) {
			for (Ditch ditchContainingPos : getDitchesAround(pos)) {
				for (PlayerData playerData : playerDataSet.values()) {
					Log.log(LogLevel.DUMP, LogModule.BOARD, "Banning Ditch: " + ditchContainingPos);
					playerData.legalMoves.remove(new Move(ditchContainingPos));
				}
			}
		}

		// Punktestand aktualisieren
		updateScore(ditch);
	}

	/**
	 * Prüft, ob sich eine Position auf diesem Board befindet.
	 * @return Ob die Position auf dem Board ist
	 */
	private boolean isOnBoard(final Position pos) {
		return pos != null
				&& pos.getColumn() > 0 && pos.getRow() > 0
				&& pos.getColumn() + pos.getRow() < size + 3;
	}

	/**
	 * Prüft, ob sich eine Blume auf diesem Board befindet.
	 * @return Ob die Blume auf dem Board ist
	 */
	private boolean isOnBoard(final Flower flower) {
		return flower != null && isOnBoard(flower.getThird());
	}

	/**
	 * Gibt einen Viewer auf das Spielbrett zurück.
	 *
	 * @return Der dazugehörige Viewer.
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
		 * {@inheritDoc} Das Ergebnis ist ein unmodifiableSet.
		 */
		@Override
		public Set<Ditch> getDitches(final PlayerColor color) {
			return Collections.unmodifiableSet(playerDataSet.get(color).ditches);
		}

		/**
		 * {@inheritDoc} Das Ergebnis ist ein unmodifiableSet.
		 */
		@Override
		public Set<Flower> getFlowers(final PlayerColor color) {
			return Collections.unmodifiableSet(playerDataSet.get(color).flowers);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getPoints(final PlayerColor color) {
			return playerDataSet.get(color).currentScore;
		}

		/**
		 * {@inheritDoc} Das Ergebnis ist ein unmodifiableSet.
		 */
		@Override
		public Set<Move> getPossibleMoves() {
			return Collections.unmodifiableSet(playerDataSet.get(currentPlayer).legalMoves);
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

		/**
		 * {@inheritDoc}
		 */
		@Override
		public LinkedList<Flower> getDirectNeighbors(final Flower f) {
			return MainBoard.this.getDirectNeighbors(f);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public LinkedList<Flower> getAllNeighbors(final Flower f) {
			return MainBoard.this.getAllNeighbors(f);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean possibleMovesContains(final Move move) {
			return playerDataSet.get(currentPlayer).legalMoves.contains(move);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean possibleMovesContainsMovesContaining(final Flower flower) {
			return playerDataSet.get(currentPlayer).legalMoves.containsMovesContaining(flower);
		}

		/**
		 * {@inheritDoc} Das Ergebnis ist ein unmodifiableSet.
		 */
		@Override
		public Collection<Move> getPossibleFlowerMoves() {
			return playerDataSet.get(currentPlayer).legalMoves.getFlowerMoves();
		}

		/**
		 * {@inheritDoc} Das Ergebnis ist ein unmodifiableSet.
		 */
		@Override
		public Collection<Flower> getPossibleFlowers() {
			return playerDataSet.get(currentPlayer).legalMoves.getFlowers();
		}

		/**
		 * {@inheritDoc} Das Ergebnis ist ein unmodifiableSet.
		 */
		@Override
		public Collection<Flower> getFlowersCombinableWith(final Flower flower) {
			return playerDataSet.get(currentPlayer).legalMoves.getFlowersCombinableWith(flower);
		}

		/**
		 * {@inheritDoc} Das Ergebnis ist eine unmodifiableMap.
		 */
		@Override
		public Map<Flower, HashSet<Flower>> getFlowerMap() {
			return playerDataSet.get(currentPlayer).legalMoves.getFlowerMap();
		}

		/**
		 * {@inheritDoc} Das Ergebnis ist ein unmodifiableSet.
		 */
		@Override
		public Collection<Move> getPossibleMovesContaining(final Flower flower) {
			return playerDataSet.get(currentPlayer).legalMoves.getMovesContaining(flower);
		}

		/**
		 * {@inheritDoc} Das Ergebnis ist ein unmodifiableSet.
		 */
		@Override
		public Collection<Move> getPossibleDitchMoves() {
			return playerDataSet.get(currentPlayer).legalMoves.getDitchMoves();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PlayerColor getFlowerColor(final Flower flower) {
			return MainBoard.this.getFlowerColor(flower);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PlayerColor getDitchColor(final Ditch flower) {
			return MainBoard.this.getDitchColor(flower);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public HashSet<Flower> getFlowerBed(final Flower flower) {
			return MainBoard.this.getFlowerBed(flower);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ArrayList<Flower> getAllFlowers() {
			return new ArrayList<Flower>(Arrays.asList(MainBoard.this.allFlowers));
		}
	}
}
