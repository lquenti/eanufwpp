package flowerwarspp.preset;

import java.io.*;

/**
 * Diese Enumeration enthaelt die moeglichen Spielstati.
 *
 * @author Dominick Leppich
 */
public enum Status implements Serializable {
    Ok,
    RedWin,
    BlueWin,
    Draw,
    Illegal
}
