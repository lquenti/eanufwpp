package flowerwarspp.board;

import flowerwarspp.preset.*;

import java.util.*;
import java.util.function.Function;

/**
 * Verwaltungsklasse, die Daten über die gemachten und noch möglichen Züge
 * eines Spielers Speichert.
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
	/**
	 * Die Felder, auf die der Spieler noch Blumen setzen kann.
	 */
	HashSet<Flower> legalFlowers = new HashSet<>();

	void banFlower(final Flower first) {
		if (!legalFlowers.contains(first)) {
			return;
		}
		for (Flower second : legalFlowers) {
			legalMoves.remove(new Move(first, second));
		}
	}
}

/**
 * Boardimplementation mit dazugehoeriger Logik. Implementiert Board und somit auch Viewable.
 *
 * @version 0.1
 */
public class MainBoard implements Board {
	/**
	 * Groesse des Boards.
	 */
	private final int size;

	/**
	 * Der Spieler, der aktuell am Zug ist.
	 */
	private PlayerColor currentPlayer = PlayerColor.Red;

	/**
	 * Der Spieler, welcher aktuell nicht am Zug ist.
	 */
	private PlayerColor oppositePlayer = PlayerColor.Blue; // Sonst macht man es x mal redundant beim checken

	// TODO: Wann schreiben wir den eigentlich mal um lol
	/**
	 * Der Aktuelle Status des Spielbretts.
	 */
	private Status currentStatus = Status.Ok;

	/**
	 * Daten über die Spieler.
	 */
	private EnumMap<PlayerColor, PlayerData> playerData = new EnumMap<>(PlayerColor.class);

	/**
	 * Liste mit allen möglichen Blumen und Referenzen auf dessen Nachbarn.
	 */
	private final HashMap<Flower, HashSet<Flower>> allFlowers;

	/**
	 * Konstruktor. Befuellt Board einer variablen Groesse zwischen [3;30].
	 * Falls Wert invalide wird dieser dem naechsten Element des Intervalls angepasst.
	 *
	 * @param size Groesse des Boardes
	 */
	public MainBoard(final int size) {
		this.size = size;

		playerData.put(PlayerColor.Red, new PlayerData());
		playerData.put(PlayerColor.Blue, new PlayerData());

		allFlowers = generateAllFlowers();

		// TODO: Nicht schoen
		Flower[] toIterate = (Flower[])allFlowers.keySet().toArray();
        for (int i = 0; i < toIterate.length; i++) {
		 	playerData.get(PlayerColor.Red).legalFlowers.add(toIterate[i]);
		 	playerData.get(PlayerColor.Blue).legalFlowers.add(toIterate[i]);
		 	for (int j = i + 1; j < toIterate.length; j++) {
		 		Move move = new Move(toIterate[i], toIterate[j]);
		 		playerData.get(PlayerColor.Red).legalMoves.add(move);
		 		playerData.get(PlayerColor.Blue).legalMoves.add(move);
		 	}
		 }
	}

	/**
	 * Generation aller Blumen und dessen Nachbarn durch an Breadth-first search angelehnten Algorithmus.
	 * @return alle Blumen mit ihren Nachbarn
	 */
	private HashMap<Flower, HashSet<Flower>> generateAllFlowers() {
		HashMap<Flower, HashSet<Flower>> board = new HashMap<>();
		LinkedList<Flower> queue = new LinkedList<>();
		queue.push(new Flower(new Position(1,1), new Position(1,2), new Position(2,1)));

		Flower current;
		HashSet<Flower> currentNeighbors;

		while(!queue.isEmpty()) {
			current = queue.pop();
			currentNeighbors = getDirectNeighbors(current);
			for (Flower neighbor : currentNeighbors) {
				if (! board.containsKey(neighbor)) {
					queue.push(neighbor);
				}
			}
		}
		return board;
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
		if (currentStatus != Status.Ok) {
			throw new IllegalStateException("Das Spielbrett kann keine Züge mehr annehmen!");
		}
		// TODO: Auswerten?
		if (!playerData.get(currentPlayer).legalMoves.contains(move)) {
			currentStatus = Status.Illegal;
		}
		// TODO: Ist es best practise nicht null zu checken weil es literally unmoeglich ist?
		// (Falls es nicht so ist Kommentar einfach removen)
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
	private void updateValidMoves(final Flower[] fs) {
        /*
        Was aktuell gemacht wird: (als Referenz zum erweitern (Kommentar kommt bei Abgabe raus))
            - Gaertencheck (done)
            - Exkludieren von Gaertenabstaenden
            - Alle Moves mit gesetzten Flowern fuer eigenen Spieler devalidieren (done)
            - Moves fuer andere Farbe exkludieren (done)
            - Grabenerstellung:
                - Gegnerische Graeben entfernen falls geblockt durch eigene Blume
                - Graben erlauben falls direkte Verbindung UND kein existierender Graben teilt (kann das der Fall sein nach Ditchchecks?)
         */
		for (Flower f : fs) {
			// Moves fuer andere Farbe exkludieren
			for (PlayerData p : playerData.values()) {
				p.banFlower(f);
			}

			// Gartencheck
			HashSet<Flower> bed = getFlowerBed(f);

			// TODO: BIG REFACTOR GEGEN REDUNDANZ
			// Exkludieren von Gartenabstaenden
			switch (bed.size()) {
				case 4:
					for (Flower bf : bed) {
						for (Flower newIllegalFlower : getAllNeighbors(bf)) {
							playerData.get(currentPlayer).banFlower(newIllegalFlower);
						}
					}
					break;
				case 3:
					/*
					Sei n die Tiefe des Suchalgorithmusses:
						n == 1: Sind null (irrelevant), eigene Blume (zaehlt nicht) oder gegnerische Blume (TODO: CHECKEN WENN MOVE ENTFERNT WIRD)
						n >  1: Invalide, da die Distanz zu weit ist
					 */
				case 2:
					/*
					size == 2
					Sei n die Tiefe des Suchalgorithmusses:
						n == 1: Sind null (irrelevant), eigene Blume (zaehlt nicht) oder gegnerische Blume (TODO: CHECKEN WENN MOVE ENTFERNT WIRD)
						n == 2: Duerfen nur noch einzelne Blumen sein, alles andere ist zu gross da fuer Gap +1
						n >  2: Per Definition invalide
					 */
				case 1:
					/*
					size == 1
					Sei n die Tiefe des Suchalgorithmusses:
						n == 1: Widerspruch
						n == 2: Darf max 2 gross sein
						n == 3: Darf max eine Blume sein
					 */
			}
		}
	}

	private PlayerColor getFlowerColor(final Flower f) {
		for (Map.Entry<PlayerColor, PlayerData> entry: playerData.entrySet()) {
			if (entry.getValue().flowers.contains(f)) {
				return entry.getKey();
			}
		}
		return null;
	}

	private HashSet<Flower> getFlowerBed(final Flower f) {
		PlayerColor flowerColor = getFlowerColor(f);
		if (flowerColor == null) { // TODO: Das kann nie passieren oder? Ansonsten ueberall Nullcheck
			return null;
		}

		// TODO: So oft wie wir einen Search machen koennte man glatt eine Funktion mit lambda als arg machen
		HashSet<Flower> result = new HashSet<>();
		LinkedList<Flower> queue = new LinkedList<>();
		queue.add(f);

		while (!queue.isEmpty()) {
			Flower visiting = queue.pop();
			for (Flower neighbor : allFlowers.get(visiting)) {
				if (!result.contains(neighbor) && playerData.get(flowerColor).flowers.contains(neighbor)) {
					queue.add(neighbor);
				}
				result.add(visiting);
			}
		}
		return result;
	}

	private HashSet<Flower> getDirectNeighbors(final Flower f) {
		HashSet<Flower> result = new HashSet<>();
		Position[] nodes = {f.getFirst(), f.getSecond(), f.getThird()};
		for (int i = 0; i < 3; i++) {
			try {
				// Vektoraddition
				Position third = new Position(
					nodes[i%3].getColumn() + nodes[(i+1)%3].getColumn() - nodes[(i+2)%3].getColumn(),
					nodes[i%3].getRow() + nodes[(i+1)%3].getRow() - nodes[(i+2)%3].getRow()
				);
				Flower neighbor = new Flower(nodes[i % 3], nodes[(i + 1) % 3], third);
				if (isOnBoard(neighbor)) { // Sinnvoll da Position 0 erlaubt :)
					result.add(neighbor);
				}
			} catch (IllegalArgumentException ignored) {
				// Wir brauchen nur valide Nachbarn daher alles okay
			}
		}
		return result;
	}

	private LinkedList<Flower> getAllNeighbors(final Flower f) {
		LinkedList<Flower> result = new LinkedList<>(getDirectNeighbors(f)); // TODO
		Position[] nodes = {f.getFirst(), f.getSecond(), f.getThird()};
		Position lastPoint = null;
		// Über die Positionen iterieren, die das Dreieck umgeben.
		for (int i = 0; i < 9; i++) {
			try {
				// Vektoraddition
				Position point = new Position(
					nodes[i/3].getColumn() + nodes[(i+1)/3%3].getColumn() - nodes[((i+2)/3+1)%3].getColumn(),
					nodes[i/3].getRow() + nodes[(i+1)/3%3].getRow() - nodes[((i+2)/3+1)%3].getRow()
				);
				if (lastPoint != null) {
					Flower neighbor = new Flower(nodes[i/3], lastPoint, point);
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

	// If we still dont use it tbDeleted
	private LinkedList<Flower> getBedNeighbors(final Collection<Flower> bed) {
		LinkedList<Flower> result = new LinkedList<>();
		for (Flower flower : bed) {
			for (Flower neighbor : allFlowers.get(flower)) {
				if (!bed.contains(neighbor)) {
					result.add(neighbor);
				}
			}
		}
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
				player.banFlower(f);
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
	 * @return den danugehoerigen Viewer
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

		// TODO: REFACTOR
		public LinkedList<Flower> getDirectNeighbors(Flower f) {
			return new LinkedList<>(MainBoard.this.getDirectNeighbors(f));
		}

		public LinkedList<Flower> getAllNeighbors(Flower f) {
			return new LinkedList<>(MainBoard.this.getAllNeighbors(f));
		}
	}
}
