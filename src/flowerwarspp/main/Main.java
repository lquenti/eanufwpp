package flowerwarspp.main;

/**
 * Hauptklasse des Spiels. Dieses Klasse muss mit den notwendigen Kommandozeilenparametern aufgerufen werden, um das
 * Spiel zu starten.
 */
public class Main {

	/**
	 * Dieser Error-Code wird vom Programm zurück gegeben, falls die übergebenen Argumente nicht valide sind.
	 */
	private static final int ERRORCODE_INVALID_ARGS = 1;

	/**
	 * Gibt Informationen zur Verwendendung des Programms auf der Standardausgabe aus und beendet das Programm mit dem
	 * Exit-Code {@link #ERRORCODE_INVALID_ARGS}.
	 */
	public static void quitWithUsage() {
		System.out.println("Verwendung:");
		System.out.println("flowerwarspp.main.Main -size <Spielfeldgröße> -red <Spielertyp> -blue <Spielertyp> "
				+ "[-delay <Verzögerung>] [-load <Spielstandname>] [-replay <Verzögerung>] [-games <Anzahl Spiele>] " +
				"[--debug] [--text] [--help]");
		System.out.println();
		System.out.println("Spielfeldgröße: Zahl zwischen 3 und 30");
		System.out.println("Spielertyp:     \"human\", \"remote\", \"random\", \"simple\", \"adv1\", oder \"adv2\"");
		System.out.println("Verzögerung:    Zeit zwischen Zügen in Millisekunden.");
		System.out.println("Spielstandname: Name des zu ladenden Spielstands, ohne Datei-Endung.");
		System.out.println("Anzahl Spiele:  Anzahl der Spiele, welche nacheinander ausgeführt werden sollen.");
		System.out.println("Debug:          Zeigt Debug-Information im Log an. Optionale Flag (hat keine " +
				"Argumente)");
		System.out.println("Text:           Die Texteingabe wird verwendet. Optionale Flag (hat keine Argumente)");
		System.out.println("Hilfe:          Zeigt diese Hilfe an. Das Programm wird dann beendet. Optionale Flag " +
				"(hat keine Argumente)");
		System.exit(ERRORCODE_INVALID_ARGS);
	}

	public static void main( String[] args ) {
		new Game(new GameParameters(args));
	}
}
