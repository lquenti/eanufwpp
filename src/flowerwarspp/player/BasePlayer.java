package flowerwarspp.player;

import flowerwarspp.board.MainBoard;
import flowerwarspp.io.BoardViewer;
import flowerwarspp.preset.*;

import java.rmi.RemoteException;
import java.util.Collection;


abstract class BasePlayer implements flowerwarspp.preset.Player {

    static final String exception_NoInit =
            "Der Spieler muss zuerst initialisiert werden!";

    private static final String exception_UnexpectedCall =
            "Unerwarteter Methoden-Aufruf.";

    static final String exception_CycleRequest =
            exception_UnexpectedCall + " Es haette update() aufgerufen werden sollen.";

    private  static final  String exception_CycleUpdate =
            exception_UnexpectedCall + " Es haette confirm() aufgerufen werden sollen.";

    private static final String exception_CycleConfirm =
            exception_UnexpectedCall + " Es haette request() aufgerufen werden sollen.";

    private static final String exception_StatusError =
            "Der Status des Hauptprogramms und der Status des Spielbretts dieses Spielers stimmen nicht ueberein!";

    /**
     * Ein unterstuetzender enum um die Ausfuehrung der durch das Interface {@link flowerwarspp.preset.Player}
     * verlangten Methoden in der korrekten Reihenfolge zu sichern.
     *
     * @see flowerwarspp.preset.Player
     */
    protected enum PlayerFunction {
        NULL,
        REQUEST,
        CONFIRM,
        UPDATE
    }

    /**
     * Stellt diesem Objekt ein eigenes {@link Board} zur Verfuegung, um die Durchfuehrung der eigenen und gegnerischen
     * Zuege nachbilden zu koennen.
     */
    protected Board board;

    /**
     * Die Farbe dieses Spielers, nach den Vorgaben des enums {@link PlayerColor}.
     */
    private PlayerColor playerColour;

    /**
     * Wird genutzt, um den aktuellen Status des Spieler-Lebenszyklus (also der Zyklus, welcher im Interface {@link
     * flowerwarspp.preset.Player} diktiert wird) darzustellen.
     */
    protected PlayerFunction cycleState;

    /**
     * Mit Hilfe des BoardViewer werden relevante und {@link Board} bezogene Daten angefordert und intern verwendet.
     */
    private BoardViewer viewer;

    /**
     * Ein <code>default</code>-Konstruktor, welcher die Instanzvariablen mit Basiswerten initialisiert.
     */
    protected BasePlayer() {
        this.playerColour = PlayerColor.Red;
        this.board = null;
        this.cycleState = PlayerFunction.NULL;
    }


    /* INFO: Method is abstract because requesting a move from the player works differently with each implementation */

    /**
     * Methode zum Anfordern eines Zugs.
     * 
     * @return Der vom Spieler geforderte Zug
     * @throws Exception       Falls der Spieler nicht in der Lage war, einen Zug zu liefern oder falls diese Methode
     *                         zum falschen Zeitpunkt innerhalb des Zyklus aufgerufen worden ist
     * @throws RemoteException Falls ein Fehler waehrend der Netzwerk-Kommunikation aufgetreten ist
     */
    @Override
    public Move request() throws Exception, RemoteException {
        // State validation
        if ( this.getCycleState() == PlayerFunction.NULL ) throw new Exception(exception_NoInit);
        if ( this.getCycleState() != PlayerFunction.REQUEST ) throw new Exception(exception_CycleRequest);

        Move move = requestMove();

        // We just assume the move is valid, might need to check later on
        // For now we just make the move as returned by requestMove
        this.board.make(move);

        // Update state
        this.cycleState = PlayerFunction.CONFIRM;

        return move;
    }

    protected abstract Move requestMove() throws Exception, RemoteException;


    /* TODO: All the things need doing. */

    /**
     * Stellt die vom Interface {@link flowerwarspp.preset.Player} geforderte Methode {@link
     * flowerwarspp.preset.Player#confirm(Status)} bereit.
     *
     * @param status Status des Spielbretts des Hauptprogramms nach Ausfuehren des zuletzt mit {@link #request()}
     *               geholten Zuges
     * @throws Exception       Falls sich der eigene Status und der Status des Hauptprogramms unterscheiden oder falls
     *                         diese Methode zum falschen Zeitpunkt innerhalb des Zyklus aufgerufen worden ist
     * @throws RemoteException Falls ein Fehler waehrend der Netzwerk-Kommunikation aufgetreten ist
     */
    @Override
    public void confirm( Status status ) throws Exception, RemoteException {
        // State validation
        if ( this.cycleState == PlayerFunction.NULL ) throw new Exception(exception_NoInit);
        if ( this.cycleState != PlayerFunction.CONFIRM ) throw new Exception(exception_CycleConfirm);

        // Verify that player's status and main program's status are equal
        Status playerBoardState = this.viewer.getStatus();

        if ( ! playerBoardState.equals(status) ) throw new Exception(exception_StatusError);

        // Update state
        this.cycleState = PlayerFunction.UPDATE;
    }

    /* TODO: Any and all of this. */

    /**
     * Stellt die vom Interface {@link flowerwarspp.preset.Player} geforderte Methode {@link
     * flowerwarspp.preset.Player#confirm(Status)} bereit.
     *
     * @param opponentMove Zug des Gegenspielers
     * @param status       Status des Spielbretts des Hauptprogramms nach Ausfuehren des Zuges des Gegenspielers
     * @throws Exception       Falls sich die Status des eigenen Spielbretts nach Ausfuehren des gegnerischen Zuges und
     *                         des Hauptprogramms unterscheiden oder falls diese Methode zum falschen Zeitpunkt
     *                         innerhalb des Zyklus aufgerufen worden ist
     * @throws RemoteException Falls ein Fehler waehrend der Netzwerk-Kommunikation aufgetreten ist
     */
    @Override
    public void update( Move opponentMove, Status status ) throws Exception, RemoteException {
        // State validation
        if ( this.cycleState == PlayerFunction.NULL ) throw new Exception(exception_NoInit);
        if ( this.cycleState != PlayerFunction.UPDATE ) throw new Exception(exception_CycleUpdate);

        // Process the opponent's move on this player's own board
        this.board.make(opponentMove);

        // Verify the status
        Status playerBoardStatus = this.viewer.getStatus();

        if ( ! playerBoardStatus.equals(status) ) throw new Exception(exception_StatusError);

        // Update state
        this.cycleState = PlayerFunction.REQUEST;
    }

    /* TODO: Properly handle beginning a new gme (i.e. calling init() in the middle of a running game) */

    /**
     * Initialisiert einen Spieler, indem dieser mit einem neuen Spielbrett passender Groesse und der gewuenschten Farbe
     * versehen wird. Falls diese Methode waehrend eines laufenden Spiels aufgerufen wird, wird dieses beendet und mit
     * dem neu initialisierten Spieler wird ein neues Spiel begonnen.
     *
     * @param boardSize    Spielbrettgroesse
     * @param playerColour Farbe des Spielers
     * @throws Exception       Falls waehrend der Initialisierung ein Fehler auftrat
     * @throws RemoteException Falls ein Fehler waehrend der Netzwerk-Kommunikation aufgetreten ist
     */
    @Override
    public void init( int boardSize, PlayerColor playerColour ) throws Exception, RemoteException {

        // Set the colour
        this.playerColour = playerColour;

        if ( this.board == null ) {

            // FIXME: Replace DummyBoard with working implementation of MainBoard (or equivalent)
            this.board = new MainBoard(boardSize);
            this.viewer = new BoardViewer(board);
        } else {

            // Here we need to handle ending the current game and starting a new one, some form of feedback would be
            // necessary to make that happen with the main program.
            // TODO: Restart game with new player
        }

        // Now set the function life cycle according to this player's colour
        if ( this.playerColour == PlayerColor.Red ) {

            // If we have a Red player, first move is request()
            this.cycleState = PlayerFunction.REQUEST;

        } else {

            // The Blue player has to process the Red player's move first
            this.cycleState = PlayerFunction.UPDATE;
        }
    }

    /**
     * Gibt die Farbe dieses Spielers zurueck.
     *
     * @return Die Farbe des Spielers
     */
    public PlayerColor getPlayerColour() {
        return this.playerColour;
    }

    protected PlayerFunction getCycleState () {
        return this.cycleState;
    }

    protected Collection<Move> getPossibleMoves () {
        return viewer.getPossibleMoves();
    }
}
