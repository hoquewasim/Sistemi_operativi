package os;

/**{c}
 * semaforo di conteggio -
 * è visto come specializzazione di Semaphore,
 * eliminando il vincolo sul valore massimo
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-05-04
 * @version 2.00 2003-10-02 package Os
 * @version 2.01 2004-11-08 p(qty, timeout)
 * @version 2.02 2005-10-07 package os
 */
public class CountSem extends Semaphore
{
    /**[c]
     * costruttore base 
     * @param v  valore iniziale del semaforo
     * @param fifo  true se coda FIFO, altrimenti LIFO
     */
    public CountSem(int v, boolean fifo) 
    { 
        super(v, v==0 ? 1 : v, fifo);
          // massimo irrilevante, controllo eleminato in v() 
    }
    
    /**[c]
     * costruttore base FIFO
     * @param v  valore iniziale del semaforo
     */
    public CountSem(int v) 
    { 
        this(v, true); 
    }
    
    // p(), p(long timeout), value() e queue()
    // sono esattamente quelle ereditate
    
    /**[m][y]
     * operazione p (wait) con quantita` -
     * attende finche' non e` disponibile 
     * la quantita` richiesta
     * @param qty  quantita` di decremento
     * 
     */
    public synchronized void p(int qty) 
    {
        if(value<qty)
        {
            // deve attendere nella coda
            waitNum++;
            Thread curTh = Thread.currentThread();
            if (isFifo)
                waiting.addElement
             ( new WaitingThread(curTh, false, qty) );
            else
                waiting.add
             (0, new WaitingThread(curTh, false, qty) );
            while(true)
            {
                try{ wait(); }
                catch( InterruptedException e ){}
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
            } // while
        } // if (value < qty)
        else
            value -= qty;
    } //[m] p

    /**[m][y]
     * operazione p (wait) con quantita` e timeout -
     * attende finche' non e` disponibile 
     * la quantita` richiesta o scade il timeout
     * @param qty  quantita` di decremento
     * @param timeout  massimo tempo di attesa in ms
     *                 NOTIMEOUT senza timeout
     *                 IMMEDIATE se sincronizzazione immediata richiesta
     * @return tempo rimasto rispetto al timeout -
     *         timeout se non sospensivo o INTIME -
     *         INTIME&lt;ret&le;timeout se v() arriva in tempo
     *         EXPIRED se spirato
     */
    public synchronized long p(int qty, long timeout) 
    {

        if (timeout == Timeout.NOTIMEOUT)
            // ricondotta a normale p(int)
        {
            p(qty);
            return Timeout.INTIME;
        }
        if(value<qty)
        {
            if (timeout == Timeout.IMMEDIATE)
                // sincronizzazione immediata non possibile
                return Timeout.EXPIRED;
            // deve attendere nella coda
            long exp = System.currentTimeMillis()+timeout;
              // istante di scadenza
            waitNum++;
            Thread curTh = Thread.currentThread();
            if (isFifo)
                waiting.addElement
             ( new WaitingThread(curTh, false, qty));
            else
                waiting.add
             (0, new WaitingThread(curTh, false, qty));
            long diffTime = timeout;  
              // tempo effettivo di attesa
            while(true)
            {
                if (diffTime > 0)
                    try{ wait(diffTime); }
                    catch( InterruptedException e ){}
                diffTime = exp-System.currentTimeMillis();

                // se si deve risvegliare questo thread per signal,
                // di norma e` il primo ma nel caso
                // di coda LIFO può 'intrufolarsi'
                // in testa un nuovo thread che si sospende,
                // quindi bisogna cercarlo in un ciclo
                for (int i=0; i<waiting.size(); i++)
                {
                    WaitingThread wt = 
                      (WaitingThread)waiting.elementAt(i);
                    if (wt.th == curTh)
                    {
                        // trovato
                        if (wt.wakenUp)
                        {
                            // risveglio per signal
                            waiting.remove(i);
// lo fa v()                            waitNum--;
                            // il valore del semaforo e`
                            // gia` a posto
                            return diffTime > 0L ? diffTime : 
                              Timeout.INTIME;
                             // comunque >0
                        }
                        else if (diffTime <= 0)
                        {
                            // risveglio per timeout
                            waiting.remove(i);
                            waitNum--;
                            // il valore del semaforo e`
                            // gia` a posto
                            return Timeout.EXPIRED;
                              // timeout spirato
                        }
                        else
                            // non era da risvegliare
                            break; // for
                    }
                    else ;
                } // for
            } // while
        } // if (value<qty)
        else
            value-=qty;
        return timeout;
          // p non sospensivo
    } //[m] p(int qty, long timeout)

    /**[m][y]
     * operazione v (signal)
     * @param qty  quantita` di incremento
     */
    public synchronized void v(int qty) 
    {
        value+=qty;
        if (waitNum>0)
        {              
            while(true)
            {  
                // cerca tra i sospesi il primo con il massimo
                // della quantita` richiesta <= value
                int maxReq = 0;
                int found = -1;
                for (int i=0; i<waiting.size(); i++)
                {
                    WaitingThread wt = 
                      (WaitingThread)waiting.elementAt(i);
                    if (!wt.wakenUp && wt.qty <= value && wt.qty > maxReq)
                    {
                        found = i;
                        maxReq = wt.qty;
                    }
                } // for
                if (found == -1)
                    // non trovato un altro risvegliabile
                    return;
                // found e` l'indice del thread da risvegliare
                value = value-maxReq;
                ((WaitingThread)waiting.elementAt(found)).wakenUp=true;         
                waitNum--;
                notifyAll();
            } // while
        } // if
    } //[m] v

    /**[m][y]
     * operazione v (signal) -
     * elimina il controllo sul massimo
     */
    public synchronized void v() 
    {
        v(1);
    } //[m] v

    /**[m]
     * Conversione a stringa
     * @return la stringa
     */
    public String toString()
    {
        return "Count "+super.toString();
    }
    
} //{c} CountSem
