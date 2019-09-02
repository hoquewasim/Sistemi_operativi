package os;

/**{c}
 * consumatore 
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-04-17
 * @version 2.00 2003-10-01 package Os
 * @version 2.01 2005-10-07 package os
 */

public class Consumer extends Thread
{
    private Buffer buf;
      // il buffer da cui leggere
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
    public Consumer(String name, Buffer b, int mind, int maxd)
    {
    	super(name);
    	buf = b;
        minDelay=mind;
        maxDelay=maxd;
    } 
    
    /**[c]
     * @see Consumer#Consumer(String name, Buffer b, int mind, int maxd)
     */
    public Consumer(String name, Buffer b)
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
     * test: ciclo consumo
     */
    public void run()
    { 
    	System.out.println("Attivato consumatore/lettore "+getName());
        while(true)
        {
        	System.out.println(getName()+" sta per leggere");
        	Object ret = buf.read();
            System.out.println(getName()+" elabora "+ret);
            Util.rsleep(minDelay, maxDelay);
        }
    }

} //{c} Consumer

