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

	/**
	 * Gibt die Dreiecke zurück, die mit einem gegebenen Dreieck eine Kante gemeinsam haben.
	 *
	 * @param center Das Dreieck, dessen Nachbarn zurück gegeben werden sollen.
	 * @return Die direkten Nachbarn
	 */
    Collection<Flower> getDirectNeighbors(Flower center);

	/**
	 * Gibt die Dreiecke zurück, die mit einem gegebenen Dreieck eine Ecke gemeinsam haben.
	 *
	 * @param center Das Dreieck, dessen Nachbarn zurück gegeben werden sollen.
	 * @return Die Nachbarn
	 */
    Collection<Flower> getAllNeighbors(Flower center);

	/**
	 * Gibt zurück, ob der gegebene Spielzug für den aktuellen Spieler erlaubt ist.
	 *
	 * @param move Der zu überprüfende Spielzug
	 * @return Ob der gegebene Spielzug für den aktuellen Spieler erlaubt ist
	 */
    boolean possibleMovesContains(Move move);

	/**
	 * Gibt zurück, ob es für den aktuellen Spieler einen Spieler einen erlaubten Spielzug gibt,
	 * der die angegebene Blume enthält.
	 *
	 * @param flower Die Blume, für die überprüft werden soll, ob es einen passenden Zug gibt.
	 * @return Ob es für den aktuellen Spieler einen Spieler einen erlaubten Spielzug gibt,
	 * der die angegebene Blume enthält
	 */
    boolean possibleMovesContainsMovesContaining(Flower flower);

	/**
	 * Gibt alle erlaubten Blumenzüge für den aktuellen Spieler zurück.
	 *
	 * @return Alle möglichen Blumenzüge des aktuellen Spielers
	 */
    Collection<Move> getPossibleFlowerMoves();

	/**
	 * Gibt alle möglichen Blumen zurück, die der aktuelle Spieler setzen kann.
	 *
	 * @return Alle möglichen Blumen, die der aktuelle Spieler setzen kann
	 */
    Collection<Flower> getPossibleFlowers();

	/**
	 * Gibt alle Blumen zurück, mit denen sich die angegebene Blume kombinieren lässt, sodass sich
	 * für den aktuellen Spieler erlaubte Spielzüge ergeben.
	 *
	 * @param flower Die Blume, für die die kombinierbaren Blumen zurückgegeben werden sollen.
	 * @return Alle Blumen, mit denen sich die angegebene Blume kombinieren lässt, sodass sich für
	 * den aktuellen Spieler erlaubte Spielzüge ergeben
	 */
    Collection<Flower> getFlowersCombinableWith(Flower flower);

	/**
	 * Gibt eine Map zurück, die jeder erlaubten Blume die Blumen zuordnet, mit denen sie sich für
	 * den aktuellen Spieler kombinieren lässt.
	 *
	 * @return Eine Map, die jeder erlaubten Blume die Blumen zuordnet, mit denen sie sich für den
	 * aktuellen Spieler kombinieren lässt.
	 */
    Map<Flower, HashSet<Flower>> getFlowerMap();
    
	/**
	 * Gibt alle für den aktuellen Spieler erlaubten Züge zurück, der die gegebene Blume enthalten.
	 *
	 * @param flower Die Blume, die enthalten sein soll
	 * @return Alle für den aktuellen Spieler erlaubten Züge zurück, der die gegebene Blume
	 * enthalten
	 */
	Collection<Move> getPossibleMovesContaining(Flower flower);

	/**
	 * Gibt alle erlaubten Grabenzüge für den aktuellen Spieler zurück.
	 *
	 * @return Alle möglichen Grabenzüge des aktuellen Spielers
	 */
    Collection<Move> getPossibleDitchMoves();

	/**
	 * Gibt zurück, welcher Spieler eine gegebene Blume gesetzt hat.
	 *
	 * @param flower Die Blume, die überprüft werden soll.
	 * @return Der Spieler, der die Blume gesetzt hat oder null, falls noch kein Spieler sie gesetzt
	 * hat
	 */
    PlayerColor getFlowerColor(Flower flower);

	/**
	 * Gibt zurück, welcher Spieler einen gegebene Graben gesetzt hat.
	 *
	 * @param flower Der Graben, der überprüft werden soll.
	 * @return Der Spieler, der den Graben gesetzt hat oder null, falls noch kein Spieler ihn
	 * gesetzt hat
	 */
    PlayerColor getDitchColor(Ditch flower);

	/**
	 * Gibt das Blumenbeet zurück, zu dem eine gegebene Blume gehört.
	 *
	 * @param flower Die Blume, deren Blumenbeet zurückgegeben werden soll
	 * @return Das Blumenbeet, zu dem die Blume gehört
	 */
    HashSet<Flower> getFlowerBed(Flower flower);
}
