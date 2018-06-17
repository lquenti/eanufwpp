package flowerwarspp.player;

import flowerwarspp.preset.Board;
import flowerwarspp.preset.Move;
import flowerwarspp.preset.PlayerColor;
import flowerwarspp.preset.Status;

import java.rmi.RemoteException;

public class Player implements flowerwarspp.preset.Player {

    /**
     * A utility enum to facilitate security in regards to the method lifecycle. Used to reference the method that was
     * last called, in compliance with the lifecycle dictated by {@link flowerwarspp.preset.Player}.
     *
     * @see flowerwarspp.preset.Player
     */
    private enum PlayerFunction {
        NULL,
        REQUEST,
        CONFIRM,
        UPDATE
    }

    /**
     * Provides the player's own board for making and processing moves without having to access the main board.
     */
    private Board board = null;

    /**
     * The player's playerColor, as indicated by the {@link PlayerColor} enum.
     */
    private PlayerColor playerColour;

    /**
     * References the function that was last called in order to ensure the method call cycles integrity.
     */
    private PlayerFunction cycleState;

    /* TODO: Integration with the GUI. For now we'll stick to StdIn for reading in the move. */

    /**
     * Request a move from the player. The returned move will be represented using the preset data types to ensure
     * universal compatibility.<br> If the player is unable to make a move, an exception is thrown.
     *
     * @return The move that has been made by the player
     * @throws Exception       If the player is unable to make or provide a move or if the function is called out of
     *                         turn
     * @throws RemoteException If network communication failed, was erroneous or interrupted
     * @see flowerwarspp.preset.Player#request()
     */
    @Override
    public Move request() throws Exception, RemoteException {
        // State validation
        if ( this.cycleState == PlayerFunction.NULL ) throw new Exception("The player must first be initialized!");
        if ( this.cycleState != PlayerFunction.REQUEST )
            throw new Exception("The function was called out of turn! update() should have been called prior.");

        // Do stuff...
        // I would need an IO interface here for reading in a move locally...

        // Update state
        this.cycleState = PlayerFunction.CONFIRM;
        return null;
    }

    /* TODO: All the things need doing. */

    /**
     * Used to confirm the previously made move by comparing the status of the main program's board and the board stored
     * by this player.<br> If there's a disparity between the two status an exception is thrown.
     *
     * @param status Status of the main program's board after the last requested move has been processed by the main
     *               program
     * @throws Exception       If the two status are different or if the method was called out of turn.
     * @throws RemoteException If network communication failed, was erroneous or interrupted
     * @see flowerwarspp.preset.Player#confirm(Status)
     */
    @Override
    public void confirm( Status status ) throws Exception, RemoteException {
        // State validation
        if ( this.cycleState == PlayerFunction.NULL ) throw new Exception("The player must first be initialized!");
        if ( this.cycleState != PlayerFunction.CONFIRM )
            throw new Exception("The function was called out of turn! request() should have been called prior.");

        // Do stuff...

        // Update state
        this.cycleState = PlayerFunction.UPDATE;
    }

    /* TODO: Any and all of this. */

    /**
     * Updates the board stored by this player for referencing purposes by processing the other player's move as passed
     * and checking for disparities in the status of the boards of this player and of the main program after the given
     * move has been processed.
     *
     * @param opponentMove The last move made by the opponent.
     * @param status       Status of the main program's board after the opponent's last move has been processed.
     * @throws Exception       If the status of this board is in disparity with the main program's board, or if the
     *                         method was called out of turn.
     * @throws RemoteException If network communication failed, was erroneous or interrupted
     * @see flowerwarspp.preset.Player#update(Move, Status)
     */
    @Override
    public void update( Move opponentMove, Status status ) throws Exception, RemoteException {
        // State validation
        if ( this.cycleState == PlayerFunction.NULL ) throw new Exception("The player must first be initialized!");
        if ( this.cycleState != PlayerFunction.UPDATE )
            throw new Exception("The function was called out of turn! confirm() should have been called prior.");

        // Do stuff...

        // Update state
        this.cycleState = PlayerFunction.REQUEST;
    }

    /* TODO: This also needs doing, I have no board to play with :c */

    /**
     * Initializes a new player on a board with given size, sporting the indicated colour. Can be called at will during
     * the running game, in that case the current game will end and a new game will be started with this newly created
     * player.
     *
     * @param boardSize    Size of the board in units.
     * @param playerColour The {@link PlayerColor colour} of the player.
     * @throws Exception       If it is not possible to initialize the player
     * @throws RemoteException If network communication failed, was erroneous or interrupted
     * @see flowerwarspp.preset.Player#init(int, PlayerColor)
     */
    @Override
    public void init( int boardSize, PlayerColor playerColour ) throws Exception, RemoteException {

        // Set the colour
        this.playerColour = playerColour;

        // Make a board here and do stuff...

        // Now set the function life cycle according to this player's colour
        if ( this.playerColour == PlayerColor.Red ) {
            // If we have a Red player, first move is request()
            this.cycleState = PlayerFunction.REQUEST;
        } else {
            // The Blue player has to wait for the Red player to move first
            this.cycleState = PlayerFunction.UPDATE;
        }
    }

    /**
     * A getter function to return the player's current colour.
     *
     * @return The player's {@link #playerColour colour}.
     */
    public PlayerColor getPlayerColour() {
        return playerColour;
    }
}
