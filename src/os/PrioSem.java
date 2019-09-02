package os;

/**{c}
 * semaforo numerico con priorita` dei thread
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-04-22
 * @version 2.00 2003-10-02 package Os
 * @version 2.01 2005-10-07 package os
 */
 
public class PrioSem extends Semaphore
{
    /**[c]
     * costruttore base 
     * @param v  valore iniziale del semaforo
     * @param mv  valore massimo del semaforo
     */
    public PrioSem(int v, int mv) 
    { 
        super(v, mv);
    }

    /**[c]
     * costruttore con solo valore iniziale
     * @param v  valore iniziale del semaforo
     */
    public PrioSem(int v) 
    { this(v, v); }

    /**[c]
     * costruttore per semaforo binario
     * @param b  valore iniziale del semaforo binario
     */
    public PrioSem(boolean b) 
    { this(b ? 1 : 0, 1); }

    /**[m][y]
     * operazione p (wait)
     * @param prio  priorità d'attesa
     */
    public synchronized void p(int prio) 
    {
        if(value==0)
        {
            // attende nella coda a priorita` 
            // anche se e` stato notificato ma non ancora risvegliato
            // un thread sospeso meno prioritario di questo
            waitNum++;
            int pos=0;                                         
            while( pos<waiting.size() &&
             ((WaitingThread)waiting.elementAt(pos)).prio<=prio ) 
                pos++;
            Thread curTh;
            waiting.insertElementAt(
              new WaitingThread(curTh=Thread.currentThread(), false, 1, prio), pos);
            while(true)
            {
                try { wait(); } 
                catch( InterruptedException e ){};
                // se si deve risvegliare questo thread
                // non e` in generale il primo della coda
                for (int i=0; i<waiting.size(); i++)
                {
                    WaitingThread wt = 
                      (WaitingThread)waiting.elementAt(i);
                    if (wt.th == curTh)
                        // trovato
                        if (wt.wakenUp)
                        {
                            // e` proprio da risvegliare
                            waiting.remove(i);
// lo fa v()                            waitNum--;
                            // il valore del semaforo e`
                            // gia` a posto
                            return;
                        }
                        else 
                            // non era da risvegliare
                            break; // for
                    else ;
                } // for

            } // while(true)
        } // if (value==0)
        else
            value--;
    } //[m] p

    /**[m]
     * Conversione a stringa
     * @return la stringa
     */
    public String toString()
    {
        return "Priority "+super.toString();
    }
    
} //{c} PrioSem

