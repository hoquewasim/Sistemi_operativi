package osTest;

import os.Region;
import os.RegionCondition;
import os.Timeout;
import os.Util;
import os.Sys;

/**{c}
 * test per regione critica condizionale:
 * allocatore globale di un pool 
 * di N risorse 
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2003-10-14
 * @version 2.00 2005-10-07 package os e osTest
 * @version 2.01 2005-10-21 prova anche enterWhen(condition, timeout)
 */

public class TestRegion
{
    private static final int TRLEV = 0;
      // livello di nesting della regione
    private int free;
      // risorse da amministrare
    private long timeout;
      // eventuale timeout
    private int acnt = 1;
      // contatore chiamate ad alloc
    private int fcnt = 1;
      // contatore chiamate a free
    private Region reg = new Region(TRLEV);
      // regione di controllo
      
    /**{c}{l}
     * condizione di valutazione della regione
     */
    private class CondEval implements RegionCondition
    {
        int qty;
          // quantita` richiesta
          
        /**[c]
         * costruttore base con la quantita` richiesta
         * @param qty  risorse richieste
         */
        public CondEval(int q)
        { qty = q; }
        
        public boolean evaluate()
        { return free>= qty; }
        
    } //{c} CondEval
      
    /**[c]
     * costruttore base che fissa le risorse da allocare
     * @param qty  risorse iniziali da amministrare
     * @param t  timeout (Timeout.NoTIMEOUT==0 se untimed)
     */
    public TestRegion(int qty, long t)
    { 
        free=qty; 
        timeout = t;
        System.out.println("         0 <"+free+"> risorse, timeout="+t);
    } //[c]
    
    /**[m]
     * acquisisce risorse
     * @param qty  risorse richieste
     * @return true se allocato, false se timeout
     */
    public boolean alloc(int qty)
    { 
        long ret = reg.enterWhen(new CondEval(qty), timeout);
        if (ret == Timeout.EXPIRED)
            return false;
        // effettivamente entrato
        free -= qty;
        System.out.println("         all"+
          (acnt++) +"(-"+qty+") <"+free+"> risorse");
        reg.leave();
        return true;
    }

    /**[m]
     * restituisce risorse
     * @param qty  risorse restituite
     */
    public void free(int qty)
    {
        reg.enterWhen();
        free += qty;
        System.out.println("         f"+
          (fcnt++) +"(+"+qty+") <"+free+"> risorse");
        reg.leave();    
    } //[m] free
    
    /**{c}
     * thread di test di allocazione
     * @author M.Moro DEI UNIPD
     * @version 1.0 2003-10-13
     */
    private static class TTAllTh extends Thread
    {
        TestRegion alloc;  // allocatore
        int minDelay, maxDelay;
          // range di tempo d'attesa
        int minAlloc, maxAlloc;
          // range risorse allocate
        
        /**[c]
         * thread per il test allocatore con regione critica
         * @param name  nome del thread
         * @param a  allocatore
         * @param mind  minima attesa
         * @param maxd  massima attesa
         * @param mina  minimo valore scelta
         * @param maxa  massimo valore scelta
         */
        public TTAllTh(String name, TestRegion a, int mind, int maxd,
          int mina, int maxa)
        {
            super(name);
            alloc = a;
            minDelay = mind;
            maxDelay = maxd;
            minAlloc = mina;
            maxAlloc = maxa;
        }
        
        /**[m]
         * test: attesa con timeout
         */
        public void run()
        {
            System.out.println("Attivato thread "+getName());
            while(true)
            {
                Util.rsleep(minDelay, maxDelay);
                int choice = Util.randVal(minAlloc, maxAlloc);
                System.out.println(getName()+" chiede "+choice+
                  " risorsa/e");
                if (alloc.alloc(choice))
                {
                    System.out.println("+++1 "+getName()+" ottenuta/e "+
                      choice+" risorsa/e");
                    Util.rsleep(minDelay, maxDelay);
                    System.out.println("---1 "+getName()+" restituisce "+
                      choice+" risorsa/e");
                    alloc.free(choice);
                }
                else
                    System.out.println("+++1 "+getName()+" !!!!! TIMEOUT !!!!");
            } // while
        } //[m] run
                    
    } // {c} TTAllTh

    /**[m][s]
     * main di collaudo
     * @param args  non usato
     */
    public static void main(String[] args) 
    {
        int numRes = Sys.in.readInt("Allocatore con Regione critica condizionale: battere risorse iniziali=");
        int numTh = Sys.in.readInt("Battere numero thread:");
        long timeout = Sys.in.readLong("Battere timeout (ms, 0 se no timeout):");
        TestRegion tr = new TestRegion(numRes, timeout);
        Thread th[] = new Thread[numTh];
        for(int i=1; i<=numTh; i++)
            th[i-1] = new TTAllTh("a"+i, tr, 3000, 5000, 1, numRes/numTh+2);
        System.err.println("** Battere Ctrl-C per terminare!");
        for(int i=0; i<numTh; th[i++].start());
    } //[m][s] main

} //{c} TestRegion