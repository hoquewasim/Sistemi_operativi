    package os;

// taglia/incolla da SyncMultiBuf
// con 2 semafori di mutua esclusione

/**{c}
 * buffer multiplo con 
 * 2 semafori numerici e un mutex di protezione 
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-04-17
 * @version 2.00 2003-10-02 package Os
 * @version 2.01 2005-10-07 package os
 */
 
public class SyncMultiBuf2 implements Buffer
{
    private Semaphore spaceAval;
      // locazioni disponibili
    private Semaphore dataAval;
      // dati disponibili
    private Semaphore mutexP = new Semaphore(true);
      // mutua esclusione PRODUTTORI
    private Semaphore mutexC = new Semaphore(true);
      // mutua esclusione CONSUMATORI
    private Object data[];
      // buffer dati
    private int numEl, head=0, tail=0;
      // numero elementi, dove si legge, dove si scrive
      
      
    /**[c]
     * inizializza al numero di elementi indicati
     * @param n  numero elementi del buffer
     */
    public SyncMultiBuf2(int n)
    {
    	numEl = n;
    	spaceAval = new Semaphore(numEl);
        dataAval = new Semaphore(0, numEl);
        data = new Object[numEl];
    } 
    
    /**[m]
     * prelievo del dato
     * @param timeout  scadenza d'attesa
     * @return dato prelevato se in tempo
     *         oggetto EXPIREDOBJ se timeout
     */
    public Object read(long timeout)
    {
        if (dataAval.p(timeout) == Timeout.EXPIRED)
            return Timeout.EXPIREDOBJ;
        // un dato e` disponibile
        mutexC.p();  // mutex
        Object ret = data[head];
        data[head] = null;
        head = (++head) % numEl;
        System.out.println("-- "+Thread.currentThread().getName()+
          " ha letto "+ret+" nel buffer ora="+dataAval.value());
        mutexC.v();
        spaceAval.v();  // segnala spazio disponibile
        return ret;
    }

    /**[m]
     * prelievo del dato
     */
    public Object read()
    {  return read(Timeout.NOTIMEOUT); }

    /**[m]
     * deposito del dato
     * @param d  dato da memorizzare
     * @param timeout  scadenza d'attesa
     * @return dato inserito se in tempo
     *         oggetto EXPIREDOBJ se timeout
     */
    public Object write(Object d, long timeout)
    {
        if (spaceAval.p(timeout) == Timeout.EXPIRED)
            return Timeout.EXPIREDOBJ;
        // spazio disponibile
        mutexP.p();  // mutex
        data[tail] = d;
        tail = (++tail) % numEl;
        System.out.println("** "+Thread.currentThread().getName()+
          " ha scritto "+d+" nel buffer ora="+(dataAval.value()+1));
        mutexP.v();
        dataAval.v();  // segnala un dato disponibile
        return d;
    }

    /**[m]
     * deposito del dato
     * @param d  dato da memorizzare
     */
    public void write(Object d)
    {  write(d, Timeout.NOTIMEOUT); }

    /**[m}
     * dati memorizzati
     * @return numero dati memorizzati
     */
    public int size()
    { return dataAval.value(); }
    
    /**[m}
     * spazio allocato
     * @return numero elementi allocati
     */
    public int dimen()
    { return numEl; }
    
} //{c} SyncMultiBuf2

