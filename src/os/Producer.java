package os;

/**{c}
 * produttore 
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-04-17
 * @version 2.00 2003-10-02 package Os
 * @version 2.01 2005-10-07 package os
 */
 
public class Producer extends Thread
{
    private Buffer buf;
      // il buffer su cui scrivere
    private int minDelay, maxDelay;
      // range di tempo d'attesa
    private static final int MINDELAY=1000, MAXDELAY=3000;
      // valori standard dell'attesa
      
    /**[c]
     * thread per il test produttore/consumatore
     * @param name  nome del thread
     * @param b  buffer condiviso
     * @param mind  minima attesa
     * @param maxd  massima attesa
     */
    public Producer(String name, Buffer b, int mind, int maxd)
    {
        super(name);
        buf = b;
        minDelay=mind;
        maxDelay=maxd;
    } 
    
    /**[c]
     * see Producer(String name, Buffer b, int mind, int maxd)
     */
    public Producer(String name, Buffer b)
    {
        this(name, b, MINDELAY, MAXDELAY);
    } 
    
    /**[m]
     * @return attesa minima
     */
    public int minDelay()
    {  return minDelay; }

    /**[m]
     * @return attesa massima
     */
    public int maxDelay()
    {  return maxDelay; }

    /**[m]
     * test: ciclo produzione
     */
    public void run()
    { 
        int cnt = 1;
        
    	System.out.println("Attivato produttore/scrittore "+getName());
        while(true)
        {
        	System.out.println(getName()+" sta preparando "+cnt);
        	Util.rsleep(minDelay, maxDelay);
        	System.out.println(getName()+" sta per scrivere "+cnt);
        	buf.write(getName()+"."+(cnt++));
        }
    }

} //{c} Producer

