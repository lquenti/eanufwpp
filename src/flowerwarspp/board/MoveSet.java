package flowerwarspp.board;

import flowerwarspp.board.MoveSet;
import flowerwarspp.preset.*;
import java.util.*;

/**
 * <p>Klasse zur Verwaltung einer Menge von Spielzügen. Diese Klasse bietet konstante Laufzeit für
 * grundlegende Operationen (add, remove, contains), sowie für Abfragen von verschiedenen Kategorien
 * von Spielzügen. Das Nullelement wird nicht unterstützt.</p>
 *
 * <p><strong>Diese Implementation ist nicht synchronisiert.</strong> Falls mehrere Threads
 * gleichzeitig auf ein MoveSet zugreifen und mindestens einer der Threads das MoveSet verändert,
 * muss es extern synchronisiert werden. Dies geht zum Beispiel mit 
 * <code>Collections.synchronizedSet</code>.<p>
 *
 * @author Thilo Wischmeyer
 */
public class MoveSet extends AbstractSet<Move> {
	/**
	 * Die Menge der Spielzüge, die Blumen enthalten.
	 */
	private HashSet<Move> flowerMoves;

	/**
	 * Eine Tabelle, die jeder Blume die Blumen zuordnet, mit denen sie sich so kombinieren lässt,
	 * dass sich Spielzüge ergeben, die in dieser Menge enthalten sind.
	 */
	private HashMap<Flower, HashSet<Flower>> flowerMap;

	/**
	 * Die Menge der Spielzüge, die Gräben enthalten.
	 */
	private HashSet<Move> ditchMoves;

	/**
	 * Die Menge der Spielzüge, die weder Blumen noch Gräben enthalten.
	 */
	private HashSet<Move> otherMoves;

	/**
	 * Erzeugt eine neue, leere Menge.
	 */
	public MoveSet() {
		flowerMoves = new HashSet<>();
		flowerMap = new HashMap<>();
		ditchMoves = new HashSet<>();
		otherMoves = new HashSet<>();
	}

	/**
	 * Erzeugt eine neue Menge, die die Spielzüge, die in der angegebenen Collection enthalten
	 * sind, enthält.
	 *
	 * @param list Die Collection, deren Elemente zu dieser Menge hinzugefügt werden sollen.
	 * @throws NullPointerException falls die angegebene Collection null ist.
	 */
	public MoveSet(Collection<Move> list) throws NullPointerException {
		this();
		addAll(list);
	}

	/**
	 * Erzeugt eine Kopie eines MoveSets. Die Implementation ist wesentlich schneller als die
	 * für allgemeine Collections, da die initiale Kapazität der HashSets korrekt gesetzt wird.
	 *
	 * @param original Das MoveSet, das kopiert werden Soll.
	 * @throws NullPointerException falls das angegebene MoveSet null ist.
	 */
	public MoveSet(MoveSet original) throws NullPointerException {
		flowerMoves = new HashSet<>(original.flowerMoves);
		flowerMap = new HashMap<>(original.flowerMap);
		ditchMoves = new HashSet<>(original.ditchMoves);
		otherMoves = new HashSet<>(original.otherMoves);
	}

	/**
	 * Gibt die Anzahl der Elemente dieser Menge zurück.
	 *
	 * @return Die Anzahl der Elemente dieser Menge
	 */
	public int size() {
		return flowerMoves.size() + ditchMoves.size() + otherMoves.size();
	}

	/**
	 * Fügt das angegebene Element zu dieser Menge hinzu, falls es nicht null ist und noch nicht
	 * vorhanden ist.
	 *
	 * @param e Das Element, dass hinzugefügt werden soll
	 * @return true, falls das Element nicht null ist und nicht schon in der Menge enthalten war
	 */
	public boolean add(Move e) {
		if (contains(e) || e == null) {
			return false;
		}
		switch (e.getType()) {
			case Flower:
				flowerMoves.add(e);
				Flower flowers[] = {e.getFirstFlower(), e.getSecondFlower()};
				for (Flower flower : flowers) {
					flowerMap.putIfAbsent(flower, new HashSet<>());
				}
				flowerMap.get(flowers[0]).add(flowers[1]);
				flowerMap.get(flowers[1]).add(flowers[0]);
				break;
			case Ditch:
				ditchMoves.add(e);
				break;
			default:
				otherMoves.add(e);
		}
		return true;
	}

	/**
	 * Entfernt das angegebene Element aus dieser Menge, fall es enthalten ist.
	 *
	 * @param o Das Element, das entfernt werden soll
	 * @return true, falls das Element enthalten war
	 */
	public boolean remove(Object o) {
		if (!contains(o)) {
			return false;
		}
		switch (((Move)o).getType()) {
			case Flower:
				flowerMoves.remove((Move)o);
				Flower flowers[] = {((Move)o).getFirstFlower(), ((Move)o).getSecondFlower()};
				flowerMap.get(flowers[0]).remove(flowers[1]);
				flowerMap.get(flowers[1]).remove(flowers[0]);
				for (Flower flower : flowers) {
					if (flowerMap.get(flower).isEmpty()) {
						flowerMap.remove(flower);
					}
				}
				break;
			case Ditch:
				ditchMoves.remove(o);
				break;
			default:
				otherMoves.remove(o);
		}
		return true;
	}

	/**
	 * Entfernt alle Spielzüge aus dieser Menge, die eine gegebene Blume enthalten.
	 *
	 * @param flower Die Blume, deren Züge entfernt werden sollen
	 * @return true, falls Züge mit der angegebenen Blume enthalten waren
	 */
	public boolean removeMovesContaining(Flower flower) {
		if (flowerMap.containsKey(flower)) {
			return removeAll(getMovesContaining(flower));
		}
		return false;
	}

	/**
	 * Gibt zurück, ob diese Menge das angegebene Element enthält.
	 *
	 * @param o Das Element, dessen Enthaltensein getestet werden soll
	 * @return true, falls diese Menge das angegebene Element enthält
	 */
	public boolean contains(Object o) {
		return flowerMoves.contains(o)
		    || ditchMoves.contains(o)
		    || otherMoves.contains(o);
	}

	/**
	 * Gibt zurück, ob diese Menge Spielzüge enthält, die die angegebene Blume enthalten.
	 *
	 * @param flower Die Blume, für die geprüft werden soll, ob zugehörige Züge enthalten sind
	 * @return true, falls diese Menge Spielzüge enthält, die die angegebene Blume enthalten
	 */
	public boolean containsMovesContaining(Flower flower) {
		return flowerMap.containsKey(flower);
	}

	/**
	 * Gibt alle in dieser Menge enthaltenen Spielzüge zurück, die Blumen enthalten. Die
	 * zurückgegebene Menge ist unveränderlich.
	 *
	 * @return Alle in dieser Menge enthaltenen Spielzüge, die Blumen enthalten
	 */
	public Set<Move> getFlowerMoves() {
		return Collections.unmodifiableSet(flowerMoves);
	}

	/**
	 * Gibt alle Blumen zurück, für die diese Menge Spielzüge enthält, die die Blume enthalten.
	 * Die zurückgegebene Menge ist unveränderlich.
	 *
	 * @return Alle Blumen, für die diese Menge Spielzüge enthält, die die Blume enthalten
	 */
	public Set<Flower> getFlowers() {
		return Collections.unmodifiableSet(flowerMap.keySet());
	}

	/**
	 * Gibt alle Blumen zurück, mit denen sich die angegebene Blume kombinieren lässt, sodass sich
	 * Spielzüge ergeben, die in dieser Menge enthalten sind. Die zurückgegebene Menge ist
	 * unveränderlich.
	 *
	 * @param flower Die Blume, für die die kombinierbaren Blumen zurückgegeben werden sollen
	 * @return Alle Blumen, mit denen sich die angegebene Blume kombinieren lässt, sodass sich
	 * Spielzüge ergeben, die in dieser Menge enthalten sind
	 */
	public Set<Flower> getFlowersCombinableWith(Flower flower) {
		return Collections.unmodifiableSet(flowerMap.get(flower));
	}

	/**
	 * Gibt alle in dieser Menge enthaltenen Spielzüge zurück, die die angegebene Blume enthalten.
	 * 
	 * @param flower Die Blume, für die die zugehörigen Züge zurückgegeben werden sollen
	 * @return Alle in dieser Menge enthaltenen Spielzüge, die die angegebene Blume enthalten
	 */
	public HashSet<Move> getMovesContaining(Flower flower) {
		if (flowerMap.containsKey(flower)) {
			HashSet<Move> result = new HashSet<>();
			for (Flower secondFlower : flowerMap.get(flower)) {
				result.add(new Move(flower, secondFlower));
			}
			return result;
		}
		return null;
	}

	/**
	 * Gibt alle in dieser Menge enthaltenen Spielzüge zurück, die Gräben enthalten. Die
	 * zurückgegebene Menge ist unveränderlich.
	 *
	 * @return Alle in dieser Menge enthaltenen Spielzüge, die Gräben enthalten
	 */
	public Set<Move> getDitchMoves() {
		return Collections.unmodifiableSet(ditchMoves);
	}

	/**
	 * Gibt einen Iterator über die Elemente dieser Menge zurück. Der Iterator iteriert zuerst über
	 * die Blumenzüge, dann über die Grabenzüge und dann über alle anderen Züge. Innerhalb dieser
	 * Kategorien gibt es allerdings keine bestimmte Ordnung.
	 *
	 * @return Ein Iterator über die Elemente dieser Menge
	 */
	public Iterator<Move> iterator() {
		return new MoveSetIterator();
	}

	/**
	 * Klasse für Iteratoren über ein MoveSet.
	 */
	private class MoveSetIterator implements Iterator<Move> {
		/**
		 * Die Iteratoren der einzelnen Unterkategorien.
		 */
		ArrayList<Iterator<Move>> iterators;

		/**
		 * Der Iterator, der zuletzt ein Element zurückgegeben hat.
		 */
		Iterator<Move> lastIterator = null;

		/**
		 * Erzeugt einen neuen MoveSetIterator.
		 */
		public MoveSetIterator() {
			iterators = new ArrayList<>(3);
			iterators.add(flowerMoves.iterator());
			iterators.add(ditchMoves.iterator());
			iterators.add(otherMoves.iterator());
		}

		/**
		 * Gibt true zurück, falls die Iteration noch Elemente enthält.
		 *
		 * return true, falls die Iteration noch Elemente enthält
		 */
		public boolean hasNext() {
			for (Iterator<Move> it : iterators) {
				if (it.hasNext()) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Gibt das nächste Element der Iteration zurück.
		 *
		 * return das nächste Element der Iteration
		 * throws NoSuchElementException falls es keine weiteren Elemente gibt
		 */
		public Move next() throws NoSuchElementException {
			for (Iterator<Move> it : iterators) {
				if (it.hasNext()) {
					lastIterator = it;
					return it.next();
				}
			}
			throw new NoSuchElementException();
		}

		/**
		 * Entfernt das letzte von diesem Iterator zurückgegebene Element aus der zugehörigen
		 * Menge.
		 *
		 * @throws IllegalStateException falls die next-Methode noch nicht aufgerufen wurde oder
		 * die remove-Methode seit dem letzten Aufruf der next-Methode schon einmal aufgerufen wurde
		 */
		public void remove() throws IllegalStateException {
			if (lastIterator == null) {
				throw new IllegalStateException();
			}
			lastIterator.remove();
		}
	}
}
