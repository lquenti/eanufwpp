package flowerwarspp.preset;

/**
 * Jede Eingabe-Klasse muss diese Schnittstelle implementieren. Der interaktive Spieler nutzt eine Klasse, die diese
 * Schnittstelle implementiert, als Quelle der Spielzuege. Das Anfordern eines Spielzuges kann beispielsweise textuell
 * auf der Standardeingabe erfolgen oder interaktiv ueber eine grafische Eingabeklasse.
 *
 * @author Dominick Leppich
 */
public interface Requestable {
    /**
     * Fordere einen Spielzug an und liefere ihn zurueck.
     *
     * @return Spielzug als {@link Move}-Objekt
     *
     * @throws Exception
     *         falls das Anfordern eines Spielzuges fehlschlaegt
     */
    Move request() throws Exception;
}
