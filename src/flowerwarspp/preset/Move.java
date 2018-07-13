package flowerwarspp.preset;

import java.io.*;
import java.util.*;

/**
 * <h1>Der Spielzug</h1>
 * <p>
 * Diese Klasse repraesentiert einen Spielzug.
 * </p>
 * <h2>Zugtypen</h2>
 * <p>
 * Ein Spielzug hat immer einen der folgenden vier Typen:
 * <ul>
 * <li>{@link MoveType#Flower}</li>
 * <li>{@link MoveType#Ditch}</li>
 * <li>{@link MoveType#Surrender}</li>
 * <li>{@link MoveType#End}</li>
 * </ul>
 * </p>
 * <h3>Blumenzug ({@code MoveType.Flower})</h3>
 * <p>
 * Der Zug besteht aus zwei Blumen und wird ueber den Konstruktor {@link #Move(Flower, Flower)} erzeugt. Der Typ des
 * Zuges wird dabei automatisch auf {@link MoveType#Flower} gesetzt.
 * </p>
 * <h3>Grabenzug ({@code MoveType.Ditch})</h3>
 * <p>
 * Der Zug besteht aus einem Graben und wird ueber den Konstruktor {@link #Move(Ditch)} erzeugt. Der Typ des Zuges wird
 * dabei automatisch auf {@link MoveType#Ditch} gesetzt.
 * </p>
 * <h3>Aufgabe- oder Spielende-Zug ({@code MoveType.Surrender} / {@code MoveType.End})</h3>
 * <p>
 * Der Zug hat weder Blumen noch Graeben und wird ueber den Konstruktor {@link #Move(MoveType)} und Angabe des Typen
 * {@link MoveType#Surrender} oder {@link MoveType#End} erzeugt. Es ist nicht moeglich Blumen- oder Grabenzuege mit
 * diesem Konstruktor zu erzeugen!
 * </p>
 * <h2>Wichtige Hinweise</h2>
 * <p>
 * Ein Zug kann nach der Erzeugung nicht mehr veraendert werden. Die Reihenfolge, in der die Blumen beim Erzeugen an den
 * Konstruktor uebergeben werden hat keine Auswirkung darauf, welche der beiden Blumen bei einem Aufruf von {@link
 * #getFirstFlower()} bzw. {@link #getSecondFlower()} zurueckgeliefert wird. Intern werden die Blumen umsortiert, damit
 * zwei Zuege mit identischen Blumen, die jedoch mit vertauschter Reihenfolge der Blumen erzeugt wurden, dennoch einfach
 * vergleichbar sind.
 * </p>
 * <h2>Zusatzfunktionalitaet</h2>
 * <p>
 * Die Methode {@link #parseMove(String)} kann einen {@link String} in ein Move-Objekt umwandeln, wenn das Format
 * gueltig ist.
 * </p>
 * <p>
 * Die Funktion {@link #hashCode()} liefert fuer alle gueltigen Zuege einen eindeutigen Hash-Wert zurueck. Spezielle
 * Datenstrukturen wie zum Beispiel ein {@link HashSet} koennen sich das zu Nutze machen. Ein Zug ist weiterhin mit
 * anderen Zuegen ueber das {@link Comparable}-Interface vergleichbar, um eine sinnvolle Sortierung zu ermoeglichen.
 * </p>
 *
 * @author Dominick Leppich
 */
public class Move implements Serializable, Comparable<Move> {
    /** Serialisierungskonstante */
    private static final long serialVersionUID = 1L;

    // ------------------------------------------------------------

    /** Zugtyp */
    private MoveType type;
    /** Blumen des Zuges */
    private Flower[] flowers;
    /** Graben des Zuges */
    private Ditch ditch;

    // ------------------------------------------------------------

    /**
     * Erzeuge einen neuen Aufgabe- oder Spielende-Zug unter Angabe des Typs. Es ist nur moeglich Zuege vom Typen {@link
     * MoveType#Surrender} und {@link MoveType#End} auf diese Weise zu erzeugen.
     *
     * @param type
     *         Zugtyp
     *
     * @throws IllegalArgumentException
     *         falls versucht wird mit diesem Konstruktor einen Zug vom Typen {@link MoveType#Flower} oder {@link
     *         MoveType#Ditch} zu erzeugen
     */
    public Move(final MoveType type) {
        if (type == MoveType.Flower || type == MoveType.Ditch)
            throw new IllegalArgumentException("flower and ditch moves have " + "separate constructors");

        this.type = type;
    }

    /**
     * Erzeuge einen neuen Blumenzug unter Angabe der beiden Blumen, die in diesem Zug gepflanzt werden sollen. Beim
     * Erzeugen werden die Blumen automatisch umsortiert.
     *
     * @param first
     *         erste zu pflanzende Blume
     * @param second
     *         zweite zu pflanzende Blume
     *
     * @throws IllegalArgumentException
     *         falls eine der Blumen {@code null} ist
     */
    public Move(final Flower first, final Flower second) {
        type = MoveType.Flower;

        flowers = new Flower[2];

        setFirstFlower(first);
        setSecondFlower(second);

        updateOrder();
    }

    /**
     * Erzeuge einen neuen Grabenzug unter Angabe des Grabens.
     *
     * @param ditch
     *         zu bauender Graben
     */
    public Move(final Ditch ditch) {
        type = MoveType.Ditch;

        this.ditch = ditch;
    }

    /**
     * Wandle einen uebergebenen {@link String} in einen Spielzug um. Die Formate fuer die moeglichen Zugtypen lauten:
     * <h3>Blumenzug</h3>
     * {@code {{(Flower1.Pos1.column,Flower1.Pos1.row),(Flower1.Pos2.column,Flower1.Pos2.row),(Flower1.Pos3.column,Flower1.Pos3.row)},
     * {(Flower2.Pos1.column,Flower2.Pos1.row),(Flower2.Pos2.column,Flower2.Pos2.row),(Flower2.Pos3.column,Flower2.Pos3.row)}}}
     * <h3>Grabenzug</h3>
     * {@code {{(Pos1.column,Pos1.row),(Pos2.column,Pos2.row)}}}
     * <h3>Aufgabe-Zug</h3>
     * {@code {Surrender}}
     * <h3>Spielende-Zug</h3>
     * {@code {End}}
     *
     * @param string
     *         umzuwandelnder String
     *
     * @return einen Spielzug als {@link Move}-Objekt
     *
     * @throws DitchFormatException
     *         falls kein gueltiger String uebergeben wird oder das Format ungueltig ist
     */
    public static Move parseMove(String string) {
        if (string == null || string.equals(""))
            throw new MoveFormatException("cannot parse empty string");

        if (!string.startsWith("{") || !string.endsWith("}"))
            throw new MoveFormatException(
                    "wrong outer format! correct format is: {MOVE}\nMOVE can be one of the following: Flower, Ditch, \"End\", \"Surrender\"");

        // The splitting needs to be merged afterwards
        String[] parts = string.substring(1, string.length() - 1)
                               .split(",");

        try {
            switch (parts.length) {
                // Primitive move type
                case 1:
                    switch (parts[0]) {
                        case "End":
                            return new Move(MoveType.End);
                        case "Surrender":
                            return new Move(MoveType.Surrender);
                        default:
                            throw new MoveFormatException("unknown move type");
                    }
                    // Ditch move
                case 4:
                    return new Move(Ditch.parseDitch(string.substring(1, string.length() - 1)));
                // Flower move
                case 12:
                    // Do some split merging here
                    return new Move(Flower.parseFlower(
                            parts[0] + ',' + parts[1] + ',' + parts[2] + ',' + parts[3] + ',' + parts[4] + ',' +
                            parts[5]), Flower.parseFlower(
                            parts[6] + ',' + parts[7] + ',' + parts[8] + ',' + parts[9] + ',' + parts[10] + ',' +
                            parts[11]));
                default:
                    throw new MoveFormatException(
                            "Illegal number of arguments! correct format is: {MOVE}\nMOVE can be one of the following: Flower, Ditch, \"End\", \"Surrender\"");
            }
        } catch (FlowerFormatException e) {
            throw new MoveFormatException("unable to parse flower", e);
        } catch (DitchFormatException e) {
            throw new MoveFormatException("unable to parse ditch", e);
        }
    }

    // ------------------------------------------------------------

    /** Sortiere die Blumen des Zuges. */
    private void updateOrder() {
        Arrays.sort(flowers);
    }

    /**
     * Gib den eindeutigen Typen des Zuges zurueck.
     *
     * @return Zugtyp
     */
    public MoveType getType() {
        return type;
    }

    /**
     * Gib die erste Blume des Zuges zurueck. Diese Funktion kann nur fuer Blumenzuege ausgefuehrt werden.
     *
     * @return erste Blume
     *
     * @throws IllegalStateException
     *         falls es sich nicht um einen Blumenzug handelt
     */
    public Flower getFirstFlower() {
        if (getType() != MoveType.Flower)
            throw new IllegalStateException("cannot be called on type " + type);
        return flowers[0];
    }

    /**
     * Setze die erste Blume des Zuges.
     *
     * @param first
     *         erste Blume
     *
     * @throws IllegalArgumentException
     *         falls die Blume {@code null} ist
     */
    private void setFirstFlower(final Flower first) {
        if (first == null)
            throw new IllegalArgumentException("first flower cannot be null");
        flowers[0] = first;
    }

    /**
     * Gib die zweite Blume des Zuges zurueck. Diese Funktion kann nur fuer Blumenzuege ausgefuehrt werden.
     *
     * @return zweite Blume
     *
     * @throws IllegalStateException
     *         falls es sich nicht um einen Blumenzug handelt
     */
    public Flower getSecondFlower() {
        if (getType() != MoveType.Flower)
            throw new IllegalStateException("cannot be called on type " + type);
        return flowers[1];
    }

    /**
     * Setze die zweite Blume des Zuges.
     *
     * @param second
     *         zweite Blume
     *
     * @throws IllegalArgumentException
     *         falls die Blume {@code null} ist
     */
    private void setSecondFlower(final Flower second) {
        if (second == null)
            throw new IllegalArgumentException("second flower cannot be null");
        flowers[1] = second;
    }

    // ------------------------------------------------------------

    /**
     * Gib den Graben des Zuges zurueck. Diese Funktion kann nur fuer Grabenzuege ausgefuehrt werden.
     *
     * @return Graben
     *
     * @throws IllegalStateException
     *         falls es sich nicht um einen Grabenzug handelt
     */
    public Ditch getDitch() {
        if (getType() != MoveType.Ditch)
            throw new IllegalStateException("cannot be called on type " + type);
        return ditch;
    }

    @Override
    public int hashCode() {
        switch (type) {
            case Flower:
                return getFirstFlower().hashCode() * Flower.COMBINATIONS + getSecondFlower().hashCode();
            case Ditch:
                return -ditch.hashCode();
            case Surrender:
                return Integer.MAX_VALUE;
            case End:
                // There is no other default case
            default:
                return Integer.MIN_VALUE;
        }
    }

    @Override
    public int compareTo(Move move) {
        // User defined ordering
        int myType = type.ordinal();
        int otherType = move.type.ordinal();
        if (myType != otherType)
            return myType - otherType;
        else {
            switch (type) {
                case Flower:
                    if (!getFirstFlower().equals(move.getFirstFlower()))
                        return getFirstFlower().compareTo(move.getFirstFlower());
                    else
                        return getSecondFlower().compareTo(move.getSecondFlower());
                case Ditch:
                    return ditch.compareTo(move.ditch);
                default:
                    return 0;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof Move))
            return false;
        Move m = (Move) o;
        if (type != m.type)
            return false;
        switch (type) {
            case Flower:
                return getFirstFlower().equals(m.getFirstFlower()) && getSecondFlower().equals(m.getSecondFlower());
            case Ditch:
                return ditch.equals(m.ditch);
            default:
                // Type is already the same
                return true;
        }
    }

    @Override
    public String toString() {
        String s = "{";
        switch (getType()) {
            case Flower:
                s += getFirstFlower().toString() + "," + getSecondFlower().toString();
                break;
            case Ditch:
                s += ditch.toString();
                break;
            default:
                s += type.toString();
        }
        return s + "}";
    }
}
