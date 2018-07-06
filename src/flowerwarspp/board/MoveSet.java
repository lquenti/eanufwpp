package flowerwarspp.board;

import flowerwarspp.preset.*;

import java.util.*;

public class MoveSet extends AbstractSet<Move> {
	private HashSet<Move> flowerMoves;
	private HashMap<Flower, HashSet<Flower>> flowerMap;

	private HashSet<Move> ditchMoves;

	private HashSet<Move> otherMoves;

	public MoveSet(Collection<Move> list) {
		this();
		addAll(list);
	}

	public MoveSet() {
		flowerMoves = new HashSet<>();
		flowerMap = new HashMap<>();
		ditchMoves = new HashSet<>();
		otherMoves = new HashSet<>();
	}

	public int size() {
		return flowerMoves.size() + ditchMoves.size() + otherMoves.size();
	}

	public boolean add(Move e) {
		if (contains(e)) {
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

	public boolean removeMovesContaining(Flower flower) {
		if (flowerMap.containsKey(flower)) {
			return removeAll(getMovesContaining(flower));
		}
		return false;
	}

	public boolean contains(Object o) {
		return flowerMoves.contains(o)
		    || ditchMoves.contains(o)
		    || otherMoves.contains(o);
	}

	public boolean containsMovesContaining(Flower flower) {
		return flowerMap.containsKey(flower);
	}

	public Set<Move> getFlowerMoves() {
		return Collections.unmodifiableSet(flowerMoves);
	}

	public Set<Flower> getFlowers() {
		return Collections.unmodifiableSet(flowerMap.keySet());
	}

	public Set<Flower> getFlowersCombinableWith(Flower flower) {
		return Collections.unmodifiableSet(flowerMap.get(flower));
	}

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

	public Set<Move> getDitchMoves() {
		return Collections.unmodifiableSet(ditchMoves);
	}

	public Iterator<Move> iterator() {
		return new MoveSetIterator();
	}

	private class MoveSetIterator implements Iterator<Move> {
		ArrayList<Iterator<Move>> iterators;
		Iterator<Move> lastIterator = null;

		public MoveSetIterator() {
			iterators = new ArrayList<>(3);
			iterators.add(flowerMoves.iterator());
			iterators.add(ditchMoves.iterator());
			iterators.add(otherMoves.iterator());
		}

		public boolean hasNext() {
			for (Iterator<Move> it : iterators) {
				if (it.hasNext()) {
					return true;
				}
			}
			return false;
		}

		public Move next() throws NoSuchElementException {
			for (Iterator<Move> it : iterators) {
				if (it.hasNext()) {
					lastIterator = it;
					return it.next();
				}
			}
			throw new NoSuchElementException();
		}

		public void remove() throws IllegalStateException {
			if (lastIterator == null) {
				throw new IllegalStateException();
			}
			lastIterator.remove();
		}
	}
}
