package osTest;

import java.util.Date;
import os.CassettaPostale;
import os.Util;
import os.Sys;
 
/**{c}
 * test con cassetta postale
 * come SimpleMonitor
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-05-03
 * @version 2.00 2003-10-03 package OsTest 
 * @version 2.01 2005-10-07 package os e osTest
 */

public class TestCassettaPostale
{
    private static CassettaPostale cp333 = new CassettaPostale();
      // la cassetta di scambio
            
    /**{c}
     * thread produttore
     * @author M.Moro DEI UNIPD
     * @version 1.0 2002-05-03
     */
    private static class Produttore extends Thread
    {
        int minDelay, maxDelay;
          // range di tempo d'attesa
        
        /**[c]
         * thread per il test Casella Postale
         * @param name  nome del thread
         * @param mind  minima attesa
         * @param maxd  massima attesa
         */
        public Produttore(String name, int mind, int maxd)
        {
            super(name);
            minDelay = mind;
            maxDelay = maxd;
        }
        
        /**[m]
         * test: produce messaggio
         */
        public void run()
        {
            String miaLettera;
            System.out.println("Attivato thread produttore "+getName());
            while(true)
            {
                Util.rsleep(minDelay, maxDelay);
                miaLettera = "Adesso e` "+(new Date()).toString();
                System.out.println("+++ "+getName()+" inserisce: "+miaLettera);
                cp333.deposita(miaLettera);
                  // ad ogni chiamata il messaggio e` un oggetto nuovo
            } // while
        } //[m] run
                    
    } // {c} produttore
        
    /**{c}
     * thread consumatore
     * @author M.Moro DEI UNIPD
     * @version 1.0 2002-05-03
     */
    private static class Consumatore extends Thread
    {
        int minDelay, maxDelay;
          // range di tempo d'attesa
        
        /**[c]
         * thread per il test Casella Postale
         * @param name  nome del thread
         * @param mind  minima attesa
         * @param maxd  massima attesa
         */
        public Consumatore (String name, int mind, int maxd)
        {
            super(name);
            minDelay = mind;
            maxDelay = maxd;
        }
        
        /**[m]
         * test: produce messaggio
         */
        public void run()
        {
            String laLettera;
            System.out.println("Attivato thread consumatore "+getName());
            while(true)
            {
                Util.rsleep(minDelay, maxDelay);
                laLettera = (String) cp333.preleva();
                System.out.println("------ "+getName()+" prelevato: "+laLettera);
            } // while
        } //[m] run
                    
    } // {c} consumatore
        
    /**[m][s]
     * main di collaudo
     * @param args  non usato
     */
    public static void main(String[] args) 
    {
    	int numPr = Sys.in.readInt("Test SimpleMonitor casella postale: battere numero produttori:");
        int numCo = Sys.in.readInt("Battere numero consumatori:");

        Thread pr[] = new Thread[numPr];
        for(int i=1; i<=numPr; i++)
            pr[i-1] = new Produttore("p"+i, 3000, 5000);
        Thread co[] = new Thread[numCo];
        for(int i=1; i<=numCo; i++)
            co[i-1] = new Consumatore("c"+i, 3000, 5000);
        System.err.println("** Battere Ctrl-C per terminare!");
        for(int i=0; i<numPr; pr[i++].start());
        for(int i=0; i<numCo; co[i++].start());
    } //[m][s] main
    
} //{c} TestCassettaPostale

