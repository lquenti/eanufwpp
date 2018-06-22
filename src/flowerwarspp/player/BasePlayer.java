package flowerwarspp.player;

import flowerwarspp.preset.*;

import java.rmi.RemoteException;

abstract class BasePlayer implements flowerwarspp.preset.Player {

    /**
     * Ein unterstuetzender enum um die Ausfuehrung der durch das Interface {@link flowerwarspp.preset.Player}
     * verlangten Methoden in der korrekten Reihenfolge zu sichern.
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
     * Stellt diesem Objekt ein eigenes {@link Board} zur Verfuegung, um die Durchfuehrung der eigenen und gegnerischen
     * Zuege nachbilden zu koennen.
     */
    private Board board = null;

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
     * Methode zum Anfordern eines Zugs.
     * 
     * @return Der vom Spieler geforderte Zug
     * @throws Exception       Falls der Spieler nicht in der Lage war, einen Zug zu liefern oder falls diese Methode
     *                         zum falschen Zeitpunkt innerhalb des Zyklus aufgerufen worden ist
     * @throws RemoteException Falls ein Fehler waehrend der Netzwerk-Kommunikation aufgetreten ist
     */
    @Override
    public abstract Move request() throws Exception, RemoteException;

    /* TODO: All the things need doing. */

    /**
     * Stellt die vom Interface {@link Player} geforderte Methode {@link Player#confirm(Status)} bereit.
     *
     * @param status Status des Spielbretts des Hauptprogramms nach Ausfuehren des zuletzt mit {@link #request()}
     *               geholten Zuges
     * @throws Exception       Falls sich der eigene Status und der Status des Hauptprogramms unterscheiden oder falls
     *                         diese Methode *                         zum falschen Zeitpunkt innerhalb des Zyklus
     *                         aufgerufen worden ist
     * @throws RemoteException Falls ein Fehler waehrend der Netzwerk-Kommunikation aufgetreten ist
     */
    @Override
    public void confirm( Status status ) throws Exception, RemoteException {
        // State validation
        if ( this.cycleState == PlayerFunction.NULL )
            throw new Exception("Der Spieler muss zuerst initialisiert werden!");
        if ( this.cycleState != PlayerFunction.CONFIRM )
            throw new Exception("Unerwarteter Methoden-Aufruf. Es haette request() aufgerufen werden.");

        // Do stuff...

        // Update state
        this.cycleState = PlayerFunction.UPDATE;
    }

    /* TODO: Any and all of this. */

    /**
     * Stellt die vom Interface {@link Player} geforderte Methode {@link Player#confirm(Status)} bereit.
     *
     * @param opponentMove Zug des Gegenspielers
     * @param status       Status des Spielbretts des Hauptprogramms nach Ausfuehren des Zuges des Gegenspielers
     * @throws Exception       Falls sich die Status des eigenen Spielbretts nach Ausfuehren des gegnerischen Zuges und
     *                         des Hauptprogramms unterscheiden oder falls diese Methode *                         zum
     *                         falschen Zeitpunkt innerhalb des Zyklus aufgerufen worden ist
     * @throws RemoteException Falls ein Fehler waehrend der Netzwerk-Kommunikation aufgetreten ist
     */
    @Override
    public void update( Move opponentMove, Status status ) throws Exception, RemoteException {
        // State validation
        if ( this.cycleState == PlayerFunction.NULL )
            throw new Exception("Der Spieler muss zuerst initialisiert werden!");
        if ( this.cycleState != PlayerFunction.UPDATE )
            throw new Exception("Unerwarteter Methoden-Aufruf. Es haette confirm() aufgerufen werden.");

        // Do stuff...

        // Update state
        this.cycleState = PlayerFunction.REQUEST;
    }

    /* TODO: This also needs doing, I have no board to play with :c */

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
     * Gibt die Farbe dieses Spielers zurueck.
     *
     * @return Die Farbe des Spielers
     */
    public PlayerColor getPlayerColour() {
        return playerColour;
    }
}
