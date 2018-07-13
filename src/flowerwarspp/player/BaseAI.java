package flowerwarspp.player;

import flowerwarspp.preset.Move;
import flowerwarspp.preset.MoveType;
import flowerwarspp.preset.Player;
import flowerwarspp.util.log.Log;
import flowerwarspp.util.log.LogLevel;

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
	 * Globale Definition des Scores, falls ein {@link MoveType#Ditch}-Move gemacht werden kann, welcher den Score des
	 * Spielers verbessern würde.
	 */
	protected static final int SCORE_DITCH = 1000;
	/**
	 * Globale Definition des Scores, falls ein {@link MoveType#End}-Move gemacht werden kann.
	 */
	protected static final int SCORE_END = 500;
	/**
	 * <code>private</code> Random Number Generator, um die zufällige Auswahl eines Spielzugs mit Hilfe von
	 * Pseudozufallszahlen leisten zu können.
	 */
	protected static final Random random = new Random();

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

		// Falls getMove() keinen Zug liefern konnte, wird eine Exception geworfen.
		if (move == null) {
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
	abstract protected int getMoveScore(Move move);

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

		// Durch alle möglichen Züge iterieren...
		for (Move move :
				boardViewer.getPossibleMoves()) {

			// Den Score eines Zuges mit der abstrakten Methode berechnen.
			final int score = getMoveScore(move);
			log(LogLevel.DUMP, "move " + move + " has score of " + score);

			if (score >= SCORE_END) {
				// Falls getMoveScore() einen Wert größer gleich der statischen Werte SCORE_END und SCORE_DITCH
				// zurückgegeben hat, wird dieser Zug sofort verwendet.
				return move;

			} else if (score > highestScore) {
				// Falls der zurück gegebene Zug einen höheren Score hat als alle Züge davor, wird dieser Zug zum höchst
				// bewerteten Zug.
				highestScore = score;
				highestScoredMoves.clear();
				highestScoredMoves.add(move);

			} else if (score == highestScore) {
				// Falls der zurück gegebene Zug einen Score gleich dem bisher höchsten Score hat, wird der aktuell
				// betrachtete Zug der Collection der höchst bewerteten Züge hinzugefügt.
				highestScoredMoves.add(move);
			}
		}

		// Nach dem Daten-Dump wird der Log manuell geflushed.
		Log.flush();

		log(LogLevel.DEBUG, "highestScore: " + highestScore + ", highestScoredMoves: " + highestScoredMoves);

		// Falls die Collection der höchste bewerteten Züge leer ist wird null zurück gegeben.
		if (highestScoredMoves.size() == 0) return null;

		// Es wird aus der Collection der am höchsten bewerteten Züge zufällig ein Zug ausgewählt.
		// skip(int n) gibt einen neuen Stream zurück, mit den verbleibenden Elementen des Streams nachdem die ersten n
		// Elemente übersprungen worden sind.
		// findFirst() gibt entweder das erste Element dieses Streams als Optional zurück, oder ein Optional mit dem
		// Wert null. Mit orElse() wird entweder dieses erste Element zurück gegeben, oder null, falls das Optional
		// diesen Wert hat.
		return highestScoredMoves
				.stream()
				.skip(( random.nextInt(highestScoredMoves.size()) ))
				.findFirst()
				.orElse(null);
	}
}
