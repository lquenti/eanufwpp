package flowerwarspp.util.log;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static flowerwarspp.util.log.LogLevel.DUMP;
import static flowerwarspp.util.log.LogLevel.NONE;
import static flowerwarspp.util.log.LogModule.ALL;

/**
 * Eine Utility-Klasse zum einfachen Loggen von Spielinformation, Fehlern, Warnungen und
 * Debug-Daten.
 * <p>
 * Dem Programm stehen in dieser Implementation verschiedene Level und Modulverweise zur Verfügung.
 * Der Benutzer kann dann angeben, Nachrichten welchen Levels und/oder welchen Moduls er/sie
 * angezeigt bekommen möchte.
 * <p>
 * Außerdem besteht die Möglichkeit, den Output des Logs nicht in der Standardausgabe anzuzeigen,
 * sondern in einer seperaten Log-Datei zu speichern und so für den Benutzer auch zu einem späteren
 * Zeitpunkt nutzbar zu machen.
 * <p>
 * Im Normalfall wird, falls ein Eintrag mit {@link #log(LogLevel, LogModule, String)} an den Logger
 * geschickt wird, der gesamte Log bis dahin mit {@link #flush()} gelehrt. {@link #flush()} schreibt
 * den gesamten Inhalt des {@link #messageBuffer} mit {@link PrintStream#print(String)} des Objekts
 * {@link #output}.
 */
public class Log {

	/**
	 * Gibt an, ob der Logger aktiv ist (bei <code>true</code>) oder nicht. Wird automatisch
	 * akitiviert, falls der {@link #logLevel}, das {@link #logModule} oder {@link #output} gesetzt
	 * wird.
	 */
	private static boolean isLogging = false;

	/**
	 * Der aktuelle Log-Level des Loggers. Nur Einträge, welche von der Priorität her gleich diesem
	 * Level (oder höher) liegen, werden vom Logger gespeichert und ausgegeben.
	 * <p>
	 * So lässt sich der Output des Logs zum Beispiel auf Fehler und Warnungen beschränken.
	 */
	private static LogLevel logLevel = NONE;

	/**
	 * Das aktuell zu überwachende Modul. Hiermit lässt sich der Log-Output beschränken auf
	 * Einträge eines bestimmten Moduls (oder packages). Zum Beispiel möchte man nur Einträge des
	 * Hauptprogramms ({@link LogModule#MAIN}) angezeigt bekommen, dann würde man diese Variable
	 * mit dem zugehörigen Setter {@link #setLogModule(LogModule)} auf eben diesen Wert setzen.
	 */
	private static LogModule logModule = ALL;

	/**
	 * Der {@link StringBuffer}, welcher die Log-Nachrichten intern speichert.
	 */
	private static StringBuffer messageBuffer = new StringBuffer();

	/**
	 * Ein {@link PrintStream}, welcher angibt, wohin der Log ausgegeben werden soll. Normalerweise
	 * ist das die Standardausgabe, also der PrintStream <code>System.out</code>.
	 */
	private static PrintStream output = System.out;

	/**
	 * Gibt an, ob der Log nach jedem neuen Eintrag mit {@link #flush()} auf {@link #output}
	 * ausgegeben werden soll.
	 */
	private static boolean flushOnLog = true;

	/**
	 * Leerer privater Konstruktor zum Verhindern der Objektinitialisierung
	 */
	private Log() {}

	/**
	 * Gibt das aktuelle Log-Level aus.
	 *
	 * @return Aktuelles Log-Level
	 */
	public static LogLevel getLogLevel() {
		return logLevel;
	}

	/**
	 * Setzt den Log-Level auf den gewünschten Wert.
	 *
	 * @param logLevel
	 * 		Der neue Log-Level des Loggers
	 */
	public static void setLogLevel(LogLevel logLevel) {
		Log.logLevel = logLevel;
		isLogging = true;
	}

	/**
	 * Gibt das aktuell geloggte Modul aus.
	 *
	 * @return Das aktuell betrachtete Modul
	 */
	public static LogModule getLogModule() {
		return logModule;
	}

	/**
	 * Setzt das betrachtete Modul auf den gewünschten Wert.
	 *
	 * @param logModule
	 * 		Das neue zu betrachtende Modul
	 */
	public static void setLogModule(LogModule logModule) {
		Log.logModule = logModule;
		isLogging = true;
	}

	/**
	 * Gibt den aktuellen Output-{@link PrintStream} aus.
	 *
	 * @return Der aktuelle Output-{@link PrintStream}
	 */
	public static PrintStream getOutput() {
		return output;
	}

	/**
	 * Setzt den, zur Ausgabe zu verwendenen, {@link PrintStream}.
	 *
	 * @param output
	 * 		Der zu verwendene {@link PrintStream}
	 */
	public static void setOutput(PrintStream output) {
		Log.output = output;
		isLogging = true;
	}

	/**
	 * Gibt zurück, ob der Log nach jedem neuen Eintrag geflushed wird.
	 *
	 * @return <code>true</code> falls nach jedem neuen Eintrag geflushed wird, <code>false</code>
	 * andererseits
	 */
	public static boolean flushOnLog() {
		return flushOnLog;
	}

	/**
	 * Aktiviert oder deaktiviert den Logger.
	 *
	 * @param isLogging
	 * 		<code>true</code> aktiviert den Logger, <code>false</code> deaktiviert ihn.
	 */
	public static void enableLogger(final boolean isLogging) {
		Log.isLogging = isLogging;
	}

	/**
	 * Aktiviere den automatischen {@link #flush()} des Logs nach jedem neuem Eintrag.
	 *
	 * @param flushOnLog
	 * 		<code>true</code> aktiviert den automatischen <code>flush</code>, <code>false</code>
	 * 		deaktiviert die Funktion
	 */
	public static void setFlushOnLog(boolean flushOnLog) {
		Log.flushOnLog = flushOnLog;
	}

	/**
	 * Gibt <code>true</code> zurück, falls der Logger aktiviert ist, <code>false</code>
	 * andererseits.
	 *
	 * @return <code>true</code>, falls der Logger aktiviert ist, <code>false</code>
	 */
	public static boolean isLogging() {
		return isLogging;
	}

	/**
	 * Sendet eine neue Nachricht angegebenen Log-Levels aus dem angegebenen Modul an den Logger.
	 *
	 * @param level
	 * 		Der Log-Level der Nachricht
	 * @param module
	 * 		Das Modul aus welchem die Nachricht gesendet worden ist
	 * @param message
	 * 		Die Nachricht des Log-Eintrags
	 */
	public static void log(LogLevel level, LogModule module, String message) {
		if (isLogging && level.compareTo(logLevel) >= 0 &&
				((logModule == ALL) || (logModule == module))) {
			messageBuffer.append(getTimeStamp()).append('\t').append(level.toString()).append('\t')
					.append(module.toString()).append('\t').append(message).append('\n');

			// Falls das geloggte Level einen Daten-Dump beschreibt, oder falls das automatische
			// Flushen explizit
			// deaktiviert ist, wird der Log nicht auf die Ausgabe geschrieben.
			if (flushOnLog && level != DUMP) {
				flush();
			}
		}
	}

	/**
	 * Erstellt den aktuellen Timestamp im Format <blockquote>dd-MM-yyyy HH:mm:ss.SS</blockquote>.
	 * Wird nur in der formartierten Ausgabe verwendet.
	 *
	 * @return Aktueller Zeitpunkt im Format <code>dd-MM-yyyy HH:mm:ss.SS</code>
	 *
	 * @see SimpleDateFormat
	 */
	private static String getTimeStamp() {
		return new SimpleDateFormat("[dd-MM-yyyy HH:mm:ss.SS]").format(new Date());
	}

	/**
	 * Gibt die geloggten Nachrichten mit {@link PrintStream#print(String)} auf dem gegebenen
	 * Output-{@link PrintStream} aus.
	 */
	public static void flush() {
		output.print(getLogOutput());
	}

	/**
	 * Gibt die im {@link StringBuffer} abgelegten Log-Einträge als String zurück.
	 *
	 * @return Die Log-Einträge im {@link #messageBuffer}
	 */
	private static String getLogOutput() {

		String output = "";

		if (messageBuffer.length() > 1) {

			output = messageBuffer.toString();
			messageBuffer = new StringBuffer();
		}

		return output;
	}
}
