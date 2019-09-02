package Airport;
import os.*;

import os.*;

/** {c}
 * AeroportoMdH.java
 * Problema Aeroporto -
 * Avvio torre di controllo Monitor Hoare e aerei
 * @version 1.00 2003-11-22
 * @author G.Clemente DEI UNIPD
 * @author M.Moro DEI UNIPD
 */

public class AeroportoMdh {
    public static void main (String [] args) {
        System.out.println("---- AEROPORTO CON MONITOR HOARE ----");
        TorreDiControlloMdh tc = new TorreDiControlloMdh();
        for( int i=1 ; i<=20 ; i++) {
            new AereoCheAtterra(i,tc).start();
            Util.rsleep(250, 8000);   // sospende il thread corrente
            new AereoCheDecolla(20+i,tc).start();
            Util.rsleep(250, 8000); // sospende il thread corrente
        }
    }

} //{c} AeroportoMdh
