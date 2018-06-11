package flowerwarspp.preset;

import java.io.*;
import java.util.*;

/**
 * <h1>Der Graben</h1>
 * <p>
 * Diese Klasse repraesentiert einen Graben. Ein Graben wird immer zwischen zwei {@link Position}en gebaut.
 * </p>
 * <h2>Wichtige Hinweise</h2>
 * <p>
 * Ein Graben kann nach der Erzeugung nicht mehr veraendert werden. Die Reihenfolge, in der die beiden Positionen beim
 * Erzeugen an den Konstruktor uebergeben werden hat keine Auswirkung darauf, welche der beiden Positionen bei einem
 * Aufruf von {@link #getFirst()} bzw. {@link #getSecond()} zurueckgeliefert wird. Intern werden die Positionen
 * umsortiert, damit zwei Graeben mit identischen Positionen, die jedoch mit vertauschter Reihenfolge der Positionen
 * erzeugt wurden, dennoch einfach vergleichbar sind.
 * </p>
 * <h2>Zusatzfunktionalitaet</h2>
 * <p>
 * Die Methode {@link #parseDitch(String)} kann einen {@link String} in ein Ditch-Objekt umwandeln, wenn das Format
 * gueltig ist.
 * </p>
 * <p>
 * Die Funktion {@link #hashCode()} liefert fuer alle gueltigen Graeben einen eindeutigen Hash-Wert zurueck. Spezielle
 * Datenstrukturen wie zum Beispiel ein {@link HashSet} koennen sich das zu Nutze machen. Ein Graben ist weiterhin mit
 * anderen Graeben ueber das {@link Comparable}-Interface vergleichbar, um eine sinnvolle Sortierung zu ermoeglichen.
 * </p>
 *
 * @author Dominick Leppich
 */
public class Ditch implements Serializable, Comparable<Ditch> {
    /** Serialisierungskonstante */
    private static final long serialVersionUID = 1L;

    // ------------------------------------------------------------

    /** Positionen des Grabens */
    private Position[] positions;

    // ------------------------------------------------------------

    /**
     * Erzeuge einen neuen Graben unter Angabe der beiden Positionen, zwischen denen der Graben gebaut werden soll. Beim
     * Erzeugen werden die Positionen automatisch umsortiert.
     *
     * @param first
     *         erste Position
     * @param second
     *         zweite Position
     *
     * @throws IllegalArgumentException
     *         falls eine der Positionen {@code null} ist
     */
    public Ditch(final Position first, final Position second) {
        positions = new Position[2];

        setFirst(first);
        setSecond(second);

        updateOrder();
    }

    /**
     * Wandle einen uebergebenen {@link String} in einen Graben um. Das Format ist: {@code
     * {(Pos1.column,Pos1.row),(Pos2.column,Pos2.row)}}
     *
     * @param string
     *         umzuwandelnder String
     *
     * @return einen Graben als {@link Ditch}-Objekt
     *
     * @throws DitchFormatException
     *         falls kein gueltiger String uebergeben wird oder das Format ungueltig ist
     */
    public static Ditch parseDitch(final String string) {
        if (string == null || string.equals(""))
            throw new DitchFormatException("cannot parse empty string");

        if (!string.startsWith("{") || !string.endsWith("}"))
            throw new DitchFormatException("wrong outer format! correct format is: {POSITION,POSITION}");

        // The splitting needs to be merged afterwards
        String[] parts = string.substring(1, string.length() - 1)
                               .split(",");

        if (parts.length != 4)
            throw new DitchFormatException("wrong number of positions! correct format is: {POSITION,POSITION}");

        try {
            // Merge splitted substrings accordingly
            return new Ditch(Position.parsePosition(parts[0] + ',' + parts[1]),
                    Position.parsePosition(parts[2] + ',' + parts[3]));
        } catch (PositionFormatException e) {
            throw new DitchFormatException("unable to parse ditch positions", e);
        }
    }

    // ------------------------------------------------------------

    /** Sortiere die Positionen des Grabens. */
    private void updateOrder() {
        Arrays.sort(positions);
    }

    /**
     * Gib die erste Position des Grabens zurueck.
     *
     * @return erste Position
     */
    public Position getFirst() {
        return positions[0];
    }

    /**
     * Setze die erste Position des Grabens.
     *
     * @param first
     *         erste Position
     *
     * @throws IllegalArgumentException
     *         falls die Position {@code null} ist
     */
    private void setFirst(final Position first) {
        if (first == null)
            throw new IllegalArgumentException("first cannot be null");
        positions[0] = first;
    }

    /**
     * Gib die zweite Position des Grabens zurueck.
     *
     * @return zweite Position
     */
    public Position getSecond() {
        return positions[1];
    }

    // ------------------------------------------------------------

    /**
     * Setze die zweite Position des Grabens.
     *
     * @param second
     *         zweite Position
     *
     * @throws IllegalArgumentException
     *         falls die Position {@code null} ist
     */
    private void setSecond(final Position second) {
        if (second == null)
            throw new IllegalArgumentException("second cannot be null");
        positions[1] = second;
    }

    @Override
    public int hashCode() {
        return getFirst().hashCode() * Position.COMBINATIONS + getSecond().hashCode();
    }

    @Override
    public int compareTo(final Ditch ditch) {
        // Due to the unique ordering it is easy to calculate
        if (!getFirst().equals(ditch.getFirst()))
            return getFirst().compareTo(ditch.getFirst());
        else
            return getSecond().getColumn() - ditch.getSecond()
                                                  .getColumn();
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null)
            return false;
        if (!(o instanceof Ditch))
            return false;
        Ditch b = (Ditch) o;
        return Arrays.equals(positions, b.positions);
    }

    @Override
    public String toString() {
        return "{" + getFirst().toString() + "," + getSecond().toString() + "}";
    }
}
