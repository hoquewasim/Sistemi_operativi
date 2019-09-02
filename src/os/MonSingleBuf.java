package os;

/**{c}
 * buffer singolo come monitor
 * @author M.Moro DEI UNIPD
 * @version 1.0 2002-04-18
 * @version 2.00 2003-10-03 package Os
 * @version 2.01 2005-10-07 package
 */
 
public class MonSingleBuf extends Monitor implements Buffer
{
    private Condition spaceAval = new Condition();
      // spazio disponibile
    private Condition dataAval = new Condition();
      // dato disponibile
    private Object data;
      // dato
    private boolean written = false;
      // true se con dati
      
    /**[m]
     * prelievo del dato
     * @return dato prelevato
     */
    public Object read()
    {
        mEnter();
        if (! written)
            dataAval.cWait();
        // ora il dato e` disponibile
    	  Object ret = data;
    	  data = null;
    	  written = false;
        System.out.println("-- "+Thread.currentThread().getName()+
          " ha letto "+ret);
        spaceAval.cSignal();
    	  mExit();
    	  return ret;
    }

    /**[m]
     * deposito del dato
     * @param d  dato da memorizzare
     */
    public void write(Object d)
    {
        mEnter();
        if (written)
            spaceAval.cWait();
        // ora lo spazio e` disponibile
    	  data = d;
        written = true;
        System.out.println("** "+Thread.currentThread().getName()+
          " ha scritto "+d);
        dataAval.cSignal();
        mExit();
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
    
} //{c} MonSingleBuf

