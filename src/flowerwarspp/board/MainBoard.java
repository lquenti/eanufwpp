package flowerwarspp.board;

import flowerwarspp.preset.*;
import flowerwarspp.util.log.Log;
import flowerwarspp.util.log.LogLevel;
import flowerwarspp.util.log.LogModule;

import java.util.*;

// TODO: UMLAUTE
// TODO: JAVADOC LINKS?
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

	PlayerData() {
		flowers = new HashSet<>();
		ditches = new HashSet<>();
		legalMoves = new MoveSet();
		currentScore = 0;
	}

	PlayerData(PlayerData original) {
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
	 * Konstruktor. Laesst das Board mit Groesse [3;30] initialisieren.
	 *
	 * @param size Groesse des Boardes.
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

		for (Map.Entry<PlayerColor, PlayerData> entry : original.playerData.entrySet()) {
			playerData.put(entry.getKey(), new PlayerData(entry.getValue()));
		}

		allFlowers = Arrays.copyOf(original.allFlowers, original.allFlowers.length);
	}

	/**
	 * Initalisiert das Board.
	 */
	private void initBoard() {
		playerData.put(PlayerColor.Red, new PlayerData());
		playerData.put(PlayerColor.Blue, new PlayerData());

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

		// Game-End Checks
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
	 * Bestimmt den Gewinner wenn keine Zuege mehr moeglich sind.
	 */
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


	/**
	 * Wertet das Spielbrett nach gesetzten Blumenzug aus.
	 * Hierzu gehoeren das Aktualisieren der moeglichen Blumen, Graeben und der aktuellen Punktzahl
	 *
	 * @param fs die gesetzten Blumen
	 */
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

			// Ditchchecks
			generateNewDitches(f);

			// Entfernen der Graeben der Blume
			for (Ditch d : getEdgeDitches(f)) {
				for (PlayerData player : playerData.values()) {
					player.legalMoves.remove(new Move(d));
				}
			}

		}
		playerData.get(currentPlayer).currentScore += updateScore(fs[0]);
		// Scorecheck
		if (!getBedChain(fs[0]).contains(getFlowerBed(fs[1]))) {
			playerData.get(currentPlayer).currentScore += updateScore(fs[1]);
		}
	}

	/**
	 * Gibt die Graeben zureuck, welche die Blume bilden.
	 *
	 * @param flower Blume wessen Graeben zurueckgegeben werden.
	 * @return Die Graeben aus welchen die Blume besteht.
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
	 * Gibt die Beete eines Spielers innerhalb einer Reichweite an Blumenabstaenden zurueck.
	 * Hierbei ist die Reichweite nur durch direkte Verbindungen definiert.
	 *
	 * @param flower Blume von welcher aus gesucht wird.
	 * @param radius Radius in welchen von der Blume aus gesucht wird.
	 * @param player Spieler wessen Beeten zurueckgegeben werden.
	 * @return Menge an Beeten des Spielers innerhalb des Radius.
	 */
	private HashSet<Collection<Flower>> getBedsNear(final Flower flower, final int radius, final PlayerColor player) {
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

	// TODO: Kommentieren
	/**
	 * Aktualisiert die validen Zuege des Spielers.
	 * Hierbei werden sich alle moeglichen Zuege nahe dem modifizierten Beet angeguckt.
	 *
	 * @param bed Das Beet zu welchen die neu gesetzte Blume gehoert.
	 */
	private void updateValidMovesForBed(final Collection<Flower> bed) {
		if (bed.size() == 4) { // Fuer aktuelles Bed
			for (Flower bedNeighbor : getAllNeighbors(bed)) {
				playerData.get(currentPlayer).legalMoves.removeMovesContaining(bedNeighbor);
			}
			return;
		}
		for (Flower bedNeighbor : getDirectNeighbors(bed)) {
			if (!playerData.get(currentPlayer).legalMoves.containsMovesContaining(bedNeighbor)) {
				continue;
			}
			playerData.get(currentPlayer).flowers.add(bedNeighbor);
			Collection<Flower> resultingBed = getFlowerBed(bedNeighbor);
			if (!isLegalBed(resultingBed, currentPlayer)) {
				playerData.get(currentPlayer).legalMoves.removeMovesContaining(bedNeighbor);
			} else if (resultingBed.size() == 4) {
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

	/**
	 * Gibt alle benachbarten Graebenmoeglichkeiten an einer Blume zurueck.
	 *
	 * @param newFlower Blume welche mit jeden potentiellen Graben verbunden ist.
	 * @return Die Menge an Graeben
	 */
	private HashSet<Ditch> getAdjacentDitches(final Flower newFlower) {
		HashSet<Ditch> res = new HashSet<>();
		for (Position p : getPositions(newFlower)) {
			res.addAll(getDitchesAround(p));
		}
		return res;
	}

	/**
	 * Gibt alle moeglichen Graebenzuege an einer Blume zurueck.
	 *
	 * @param newFlower Blume welche mit jeden potentiellen Graben verbunden ist.
	 * @return Die Menge an Graeben.
	 */
	private HashSet<Ditch> getPossibleDitches(final Flower newFlower) {
		HashSet<Ditch> res = getAdjacentDitches(newFlower);
		Position[] flowerPositions = getPositions(newFlower);
		HashSet<Ditch> tobeRemoved = new HashSet<>(); // Um ConcurrentModificationException zu umgehen
		for (Ditch d : res) {
			// 1. Condition: Ob auf der anderen Seite eine Flower ist
			Position p = (Arrays.asList(flowerPositions).contains(d.getFirst())) ? d.getSecond() : d.getFirst();
			if (getFlowersAround(p).stream().noneMatch(f -> playerData.get(currentPlayer).flowers.contains(f))) {
				tobeRemoved.add(d);
				continue;
			}
			// 2. Condition: Ob die Blumen daneben schon eingefaerbt sind
			for (Flower ditchNeighbor : getDirectNeighbors(d)) {
				if ( getFlowerColor(ditchNeighbor) != null ) {
					tobeRemoved.add(d);
				}
			}
			// 3. Condition: Das keine andere Ditches
			boolean remove = Arrays.stream(getPositions(d))
					.map(this::getDitchesAround)
					.flatMap(Collection::stream)
					.anyMatch(ditchContainingPos -> getDitchColor(ditchContainingPos) != null);
			if (remove) {
				tobeRemoved.add(d);
			}
		}
		res.removeAll(tobeRemoved);
		Log.log(LogLevel.DUMP, LogModule.BOARD, "Allowing ditches: " + res);
		return res;
	}

	/**
	 * Erlaubt alle neuen legalen Graebenzuege an einer Blume.
	 *
	 * @param f Blume welche mit jeden potentiellen Graben verbunden ist.
	 */
	private void generateNewDitches(final Flower f) {
		HashSet<Ditch> allDitchMoves = getPossibleDitches(f);
		for (Ditch d : allDitchMoves) {
			if (getDitchColor(d) == null) {
				playerData.get(currentPlayer).legalMoves.add(new Move(d));
			}
		}
	}

	/**
	 * Inkrementiert die Punktzahl nachdem ein neuer Garten entstanden ist.
	 *
	 * @param f Blume welche gesetzt wurde.
	 * @return Um wieviel sich die Punktzahl verbessert hat
	 */
	private int updateScore(final Flower f) {
		if (getFlowerBed(f).size() != 4) {
			return 0;
		}
		return getBedChainScore(f);
	}

	/**
	 * Aktualisiert den Punktestand eines Spielers nach dem Setzen eines Grabens.
	 *
	 * @param d der Graben welcher gesetzt wurde.
	 */
	private void updateScore(final Ditch d) {
		// Temporäres entfernen der Ditch
		LinkedList<Integer> scores = new LinkedList<>();
		playerData.get(currentPlayer).ditches.remove(d);

		HashSet<HashSet<Flower>> visitedBeds = new HashSet<>();

		for (Position p : getPositions(d)) {
			int score = 0;
			for (Flower flowerConnectedToPos : getFlowersAround(p)) {
				if (playerData.get(currentPlayer).flowers.contains(flowerConnectedToPos) &&
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

	/**
	 * Gibt die Kette an verbundenen Beete zurueck, welcher eine Blume zugehoert.
	 *
	 * @param f Blume, welche eine Kette hinzugehoert.
	 * @return Die Kette der verbundenen Beete.
	 */
	private HashSet<HashSet<Flower>> getBedChain(final Flower f) {
		HashSet<HashSet<Flower>> bedChain = new HashSet<>();
		LinkedList<HashSet<Flower>> queue = new LinkedList<>();
		queue.add(getFlowerBed(f));
		while (!queue.isEmpty()) {
			HashSet<Flower> currentBed = queue.pop();
			bedChain.add(currentBed);
			getBedsConnectedToBed(currentBed).stream().filter(x -> !bedChain.contains(x)).forEach(queue::add);
		}
		return bedChain;
	}

	/**
	 * Gibt den Wert einer aktuellen Beetenkette zurueck.
	 *
	 * @param f Blume welche der Beetenkette angehoert.
	 * @return Der Wert an Punkten.
	 */
	private int getBedChainScore(final Flower f) {
		HashSet<HashSet<Flower>> bedChain = getBedChain(f);
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
	 * @param f Blume wessen Positionen zurueckgegeben werden.
	 * @return Positionen der Blume.
	 */
	private Position[] getPositions(final Flower f) {
		return new Position[]{f.getFirst(), f.getSecond(), f.getThird()};
	}

	/**
	 * Gibt die Positionen eines Grabens zurueck.
	 *
	 * @param d Graben wessen Positionen zurueckgegeben werden.
	 * @return Position des Grabens.
	 */
	private Position[] getPositions(final Ditch d) {
		return new Position[]{d.getFirst(), d.getSecond()};
	}

	/**
	 * Checkt ob ein Beet legal ist.
	 *
	 * @param bed Beet welches ueberprueft wird.
	 * @param player Farbe des Spielers, dem das Bett gehoert.
	 * @return Ob das Beet legal ist.
	 */
	private boolean isLegalBed(final Collection<Flower> bed, final PlayerColor player) {
		return bed.size() < 4
				|| bed.size() == 4
				&& Collections.disjoint(getAllNeighbors(bed), playerData.get(player).flowers);
	}

	/**
	 * Gibt die Spielerfarbe eines Grabens zurueck. Returniert null wenn Graben noch nicht gesetzt wurde.
	 *
	 * @param d Graben, welcher ueberprueft wird.
	 * @return Farbe des Grabens
	 */
	private PlayerColor getDitchColor(final Ditch d) {
		for (Map.Entry<PlayerColor, PlayerData> entry : playerData.entrySet()) {
			if (entry.getValue().ditches.contains(d)) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * Gibt die Spielerfarbe einer Blume zurueck. Returniert null wenn Blume noch nicht gesetzt wurde.
	 *
	 * @param f Blume, welche ueberprueft wird.
	 * @return Farbe der Blume
	 */
	private PlayerColor getFlowerColor(final Flower f) {
		for (Map.Entry<PlayerColor, PlayerData> entry : playerData.entrySet()) {
			if (entry.getValue().flowers.contains(f)) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * Gibt das dazugehoerige Beet einer Blume zurueck.
	 *
	 * @param f Blume, welche zu dem Beet gehoert.
	 * @return Beet.
	 */
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
	 * Returniert die Blumen, die direkt an einem Beet anliegen anliegen.
	 *
	 * @param bed Beet, wessen direkte Nachbarn gesucht werden.
	 * @return die direkten Nachbarn des Beetes.
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
	 * Returniert alle Blumen, welche sich mindestens eine Position mit einem Beet teilen.
	 *
	 * @param bed Beet, wessen Nachbarn gesucht werden.
	 * @return die Nachbarn des Beetes.
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

	/**
	 * Gibt alle Beete zurueck welche mit einem Beet verbunden sind.
	 *
	 * @param bed Beet, welches ueberprueft wird.
	 * @return Die Menge aller verbundenen Beete.
	 */
	private HashSet<HashSet<Flower>> getBedsConnectedToBed(final HashSet<Flower> bed) {
		HashSet<HashSet<Flower>> bedsConnectedToBed = new HashSet<>();
		for (Flower bedFlower : bed) {
			HashSet<Ditch> flowerDitches = getAdjacentDitches(bedFlower);

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

	/**
	 * Wertet das Spielbrett nach gesetzten Grabenzug aus.
	 * Hierzu gehoeren das aktualisieren der moeglichen Graeben und der aktuellen Punktzahl.
	 *
	 * @param d der Graben welcher gesetzt wurde.
	 */
	private void updateAfterMove(final Ditch d) {
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
					Log.log(LogLevel.DUMP
							, LogModule.BOARD, "Banning Ditch: " + samePos);
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
	private boolean isOnBoard(final Position position) {
		return position != null
				&& position.getColumn() > 0 && position.getRow() > 0
				&& position.getColumn() + position.getRow() < size + 3;
	}

	/**
	 * Prüft, ob eine Flower sich auf diesem Board befindet.
	 * @return ob die Flower auf dem Board ist
	 */
	private boolean isOnBoard(final Flower flower) {
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
		public Set<Ditch> getDitches(final PlayerColor color) {
			return Collections.unmodifiableSet(playerData.get(color).ditches);
		}

		/**
		 * {@inheritDoc} Das Ergebnis ist ein unmodifiableSet.
		 */
		@Override
		public Set<Flower> getFlowers(final PlayerColor color) {
			return Collections.unmodifiableSet(playerData.get(color).flowers);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getPoints(final PlayerColor color) {
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
			return playerData.get(currentPlayer).legalMoves.contains(move);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean possibleMovesContainsMovesContaining(final Flower flower) {
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
		public Collection<Flower> getFlowersCombinableWith(final Flower flower) {
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
		public Collection<Move> getPossibleMovesContaining(final Flower flower) {
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
	}
}
