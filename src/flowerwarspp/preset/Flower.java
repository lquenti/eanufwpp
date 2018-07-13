package flowerwarspp.preset;

import java.io.*;
import java.util.*;

/**
 * <h1>Die Blume</h1>
 * <p>
 * Diese Klasse repraesentiert eine Blume. Eine Blume wird immer durch die drei umliegenden {@link Position}en
 * definiert.
 * </p>
 * <h2>Wichtige Hinweise</h2>
 * <p>
 * Eine Blume kann nach der Erzeugung nicht mehr veraendert werden. Die Reihenfolge, in der die drei Positionen beim
 * Erzeugen an den Konstruktor uebergeben werden hat keine Auswirkung darauf, welche der drei Positionen bei einem
 * Aufruf von {@link #getFirst()}, {@link #getSecond()} bzw. {@link #getThird()} zurueckgeliefert wird. Intern werden
 * die Positionen umsortiert, damit zwei Blumen mit identischen Positionen, die jedoch mit vertauschter Reihenfolge der
 * Positionen erzeugt wurden, dennoch einfach vergleichbar sind.
 * </p>
 * <h2>Zusatzfunktionalitaet</h2>
 * <p>
 * Die Methode {@link #parseFlower(String)} kann einen {@link String} in ein Flower-Objekt umwandeln, wenn das Format
 * gueltig ist.
 * </p>
 * <p>
 * Die Funktion {@link #hashCode()} liefert fuer alle gueltigen Blumen einen eindeutigen Hash-Wert zurueck. Spezielle
 * Datenstrukturen wie zum Beispiel ein {@link HashSet} koennen sich das zu Nutze machen. Eine Blume ist weiterhin mit
 * anderen Blumen ueber das {@link Comparable}-Interface vergleichbar, um eine sinnvolle Sortierung zu ermoeglichen.
 * </p>
 *
 * @author Dominick Leppich
 */
public class Flower implements Serializable, Comparable<Flower> {
    /** Anzahl an moeglichen Positionen */
    public static final int COMBINATIONS = Position.COMBINATIONS * 2 * Position.COMBINATIONS;
    /** Serialisierungskonstante */
    private static final long serialVersionUID = 1L;

    // ------------------------------------------------------------
    /** Positionen der Blume */
    private Position[] positions;

    // ------------------------------------------------------------

    /**
     * Erzeuge eine neue Blume unter Angabe der drei umliegenden Positionen. Beim Erzeugen werden die Positionen
     * automatisch umsortiert.
     *
     * @param first
     *         erste Position
     * @param second
     *         zweite Position
     * @param third
     *         dritte Position
     *
     * @throws IllegalArgumentException
     *         falls eine der Positionen {@code null} ist
     */
    public Flower(final Position first, final Position second, final Position third) {
        positions = new Position[3];

        setFirst(first);
        setSecond(second);
        setThird(third);

        updateOrder();
    }

    /**
     * Wandle einen uebergebenen {@link String} in eine Blume um. Das Format ist: {@code
     * {(Pos1.column,Pos1.row),(Pos2.column,Pos2.row), (Pos3.column,Pos3.row)}}
     *
     * @param string
     *         umzuwandelnder String
     *
     * @return eine Blume als {@link Flower}-Objekt
     *
     * @throws FlowerFormatException
     *         falls kein gueltiger String uebergeben wird oder das Format ungueltig ist
     */
    public static Flower parseFlower(final String string) {
        if (string == null || string.equals(""))
            throw new FlowerFormatException("cannot parse empty string");

        if (!string.startsWith("{") || !string.endsWith("}"))
            throw new FlowerFormatException("wrong outer format! correct format is: {POSITION," + "POSITION,POSITION}");

        // The splitting needs to be merged afterwards
        String[] parts = string.substring(1, string.length() - 1)
                               .split(",");

        if (parts.length != 6)
            throw new FlowerFormatException(
                    "wrong number of positions! correct format is: {POSITION," + "POSITION,POSITION}");

        try {
            // Merge splitted substrings accordingly
            return new Flower(Position.parsePosition(parts[0] + ',' + parts[1]),
                    Position.parsePosition(parts[2] + ',' + parts[3]),
                    Position.parsePosition(parts[4] + ',' + parts[5]));
        } catch (PositionFormatException e) {
            throw new FlowerFormatException("unable to parse flower positions", e);
        }
    }

    // ------------------------------------------------------------

    /** Sortiere die Positionen der Blume. */
    private void updateOrder() {
        Arrays.sort(positions);
    }

    /**
     * Gib die erste Position der Blume zurueck.
     *
     * @return erste Position
     */
    public Position getFirst() {
        return positions[0];
    }

    /**
     * Setze die erste Position der Blume.
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
     * Gib die zweite Position der Blume zurueck.
     *
     * @return zweite Position
     */
    public Position getSecond() {
        return positions[1];
    }

    /**
     * Setze die zweite Position der Blume.
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

    /**
     * Gib die dritte Position der Blume zurueck.
     *
     * @return dritte Position
     */
    public Position getThird() {
        return positions[2];
    }

    // ------------------------------------------------------------

    /**
     * Setze die dritte Position der Blume.
     *
     * @param third
     *         dritte Position
     *
     * @throws IllegalArgumentException
     *         falls die Position {@code null} ist
     */
    private void setThird(final Position third) {
        if (third == null)
            throw new IllegalArgumentException("third cannot be null");
        positions[2] = third;
    }

    @Override
    public int hashCode() {
        // Distributes perfectly for valid moves. For arbitrary flowers of
        // valid position combinations which does not result in correct
        // flowers collisions might occur!
        return getFirst().hashCode() * 2 + (getSecond().getColumn() > getFirst().getColumn() ? 0 : 1);
    }

    @Override
    public int compareTo(final Flower flower) {
        // Due to the unique ordering it is easy to calculate
        if (!getFirst().equals(flower.getFirst()))
            return getFirst().compareTo(flower.getFirst());
        else
            return getSecond().getColumn() - flower.getSecond()
                                                   .getColumn();
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null)
            return false;
        if (!(o instanceof Flower))
            return false;
        Flower l = (Flower) o;
        return Arrays.equals(positions, l.positions);
    }

    @Override
    public String toString() {
        return "{" + getFirst().toString() + "," + getSecond().toString() + "," + getThird().toString() + "}";
    }
}
