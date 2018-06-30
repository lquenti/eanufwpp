package flowerwarspp.player;

import flowerwarspp.board.MainBoard;
import flowerwarspp.preset.*;

import java.rmi.RemoteException;
import java.util.Collection;

/**
 * Implementiert die abstrakte Klasse {@link BasePlayer} mit einer simplen KI. Diese KI bedient sich einer limitierten
 * Bewertungsstrategie zur Auswahl eines Zuges und gibt diesen auf Anforderung zurueck.
 *
 * @author Michael Merse
 */
public class SimpleAI extends BasePlayer {

    /**
     * Eine vordefinierte Nachricht einer {@link Exception}, welche geworfen wird, wenn die einfache KI mit ihrer
     * Strategie keinen Zug auswaehlen konnte.
     */
    private static final String exception_NoMove =
            "Die einfache KI konnte keinen Zug auswaehlen.";


    /**
     * Fordert einen Zug an, nach den Vorgaben der Interface-Methode {@link Player#request()}. Diese Methode bedient
     * sich einer einfachen Strategie zur Bewertung und anschliessend zur Auswahl eines Zuges.
     *
     * @return Der vom Spieler angeforderte Zug
     * @throws Exception       Falls der Spieler nicht in der Lage war, einen Zug zu liefern oder falls diese Methode
     *                         zum falschen Zeitpunkt innerhalb des Zyklus aufgerufen worden ist
     * @throws RemoteException Falls ein Fehler waehrend der Netzwerk-Kommunikation aufgetreten ist
     */
    // TODO: Implement the actual weighted algorithm
    protected Move requestMove() throws Exception, RemoteException {

        int highestScore = 0;
        Move highestScoredMove = null;

        // Iterate through all the possible moves...
        for ( final Move move :
                this.boardViewer.getPossibleMoves() ) {

            // We are only concerned with moves that actually make flowers.
            if ( ! move.getType().equals(MoveType.Flower) ) continue;

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

    private int getMoveScore( Move move ) {

        int n1 = 0;
        int n2 = 0;
        int score;

        // Obtain the direct neighbors of both flowers.
        final Collection<Flower> firstFlowerNeighbors = boardViewer.getDirectNeighbors(move.getFirstFlower());
        final Collection<Flower> secondFlowerNeighbors = boardViewer.getDirectNeighbors(move.getSecondFlower());

        final Collection<Flower> playerFlowers = boardViewer.getFlowers(this.getPlayerColour());

        // Iterate through all the first flower's neighbours and calculate the score.
        for ( final Flower neighbor : firstFlowerNeighbors ) {
            if ( playerFlowers.contains(neighbor) )
                n1++;
        }

        // Iterate through all the second flower's neighbours and calculate the score.
        for ( final Flower neighbor : secondFlowerNeighbors ) {
            if ( playerFlowers.contains(neighbor) )
                n2++;
        }

        // Calculate the score as indicated by the strategy.
        score = ( n1 + 1 ) * ( n2 + 1 );

        // If both flowers are attached (i.e. if they're neighbors) double the score.
        if ( firstFlowerNeighbors.contains(move.getSecondFlower()) )
            score *= 2;

        return score;
    }

}
