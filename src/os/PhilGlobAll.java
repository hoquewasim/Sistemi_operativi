package os;

/**{c}
 * filosofo con allocazione globale -
 * si utilizza lo schema a semaforo privato
 * @author M.Moro DEI UNIPD
 * @version 1.002002-05-06
 * @version 2.00 2003-10-03package Os
 * @version 2.01 2005-10-07 package os
 */

public class PhilGlobAll extends Phil
{
    private int numForks;
      // numero forchette
    private boolean forks[];
      // le forchette
    private boolean philSusp[];
      // filosofi sospesi
    private Semaphore philSem[];
      // semafori privati
    private Semaphore mutex;
      // semaforo di mutua esclusione
    private PhilAnim phan;
      // animatore

    /**[c]
     * filosofo con nome
     * @param name  nome del thread
     * @param id  indice del filosofo
     * @param mind  minima attesa
     * @param maxd  massima attesa
     * @param fk  stato forchette
     * @param ps  filosofi sospesi
     * @param psem  semafori privati
     * @param mtx  semaforo mutua esclusione
     * @param pa  animatore
     */
    public PhilGlobAll(String name, int id, int mind, int maxd, 
      boolean fk[], boolean ps[], Semaphore psem[], Semaphore mtx,
      PhilAnim pa)
    { 
        super(name, id, mind, maxd); 
        forks = fk;
        philSusp = ps;
        philSem = psem;
        mutex = mtx;
        phan = pa;
    } //[c]
        
    /**[m]
     * acquisizione forchette
     * @param idxPh  indice filosofo
     */
    public void getForks(int idxPh)
    {
        if (philSem[idxPh] == null)
            // semaforo privato non ancora creato
            philSem[idxPh] = new Semaphore();
        mutex.p();
        if (!forks[idxPh] || !forks[(idxPh+1)%forks.length])
        {
            // manca almeno una forchetta
            philSusp[idxPh] = true;
            mutex.v();
            System.out.println("^^^^^ Il filosofo "+getName()+" deve attendere");
            philSem[idxPh].p();
              // attesa su semaforo privato
            // si noti, che al risveglio, NON si puo` rientrare
            // in mutua esclusione
        }
        else
        {
            forks[idxPh]=false;
            forks[(idxPh+1)%forks.length] = false;
            mutex.v();
        }
        // qui comunque le forchette sono allocate
        System.out.println("Il filosofo "+getName()+" ha entrambe le forchette");
        phan.notifyEvent(idxPh, PhilAnim.BOTHSTATUS);
    } //[m] getForks
    
    /**[m]
     * rilascio forchette
     * @param idxPh  indice filosofo
     */
    public void putForks(int idxPh)
    {
        mutex.p();
        
        // controlla stato del precedente
        if (philSusp[(idxPh-1+forks.length)%forks.length] &&
          forks[(idxPh-1+forks.length)%forks.length])
        {
            // alloca le forchette per il precedente
            philSusp[(idxPh-1+forks.length)%forks.length] = false;
            // forks[idxPh] e` gia` false
            forks[(idxPh-1+forks.length)%forks.length] = false;
            philSem[(idxPh-1+forks.length)%forks.length].v();
        }
        else
            forks[idxPh] = true;
            
        // controlla stato del successore
        if (philSusp[(idxPh+1)%forks.length] &&
          forks[(idxPh+2)%forks.length])
        {
            // alloca le forchette per il precedente
            philSusp[(idxPh+1)%forks.length] = false;
            // forks[(idxPh+1)%forks.length] e` gia` false
            forks[(idxPh+2)%forks.length] = false;
            philSem[(idxPh+1)%forks.length].v();
        }
        else
            forks[(idxPh+1)%forks.length] = true;

        phan.notifyEvent(idxPh, PhilAnim.THINKSTATUS);
        mutex.v();
    } //[m] putForks
    
} //{c} PhilGlobAll