package flowerwarspp.preset;

import java.rmi.*;

/**
 * <h1>Spieler-Schnittstelle</h1>
 * <p>
 * Diese Schnittstelle muss von allen Spielern von FlowerWarsPP implementiert werden. Auf diese Weise ist es moeglich
 * die Spielablauflogik komplett unabhaengig von den tatsaechlichen Spielern implementieren zu koennen. Dabei ist es
 * sogar moeglich, dass einer der Spieler ein Netzwerkspieler ist.
 * </p>
 * <p>
 * Ein Spieler hat beim Erzeugen keine Informationen darueber auf welchem Spielbrett (genauer welche Groesse) er spielen
 * wird und welche Farbe er hat. Diese Informationen muss er vor Spielbeginn durch einen Aufruf der Methode {@link
 * #init(int, PlayerColor)} bekommen. Jeder Spieler muss ein eigenes Spielbrett haben, damit er die Spielsituation
 * nachvollziehen kann und in der Lage ist Konflikte in der Spiellogik mit dem Hauptprogramm zu erkennen.
 * </p>
 * <h2>Ablauf</h2>
 * <ul>
 * <li>
 * Zuallererst muss die Methode {@link #init(int, PlayerColor)} aufgerufen werden. Ohne einen Aufruf dieser Methode,
 * kann der Spieler nicht spielen. Wird dennoch eine andere Methode zuerst aufgerufen, muss darauf mit einer {@link
 * Exception} reagiert werden. Je nach Spielerfarbe ist die naechste Methode die ausgefuehrt ist eine andere. Der rote
 * Spieler muss als naechstes {@link #request()} aufrufen, der blaue {@link #update(Move, Status)}.
 * </li>
 * <li>
 * Die Reihenfolge der Methoden ist immer {@code request -> confirm -> update -> request -> ...}. Der Ablauf startet
 * fuer den blauen Spieler entsprechend bei der Methode {@code update}.
 * </li>
 * <li>
 * Die Methode {@link #init(int, PlayerColor)} kann zu einem beliebigen Zeitpunkt waehrend eines Spiels erneut
 * aufgerufen werden. Das aktuelle Spiel ist damit beendet und der Spieler muss sich auf ein neues Spiel, mit neuem
 * Brett und einer potentiell anderen Farbe vorbereiten.
 * </li>
 * </ul>
 *
 * @author Dominick Leppich
 */
public interface Player extends Remote {
    /**
     * Diese Funktion fordert vom Spieler einen Zug an. Kann der Spieler keinen Zug liefern (aus welchen Gruenden auch
     * immer) oder die Reihenfolge der Spielermethoden ist nicht korrekt, muss eine {@link Exception} geworfen werden.
     *
     * @return Spielzug als {@link Move}-Objekt
     *
     * @throws Exception
     *         falls der Spieler keinen Zug zurueckliefern kann oder {@code request} nicht die Funktion ist, die aktuell
     *         aufgerufen werden soll
     * @throws RemoteException
     *         falls bei der Netzwerkkommunikation etwas schief gelaufen ist
     */
    Move request() throws Exception, RemoteException;

    /**
     * Diese Funktion bestaetigt dem Spieler den zuletzt mit {@link #request()} angeforderten Zug. Im Parameter {@code
     * status} wird der Status dieses Spielzuges vom Hauptprogramm mitgegeben. Mit dieser Information kann und muss der
     * Spieler pruefen, ob es zu Inkonsistenzen der Spiellogik gekommen ist. Stimmt der Status nicht mit dem eigenen
     * Status ueberein oder ist die Reihenfolge der Spielermethoden nicht korrekt, muss eine {@link Exception} geworfen
     * werden.
     *
     * @param status
     *         Status des Spielbretts des Hauptprogramms nach Ausfuehren des zuletzt mit {@link #request()} geholten
     *         Zuges
     *
     * @throws Exception
     *         falls die Stati der Spielbretter nicht uebereinstimmen oder {@code confirm} nicht die Funktion ist, die
     *         aktuell aufgerufen werden soll
     * @throws RemoteException
     *         falls bei der Netzwerkkommunikation etwas schief gelaufen ist
     */
    void confirm(Status status) throws Exception, RemoteException;

    /**
     * Diese Funktion uebermittelt dem Spieler den Zug und Status des Zuges des Gegenspielers. Im Parameter {@code
     * opponentMove} wird der Zug des Gegenspielers, im Parameter {@code status} der Status dieses Spielzuges vom
     * Hauptprogramm mitgegeben. Mit dieser Information kann und muss der Spieler pruefen, ob es zu Inkonsistenzen der
     * Spiellogik gekommen ist. Stimmt der Status nicht mit dem eigenen Status ueberein oder ist die Reihenfolge der
     * Spielermethoden nicht korrekt, muss eine {@link Exception} geworfen werden.
     *
     * @param opponentMove
     *         Zug des Gegenspielers
     * @param status
     *         Status des Spielbretts des Hauptprogramms nach Ausfuehren des Zuges des Gegenspielers
     *
     * @throws Exception
     *         falls die Stati der Spielbretter nicht uebereinstimmen oder {@code update} nicht die Funktion ist, die
     *         aktuell aufgerufen werden soll
     * @throws RemoteException
     *         falls bei der Netzwerkkommunikation etwas schief gelaufen ist
     */
    void update(Move opponentMove, Status status) throws Exception, RemoteException;

    /**
     * Initialisiere den Spieler und teile ihm die Groesse des Spielbretts und seine Spielerfarbe mit. Diese Funktion
     * muss bei einem neuen Spieler die erste ausgefuehrte Funktion sein und kann im Verlauf zu einem beliebigen
     * Zeitpunkt wieder aufgerufen werden, um ein neues Spiel zu starten.
     *
     * @param boardSize
     *         Spielbrettgroesse
     * @param color
     *         Spielerfarbe
     *
     * @throws Exception
     *         falls es nicht moeglich ist den Spieler zu initialisieren
     * @throws RemoteException
     *         falls bei der Netzwerkkommunikation etwas schief gelaufen ist
     */
    void init(int boardSize, PlayerColor color) throws Exception, RemoteException;
}
