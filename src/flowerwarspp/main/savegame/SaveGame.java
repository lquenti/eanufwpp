package flowerwarspp.main.savegame;

import flowerwarspp.preset.*;
import flowerwarspp.util.log.*;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Iterator;

import static flowerwarspp.preset.PlayerColor.Blue;
import static flowerwarspp.preset.PlayerColor.Red;

/**
 * Diese Klasse erlaubt das Speichern und Laden des aktuellen Status des Spiels. Dies wird ermöglicht durch das
 * Speichern aller gemachten Spielzüge in einer {@link ArrayDeque}.
 * <p>
 * Mit {@link #save(String)} werden diese gemachten Züge zusammen mit gewissen Meta-Daten in einer Datei mit dem
 * gegebenen Namen gespeichert.
 * <p>
 * Diese Datei kann zu einem späteren Zeitpunkt mit {@link #load(String)} wieder geladen werden, dabei werden die, von
 * der Datei beschriebenen, Spielzüge in der {@link ArrayDeque} einer neuen Instanz dieser Klasse gespeichert. Diese
 * Instanz wird dann zurückgegeben.
 */
public class SaveGame implements Iterable<Move> {
	/**
	 * Diese {@link ArrayDeque} speichert die ausgeführten Spielzüge.
	 */
	private ArrayDeque<Move> madeMoves;

	/**
	 * Die Größe des aktuellen Spielbretts.
	 */
	private int boardSize;

	/**
	 * Dieser Konstruktor initialisiert ein neues SaveGame-Objekt mit der gegebenen Spielbrett-Größe.
	 *
	 * @param boardSize Die Spielbrettgröße des aktuellen Spiels
	 */
	public SaveGame(int boardSize) {

		this.madeMoves = new ArrayDeque<>();
		this.boardSize = boardSize;
	}

	/**
	 * Diese Methode ermöglicht das Laden eines mit {@link #save(String)} gespeicherten Spielstands.
	 * <p>
	 * Dabei werden die, von der Datei beschriebenen, Spielzüge in der {@link ArrayDeque} einer neuen Instanz dieser
	 * Klasse gespeichert. Diese Instanz wird dann zurückgegeben.
	 *
	 * @param saveGameName Name der zu ladenden Spielstand-Datei mit Pfad
	 * @return Die, mit den in der Datei gespeicherten Spielzüge, intialisierte Instanz dieser Klasse
	 * @throws LoadException Falls während des Ladevorgangs ein Fehler aufgetreten ist
	 */
	public static SaveGame load(String saveGameName) throws LoadException {

		/*
		 * Mit einem try-with-resources wird ein neuer BufferedReader instanziiert. Falls während des Ladens ein
		 * Fehler auftritt und eine Exception geworfen wird, wird dieser BufferedReader automatisch geschlossen.
		 */
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(saveGameName))) {
			/*
			 * Die erste Zeile einer Savegame-Datei beinhaltet die Größe des Spielbretts. Diese wird eingelesen und
			 * damit ein neues Objekt dieser Klasse als Rückgabewert instanziiert.
			 */
			int boardSize = Integer.parseInt(bufferedReader.readLine());
			SaveGame saveGame = new SaveGame(boardSize);

			// Die nächste Zeile wird eingelesen. Dann wird solange iteriert, bis keine Zeile mehr gelesen werden kann.
			String currentLine = bufferedReader.readLine();

			while (currentLine != null) {

				/*
				 * Es wird versucht, die aktuell eingelesene Zeile als Zug zu parsen. Außerdem lesen wir den
				 * gespeicherten Hash-Code des Zuges aus der gleichen Zeile aus.
				 */
				Move move = Move.parseMove(currentLine.split(";", 2)[0]);
				int hashCode = Integer.parseInt(currentLine.split(";", 3)[1]);

				/*
				 * Falls sich der gespeicherte Hash-Code nicht mit dem Hash-Code des eingelesenen Spielzugs gleicht,
				 * wird der Ladevorgang abgebrochen und eine Exception geworfen.
				 */
				if (move.hashCode() != hashCode) {
					Log.log(LogLevel.ERROR, LogModule.MAIN, "The hasCode of the loaded move was not equal " +
							"to the hasCode stored in the savegame");
					throw new LoadException("Der hashCode des geladenen Spielzugs stimmt nicht mit dem hinterlegten" +
							"hashCode überein!");
				}

				// Dem neu instanziierten Objekt wird der eingelesene Zug mitgeteilt.
				saveGame.add(move);

				// Und die nächste Zeile wird gelesen.
				currentLine = bufferedReader.readLine();
			}
			return saveGame;
		} catch (IOException e) {
			throw new LoadException(e);
		}
	}

	/**
	 * Gibt den Wert von {@link #boardSize} zurück.
	 *
	 * @return Wert von {@link #boardSize}.
	 */
	public int getBoardSize() {
		return boardSize;
	}

	/**
	 * Fügt den gegeben Zug der Liste der gespeicherten Spielzüge hinzu.
	 *
	 * @param move Spielzug, welcher der {@link ArrayDeque} hinzugefügt werden soll
	 */
	public void add(Move move) {
		if (move == null) return;

		synchronized (this) {

			madeMoves.add(move);
		}
	}

	/**
	 * Speichert den Spielstand in einer Datei mit gegebenen Namen.
	 *
	 * @param saveGameName Name des Spielstands mit Pfad.
	 * @throws IOException Falls während des Speicherns des Spielstands ein Fehler aufgetreten ist.
	 */
	public void save(String saveGameName) throws IOException {

		synchronized (this) {

			/*
			 * Das Speichern der Züge wird mit einem try-with-resources ermöglicht. Der so erstellte PrintWriter wird
			 * automatisch geschlossen, falls während des Speicherns ein Fehler aufgetreten ist und eine Exception geworfen
			 * wurde.
			 */
			try (PrintWriter printWriter = new PrintWriter(saveGameName, "UTF-8")) {

				// In der ersten Zeile wird die Größe des Spielbretts gespeichert.
				printWriter.println(boardSize);
				PlayerColor currentPlayer = Red;

				// Hilfsvariable zum Speichern der Anzahl der gemachten Züge.
				int i = 0;

				// Iterieren durch die gemachten Züge. Pro Zeile wird genau ein Zug gespeichert.
				for (Move m : madeMoves) {

					/*
					 * Ein Zug wird in folgendem Format gespeichert:
					 * String-Repräsentation des Moves;HashCode des Zugs;Aktueller Spieler, #i
					 * Das Semikolon (;) wird hier als Seperator verwendet, da es nicht in der String-Repräsentation
					 * eines Spielzugs vorkommt.
					 */
					printWriter.println(m + ";" + m.hashCode() + ";" + currentPlayer
							+ ", " + "#" + i);

					// Wechseln des aktuellen Spielers mit dem Elvis-Operator.
					currentPlayer = currentPlayer == Red ? Blue : Red;
					i++;
				}

				// Den PrintWriter schließen, falls Speichern erfolgreich.
				printWriter.close();

				Log.log(LogLevel.INFO, LogModule.MAIN, "Game was saved to: " + saveGameName);

			} catch (IOException e) {
				Log.log(LogLevel.ERROR, LogModule.MAIN, "Saving the game failed: " + e.getMessage());
				throw e;
			}
		}
	}

	/**
	 * Gibt einen Iterator für diese Klasse zurück. Der Iterator ist anonym implementiert.
	 *
	 * @return ein Iterator
	 */
	@Override
	public Iterator<Move> iterator() {
		return new Iterator<Move>() {

			Iterator<Move> it = madeMoves.iterator();

			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public Move next() {
				return it.next();
			}
		};
	}
}
