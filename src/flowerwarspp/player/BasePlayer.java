package flowerwarspp.player;

import flowerwarspp.board.MainBoard;
import flowerwarspp.preset.*;

import java.rmi.RemoteException;

/**
 * Abstrakte Basis-Klasse welche die grundlegende Implementation eines Spielers beschreibt, welcher die Anforderungen
 * des Interfaces {@link Player} erfuellt. Die einzige abstrakte Methode deren Implementation gefordert wird, ist {@link
 * #requestMove()}. Diese Methode fordert einen Zug vom jeweiligen Spieler an, und leitet diesen Zug an die Methode
 * {@link #request()} weiter.
 *
 * @author Michael Merse
 */
abstract class BasePlayer implements flowerwarspp.preset.Player {

    /**
     * Eine vordefinierte Nachricht einer {@link Exception}, welche geworfen wird, wenn der Spieler noch nicht
     * initialisiert worden ist.
     */
    private static final String exception_NoInit =
            "Der Spieler muss zuerst initialisiert werden!";

    /**
     * Eine vordefinierte Nachricht einer {@link Exception}, welche geworfen wird, wenn eine der Methoden {@link
     * #confirm(Status)}, {@link #request()} oder {@link #update(Move, Status)} zu einem unerwarteten Zeitpunkt
     * aufgerufen worden wird.
     */
    private static final String exception_UnexpectedCall =
            "Unerwarteter Methoden-Aufruf.";

    /**
     * Eine vordefinierte Nachricht einer {@link Exception}, welche geworfen wird, wenn {@link #request()} zum falschen
     * Zeitpunkt aufgerufen worden ist.
     */
    private static final String exception_CycleRequest =
            exception_UnexpectedCall + " Es haette update() aufgerufen werden sollen.";

    /**
     * Eine vordefinierte Nachricht einer {@link Exception}, welche geworfen wird, wenn {@link #update(Move, Status)}
     * zum falschen Zeitpunkt aufgerufen worden ist.
     */
    private static final String exception_CycleUpdate =
            exception_UnexpectedCall + " Es haette confirm() aufgerufen werden sollen.";

    /**
     * Eine vordefinierte Nachricht einer {@link Exception}, welche geworfen wird, wenn {@link #confirm(Status)} zum
     * falschen Zeitpunkt aufgerufen worden ist.
     */
    private static final String exception_CycleConfirm =
            exception_UnexpectedCall + " Es haette request() aufgerufen werden sollen.";

    /**
     * Eine vordefinierte Nachricht einer {@link Exception}, welche geworfen wird, wenn es eine Disparitaet zwischen den
     * Status des Hauptprogramms und des eigenen Spielbretts gab.
     */
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
     * Ermoeglicht den Zugriff auf relevante Daten des Spielbretts, welche fuer die Verifikation und die Ausarbeitung
     * von Spielzuegen benoetigt werden.
     */
    protected Viewer boardViewer;

    /**
     * Die Farbe dieses Spielers, nach den Vorgaben des enums {@link PlayerColor}.
     */
    private PlayerColor playerColour;

    /**
     * Wird genutzt, um den aktuellen Status des Spieler-Lebenszyklus (also der Zyklus, welcher im Interface {@link
     * flowerwarspp.preset.Player} diktiert wird) darzustellen.
     */
    private PlayerFunction cycleState;

    /**
     * Ein <code>default</code>-Konstruktor, welcher die Instanzvariablen mit Basiswerten initialisiert.
     */
    protected BasePlayer() {
        this.playerColour = PlayerColor.Red;
        this.board = null;
        this.cycleState = PlayerFunction.NULL;
    }


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
        if ( this.cycleState == PlayerFunction.NULL ) throw new Exception(exception_NoInit);
        if ( this.cycleState != PlayerFunction.REQUEST ) throw new Exception(exception_CycleRequest);

        final Move move = requestMove();

        // We just assume the move is valid, might need to check later on
        // For now we just make the move as returned by requestMove
        this.board.make(move);

        // Update state
        this.cycleState = PlayerFunction.CONFIRM;

        return move;
    }

    /**
     * Fordert einen Zug vom Spieler an, wie der {@link Move} angefordert wird, wird der jeweiligen Implementation der
     * abstrakten Klasse ueberlassen.
     *
     * @return Der vom Spieler zurueckgegebene Zug.
     * @throws Exception       Falls der jeweilige Spieler keinen Zug angeben konnte.
     * @throws RemoteException Falls ein Fehler waehrend der Netzwerk-Kommunikation aufgetreten ist
     */
    /* INFO: Method is abstract because requesting a move from the player works differently with each implementation */
    protected abstract Move requestMove() throws Exception, RemoteException;

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
        final Status playerBoardState = this.boardViewer.getStatus();

        if ( ! playerBoardState.equals(status) ) throw new Exception(exception_StatusError);

        // Update state
        this.cycleState = PlayerFunction.UPDATE;
    }

    /**
     * Stellt die vom Interface {@link flowerwarspp.preset.Player} geforderte Methode {@link
     * flowerwarspp.preset.Player#update(Move, Status)} bereit.
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
        final Status playerBoardStatus = this.boardViewer.getStatus();

        if ( ! playerBoardStatus.equals(status) ) throw new Exception(exception_StatusError);

        // Update state
        this.cycleState = PlayerFunction.REQUEST;
    }


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
    /* TODO: Properly handle beginning a new game (i.e. calling init() in the middle of a running game) */
    @Override
    public void init( int boardSize, PlayerColor playerColour ) throws Exception, RemoteException {

        // Set the colour
        this.playerColour = playerColour;

        if ( this.board == null ) {

            this.board = new MainBoard(boardSize);
            this.boardViewer = board.viewer();
        } else {

            // Here we need to handle ending the current game and starting a new one, some form of feedback would be
            // necessary to make that happen with the main program.
            // TODO: Restart game with new player
            throw new Exception("Noch nicht implementiert.");
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
}
