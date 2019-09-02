package Airport;
import os.*;

/** {c}
 * AereoCheDecolla.java
 * Problema Aeroporto -
 * Un aereo che decolla ha bisogno di una sola corsia
 * @version 1.00 2001-03-28
 * @version 2.00 2003-11-21
 * @author S.Cecchin Ist. Negrelli Feltre
 * @author M.Moro DEI UNIPD
 */

public class AereoCheDecolla extends Thread {
    private int io ;
    private TorreDiControllo tc;

    /**[c]
     * @param io  indice del volo
     * @param tc  rif. torre di controllo
     */
    public AereoCheDecolla(int io , TorreDiControllo tc) {
        this.io=io;
        this.tc=tc;
    }

    public void run() {
        Util.rsleep(1000, 3000);		// si presenta all'ingresso
        tc.richAccessoPista(io);
        Util.rsleep(1000, 3000);		// rulla in posizione
        tc.richAutorizDecollo(io);
        Util.rsleep(1000, 3000);		// in accelerazione
        tc.inVolo(io);					// avvisa la torre di controllo
    }
} // class AereoCheDecolla
