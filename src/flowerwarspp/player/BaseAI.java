package flowerwarspp.player;

import flowerwarspp.preset.Move;
import flowerwarspp.preset.MoveType;
import flowerwarspp.preset.Player;
import flowerwarspp.util.log.Log;
import flowerwarspp.util.log.LogLevel;
import flowerwarspp.util.log.LogModule;

import java.util.HashSet;
import java.util.Random;

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
	protected static final String exception_NoMove =
			"Die KI konnte keinen Zug auswaehlen.";

	/**
	 * <code>private</code> Random Number Generator, um die zufällige Auswahl eines Spielzugs mit Hilfe von
	 * Pseudozufallszahlen leisten zu können.
	 */
	static final Random aiRNG = new Random();

	/**
	 * Globale Definition des Scores, falls ein {@link MoveType#Ditch}-Move gemacht werden kann, welcher den Score des
	 * Spielers verbessern würde.
	 */
	protected static final int SCORE_DITCH = 1000;

	/**
	 * Globale Definition des Scores, falls ein {@link MoveType#End}-Move gemacht werden kann.
	 */
	protected static final int SCORE_END = 500;

	/**
	 * Default-Konstruktor, welcher dieses Objekt mit Standardwerten versieht.
	 */
	protected BaseAI() {
		super();
	}

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
		if ( move == null ) {
			log(LogLevel.ERROR, "AI was unable to return a move");
			throw new Exception(exception_NoMove);
		}

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
		HashSet<Move> highestScoredMoves = new HashSet<>();

		// Iterate through all the possible moves...
		for ( final Move move :
				boardViewer.getPossibleMoves() ) {

			final int score = getMoveScore(move);
			log(LogLevel.DUMP, "move " + move + " has score of " + score);

			// If the score of the currently observed move is higher than the previously highest score, update the relevant variables.

			if (score >= SCORE_END) {
				return move;
			} else if ( score > highestScore ) {
				highestScore = score;
				highestScoredMoves.clear();
				highestScoredMoves.add(move);
			} else if ( score == highestScore ) {
				highestScoredMoves.add(move);
			}
		}

		// Manually flush the log now that the data dump is complete...
		Log.getInstance().flush();

		log(LogLevel.DEBUG, "highestScore: " + highestScore + ", highestScoredMoves: " + highestScoredMoves);

		if ( highestScoredMoves.size() == 0 ) return null;

		// Since we are using
		return highestScoredMoves
				.stream()
				.skip((aiRNG.nextInt(highestScoredMoves.size())))
				.findFirst()
				.orElse(null);
	}
}
