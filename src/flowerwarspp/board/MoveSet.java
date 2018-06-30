package flowerwarspp.board;

import flowerwarspp.preset.*;

import java.util.*;

public class MoveSet extends AbstractSet<Move> {
	private HashSet<Move> flowerMoves;
	private HashMap<Flower, HashSet<Move>> flowerMap;

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
		try {
			switch (e.getType()) {
				case Flower:
					flowerMoves.add(e);
					Flower flowers[] = {e.getFirstFlower(), e.getSecondFlower()};
					for (Flower flower : flowers) {
						if (!flowerMap.containsKey(flower)) {
							flowerMap.put(flower, new HashSet<>());
						}
						flowerMap.get(flower).add(e);
					}
					break;
				case Ditch:
					ditchMoves.add(e);
					break;
				default:
					otherMoves.add(e);
			}
		} catch (Exception ex) {
			System.out.println(e);
			ex.printStackTrace();
			System.exit(1);
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
				for (Flower flower : flowers) {
					HashSet<Move> flowerMoves = flowerMap.get(flower);
					flowerMoves.remove(o);
					if (flowerMoves.isEmpty()) {
						flowerMap.remove(flower);
					}
				}
				flowerMap.get(((Move)o).getFirstFlower()).remove(o);
				flowerMap.get(((Move)o).getSecondFlower()).remove(o);
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
		return removeAll(flowerMap.get(flower));
	}

	public boolean contains(Object o) {
		return flowerMoves.contains(o)
		    || ditchMoves.contains(o)
		    || otherMoves.contains(o);
	}

	public boolean containsMovesContaining(Flower flower) {
		return flowerMap.get(flower) == null;
	}

	public HashSet<Move> getFlowerMoves() {
		return new HashSet<>(flowerMoves);
	}

	public Set<Flower> getFlowers() {
		return flowerMap.keySet();
	}

	public HashSet<Move> getMovesContaining(Flower flower) {
		return new HashSet<>(flowerMap.get(flower));
	}

	public HashSet<Move> getDitchMoves() {
		return new HashSet<>(ditchMoves);
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
