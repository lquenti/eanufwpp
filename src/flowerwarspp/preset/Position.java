package flowerwarspp.preset;

import java.io.*;
import java.util.*;

/**
 * <h1>Die Position</h1>
 * <p>
 * Diese Klasse repraesentiert eine Position. Eine Position hat eine Spalte und eine Zeile mit Werten zwischen {@code 1}
 * und {@code 31}.
 * </p>
 * <h2>Wichtige Hinweise</h2>
 * <p>
 * Eine Position kann nach der Erzeugung nicht mehr veraendert werden.
 * </p>
 * <h2>Zusatzfunktionalitaet</h2>
 * <p>
 * Die Methode {@link #parsePosition(String)} kann einen {@link String} in ein Position-Objekt umwandeln, wenn das
 * Format gueltig ist.
 * </p>
 * <p>
 * Die Funktion {@link #hashCode()} liefert fuer alle gueltigen Positionen einen eindeutigen Hash-Wert zurueck.
 * Spezielle Datenstrukturen wie zum Beispiel ein {@link HashSet} koennen sich das zu Nutze machen. Eine Position ist
 * weiterhin mit anderen Positionen ueber das {@link Comparable}-Interface vergleichbar, um eine sinnvolle Sortierung zu
 * ermoeglichen.
 * </p>
 *
 * @author Dominick Leppich
 */
public class Position implements Serializable, Comparable<Position> {
    /** Maximalwert einer Spalte oder Zeile einer Position */
    public static final int MAX_VALUE = 31;
    /** Anzahl an moeglichen Positionen */
    public static final int COMBINATIONS = MAX_VALUE * MAX_VALUE;
    /** Serialisierungskonstante */
    private static final long serialVersionUID = 1L;

    // ------------------------------------------------------------
    /** Spalte */
    private int column;
    /** Zeile */
    private int row;

    // ------------------------------------------------------------

    /**
     * Erzeuge eine neue Position unter Angabe der Spalte und Zeile.
     *
     * @param column
     *         Zeile
     * @param row
     *         Spalte
     *
     * @throws IllegalArgumentException
     *         falls die Spalte oder Zeile ungueltige Werte hat
     */
    public Position(final int column, final int row) {
        setColumn(column);
        setRow(row);
    }

    // ------------------------------------------------------------

    /**
     * Wandle einen uebergebenen {@link String} in eine Position um. Das Format ist: {@code (Pos.column,Pos.row)}
     *
     * @param string
     *         umzuwandelnder String
     *
     * @return eine Position als {@link Position}-Objekt
     *
     * @throws PositionFormatException
     *         falls kein gueltiger String uebergeben wird oder das Format ungueltig ist
     */
    public static Position parsePosition(final String string) {
        if (string == null || string.equals(""))
            throw new PositionFormatException("cannot parse empty string");

        if (!string.startsWith("(") || !string.endsWith(")"))
            throw new PositionFormatException("wrong outer format! correct format is: (COLUMN,ROW)");

        String[] parts = string.substring(1, string.length() - 1)
                               .split(",");

        if (parts.length != 2)
            throw new PositionFormatException("wrong number of arguments! correct format is: (COLUMN,ROW)");

        try {
            return new Position(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        } catch (NumberFormatException e) {
            throw new PositionFormatException("wrong number format! valid numbers are 1 to " + MAX_VALUE, e);
        }
    }

    /**
     * Gib die Spalte der Position zurueck.
     *
     * @return Spalte
     */
    public int getColumn() {
        return column;
    }

    /**
     * Setze die Spalte der Position.
     *
     * @param column
     *         zu setzende Spalte
     *
     * @throws IllegalArgumentException
     *         falls die Spalte einen ungueltigen Wert hat
     */
    private void setColumn(final int column) {
        if (column <= 0 || column > MAX_VALUE)
            throw new IllegalArgumentException("illegal column value: " + column);
        this.column = column;
    }

    /**
     * Gib die Zeile der Position zurueck.
     *
     * @return Zeile
     */
    public int getRow() {
        return row;
    }

    /**
     * Setze die Zeile der Position.
     *
     * @param row
     *         zu setzende Zeile
     *
     * @throws IllegalArgumentException
     *         falls die Zeile einen ungueltigen Wert hat
     */
    private void setRow(final int row) {
        if (row <= 0 || row > MAX_VALUE)
            throw new IllegalArgumentException("illegal row value: " + row);
        this.row = row;
    }

    // ------------------------------------------------------------

    @Override
    public int hashCode() {
        return getColumn() * MAX_VALUE + getRow();
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null)
            return false;
        if (!(o instanceof Position))
            return false;
        Position p = (Position) o;
        return getColumn() == p.getColumn() && getRow() == p.getRow();
    }

    @Override
    public String toString() {
        return "(" + getColumn() + "," + getRow() + ")";
    }

    @Override
    public int compareTo(final Position p) {
        if (getRow() != p.getRow())
            return getRow() - p.getRow();
        return getColumn() - p.getColumn();
    }
}
