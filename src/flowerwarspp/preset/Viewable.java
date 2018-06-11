package flowerwarspp.preset;

/**
 * Diese Schnittstelle ermoeglicht es einen {@link Viewer} zu bekommen, ueber den Informationen ueber den Zustand des
 * Spielbretts abgefragt werden koennen. Die {@link Board}-Schnittstelle ist eine Erweiterung dieser Schnittstelle.
 *
 * @author Dominick Leppich
 */
public interface Viewable {
    /**
     * Liefere einen {@link Viewer} zurueck.
     *
     * @return einen Viewer
     */
    Viewer viewer();
}
