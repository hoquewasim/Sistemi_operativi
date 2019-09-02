package os;

/**{c}
 * struttura base del filosofo 
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-05-03
 * @version 2.00 2003-10-02 package Os
 * @version 2.01 2005-10-07 package os
 */

public abstract class Phil 
  extends Thread 
{
    /** range di tempo d'attesa
     */
    protected int minDelay, maxDelay;
    /** indice del filosofo
     */
    protected int idx;
        
    /**[c]
     * filosofo con nome
     * @param name  nome del thread
     * @param id  indice del filosofo
     * @param mind  minima attesa
     * @param maxd  massima attesa
     */
    public Phil(String name, int id, int mind, int maxd)
    {
        super(name);
        idx = id;
        minDelay = mind;
        maxDelay = maxd;
    } //[c]
        
    /**[m]
     * esecutore filosofo
     */
    public void run() 
    {
        while (true) 
        { 
            System.out.println("Il filosofo "+getName()+" sta pensando");
            Util.rsleep(minDelay, maxDelay);
            System.out.println("Il filosofo "+getName()+" vuole le forchette");
            getForks(idx);
            System.out.println("*** Il filosofo "+getName()+" ora mangia");
            Util.rsleep(minDelay, maxDelay);
            System.out.println("--- Il filosofo "+getName()+" restituisce le forchette");
            putForks(idx);
        } // while
    } //[m] run
   
    // implementazione diversificata
    /**[m][a]
     * acquisizione forchette
     * @param philIdx  indice filosofo
     */
    public abstract void getForks(int philIdx);

    /**[m][a]
     * rilascio forchette
     * @param philIdx  indice filosofo
     */
    public abstract void putForks(int philIdx);
    
} //{c}Phil