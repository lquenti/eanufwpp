package flowerwarspp.preset;

/**
 * Diese Enumeration enthaelt die moeglichen Spielertypen. Der {@link ArgumentParser} kann mit den Methoden {@link
 * ArgumentParser#getRed()} bzw. {@link ArgumentParser#getBlue()} die Spielertypen fuer den roten bzw. blauen Spieler
 * als Wert dieser Enumeration zurueckgeben.
 *
 * @author Dominick Leppich
 */
public enum PlayerType {
    HUMAN,
    RANDOM_AI,
    SIMPLE_AI,
    ADVANCED_AI_1,
    ADVANCED_AI_2,
    ADVANCED_AI_3,
    ADVANCED_AI_4,
    ADVANCED_AI_5,
    REMOTE
}
