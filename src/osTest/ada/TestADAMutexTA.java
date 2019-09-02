package osTest.ada;

import os.Util;
import os.Sys;
import os.ada.*;

/**{c}
 * test di ADATASys 
 * @author M.Moro DEI UNIPD
 * @version 1.00 2010-12-17
 */
 
public class TestADAMutexTA extends ADAThread
{
    private static final String mtxStr = "mutex";
    private static final String pStr = "p";
    private static final String vStr = "v";
    private static final String valStr = "val";

    private static final String taSysServStr = "tasysserver";
    private static final String prtStr = "print";
    private static final String prtlnStr = "println";
    private boolean isMutex;
      // con protezione mutex
      
    /**[c]
     * thread per il test in mutua esclusione di ADATASys
     * @param name  nome del thread
     * @param prot  true se con mutex
     */
    public TestADAMutexTA(String name, boolean prot)
    {
    	super(name);
    	isMutex = prot;
    } 
    
    public void run()
    {
        int cnt = 1;

        System.out.println("+++ Attivato "+getName()+
          (isMutex ? " con" : " senza")+" mutex");
        Util.sleep(1000); // per consentire l'avvio degli altri
        while(true)
        {
            if (isMutex)  // mutua esclusione
                // mutex.p();
                entryCall(null, mtxStr, pStr);
            entryCall(getName(), taSysServStr, prtStr);
            Util.sleep(100);
            entryCall(" frase n.", taSysServStr, prtStr);
            Util.sleep(100);
            entryCall(""+(cnt++), taSysServStr, prtlnStr);
            if (isMutex)  // mutua esclusione
                // mutex.v();
                entryCall(null, mtxStr, vStr);
            Util.rsleep(500, 2000);
        } // while
    } //[m] run

    /**[m][s]
     * main di collaudo
     */
    public static void main(String[] args) 
    {
        String mtx = Sys.in.readLine("Con mutex (Y,n)? ");
        boolean prot = true;
        if (mtx.length() != 0 &&
          Character.toUpperCase(mtx.charAt(0)) == 'N')
            prot = false;
        int numTh = Sys.in.readInt("Battere numero thread:");
        // crea e avvia il semaforo mutex se previsto
        if (prot)
            new ADANSem(mtxStr, 1).start();
        Util.sleep(500); // per consentirne l'avvio
        ADATASys ta = new ADATASys();
          // implementa Runnable
        Thread serv = new Thread(ta, taSysServStr);
        serv.start();
        Util.sleep(500); // per consentirne l'avvio
        Thread th[] = new Thread[numTh];
        for(int i=1; i<=numTh; i++)
            th[i-1] = new TestADAMutexTA("t"+i, prot);
        System.err.println("** Battere Ctrl-C per terminare!");
        for(int i=0; i<numTh; th[i++].start());
        
        ADAThread toCall = new ADAThread("tocall");
        while(true)
        {
            Util.sleep(5000);
            try {
                // equivale a if (prot)
                CallOut co = toCall.entryCall(null, mtxStr, valStr);
                  // stato del semaforo
                System.out.println("-- Semaforo mutex val="+
                  (Integer)co.getParams());
            } catch(InvalidPortNameException e)
            {
                System.out.println("-- Senza mutex");
            }
        } // while
    } //[m][s] main
    
} //{c} TestMutexTA
