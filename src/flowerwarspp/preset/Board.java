package flowerwarspp.preset;

/**
 * <h1>Board</h1>
 * <p>
 * Jede Spielbrettklasse fuer FlowerWarsPP implementiert diese Schnittstelle.
 * </p>
 * <p>
 * Ein Spielbrett muss sowohl das Interface {@link Viewable} implementieren, als auch eine Methode zum Entgegennehmen
 * von Spielzuegen bereitstellen.
 * </p>
 *
 * @author Dominick Leppich
 */
public interface Board extends Viewable {
    /**
     * Fuehre den uebergebenen Zug auf dem Spielbrett aus.
     *
     * @param move
     *         auszufuehrender Zug
     *
     * @throws IllegalStateException
     *         falls das Spielbrett aufgrund der Spielsituation keine Zuege mehr entgegennehmen kann.
     */
    void make(final Move move) throws IllegalStateException;
}
