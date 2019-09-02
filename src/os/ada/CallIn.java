package os.ada;

import os.Semaphore;

/**{c}
 * fornisce i parametri d'ingresso 
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2010-12-12
 */

public class CallIn
{
    // queste informazioni sono utili al server
    Thread th;
      // caller thread
    int idx;
      // caller's call index (1..n)
    Port<CallIn> dest;
      // port del server
    Semaphore mtx;
      // mutex
    Semaphore wait1;
      // per la prima sincronizzazione (con eventuale timeout)
    Semaphore wait2;
      // per la seconda sincronizzazione (risposta)
    Port<CallOut> reply;
      // port per la risposta al client
    boolean isImmediate;
      // se ha rilevato il server gia' in attesa

    /**
     * parametri d'ingresso effettivi impacchettati
     */
    Object params;
      
    /**[c]
     * parametri d'ingresso
     * @param pms  parametri d'input della chiamata
     */
    public CallIn(Object pms)
    {
        // gli altri campi sono impostati in ADAThread
        params = pms;
    } //[c]
      
} //{i} CallIn
