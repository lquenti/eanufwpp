package flowerwarspp.player;

import flowerwarspp.preset.Move;
import flowerwarspp.preset.Player;


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

        // If we do not have a highest scored move, we throw.
        if ( highestScoredMove == null )
            throw new Exception(exception_NoMove);

        return highestScoredMove;
    }

    /**
     * Methode zur Berechnung des Scores eines gegebenen Zuges. Der höchst-bewerteste Zug wird von {@link
     * #requestMove()} ausgewählt und ausgeführt.
     *
     * @param move Der {@link Move} dessen Score berechnet werden soll
     * @return Der Score des Spielzugs
     */
    abstract protected int getMoveScore( Move move );
}
