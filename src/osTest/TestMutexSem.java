package osTest;

import os.MutexSem;
import os.Util;
import os.Sys;

/**{c}
 * test sulla semaforo di controllo esclusivo risorsa 
 * (MutexSem)
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-04-21
 * @version 2.00 2003-09-30 package OsTest
 * @version 2.01 2005-10-07 package os e osTest
 */
 
public class TestMutexSem extends Thread
{
    private boolean isMutex;
      // con protezione mutex
    private static MutexSem mutex = new MutexSem();
      // mutex unico
      
    /**[c]
     * thread per il test di mutua esclusione
     * @param name  nome del thread
     * @param prot  true se con mutex
     */
    public TestMutexSem(String name, boolean prot)
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
          (isMutex ? " con" : " senza")+" mutex sem");
        Util.sleep(1000); // per consentire l'avvio degli altri
        while(true)
        {
            int rep = 0;
            
            if (isMutex)  // mutua esclusione
            {            
                rep = Util.randVal(1,5);
                for (int i=1; i<=rep; i++)
                    mutex.p();
                System.out.println("+++ "+
                  getName()+
                  " count="+mutex.getVal());
            }
            System.out.print(getName());
            Util.sleep(100);
            System.out.print(" frase n.");
            Util.sleep(100);
            System.out.println(""+(cnt++));
            if (isMutex)
                for (int i=1; i<=rep; i++)
                {
                    System.out.println("--- "+
                    getName()+
                      " un v() con cnt="+
                      mutex.getVal());
                    mutex.v();
                    Util.sleep(500);
                }
            Util.rsleep(500, 2000);
        }
    }

    /**[m][s]
     * main di collaudo
     * @param args non usato
     */
    public static void main(String[] args) 
    {
        String mtx = Sys.in.readLine("Con mutex (Y,n)? ");
        boolean prot = true;
        if (mtx.length() != 0 &&
          Character.toUpperCase(mtx.charAt(0)) == 'N')
            prot = false;
        int numTh = Sys.in.readInt("Battere numero thread:");
        Thread th[] = new Thread[numTh];
        for(int i=1; i<=numTh; i++)
            th[i-1] = new TestMutexSem("t"+i, prot);
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
    
} //{c} TestMutexSem

