package os;

/**{c}
 * filosofo con acquisizione circolare
 * che provoca deadlock
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-05-06
 * @version 2.00 2003-10-02 package Os
 * @version 2.01 2005-10-07 package os
 */

public class PhilDeadlock extends Phil
{
    private Semaphore forks[];
      // le forchette
    private PhilAnim phan;
      // animatore

    /**[c]
     * filosofo con nome
     * @param name  nome del thread
     * @param id  indice del filosofo
     * @param mind  minima attesa
     * @param maxd  massima attesa
     * @param fk  set forchette
     * @param pa  animatore
     */
    public PhilDeadlock(String name, int id, int mind, int maxd, 
      Semaphore fk[], PhilAnim pa)
    { 
        super(name, id, mind, maxd); 
        forks = fk;
        phan = pa;
    } //[c]
        
    /**[m]
     * acquisizione forchette
     * @param idxPh  indice filosofo
     */
    public void getForks(int idxPh)
    {
        forks[idxPh].p();
        System.out.println("^^^ Il filosofo "+getName()+" ha preso la prima e passa mano");
        phan.notifyEvent(idxPh, PhilAnim.RIGHTSTATUS);
        Util.sleep(2*maxDelay); 
        System.out.println("^^^^^ Il filosofo "+getName()+" tenta di prendere la seconda");
        forks[(idxPh+1)%forks.length].p();
          // acquisizione separata circolare
        phan.notifyEvent(idxPh, PhilAnim.BOTHSTATUS);
    } //[m] getForks
    
    /**[m]
     * rilascio forchette
     * @param idxPh  indice filosofo
     */
    public void putForks(int idxPh)
    {
        forks[idxPh].v();
        forks[(idxPh+1)%forks.length].v();
        phan.notifyEvent(idxPh, PhilAnim.THINKSTATUS);
    } //[m] putForks
    
} //{c} PhilDeadlock