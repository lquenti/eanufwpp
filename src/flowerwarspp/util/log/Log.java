package flowerwarspp.util.log;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static flowerwarspp.util.log.LogLevel.*;
import static flowerwarspp.util.log.LogModule.*;

/**
 * Eine Utility-Klasse zum einfachen Loggen von Spielinformation, Fehlern, Warnungen und Debug-Daten.<br> Diese Klasse
 * bedient sich des Singleton-Prinzips, es kann immer nur genau eine einzige Instanz dieser Klasse existieren. So lassen
 * sich global Informationen loggen, ohne dass es zu Inkonsistenzen des Logs kommt.<br>
 * <p>
 * Dem Programm stehen in dieser Implementation verschiedene Level und Modulverweise zur Verfügung. Der Benutzer kann
 * dann angeben, Nachrichten welchen Levels und/oder welchen Moduls er/sie angezeigt bekommen möchte.<br>
 * <p>
 * Außerdem besteht die Möglichkeit, den Output des Logs nicht in der Standardausgabe anzuzeigen, sondern in einer
 * seperaten Log-Datei zu speichern und so für den Benutzer auch zu einem späteren Zeitpunkt nutzbar zu machen.<br>
 * <p>
 * Im Normalfall wird, falls ein Eintrag mit {@link #log(LogLevel, LogModule, String)} oder {@link #log(LogLevel,
 * String)} an den Logger geschickt wird, der gesamte Log bis dahin mit {@link #flush()} gelehrt. {@link #flush()}
 * schreibt den gesamten Inhalt des {@link #messageBuffer} mit {@link PrintStream#print(String)} des Objekts {@link
 * #output}.
 *
 * @author Michael Merse
 */
public class Log {

	/**
	 * Private Referenz auf die singuläre Instanz dieser Klasse.
	 */
	private static Log instance;

	/**
	 * Gibt an, ob der Logger aktiv ist (bei <code>true</code>) oder nicht. Wird automatisch akitiviert, falls der
	 * {@link #logLevel}, das {@link #logModule} oder {@link #output} gesetzt wird.
	 */
	private boolean isLogging = false;

	/**
	 * Ob die Log-Einträge formatiert werden sollen, oder nicht.<br> Deaktivieren der Formatierung könnte evtl. die
	 * Performance verbessern, ob dem so ist müsste aber überprüft werden.
	 */
	private boolean useFormatting = true;

	/**
	 * Der aktuelle Log-Level des Loggers. Nur Einträge, welche von der Priorität her gleich diesem Level (oder höher)
	 * liegen, werden vom Logger gespeichert und ausgegeben.
	 * <p>
	 * So lässt sich der Output des Logs zum Beispiel auf Fehler und Warnungen beschränken.
	 */
	private LogLevel logLevel = NONE;

	/**
	 * Das aktuell zu überwachende Modul. Hiermit lässt sich der Log-Output beschränken auf Einträge eines bestimmten
	 * Moduls (oder packages). Zum Beispiel möchte man nur Einträge des Hauptprogramms ({@link LogModule#MAIN})
	 * angezeigt bekommen, dann würde man diese Variable mit dem zugehörigen Setter {@link #setLogModule(LogModule)} auf
	 * eben diesen Wert setzen.
	 */
	private LogModule logModule = ALL;

	/**
	 * Der {@link StringBuffer}, welcher die Log-Nachrichten intern speichert.
	 */
	private StringBuffer messageBuffer;

	/**
	 * Ein {@link PrintStream}, welcher angibt, wohin der Log ausgegeben werden soll. Normalerweise ist das die
	 * Standardausgabe, also der PrintStream <code>System.out</code>.
	 */
	private PrintStream output = System.out;

	/**
	 * Gibt an, ob der Log nach jedem neuen Eintrag mit {@link #flush()} auf {@link #output} ausgegeben werden soll.
	 */
	private boolean flushOnLog = true;

	/**
	 * Privater Konstruktor, um eine neue Instanz dieser Klasse zu erstellen. Wird nur von {@link #getInstance()}
	 * ausferufen.
	 */
	private Log() {
		messageBuffer = new StringBuffer();
	}

	/**
	 * Gibt das aktuelle Log-Level aus.
	 *
	 * @return Aktuelles Log-Level
	 */
	public LogLevel getLogLevel() {
		return logLevel;
	}

	/**
	 * Gibt das aktuell geloggte Modul aus.
	 *
	 * @return Das aktuell betrachtete Modul
	 */
	public LogModule getLogModule() {
		return logModule;
	}

	/**
	 * Gibt den aktuellen Output-{@link PrintStream} aus.
	 *
	 * @return Der aktuelle Output-{@link PrintStream}
	 */
	public PrintStream getOutput() {
		return output;
	}

	/**
	 * Gibt zurück, ob der Log nach jedem neuen Eintrag geflushed wird.
	 *
	 * @return <code>true</code> falls nach jedem neuen Eintrag geflushed wird, <code>false</code> andererseits
	 */
	public boolean flushOnLog() {
		return flushOnLog;
	}

	/**
	 * Setzt den Log-Level auf den gewünschten Wert.
	 *
	 * @param logLevel Der neue Log-Level des Loggers
	 */
	public void setLogLevel( LogLevel logLevel ) {
		this.logLevel = logLevel;
		isLogging = true;
	}

	/**
	 * Setzt das betrachtete Modul auf den gewünschten Wert.
	 *
	 * @param logModule Das neue zu betrachtende Modul
	 */
	public void setLogModule( LogModule logModule ) {
		this.logModule = logModule;
		isLogging = true;
	}

	/**
	 * Setzt den, zur Ausgabe zu verwendenen, {@link PrintStream}.
	 *
	 * @param output Der zu verwendene {@link PrintStream}
	 */
	public void setOutput( PrintStream output ) {
		this.output = output;
		isLogging = true;
	}

	/**
	 * Aktiviere den automatischen {@link #flush()} des Logs nach jedem neuem Eintrag.
	 *
	 * @param flushOnLog <code>true</code> aktiviert den automatischen <code>flush</code>, <code>false</code>
	 *                   deaktiviert die Funktion
	 */
	public void setFlushOnLog( boolean flushOnLog ) {
		this.flushOnLog = flushOnLog;
	}

	/**
	 * Gibt einen Verweis auf die singuläre Instanz dieser Singleton-Klasse zurück. Falls noch keine Instanz existiert,
	 * wird eine neue erzeugt.
	 *
	 * @return Die singuläre Instanz des Loggers
	 */
	public synchronized static Log getInstance() {

		if ( Log.instance == null ) {
			Log.instance = new Log();
			return Log.instance;
		} else {
			return Log.instance;
		}
	}

	/**
	 * Gibt die im {@link StringBuffer} abgelegten Log-Einträge als String zurück.
	 *
	 * @return Die Log-Einträge im {@link #messageBuffer}
	 */
	public String getLogOutput() {

		String output = "";

		if ( messageBuffer.length() > 1 ) {

			output = messageBuffer.toString();
			messageBuffer = new StringBuffer();
		}

		return output;
	}

	/**
	 * Gibt <code>true</code> zurück, falls der Logger aktiviert ist, <code>false</code> andererseits.
	 *
	 * @return <code>true</code>, falls der Logger aktiviert ist, <code>false</code>
	 */
	public boolean isLogging() {
		return isLogging;
	}

	/**
	 * Gibt <code>true</code> zurück, falls das Formatieren der Log-Einträge aktiviert ist, <code>false</code>
	 * andererseits.
	 *
	 * @return <code>true</code>, falls das Formatieren der Log-Einträge aktiviert ist, <code>false</code> andererseits
	 */
	public boolean useFormatting() {
		return useFormatting;
	}

	/**
	 * Aktiviert oder deaktiviert das Formatieren der Log-Einträge.
	 *
	 * @param useFormatting <code>true</code>: Formatierung aktiviert; <code>false</code>: Formatierung deaktiviert
	 */
	public void setUseFormatting( Boolean useFormatting ) {
		this.useFormatting = useFormatting;
	}

	/**
	 * Sendet eine neue Nachricht angegebenen Log-Levels an den Logger. Diese Methode ist zu verwenden, falls sie keinem
	 * Modul zugerodnet werden soll/kann.
	 *
	 * @param level   Der Log-Level der Nachricht
	 * @param message Die Nachricht des Log-Eintrags
	 * @see #log(LogLevel, LogModule, String)
	 */
	public void log( LogLevel level, String message ) {
		log(level, ALL, message);
	}

	/**
	 * Sendet eine neue Nachricht angegebenen Log-Levels aus dem angegebenen Modul an den Logger.
	 *
	 * @param level   Der Log-Level der Nachricht
	 * @param module  Das Modul aus welchem die Nachricht gesendet worden ist
	 * @param message Die Nachricht des Log-Eintrags
	 */
	public void log( LogLevel level, LogModule module, String message ) {
		if ( isLogging && level.compareTo(logLevel) >= 0 && ( ( logModule == ALL ) || ( logModule == module ) ) ) {
			if ( useFormatting )
				messageBuffer.append(getTimeStamp())
						.append('\t').append(logLevelToString(level))
						.append('\t').append(logModuleToString(module))
						.append('\t').append(message).append('\n');
			else
				messageBuffer.append(level)
						.append(' ').append(module)
						.append(' ').append(message).append('\n');

			// We don't want to flush on data dumps, since they amass a good amount of messages in short time.
			// Not flushing while the dump is in progress saves a lot of performance.
			if ( flushOnLog && level != DUMP ) flush();
		}
	}

	/**
	 * Statische Methode zum loggen eines Eintrages, ohne das jeweils die Methode über {@link #getInstance()} aufgerufen
	 * werden muss.
	 *
	 * @param level   Der Log-Level der Nachricht
	 * @param module  Das Modul aus welchem die Nachricht gesendet worden ist
	 * @param message Die Nachricht des Log-Eintrags
	 */
	public static void log0( LogLevel level, LogModule module, String message ) {
		Log.getInstance().log(level, module, message);
	}

	/**
	 * Gibt die {@link String}-Repräsentation eines gegebenen {@link LogModule} zurück.
	 *
	 * @param module Das {@link LogModule} dessen {@link String}-Repräsentation ausgegeben werden soll
	 * @return {@link String}-Repräsentation des gegebenen Moduls.
	 */
	private String logModuleToString( LogModule module ) {
		switch ( module ) {
			case ALL:
			default:
				return "(GENERIC)";
			case MAIN:
				return "(MAIN)";
			case BOARD:
				return "(BOARD)";
			case IO:
				return "(IO)";
			case PLAYER:
				return "(PLAYER)";
		}
	}

	/**
	 * Gibt die {@link String}-Repräsentation eines gegebenen {@link LogLevel} zurück.
	 *
	 * @param level Das {@link LogLevel} dessen {@link String}-Repräsentation ausgegeben werden soll
	 * @return {@link String}-Repräsentation des gegebenen Levels.
	 */
	private String logLevelToString( LogLevel level ) {
		switch ( level ) {
			case NONE:
			default:
				return "[NONE]";

			case DUMP:
				return "[DUMP]";

			case DEBUG:
				return "[DEBUG]";

			case INFO:
				return "[INFO]";

			case WARNING:
				return "[WARNING]";

			case ERROR:
				return "[ERROR]";

			case CRITICAL:
				return "[CRITICAL]";
		}
	}

	/**
	 * Gibt die geloggten Nachrichten zurück, siehe {@link #getLogOutput()} wie der Output generiert wird.
	 *
	 * @return Die geloggten Nachrichten als {@link String}
	 */
	@Override
	public String toString() {
		return getLogOutput();
	}

	/**
	 * Gibt die geloggten Nachrichten mit {@link PrintStream#print(String)} auf dem gegebenen Output-{@link PrintStream}
	 * aus.
	 */
	public void flush() {
		output.print(getLogOutput());
	}

	/**
	 * Erstellt den aktuellen Timestamp im Format <blockquote>dd-MM-yyyy HH:mm:ss.SS</blockquote>. Wird nur in der
	 * formartierten Ausgabe verwendet.
	 *
	 * @return Aktueller Zeitpunkt im Format <code>dd-MM-yyyy HH:mm:ss.SS</code>
	 * @see SimpleDateFormat
	 */
	private String getTimeStamp() {
		return new SimpleDateFormat("[dd-MM-yyyy HH:mm:ss.SS]").format(new Date());
	}
}
