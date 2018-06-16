package flowerwarspp.io;

import flowerwarspp.preset.*;

import java.util.Collection;

/**
 * A dummy board class, only for dummy use.
 *
 * @author Fabian Winter
 */
/* FIXME: Get rid of DummyBoard */
public class DummyBoard implements Board
{
	@Override
	public void make(Move move) throws IllegalStateException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Viewer viewer()
	{
		throw new UnsupportedOperationException();
	}

	public PlayerColor getTurn()
	{
		throw new UnsupportedOperationException();
	}

	public int getSize()
	{
		throw new UnsupportedOperationException();
	}

	public Status getStatus()
	{
		throw new UnsupportedOperationException();
	}

	public Collection<Flower> getFlowers(PlayerColor colour)
	{
		throw new UnsupportedOperationException();
	}

	public Collection<Ditch> getDitches(PlayerColor colour)
	{
		throw new UnsupportedOperationException();
	}

	public Collection<Move> getPossibleMoves()
	{
		throw new UnsupportedOperationException();
	}

	public int getPoints(PlayerColor playerColour)
	{
		throw new UnsupportedOperationException();
	}
}
