package os;

/**{c}
 * filosofo con allocazione gerarchica -
 * rispetta l'ordine d'indice delle forchette
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-05-06
 * @version 2.00 2003-10-03 package Os
 * @version 2.01 2005-10-07 package os, attesa prima della prima forchetta
 */

public class PhilGerAll extends Phil
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
    public PhilGerAll(String name, int id, int mind, int maxd, 
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
        int one = idxPh;
        int two = (idxPh+1)%forks.length;
          // le due forchette
        forks[Math.min(one, two)].p();
          // prima la 'minore' delle due
        System.out.println("^^^ Il filosofo "+getName()+" ha preso la prima ("+
          Math.min(one, two)+") e passa mano");
        phan.notifyEvent(idxPh, 
          Math.min(one, two)==idxPh ? PhilAnim.RIGHTSTATUS : PhilAnim.LEFTSTATUS);
        Util.sleep(2*maxDelay); 
        System.out.println("^^^^^ Il filosofo "+getName()+" tenta di prendere la seconda");
        forks[Math.max(one, two)].p();
          // poi la 'maggiore' delle due
          // acquisizione separata gerarchica
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
    
} //{c} PhilGerAll