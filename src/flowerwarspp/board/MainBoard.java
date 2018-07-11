package flowerwarspp.board;

import flowerwarspp.preset.*;
import flowerwarspp.util.log.Log;
import flowerwarspp.util.log.LogLevel;
import flowerwarspp.util.log.LogModule;

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
	 * Erzeugt eine Kopie eines vorhandenen MainBoards
	 *
	 * param original Das Brett, das kopiert werden soll.
	 */
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
	 * {@inheritDoc}
	 * Verifiziert Zug und fuehrt diesen dann aus und berechnet Score.
	 *
	 * @param move auszufuehrender Zug
	 * @throws IllegalStateException Wenn Zug nicht valide ist.
	 */
	@Override
	public void make(final Move move) throws IllegalStateException {
		Log.log(LogLevel.DEBUG, LogModule.BOARD, "Status at beginning of make: " + currentStatus);
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
				updateAfterMove(move.getDitch());
				break;
			case Flower:
				playerData.get(currentPlayer).flowers.add(move.getFirstFlower());
				playerData.get(currentPlayer).flowers.add(move.getSecondFlower());
				updateAfterMove(new Flower[]{move.getFirstFlower(), move.getSecondFlower()});
				break;
			case End:
				endGame();
				return;
			case Surrender:
				endGame(oppositePlayer);
				return;
		}

		for (PlayerData player : playerData.values()) {
			if (player.legalMoves.getFlowerMoves().isEmpty()) {
				player.legalMoves.add(new Move(MoveType.End));
			}
		}

		if (playerData.get(oppositePlayer).legalMoves.getFlowerMoves().isEmpty() &&
		    playerData.get(oppositePlayer).legalMoves.getDitchMoves().isEmpty()) {
			Log.log(LogLevel.DEBUG, LogModule.BOARD, "Ending game because next Player can't make more moves");
			endGame();
			return;
		}

		PlayerColor t = currentPlayer;
		currentPlayer = oppositePlayer;
		oppositePlayer = t;
	}

	private void endGame(PlayerColor winner) {
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

	private void endGame() {
		int redPoints = playerData.get(PlayerColor.Red).currentScore;
		Log.log(LogLevel.DEBUG, LogModule.BOARD, "Red player has " + redPoints + "points at end of game.");
		int bluePoints = playerData.get(PlayerColor.Blue).currentScore;
		Log.log(LogLevel.DEBUG, LogModule.BOARD, "Blue player has " + bluePoints + "points at end of game.");
		if (redPoints > bluePoints) {
			endGame(PlayerColor.Red);
		} else if (bluePoints > redPoints) {
			endGame(PlayerColor.Blue);
		} else {
			endGame(null);
		}
	}


	private void updateAfterMove(final Flower[] fs) {
		for (Flower f : fs) {
			// Gesetzte Flowers für alle verbieten
			for (PlayerData playerData : playerData.values()) {
				playerData.legalMoves.removeMovesContaining(f);
			}

			// Gartencheck
			for (Collection<Flower> bed : getBedsNear(f, 4, currentPlayer)) {
				updateValidMovesForBed(bed);
			}

			// Ditchchecks:
			//  - Ditches halt checken
			//  - Diese duerfen nicht an Blumen anliegen
			//  - Ditch liegt noch nicht auf
			generateNewDitches(getPossibleDitches(f));

			for (Ditch d : getEdgeDitches(f)) {
				for (PlayerData player : playerData.values()) {
					player.legalMoves.remove(new Move(d));
				}
			}

		}
		playerData.get(currentPlayer).currentScore += updateScore(fs[0]);
		// Scorecheck
		if (!getFlowerChain(fs[0]).contains(getFlowerBed(fs[1]))) {
			playerData.get(currentPlayer).currentScore += updateScore(fs[1]);
		}
	}

	private Ditch[] getEdgeDitches(Flower flower) {
		Ditch[] result = new Ditch[3];
		Position[] positions = getPositions(flower);
		for ( int i = 0; i < positions.length; i++ ) {
			result[i] = new Ditch(positions[i], positions[(i+1)%positions.length]);
		}
		return result;
	}

	// TODO: Checken ob performant
	private HashSet<Collection<Flower>> getBedsNear(Flower flower, int radius, PlayerColor player) {
		HashSet<Collection<Flower>> result = new HashSet<>();
		if (playerData.get(player).flowers.contains(flower)) {
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

	private HashSet<Ditch> getAdjacentDitches(final Flower newFlower) {
		HashSet<Ditch> res = new HashSet<>();
		for (Position p : getPositions(newFlower)) {
			res.addAll(getDitchesAround(p));
		}
		return res;
	}

	private HashSet<Ditch> getPossibleDitches(final Flower newFlower) {
		HashSet<Ditch> res = getAdjacentDitches(newFlower);
		Position[] flowerPositions = getPositions(newFlower);
		HashSet<Ditch> tobeRemoved = new HashSet<>(); // Um ConcurrentModificationException zu umgehen
		for (Ditch d : res) {
			// 1. Condition: Ob auf der anderen Seite eine Flower ist
			// 2. Condition: Ob die Blumen daneben schon eingefaerbt sind
			Position p = (Arrays.asList(flowerPositions).contains(d.getFirst())) ? d.getSecond() : d.getFirst();
			if (getFlowersAround(p).stream().noneMatch(f -> playerData.get(currentPlayer).flowers.contains(f))) {
				tobeRemoved.add(d);
				continue;
			}
			for (Flower ditchNeighbor : getDirectNeighbors(d)) {
				if ( getFlowerColor(ditchNeighbor) != null ) {
					tobeRemoved.add(d);
				}
			}
		}
		res.removeAll(tobeRemoved);
		Log.log(LogLevel.DEBUG, LogModule.BOARD, "Allowing ditches: " + res);
		return res;
	}

	private void generateNewDitches(final Collection<Ditch> ds) {
		for (Ditch d : ds) {
			boolean remove = false;
			for (Position pos : getPositions(d)) {
				for (Ditch ditchContainingPosition : getDitchesAround(pos)) {
					if (getDitchColor(ditchContainingPosition) != null) {
						remove = true;
					}
				}
			}
			if (getDitchColor(d) == null && !remove) {
				playerData.get(currentPlayer).legalMoves.add(new Move(d));
			}
		}
	}

	private int updateScore(Flower f) {
		LinkedList<HashSet<Flower>> queue = new LinkedList<>();

		HashSet<Flower> changedBed = getFlowerBed(f);
		if (changedBed.size() != 4) {
			return 0;
		}
		queue.add(changedBed);
		return depthFirstSearch(new HashSet<>(), queue);
	}

	private void updateScore(final Ditch d) {
		// Temporäres entfernen der Ditch
		Position[] ps = new Position[]{d.getFirst(), d.getSecond()};
		LinkedList<Integer> scores = new LinkedList<>();
		playerData.get(currentPlayer).ditches.remove(d);

		HashSet<HashSet<Flower>> visitedBeds = new HashSet<>();
		LinkedList<HashSet<Flower>> queue = new LinkedList<>();

		for (Position p : ps) {
			// Für Randfälle wie dass 2 Pfade an einem Ditchende verbunden sind welche kein Kreis sind
			for (Flower flowerConnectedToPos : getFlowersAround(p)) {
				if (playerData.get(currentPlayer).flowers.contains(flowerConnectedToPos)) {
					queue.add(getFlowerBed(flowerConnectedToPos));
				}
			}

			scores.add(depthFirstSearch(visitedBeds, queue));
		}

		// Hier muss dann jeweils der Score der einzelnen Pfade entfernt werden und dann die Summe der Summe
		// aller Pfäden hinzugefügt werden
		for (int score : scores) {
			// Kleiner Gauss
			playerData.get(currentPlayer).currentScore -= (score*score+score)/2;
		}

		int newScore = 0;
		for (int score : scores) {
			newScore += score;
		}
		playerData.get(currentPlayer).currentScore += (newScore*newScore+newScore)/2;

		// Wieder hinzufügen
		playerData.get(currentPlayer).ditches.add(d);
	}

	private HashSet<HashSet<Flower>> getFlowerChain(Flower f) {
		HashSet<HashSet<Flower>> flowerChain = new HashSet<>();
		LinkedList<HashSet<Flower>> queue = new LinkedList<>();
		queue.add(getFlowerBed(f));
		depthFirstSearch(flowerChain, queue);
		return flowerChain;
	}

	private int depthFirstSearch(HashSet<HashSet<Flower>> flowerChain, LinkedList<HashSet<Flower>> queue) {
		int score=0;
		while (!queue.isEmpty()) {
			HashSet<Flower> currentBed = queue.pop();
			if (currentBed.size() == 4 && !flowerChain.contains(currentBed)) {
				score += 1;
			}
			flowerChain.add(currentBed);
			getBedsConnectedToBed(currentBed).stream().filter(x -> !flowerChain.contains(x)).forEach(queue::add);
		}
		return score;
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

	/**
	 * Gibt die Dreiecke zurück, die mit einem gegebenen Dreieck eine Kante gemeinsam haben.
	 *
	 * @param center Das Dreieck, dessen Nachbarn zurück gegeben werden sollen.
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
	 * Gibt die Dreiecke zurück, die mit einem gegebenen Dreieck eine Ecke gemeinsam haben.
	 *
	 * @param center Das Dreieck, dessen Nachbarn zurück gegeben werden sollen.
	 * @return Die Nachbarn
	 */
	private LinkedList<Flower> getAllNeighbors(final Flower center) {
		// Die Dreiecke, die eine Kante gemeinsam haben holen wir uns von der vorhandenen Methode.
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

	private HashSet<HashSet<Flower>> getBedsConnectedToBed(final HashSet<Flower> bed) {
		HashSet<HashSet<Flower>> bedsConnectedToBed = new HashSet<>();
		for (Flower bedFlower : bed) {
			HashSet<Ditch> flowerDitches = getPossibleDitches(bedFlower);

			for (Ditch d : flowerDitches) {
				if (!playerData.get(currentPlayer).ditches.contains(d)) {
					continue;
				}
				// Nun muessen wir herausfinden welche Seite zum neuen Beet gehoert.
				Position p = (Arrays.asList(getPositions(bedFlower)).contains(d.getFirst())) ? d.getSecond() : d.getFirst();

				LinkedList<Flower> nearby = getFlowersAround(p);
				for (Flower nearbyFlower : nearby) {
					if (playerData.get(currentPlayer).flowers.contains(nearbyFlower)) {
						bedsConnectedToBed.add(getFlowerBed(nearbyFlower));
					}
				}
			}
		}
		return bedsConnectedToBed;
	}

	private void updateAfterMove(Ditch d) {
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
				for (PlayerData player : playerData.values()) {
					Log.log(LogLevel.DEBUG, LogModule.BOARD, "Banning Ditch: " + samePos);
					player.legalMoves.remove(new Move(samePos));
				}
			}
		}

		// Scorecheck
		updateScore(d);
	}

	/**
	 * Prüft, ob eine Position sich auf diesem Board befindet.
	 * @return ob die Position auf dem Board ist
	 */
	private boolean isOnBoard(Position position) {
		return position != null
				&& position.getColumn() > 0 && position.getRow() > 0
				&& position.getColumn() + position.getRow() < size + 3;
	}

	/**
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
		 * {@inheritDoc} Das Ergebnis ist ein unmodifiableSet.
		 */
		@Override
		public Set<Ditch> getDitches(PlayerColor color) {
			return Collections.unmodifiableSet(playerData.get(color).ditches);
		}

		/**
		 * {@inheritDoc} Das Ergebnis ist ein unmodifiableSet.
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
			return playerData.get(color).currentScore;
		}

		/**
		 * {@inheritDoc} Das Ergebnis ist ein unmodifiableSet.
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

		/**
		 * {@inheritDoc}
		 */
		@Override
		public LinkedList<Flower> getDirectNeighbors(Flower f) {
			return MainBoard.this.getDirectNeighbors(f);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public LinkedList<Flower> getAllNeighbors(Flower f) {
			return MainBoard.this.getAllNeighbors(f);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean possibleMovesContains(Move move) {
			return playerData.get(currentPlayer).legalMoves.contains(move);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean possibleMovesContainsMovesContaining(Flower flower) {
			return playerData.get(currentPlayer).legalMoves.containsMovesContaining(flower);
		}

		/**
		 * {@inheritDoc} Das Ergebnis ist ein unmodifiableSet.
		 */
		@Override
		public Collection<Move> getPossibleFlowerMoves() {
			return playerData.get(currentPlayer).legalMoves.getFlowerMoves();
		}

		/**
		 * {@inheritDoc} Das Ergebnis ist ein unmodifiableSet.
		 */
		@Override
		public Collection<Flower> getPossibleFlowers() {
			return playerData.get(currentPlayer).legalMoves.getFlowers();
		}

		/**
		 * {@inheritDoc} Das Ergebnis ist ein unmodifiableSet.
		 */
		@Override
		public Collection<Flower> getFlowersCombinableWith(Flower flower) {
			return playerData.get(currentPlayer).legalMoves.getFlowersCombinableWith(flower);
		}

		/**
		 * {@inheritDoc} Das Ergebnis ist eine unmodifiableMap.
		 */
		@Override
		public Map<Flower, HashSet<Flower>> getFlowerMap() {
			return playerData.get(currentPlayer).legalMoves.getFlowerMap();
		}

		/**
		 * {@inheritDoc} Das Ergebnis ist ein unmodifiableSet.
		 */
		@Override
		public Collection<Move> getPossibleMovesContaining(Flower flower) {
			return playerData.get(currentPlayer).legalMoves.getMovesContaining(flower);
		}

		/**
		 * {@inheritDoc} Das Ergebnis ist ein unmodifiableSet.
		 */
		@Override
		public Collection<Move> getPossibleDitchMoves() {
			return playerData.get(currentPlayer).legalMoves.getDitchMoves();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PlayerColor getFlowerColor(Flower flower) {
			return MainBoard.this.getFlowerColor(flower);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PlayerColor getDitchColor(Ditch flower) {
			return MainBoard.this.getDitchColor(flower);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public HashSet<Flower> getFlowerBed(final Flower flower) {
			return MainBoard.this.getFlowerBed(flower);
		}
	}

	public static void main(String[] args) {
		MainBoard board = new MainBoard(5);

		System.out.println(new Move(new Flower(new Position(2, 1), new Position(1, 2), new Position(2, 2)), new Flower(new
				Position
				(1, 2), new Position(2, 2), new Position(1, 3))));
		System.out.println(new Move(new Flower(new Position(3, 2), new Position(4, 2), new Position(3, 3)), new Flower(new
				Position(4,	2), new Position(3, 3), new Position(4, 3))));
		System.out.println(new Move(new Flower(new Position(2, 2), new Position(1, 3), new Position(2, 3)), new Flower(new Position(2, 2), new Position(3, 2), new Position(2, 3))));
		System.out.println(new Move(new Flower(new Position(3, 3), new Position(2, 4), new Position(3, 4)), new Flower(new Position(2, 4), new Position(3, 4), new Position(2, 5))));
		System.out.println(new Move(new Flower(new Position(4, 1), new Position(5, 1), new Position(4, 2)), new Flower(new Position(1, 4), new Position(2, 4), new Position(1, 5))));
		System.out.println(new Move(new Flower(new Position(2, 1), new Position(3, 1), new Position(2, 2)), new Flower(new Position(3, 1), new Position(2, 2), new Position(3, 2))));
		System.out.println(new Move(new Ditch(new Position(2, 3), new Position(2, 4))));
		System.out.println(new Move(new Flower(new Position(1, 3), new Position(2, 3), new Position(1, 4)), new Flower(new Position(2, 4), new Position(1, 5), new Position(2, 5))));
		System.out.println(new Move(new Flower(new Position(5, 1), new Position(4, 2), new Position(5, 2)), new Flower(new Position(4, 2), new Position(5, 2), new Position(4, 3))));
		System.out.println(new Move(new Flower(new Position(1, 1), new Position(2, 1), new Position(1, 2)), new Flower(new Position(4, 1), new Position(3, 2), new Position(4, 2))));
		System.out.println(new Move(new Flower(new Position(3, 3), new Position(4, 3), new Position(3, 4)), new Flower(new Position(1, 5), new Position(2, 5), new Position(1, 6))));
		
		board.make(new Move(new Flower(new Position(2, 1), new Position(1, 2), new Position(2, 2)), new Flower(new
				Position
				(1, 2), new Position(2, 2), new Position(1, 3))));
		board.make(new Move(new Flower(new Position(3, 2), new Position(4, 2), new Position(3, 3)), new Flower(new
				Position(4,	2), new Position(3, 3), new Position(4, 3))));
		board.make(new Move(new Flower(new Position(2, 2), new Position(1, 3), new Position(2, 3)), new Flower(new Position(2, 2), new Position(3, 2), new Position(2, 3))));
		board.make(new Move(new Flower(new Position(3, 3), new Position(2, 4), new Position(3, 4)), new Flower(new Position(2, 4), new Position(3, 4), new Position(2, 5))));
		board.make(new Move(new Flower(new Position(4, 1), new Position(5, 1), new Position(4, 2)), new Flower(new Position(1, 4), new Position(2, 4), new Position(1, 5))));
		board.make(new Move(new Flower(new Position(2, 1), new Position(3, 1), new Position(2, 2)), new Flower(new Position(3, 1), new Position(2, 2), new Position(3, 2))));
		board.make(new Move(new Ditch(new Position(2, 3), new Position(2, 4))));
		board.make(new Move(new Flower(new Position(1, 3), new Position(2, 3), new Position(1, 4)), new Flower(new Position(2, 4), new Position(1, 5), new Position(2, 5))));
		board.make(new Move(new Flower(new Position(5, 1), new Position(4, 2), new Position(5, 2)), new Flower(new Position(4, 2), new Position(5, 2), new Position(4, 3))));
		board.make(new Move(new Flower(new Position(1, 1), new Position(2, 1), new Position(1, 2)), new Flower(new Position(4, 1), new Position(3, 2), new Position(4, 2))));
		board.make(new Move(new Flower(new Position(3, 3), new Position(4, 3), new Position(3, 4)), new Flower(new Position(1, 5), new Position(2, 5), new Position(1, 6))));

		System.out.println(board.viewer().getPossibleMoves());
	}
}
