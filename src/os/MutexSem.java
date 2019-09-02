package os;

/**{c}
 * semaforo di controllo risorsa -
 * controlla una mutua escusione e mantiene
 * un contatore che tiene memoria del numero
 * di allocazioni ripetute dallo stesso thread
 * @author M.Moro DEI UNIPD
 * @version 1.00 2003-10-02
 * @version 2.01 2005-10-07 package os
 * @version 2.02 2014-05-06 cambio owner (setOwner)
 */
 
public class MutexSem extends Semaphore
{
    private int lockCount = 0;
      // conta il numero di p() eseguite 
      // dallo stesso thread
    private Thread mOwner = null;
      // thread che possiede la risorsa
      
    /**[c]
     * costruttore base 
     */
    public MutexSem() 
    { 
        super(true);
          // semaforo binario, init verde
    }
    
    /**[m][y]
     * operazione p (wait) con conteggio -
     * viene tenuta traccia del numero di p()
     * eseguite dal medesimo thread che ha ottenuto
     * la risorsa (cioe` ha fatto una prima p()
     * che non lo ha sospeso o da cui e` stato
     * risvegliato)
     */
    public synchronized void p() 
    {
        if (lockCount > 0 && mOwner == Thread.currentThread())
        {
            // p() rieseguita dallo stesso thread
            lockCount++;
            return;
        }
        super.p();
          // al ritorno da questa chiamata
          // il thread ha accesso alla risorsa
        if (lockCount != 0 || mOwner != null)
            // non dovrebbe mai verificarsi
            System.out.println("???????  lc != 0 ?????? lc="+this);
         // lockCount == 0
         lockCount++;
         mOwner = Thread.currentThread();
    } //[m] p

    /**[m][y]
     * operazione p (wait) non sospensiva con conteggio -
     * se puo` acquisire o riacquisire il mutex, 
     * lo fa, altrimenti
     * si limita a tornare false
     * @return true se acquisito il mutex
     *         false altrimenti
     */
    public synchronized boolean testp() 
    {
        if (lockCount > 0 && mOwner != Thread.currentThread())
            return false;
        // puo` acquisire o riacquisire il mutex
        p();
        return true;
    } //[m] testp

    /**[m][y]
     * operazione v (signal)
     */
    public synchronized void v() 
    {
        if(mOwner != Thread.currentThread())
            throw new InvalidThreadException(
              "Not owner of MutexSem ["+
              Thread.currentThread().getName()+"]");
        if (--lockCount == 0)
        {
            mOwner = null;
            super.v();
        }
    } //[m][y] v

    /**[m]
     * lockCount
     * @return numero riacquisizioni
     */
    public int getVal()
    {  return lockCount; }
    
    /**[m]
     * thread owner
     * @return owner
     */
    public Thread getOwner()
    {  return mOwner; }
    
    /**[m]
     * set thread owner
     * @return owner precedente
     */
    public Thread setOwner()
    {  
        Thread old = mOwner;
        mOwner = Thread.currentThread();
        return old; 
    }
    
    /**[m]
     * set thread owner
     * @param newt  nuovo owner
     * @return owner precedente
     */
    public Thread setOwner(Thread newt)
    {
        Thread old = mOwner;
        mOwner = newt;
        return old; 
    }
    
    /**[m]
     * Conversione a stringa
     * @return la stringa
     */
    public String toString()
    {
        return "Mutex "+super.toString()+
        " lockCount="+lockCount+" mutex owner="+
        ((mOwner==null)? "NOOWNER" : mOwner.getName());
    }
    
} //{c} MutexSem
