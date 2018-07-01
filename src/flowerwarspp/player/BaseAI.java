package flowerwarspp.player;

import flowerwarspp.preset.Move;
import flowerwarspp.preset.Player;

/**
 * Eine abstrakte Klasse, welche grundlegende Methoden vordefiniert um KIs zu implementieren. Dabei wird im Laufe der
 * {@link #requestMove()}-Methode für die möglichen Spielzüge ein Bewertungsalgorithmus durchlaufen, welcher einen Zug
 * nach Strategie auswählt, zurück gibt und ausführt.
 *
 * @author Michael Merse
 */
abstract class BaseAI extends BasePlayer {
    /**
     * Eine vordefinierte Nachricht einer {@link Exception}, welche geworfen wird, wenn die KI mit ihrer Strategie
     * keinen Zug auswählen konnte.
     */
    private static final String exception_NoMove =
            "Die einfache KI konnte keinen Zug auswaehlen.";


    /**
     * Fordert einen Zug an, nach den Vorgaben der Interface-Methode {@link Player#request()}. Diese Methode bedient
     * sich einer Strategie zur Bewertung und anschließend zur Auswahl eines Zuges, welche von der Implementation der
     * Methode {@link #getMoveScore(Move)} abhängt.
     *
     * @return Der vom Spieler zurück gegebene Zug
     * @throws Exception Falls der Spieler nicht in der Lage war, einen Zug zu liefern oder falls diese Methode zum
     *                   falschen Zeitpunkt innerhalb des Zyklus aufgerufen worden ist
     */
    protected Move requestMove() throws Exception {

        final Move move = getMove();

        // If we do not have a highest scored move, we throw.
        if ( move == null )
            throw new Exception(exception_NoMove);

        return move;
    }

    /**
     * Methode zur Berechnung des Scores eines gegebenen Zuges. Der höchst-bewerteste Zug wird von {@link
     * #requestMove()} ausgewählt und ausgeführt.
     *
     * @param move Der {@link Move} dessen Score berechnet werden soll
     * @return Der Score des Spielzugs
     */
    abstract protected int getMoveScore( final Move move );

    /**
     * Gibt einen Spielzug zurück, per default wird dieser Spielzug nach einem implementierten Bewertungsalgorithmus
     * ausgewählt. Der Spielzug mit dem höchsten Score wird zurück gegeben. Klassen die diese abstrakte Klasse
     * implementieren können die Auswahl eines Zuges aber durchaus anders definieren.
     *
     * @return Der Spielzug mit dem höchsten Score.
     */
    protected Move getMove() {

        int highestScore = 0;
        Move highestScoredMove = null;

        // Iterate through all the possible moves...
        for ( final Move move :
                this.boardViewer.getPossibleMoves() ) {

            final int score = getMoveScore(move);

            // If the score of the currently observed move is higher than the previously highest score, update the relevant variables.
            if ( score > highestScore ) {
                highestScore = score;
                highestScoredMove = move;
            }
        }

        return highestScoredMove;
    }
}
