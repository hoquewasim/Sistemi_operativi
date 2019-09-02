package osTest;

import os.SimpleSemaphore;
import os.Util;
import os.Sys;
import os.InvalidThreadException;
 
/**{c}
 * test sulla mutua esclusione 
 * con semafori binari
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-04-21
 * @version 2.00 2003-09-30 package OsTest
 * @version 2.01 2005-10-07 package os e osTest
 */
 
public class TestMutex extends Thread
{
    private boolean isMutex;
      // con protezione mutex
    private static SimpleSemaphore mutex = new SimpleSemaphore(true);
      // mutex unico
      
    /**[c]
     * thread per il test di mutua esclusione
     * @param name  nome del thread
     * @param prot  true se con mutex
     */
    public TestMutex(String name, boolean prot)
    {
    	super(name);
    	isMutex = prot;
    } 
    
    /**[m]
     * test: ciclo stampe divisibili
     */
    public void run()
    { 
        int cnt = 1;
        
        System.out.println("Attivato "+getName()+
          (isMutex ? " con" : " senza")+" mutex");
        Util.sleep(1000); // per consentire l'avvio degli altri
        while(true)
        {
            if (isMutex)  // mutua esclusione
                mutex.p();
            System.out.print(getName());
            Util.sleep(100);
            System.out.print(" frase n.");
            Util.sleep(100);
            System.out.println(""+(cnt++));
            if (isMutex)
                mutex.v();
            Util.rsleep(500, 2000);
        }
    }

    /**[m][s]
     * main di collaudo
     * @param args non usato
     */
    public static void main(String[] args) 
    {
        String mtx = Sys.in.readLine("Con mutex (senza le scritte si mescolano) (Y,n)? ");
        boolean prot = true;
        if (mtx.length() != 0 &&
          Character.toUpperCase(mtx.charAt(0)) == 'N')
            prot = false;
        int numTh = Sys.in.readInt("Battere numero thread:");
        Thread th[] = new Thread[numTh];
        for(int i=1; i<=numTh; i++)
            th[i-1] = new TestMutex("t"+i, prot);
        System.err.println("** Battere Ctrl-C per terminare!");
        for(int i=0; i<numTh; th[i++].start());
        while(true)
        {
            Util.sleep(5000);
            if (prot)  // mutua esclusione
                mutex.p();
            // stato del semaforo
            System.out.println(mutex);
            if (prot)  // mutua esclusione
                mutex.v();
        } // while
    } //[m][s] main
    
} //{c} TestMutex

