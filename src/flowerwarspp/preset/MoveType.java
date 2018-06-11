package flowerwarspp.preset;

import java.io.*;

/**
 * Diese Enumeration enthaelt alle moeglichen Spielzugtypen.
 *
 * @author Dominick Leppich
 */
public enum MoveType implements Serializable {
    Flower,
    Ditch,
    Surrender,
    End
}
