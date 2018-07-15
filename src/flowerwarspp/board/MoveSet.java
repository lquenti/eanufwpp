package flowerwarspp.board;

import flowerwarspp.preset.Ditch;
import flowerwarspp.preset.Flower;
import flowerwarspp.preset.Move;

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
 */
public class MoveSet extends AbstractSet<Move> {
	/**
	 * Die Menge der Spielzüge, die {@link Flower}s enthalten.
	 */
	private HashSet<Move> flowerMoves;

	/**
	 * Eine Tabelle, die jeder {@link Flower} die {@link Flower}s zuordnet, mit denen sie sich so
	 * kombinieren lässt, dass sich Spielzüge ergeben, die in dieser Menge enthalten sind.
	 */
	private HashMap<Flower, HashSet<Flower>> flowerMap;

	/**
	 * Die Menge der Spielzüge, die {@link Ditch}es enthalten.
	 */
	private HashSet<Move> ditchMoves;

	/**
	 * Die Menge der Spielzüge, die weder {@link Flower}s noch {@link Ditch}es enthalten.
	 */
	private HashSet<Move> otherMoves;

	/**
	 * Erzeugt eine neue Menge, die die Spielzüge, die in der angegebenen {@link Collection}
	 * enthalten sind, enthält.
	 *
	 * @param list
	 * 		Die {@link Collection}, deren Elemente zu dieser Menge hinzugefügt werden sollen.
	 *
	 * @throws NullPointerException
	 * 		falls die angegebene {@link Collection} null ist.
	 */
	public MoveSet(Collection<Move> list) throws NullPointerException {
		this();
		addAll(list);
	}

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
	 * Erzeugt eine Kopie eines MoveSets. Die Implementation ist wesentlich schneller als die für
	 * allgemeine {@link Collection}s, da die initiale Kapazität der HashSets korrekt gesetzt wird.
	 *
	 * @param original
	 * 		Das MoveSet, das kopiert werden soll.
	 *
	 * @throws NullPointerException
	 * 		falls das angegebene {@link MoveSet} null ist.
	 */
	public MoveSet(MoveSet original) throws NullPointerException {
		flowerMoves = new HashSet<>(original.flowerMoves);
		flowerMap = new HashMap<>();
		for (Map.Entry<Flower, HashSet<Flower>> entry : original.flowerMap.entrySet()) {
			flowerMap.put(entry.getKey(), new HashSet<>(entry.getValue()));
		}
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
	 * @param e
	 * 		Das Element, dass hinzugefügt werden soll
	 *
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
	 * Gibt zurück, ob diese Menge das angegebene Element enthält.
	 *
	 * @param o
	 * 		Das Element, dessen Enthaltensein getestet werden soll
	 *
	 * @return true, falls diese Menge das angegebene Element enthält
	 */
	public boolean contains(Object o) {
		return flowerMoves.contains(o) || ditchMoves.contains(o) || otherMoves.contains(o);
	}

	/**
	 * Entfernt das angegebene Element aus dieser Menge, fall es enthalten ist.
	 *
	 * @param o
	 * 		Das Element, das entfernt werden soll
	 *
	 * @return true, falls das Element enthalten war
	 */
	public boolean remove(Object o) {
		if (!contains(o)) {
			return false;
		}
		switch (((Move) o).getType()) {
			case Flower:
				flowerMoves.remove(o);
				Flower flowers[] = {((Move) o).getFirstFlower(), ((Move) o).getSecondFlower()};
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
	 * Entfernt alle Spielzüge aus dieser Menge, die eine gegebene {@link Flower} enthalten.
	 *
	 * @param flower
	 * 		Die {@link Flower}, deren {@link Move}s entfernt werden sollen
	 *
	 * @return true, falls {@link Move}s mit der angegebenen {@link Flower} enthalten waren
	 */
	public boolean removeMovesContaining(Flower flower) {
		if (flowerMap.containsKey(flower)) {
			return removeAll(getMovesContaining(flower));
		}
		return false;
	}

	/**
	 * Gibt alle in dieser Menge enthaltenen {@link Move}s zurück, die die angegebene
	 * {@link Flower}
	 * enthalten.
	 *
	 * @param flower
	 * 		Die {@link Flower}, für die die zugehörigen {@link Move}s zurückgegeben werden sollen
	 *
	 * @return Alle in dieser Menge enthaltenen {@link Move}s, die die angegebene {@link Flower}
	 * enthalten
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
	 * Gibt zurück, ob diese Menge {@link Move}s enthält, die die angegebene {@link Flower}
	 * enthalten.
	 *
	 * @param flower
	 * 		Die {@link Flower}, für die geprüft werden soll, ob zugehörige {@link Move} enthalten
	 * 		sind
	 *
	 * @return true, falls diese Menge {@link Move}s enthält, die die angegebene {@link Flower}
	 * enthalten
	 */
	public boolean containsMovesContaining(Flower flower) {
		return flowerMap.containsKey(flower);
	}

	/**
	 * Gibt alle in dieser Menge enthaltenen {@link Move}s zurück, die {@link Flower}s enthalten.
	 * Die zurückgegebene Menge ist ein unmodifiableSet.
	 *
	 * @return Alle in dieser Menge enthaltenen {@link Move}s, die {@link Flower}s enthalten
	 */
	public Set<Move> getFlowerMoves() {
		return Collections.unmodifiableSet(flowerMoves);
	}

	/**
	 * Gibt alle {@link Flower}s zurück, für die diese Menge Spielzüge enthält, die die {@link
	 * Flower}s enthalten. Die zurückgegebene Menge ist ein
	 * {@link java.util.Collections.UnmodifiableSet}.
	 *
	 * @return Alle {@link Flower}s, für die diese Menge {@link Move}s enthält, die die {@link
	 * Flower} enthalten
	 */
	public Set<Flower> getFlowers() {
		return Collections.unmodifiableSet(flowerMap.keySet());
	}

	/**
	 * Gibt alle {@link Flower}s zurück, mit denen sich die angegebene {@link Flower} kombinieren
	 * lässt, sodass sich {@link Move}s ergeben, die in dieser Menge enthalten sind. Die
	 * zurückgegebene Menge ist ein {@link java.util.Collections.UnmodifiableSet}.
	 *
	 * @param flower
	 * 		Die {@link Flower}, für die die kombinierbaren {@link Flower}s zurückgegeben werden
	 * 		sollen
	 *
	 * @return Alle {@link Flower}s, mit denen sich die angegebene {@link Flower} kombinieren
	 * lässt, sodass sich {@link Move}s ergeben, die in dieser Menge enthalten sind
	 */
	public Set<Flower> getFlowersCombinableWith(Flower flower) {
		return Collections.unmodifiableSet(flowerMap.get(flower));
	}

	/**
	 * Gibt eine {@link Map} zurück, die jeder erlaubten {@link Flower} die {@link Flower}s
	 * zuordnet, mit denen sie sich kombinieren lässt. Das Ergebnis ist eine {@link
	 * java.util.Collections.UnmodifiableMap}.
	 *
	 * @return Eine {@link Map}, die jeder erlaubten {@link Flower} die {@link Flower}s zuordnet,
	 * mit denen sie sich kombinieren lässt.
	 */
	public Map<Flower, HashSet<Flower>> getFlowerMap() {
		return Collections.unmodifiableMap(flowerMap);
	}

	/**
	 * Gibt alle in dieser Menge enthaltenen {@link Move}s zurück, die {@link Ditch} enthalten. Die
	 * zurückgegebene Menge ist ein {@link java.util.Collections.UnmodifiableSet}.
	 *
	 * @return Alle in dieser Menge enthaltenen {@link Move}s, die {@link Ditch}es enthalten
	 */
	public Set<Move> getDitchMoves() {
		return Collections.unmodifiableSet(ditchMoves);
	}

	/**
	 * Gibt einen {@link MoveSetIterator} über die Elemente dieser Menge zurück. Der Iterator
	 * iteriert zuerst über die Blumenzüge, dann über die Grabenzüge und dann über alle anderen
	 * Züge. Innerhalb dieser Kategorien gibt es allerdings keine bestimmte Ordnung.
	 *
	 * @return Ein Iterator über die Elemente dieser Menge
	 */
	public Iterator<Move> iterator() {
		return new MoveSetIterator();
	}

	/**
	 * Klasse für Iteratoren über ein {@link MoveSet}.
	 */
	private class MoveSetIterator implements Iterator<Move> {
		/**
		 * Die Iteratoren der einzelnen Unterkategorien.
		 */
		List<Iterator<Move>> iterators;

		/**
		 * Der Iterator, der zuletzt ein Element zurückgegeben hat.
		 */
		Iterator<Move> lastIterator = null;

		/**
		 * Erzeugt einen neuen MoveSetIterator.
		 */
		public MoveSetIterator() {
			iterators = Arrays.asList(flowerMoves.iterator(), ditchMoves.iterator(),
					otherMoves.iterator());
		}

		/**
		 * Gibt true zurück, falls die Iteration noch Elemente enthält.
		 * <p>
		 *
		 * @return true, falls die Iteration noch Elemente enthält
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
		 * <p>
		 *
		 * @return das nächste Element der Iteration throws NoSuchElementException falls es keine
		 * weiteren Elemente gibt
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
		 * @throws IllegalStateException
		 * 		falls die next-Methode noch nicht aufgerufen wurde oder die remove-Methode seit dem
		 * 		letzten Aufruf der next-Methode schon einmal aufgerufen wurde
		 */
		public void remove() throws IllegalStateException {
			if (lastIterator == null) {
				throw new IllegalStateException();
			}
			lastIterator.remove();
		}
	}
}
