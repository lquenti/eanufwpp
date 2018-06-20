package flowerwarspp.board;

import flowerwarspp.io.BoardViewer;
import flowerwarspp.preset.*;

import java.util.ArrayList;
import java.util.HashSet;

/*
TODO: eigenen besseren Floweralgo benutzen
 */

/**
 * Boardimplementation. Implementiert Board und somit auch Viewable
 *
 * @author Lars Quentin
 * @version 0.1
 */
public class MainBoard implements Board {
    /**
     * Menge der existierenden Blumen als HashSet
     */
    private HashSet<Flower> fBoard = new HashSet<>();
    /**
     * Menge der existierenden Graeben als HashSet
     */
    private HashSet<Ditch> dBoard;
    /**
     * Groesse des Boards
     */
    private final int size;

    /**
     * Konstruktor. Befuellt Board einer variablen Groesse zwischen [3;30].
     * Falls Wert invalide wird dieser dem naechsten Element des Intervalls angepasst.
     *
     * @param size Groesse des Boardes.
     */
    public MainBoard(final int size) {
        this.size = (size < 3) ? 3 : ((size > 30) ? 30 : size);
    }

    /**
     * {@inheritDoc}
     * Verifiziert Zug und fuehrt diesen dann aus.
     *
     * @param move auszufuehrender Zug
     * @throws IllegalStateException Wenn Zug nicht valide ist.
     */
    @Override
    public void make(final Move move) throws IllegalStateException {
        boolean turnWasValid;

        // TODO: CATCH NULLPTR
        MoveType moveType = move.getType();
        if (moveType == MoveType.End) {
            // TODO
            return;
        } else if (moveType == MoveType.Surrender) {
            // TODO
            return;
        } else if (moveType == MoveType.Flower) {
            turnWasValid = flowerTurn(move.getFirstFlower(), move.getSecondFlower());
        } else if (moveType == MoveType.Ditch) {
            turnWasValid = ditchTurn(move.getDitch());
        } else {
            turnWasValid = false;
        }

        // Endcheck
        if (!turnWasValid) {
            throw new IllegalStateException("Zug war nicht valide!");
        }
    }

    // TODO: CHECK IF POSITION INSIDE SIZE
    private boolean flowerTurn(final Flower first, final Flower second) throws IllegalStateException {
        // If first move its always legal
        if (fBoard.size() == 0) {
            fBoard.add(first);
            fBoard.add(second);
            return true;
        }
        // contains check since it will make validation way faster and just has O(1)
        if (fBoard.contains(first) || fBoard.contains(second)) {
            throw new IllegalStateException("Flower already existing!");
        }

        // Temporary add
        fBoard.add(first);
        fBoard.add(second);

        // TODO: Sorting
        // clean up
        fBoard.remove(first);
        fBoard.remove(second);
        return false;
    }

    private boolean ditchTurn(final Ditch ditch) {
        // TODO
        return true;
    }

    /**
     * Gibt den dazugehoerigen Viewer der Klasse BoardViewer zurueck.
     *
     * @return den dazugehoerigen Viewer
     */
    @Override
    public Viewer viewer() {
        return new BoardViewer(this);
    }
}
