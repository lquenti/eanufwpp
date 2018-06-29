package flowerwarspp.board;

import flowerwarspp.preset.*;

import java.util.*;

public class FlowerWithNeighbors extends Flower {
	private HashSet<FlowerWithNeighbors> neighbors = new HashSet<>();

    public FlowerWithNeighbors(final Position first, final Position second, final Position third, final Collection<FlowerWithNeighbors> neighbors) {
		this(first, second, third);
		this.neighbors.addAll(neighbors);
	}

	public FlowerWithNeighbors(Flower flower, Collection<FlowerWithNeighbors> neighbors) {
		this(flower.getFirst(), flower.getSecond(), flower.getThird(), neighbors);
	}

    public FlowerWithNeighbors(final Position first, final Position second, final Position third) {
		super(first, second, third);
	}

	public FlowerWithNeighbors(Flower flower) {
		super(flower.getFirst(), flower.getSecond(), flower.getThird());
	}

	public HashSet<FlowerWithNeighbors> getNeighbors() {
		return neighbors;
	}
}
