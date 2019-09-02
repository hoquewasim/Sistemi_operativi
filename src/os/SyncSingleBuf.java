package os;

/**{c}
 * buffer singolo con 
 * 2 semafori binari di protezione 
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-04-17
 * @version 2.00 2003-10-02 package Os
 * @version 2.01 2005-10-07 package os
 */
 
public class SyncSingleBuf implements Buffer
{
    private Semaphore spaceAval = new Semaphore(true);
      // spazio disponibile
    private Semaphore dataAval = new Semaphore(false);
      // dato disponibile
    private Object data;
      // dato
      
    /**[m]
     * prelievo del dato
     * @return dato prelevato
     */
    public Object read()
    {
    	dataAval.p();  // il dato e` disponibile?
    	Object ret = data;
    	data = null;
        System.out.println("-- "+Thread.currentThread().getName()+
          " ha letto "+ret);
        spaceAval.v();  // segnala spazio disponibile
    	return ret;
    }

    /**[m]
     * deposito del dato
     * @param d  dato da memorizzare
     */
    public void write(Object d)
    {
    	spaceAval.p();  // lo spazio e` disponibile?
    	data = d;
        System.out.println("** "+Thread.currentThread().getName()+
          " ha scritto "+d);
        dataAval.v();  // segnala dato disponibile
    }

    /**[m}
     * dati memorizzati
     * @return numero dati memorizzati
     */
    public int size()
    { return dataAval.value(); }
    
    /**[m}
     * spazio allocato
     * @return 1 (uno)
     */
    public int dimen()
    { return 1; }
    
} //{c} SyncSingleBuf

