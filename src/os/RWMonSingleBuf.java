package os;

/**{c}
 * buffer singolo come Monitor 
 * accesso per lettori/scrittori -
 * gli scrittori hanno priorita` sui lettori
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-04-18
 * @version 2.00 2003-10-03 package Os
 * @version 2.01 2005-10-07 package os
 * @version 2.02 2014-05-09 aggiunta gestione buffer vuoto
 */

 
public class RWMonSingleBuf extends Monitor implements Buffer
{
    private Condition readers = new Condition();
      // attesa dei lettori
    private Condition writers = new Condition();
      // attesa degli scrittori
    private Object data = "Buffer vuoto";
      // dato
    private int waitingReaders=0, waitingWriters=0,
      readingReaders=0;
      // numero lettori in attesa, scrittori in attesa,
      // lettori in lettura
    private boolean writingWriter = false;
      // scrittore in scrittura
    // 2.02 buffer vuoto o svuotato
    private boolean empty = true;
      
    /**[m]
     * metodo di sincronizzazione per ingresso lettore
     */
    private void startRead()
    {
        mEnter();
        if (writingWriter || waitingWriters > 0 || empty)
        {
            // il lettore non puo` entrare
            waitingReaders++;
            readers.cWait();
            waitingReaders--;
        }
        readingReaders++;
        readers.cSignal();
        mExit();
    }
      
    /**[m]
     * metodo di sincronizzazione per uscita lettore
     */
    private void stopRead()
    {
        mEnter();
        if (--readingReaders == 0)
            // non ci sono lettori in lettura
            writers.cSignal();
        mExit();
    }
    
    /**[m]
     * prelievo del dato
     * @return dato prelevato
     */
    public Object read()
    {
        startRead();
        Reader redr = (Reader)Thread.currentThread();
        Util.rsleep(redr.minDelay()/8, redr.maxDelay()/8);
        Object ret = data;
        System.out.println("----- "+Thread.currentThread().getName()+
          " ha letto "+ret);
        stopRead();
        return ret;
    }
    
    /**[m]
     * metodo di sincronizzazione per ingresso scrittore
     */
    private void startWrite()
    {
        mEnter();
        if (readingReaders > 0 || writingWriter)
        {
            // lo scrittore non puo` entrare
            waitingWriters++;
            writers.cWait();
            waitingWriters--;
        }
        writingWriter = true;
        mExit();
    }
      
    /**[m]
     * metodo di sincronizzazione per uscita scrittore
     */
    private void stopWrite()
    {
        mEnter();
        writingWriter = false;
        if (waitingWriters > 0)
            // ci sono altri scrittori in attesa:
            // hanno priorita` sui lettori
            writers.cSignal();
        else 
            // forse ci sono lettori in attesa
            readers.cSignal();
        mExit();
    }
    
    /**[m]
     * inserimento del dato
     * @param d  dato da memorizzare
     */
    public void write(Object d)
    {
        startWrite();
        Writer writ = (Writer)Thread.currentThread();
        Util.rsleep(writ.minDelay()/8, writ.maxDelay()/8);
        data = d;
        System.out.println("***** "+Thread.currentThread().getName()+
          " ha scritto "+d);
        empty = false;
        stopWrite();
    }

    /**[m]
     * cancellazione del dato
     */
    public void cancel()
    {
        startWrite();
        System.out.println("***** "+Thread.currentThread().getName()+
          " ha cancellato "+data);
        data = "Buffer svuotato";
        empty = true;
        stopWrite();
    }

    /**[m}
     * dati memorizzati
     * @return numero dati memorizzati
     */
    public int size()
    { return 1; }
    
    /**[m}
     * spazio allocato
     * @return 1 (uno)
     */
    public int dimen()
    { return 1; }
    
} //{c} RWMonSingleBuf

