package osTest.ada;


import os.Util;
import os.Sys;
import os.Timeout;
import os.ada.*;

/**{c}
 * Allocatore 123 in Ada
 * client ed esecutore
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2010-12-22
 * @version 1.01 2019-05-25  deprecated eliminato
*/

public class TestADAAll123 implements ADAAll123Str
{

    private static final long CLIENTTMO = 3000L;
      // timeout vari

    /**{c}
     * thread client richiedente
     */
    private class ADAAll123Clnt extends ADAThread
    {
        private int minDelay, maxDelay;
          // ritardo minimo e massimo
        
        /**[c]
         * thread client allocatore 1-2-3RV, 
         * @param name  nome del thread
         * @param mind  delay minimo
         * @param maxd  delay massimo
         */
        public ADAAll123Clnt(String name, int mind, int maxd)
        {
            super(name);
            minDelay = mind;
            maxDelay = maxd;
        } //[c]
        
        /**[m]
         * test produttore
         */
        public void run()
        {
            int cnt = 1;

            while(true)
            {
                Util.rsleep(minDelay, maxDelay);
                int val = Util.randVal(1, 3);
               
                System.out.println("+++ "+getName()+" cnt="+cnt+
                  " richiede risorse="+val);
                
                // allocazione con timeout
                CallOut rep = entryCall(null, all123Str, (val==3) ? prel3Str : 
                  ((val==2) ? prel2Str : prel1Str), CLIENTTMO);
                  // chiamata di RV
                if (rep.getTimeout() == Timeout.EXPIRED)
                {
                    System.out.println("+++!!!! "+getName()+" cnt="+cnt+
                      " allocazione timeout EXPIRED");
                    continue;
                }

                System.out.println("+++ "+getName()+" cnt="+cnt+
                  " allocate "+val+" risorse timeout="+
                  (rep.getTimeout()==Timeout.INTIME ? "INTIME" : 
                  rep.getTimeout())+" risorse rim.="+
                  ((Integer)rep.getParams()).intValue());
                Util.rsleep(minDelay, maxDelay);
                System.out.println("+++ "+getName()+" cnt="+cnt+
                  " restituisce risorse="+val);
// 1.01                rep = entryCall(new Integer(val), all123Str, rilaStr);
                rep = entryCall(Integer.valueOf(val), all123Str, rilaStr);
                System.out.println("+++ "+getName()+" cnt="+cnt+
                  " restituito risorse="+val+" risorse rim.="+
                  ((Integer)rep.getParams()).intValue());
            } // while
        } //[m] run
                    
    } // {c} ADAAll123Clnt 
        
    /**[m][s]
     * main di collaudo
     */
    public static void main(String[] args) 
    {
    	int numCl = Sys.in.readInt("all. 1-2-3 ADA RV: battere numero clienti:");
    	int ris = Sys.in.readInt("Battere numero risorse:");
        TestADAAll123 all = new TestADAAll123();

        Thread srv = new ADAAll123(all123Str, ris);
        Thread cl[] = new Thread[numCl];
        for(int i=1; i<=numCl; i++)
            cl[i-1] = all.new ADAAll123Clnt("cl"+i, 3000, 5000);
        System.err.println("** Battere Ctrl-C per terminare!");
        srv.start();
        Util.sleep(1000); // cosi' il server inizializza tutti gli entry
        for(int i=0; i<numCl; cl[i++].start());
    } //[m][s] main

} //{c} TestADAAll123

