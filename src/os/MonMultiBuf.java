package os;

/**{c}
 * buffer multiplo come monitor
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-04-18
 * @version 2.00 2003-10-03 package Os
 * @version 2.01 2005-10-07 package os
 */
 
public class MonMultiBuf extends Monitor implements Buffer
{
    private Condition spaceAval = new Condition();
      // spazio disponibile
    private Condition dataAval = new Condition();
      // dato disponibile
    private Object data[];
      // buffer dati
    private int numEl, numData=0, head=0, tail=0;
      // numero elementi, numero dati, 
      // dove si legge, dove si scrive
      
      
    /**[c]
     * inizializza al numero di elementi indicati
     * @param n  numero elementi del buffer
     */
    public MonMultiBuf(int n)
    {
    	  numEl = n;
        data = new Object[numEl];
    } 
    
    /**[m]
     * prelievo del dato
     * @return dato prelevato
     */
    public Object read()
    {
        mEnter();
        if (numData == 0)
            // non ci sono dati
            dataAval.cWait();
        Object ret = data[head];
        data[head] = null;
        head = (++head) % numEl;
        numData--;
        System.out.println("-- "+Thread.currentThread().getName()+
          " ha letto "+ret+" nel buffer ora="+numData);
        spaceAval.cSignal();  // segnala spazio disponibile
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
        if (numData == numEl)
            // non c'e` spaxio
            spaceAval.cWait();
        data[tail] = d;
        tail = (++tail) % numEl;
        numData++;
        System.out.println("** "+Thread.currentThread().getName()+
          " ha scritto "+d+" nel buffer ora="+numData);
        dataAval.cSignal();  // segnala dato disponibile
        mExit();
    }

    /**[m}
     * dati memorizzati
     * @return numero dati memorizzati
     */
    public int size()
    { return numData; }
    
    /**[m}
     * spazio allocato
     * @return numero elemnti allocati
     */
    public int dimen()
    { return numEl; }
    
} //{c} MonMultiBuf

