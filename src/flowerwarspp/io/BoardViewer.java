package flowerwarspp.io;

import flowerwarspp.preset.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A {@link Viewer} implementation, providing very basic access
 * to the underlying Board, without actually allowing access to
 * their underlying data structures.
 *
 * @author Fabian Winter
 */
/* TODO: BoardViewer needs to take the actual class instead */
public class BoardViewer implements Viewer
{
	/**
	 * A reference to the {@link Board} that should be viewable
	 * through this {@link Viewer}.
	 */
	private DummyBoard board = null;

	/**
	 * A dummy constructor, only taking {@link DummyBoard}s.
	 * @param board
	 */
	public BoardViewer(DummyBoard board)
	{
		this.board = board;
	}

	/* TODO: Implement getTurn in BoardViewer */

	/**
	 * A getter for whose turn it is right now.
	 *
	 * @return
	 * The {@link PlayerColor} associated with the {@link Player} who
	 * is currently allowed to make a turn.
	 */
	@Override
	public PlayerColor getTurn()
	{
		return null;
	}

	/* TODO: Implement getSize in BoardViewer */

	/**
	 * A getter for the size of the {@link Board} in units.
	 *
	 * @return
	 * The size of the {@link Board} in units.
	 * The size is the count of triangles that have one of their
	 * three edges as part of the edge of the triangle that makes
	 * up the board.
	 */
	@Override
	public int getSize()
	{
		return this.board.getSize();
	}

	/* TODO: Implement getStatus in BoardViewer */
	@Override
	public Status getStatus()
	{
		return this.board.getStatus();
	}

	/* TODO: Implement getFlowers in BoardViewer */
	/**
	 * Get the set of {@link Flower}s that are on the board for a player.
	 *
	 * @param colour
	 * The colour of the {@link Player} whose {@link Flower}s should be gotten.
	 *
	 * @return
	 * A {@link Collection}&lt;{@link Flower}&gt; containing the flowers
	 * for the respective player.
	 */
	@Override
	public Collection<Flower> getFlowers(PlayerColor colour)
	{
		Collection<Flower> flowers = this.board.getFlowers(colour);
		return new ArrayList<>(flowers);
	}

	/* TODO: Implement getDitches in BoardViewer */
	/**
	 * Get the set of {@link Ditch}es that are on the board for a player.
	 *
	 * @param colour
	 * The colour of the {@link Player} whose {@link Ditch}es should be gotten.
	 *
	 * @return
	 * A {@link Collection}&lt;{@link Ditch}&gt; containing the ditches
	 * that were laid out by the specified player.
	 */
	@Override
	public Collection<Ditch> getDitches(PlayerColor colour)
	{
		Collection<Ditch> ditches = this.board.getDitches(colour);
		return new ArrayList<>(ditches);
	}

	/* TODO: Implement getPossibleMoves in BoardViewer */

	/**
	 * Get a {@link Collection} of {@link Move}s that are currently possible.
	 *
	 * @return
	 * A {@link Collection}&lt;{@link Move}&gt; of currently possible moves.
	 */
	@Override
	public Collection<Move> getPossibleMoves()
	{
		Collection<Move> possibleMoves = this.board.getPossibleMoves();
		return new ArrayList<>(possibleMoves);
	}

	/* TODO: Implement getPoints in BoardViewer */

	/**
	 * A getter for the amount of points of a specified {@link PlayerColor}.
	 *
	 * @param playerColour
	 * The {@link PlayerColor} of the {@link Player}
	 * whose points should be returned.
	 *
	 * @return
	 * The amount of points the {@link Player} associated with the
	 * {@link PlayerColor} currently has.
	 */
	@Override
	public int getPoints(PlayerColor playerColour)
	{
		return this.board.getPoints(playerColour);
	}
}
