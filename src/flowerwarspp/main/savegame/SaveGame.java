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
 *
 * @author Michael Merse
 */
public class SaveGame implements Iterable<Move> {

	/**
	 * Der Basispfad einer Spielstand-Datei. Die Spielstände werden in einem Ordner <code>SaveGames</code> im aktuellen
	 * Verzeichnis (in welchem das Spiel ausgeführt wird) gespeichert.
	 */
	private static final String SAVE_PATH_ROOT = System.getProperty("user.dir") + File.separator + "SaveGames"
			+ File.separator;

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
	 * Stellt den gesamten Pfad zu einer Spielstand-Datei mit gegebenen Namen zusammen und gibt diesen zurück.
	 * <p>
	 * Der gesamte Pfad setzt sich zusammen aus {@link #SAVE_PATH_ROOT}, dem Namen der Spielstand-Datei und der Datei-
	 * Endung ".sav".
	 *
	 * @param saveGameName Name des Spielstands
	 * @return Der gesamte absolute Pfad zu der Spielstand-Datei.
	 */
	private static String getFilePath(String saveGameName) {

		return SAVE_PATH_ROOT + saveGameName + ".sav";
	}

	/**
	 * Diese Methode ermöglicht das Laden eines mit {@link #save(String)} gespeicherten Spielstands.
	 * <p>
	 * Dabei werden die, von der Datei beschriebenen, Spielzüge in der {@link ArrayDeque} einer neuen Instanz dieser
	 * Klasse gespeichert. Diese Instanz wird dann zurückgegeben.
	 *
	 * @param saveGameName Name der zu ladenden Spielstand-Datei ohne Endung
	 * @return Die, mit den in der Datei gespeicherten Spielzüge, intialisierte Instanz dieser Klasse
	 * @throws LoadException Falls während des Ladevorgangs ein Fehler aufgetreten ist
	 */
	public static SaveGame load(String saveGameName) throws LoadException {

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(getFilePath(saveGameName)))) {

			int boardSize = Integer.parseInt(bufferedReader.readLine());
			SaveGame saveGame = new SaveGame(boardSize);

			String currentLine = bufferedReader.readLine();

			while (currentLine != null) {

				Move move = Move.parseMove(currentLine.split(";", 2)[0]);
				int hashCode = Integer.parseInt(currentLine.split(";", 3)[1]);

				if (move.hashCode() != hashCode) {
					Log.log(LogLevel.ERROR, LogModule.MAIN, "The hasCode of the loaded move was not equal " +
							"to the hasCode stored in the savegame");
					throw new LoadException("Der hashCode des geladenen Spielzugs stimmt nicht mit dem hinterlegten" +
							"hashCode überein!");
				}

				saveGame.add(move);

				currentLine = bufferedReader.readLine();
			}
			return saveGame;
		} catch (IOException e) {
			throw new LoadException();
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

		madeMoves.add(move);
	}

	/**
	 * Speichert den Spielstand in einer Datei mit gegebenen Namen.
	 *
	 * @param saveGameName Name des Spielstands.
	 * @throws IOException Falls während des Speicherns des Spielstands ein Fehler aufgetreten ist.
	 */
	public void save(String saveGameName) throws IOException {

		try {
			File saveDir = new File(SAVE_PATH_ROOT);
			saveDir.mkdir();

			PrintWriter printWriter = new PrintWriter(getFilePath(saveGameName), "UTF-8");

			printWriter.println(boardSize);
			PlayerColor currentPlayer = Red;

			int i = 0;

			for (Move m
					: madeMoves) {

				printWriter.println(m + ";" + m.hashCode() + ";" + currentPlayer
						+ ", " + "#" + i);
				currentPlayer = currentPlayer == Red ? Blue : Red;
				i++;
			}

			printWriter.close();
			System.out.println("Spielstand " + saveGameName + " wurde gespeichert unter");
			System.out.println(getFilePath(saveGameName));
			Log.log(LogLevel.INFO, LogModule.MAIN, "Game was saved to: " + getFilePath(saveGameName));

		} catch (IOException e) {
			Log.log(LogLevel.ERROR, LogModule.MAIN, "Saving the game failed: " + e.getMessage());
			throw e;
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
