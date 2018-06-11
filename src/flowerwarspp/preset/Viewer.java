package flowerwarspp.preset;

import java.util.*;

/**
 * <h1>Viewer</h1>
 * <p>
 * Ueber diese Schnittstelle koennen alle Informationen zum aktuellen Zustand eines Spielbretts erfragt werden. Diese
 * Informationen umfassen alles Notwendige zum Anzeigen und Mitverfolgen des Spiels. Durch diese Schnittstelle ist es
 * moeglich, dass der Zustand des Spielbretts einer anderen Klasse zur Verfuegung gestellt werden kann, ohne direkten
 * Zugriff auf das Spielbrett (wie beispielsweise das Setzen von Spielzuegen) zu ermoeglichen.
 * </p>
 *
 * @author Dominick Leppich
 */
public interface Viewer {
    /**
     * Gib den Spieler (durch seine Spielerfarbe) zurueck, der gerade am Zug ist.
     *
     * @return Spielerfarbe als {@link PlayerColor}
     */
    PlayerColor getTurn();

    /**
     * Gib die Groesse des Spielbretts zurueck.
     *
     * @return Spielbrettgroesse
     */
    int getSize();

    /**
     * Gib den Status des Spielbretts zurueck.
     *
     * @return Status als {@link Status}
     */
    Status getStatus();

    /**
     * Gib alle gepflanzten Blumen eines bestimmten Spielers (durch seine Farbe) zurueck.
     *
     * @param color
     *         Spielerfarbe, dessen Blumen abgefragt werden
     *
     * @return die Menge der gepflanzten Blumen dieses Spielers als {@link Collection}
     */
    Collection<Flower> getFlowers(final PlayerColor color);

    /**
     * Gib alle gebauten Graeben eines bestimmten Spielers (durch seine Farbe) zurueck.
     *
     * @param color
     *         Spielerfarbe, dessen Graeben abgefragt werden
     *
     * @return die Menge der gebauten Graeben dieses Spielers als {@link Collection}
     */
    Collection<Ditch> getDitches(final PlayerColor color);

    /**
     * Gib alle moeglichen Zuege des aktuellen Spielers zurueck.
     *
     * @return die Menge gueltigen Zuege als {@link Collection}
     */
    Collection<Move> getPossibleMoves();

    /**
     * Gib die aktuelle Punktzahl eines bestimmten Spielers (durch seine Farbe) zurueck.
     *
     * @param color
     *         Spielerfarbe, dessen Punktestand abgefragt wird
     *
     * @return der aktuelle Punktestand des Spielers
     */
    int getPoints(final PlayerColor color);

    // ********************************************************************
    //  Hier koennen weitere Funktionen ergaenzt werden...
    // ********************************************************************


}
