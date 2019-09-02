package os.ada;

import java.util.Date;
import os.Util;
import os.Sys;
import os.Timeout;

/**{c}
 * multi buffer client in Ada
 * prod/cons ed esecutore
 * @author M.Moro DEI UNIPD
 * @version 1.00 2010-12-22
*/


public class ADAMBufPC
{

    private static final long PRODTMO = 3000L;
    private static final long CONSTMO = 4000L;
      // timeout vari
      
    /**{c}
     * contenitore per i parametri,
     * d'ingresso per put, d'uscita per get
     */
    private class ProdObj
    {
        int val;
        String s;
        
        /**[c]
         */
        ProdObj(int pv, String ps)
        {
            val = pv;
            s = ps;
        }
    } //{c} ProdObj

    /**{c}
     * thread client produttore
     */
    private class ADAMBufProd extends ADAThread
    {
        private int minDelay, maxDelay;
          // ritardo minimo e massimo
        
        /**[c]
         * thread client produttore per il test RV, 
         * @param name  nome del thread
         * @param mind  delay minimo
         * @param maxd  delay massimo
         */
        public ADAMBufProd(String name, int mind, int maxd)
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
                int val = Util.randVal(1, 100);
               
                String s = "Adesso e` "+(new Date()).toString();
                System.out.println("+++ "+getName()+" cnt="+cnt+
                  " invia: val="+val+" s:"+s);
                
                // impacchetta i parametri di ingresso
                // (un intero e una stringa)
                CallOut ret = entryCall(new ProdObj(val, s), 
                  "MBUF", "PUT", PRODTMO);
                  // chiamata di RV
                if (ret.getTimeout() == Timeout.EXPIRED)
                {
                    System.out.println("+++!!!! "+getName()+" cnt="+cnt+
                      " timeout EXPIRED");
                    continue;
                }
                System.out.println("+++ "+getName()+" cnt="+cnt+
                  " inviato, timeout="+
                  (ret.getTimeout()==Timeout.INTIME ? "INTIME" : 
                  ret.getTimeout()));
                System.out.println("+++ "+getName()+" cnt="+(cnt++)+
                  " numEl="+
                  entryCall(null, "MBUF", "NUM", Timeout.NOTIMEOUT).getParams());
//?? Util.sleep(20000);                  
            } // while
        } //[m] run
                    
    } // {c} ADAMBufProd
        
    /**{c}
     * thread client consumatore
     */
    private class ADAMBufCons extends ADAThread
    {
    
        private int minDelay, maxDelay;
          // ritardo minimo e massimo
        
        /**[c]
         * thread client consumatore per il test RV, 
         * @param name  nome del thread
         * @param mind  delay minimo
         * @param maxd  delay massimo
         */
        public ADAMBufCons(String name, int mind, int maxd)
        {
            super(name);
            minDelay = mind;
            maxDelay = maxd;
        } //[c]
        
        /**[m]
         * test consumatore
         */
        public void run()
        {
            int cnt = 1;

            while(true)
            {
                Util.rsleep(minDelay, maxDelay);
                System.out.println("---- "+getName()+" cnt="+cnt+
                  " tenta ricezione");
                
                // parametro di ingresso non necessario
                CallOut ret = entryCall(null, "MBUF", "GET", CONSTMO);
                  // chiamat di RV
                if (ret.getTimeout()==Timeout.EXPIRED)
                    System.out.println("---- "+getName()+" cnt="+(cnt++)+" timeout");
                else
                    System.out.println("---- "+getName()+" cnt="+(cnt++)+
                      " ricevuto, timeout="+
                      (ret.getTimeout()==Timeout.INTIME ? "INTIME" : 
                      ret.getTimeout())+" val="+
                      ((ProdObj)ret.getParams()).val+
                      " s:"+((ProdObj)ret.getParams()).s);
            } // while
        } //[m] run
                    
    } // {c} ADAMBufCons
        
    /**[m][s]
     * main di collaudo
     * @param args  not used
     */
    public static void main(String[] args) 
    {
    	int numPr = Sys.in.readInt("Test ADA RV: battere numero produttori:");
        int numCo = Sys.in.readInt("Battere numero consumatori:");
        ADAMBufPC amb = new ADAMBufPC();

        Thread srv = new ADAMBuf("MBUF", 3);
        Thread pr[] = new Thread[numPr];
        for(int i=1; i<=numPr; i++)
            pr[i-1] = amb.new ADAMBufProd("p"+i, 3000, 5000);
        Thread co[] = new Thread[numCo];
        for(int i=1; i<=numCo; i++)
            co[i-1] = amb.new ADAMBufCons("c"+i, 3000, 5000);
        System.err.println("** Battere Ctrl-C per terminare!");
        srv.start();
Util.sleep(1000); // cosi' il server inizializza tutti gli entry
        for(int i=0; i<numPr; pr[i++].start());
//?? Util.sleep(20000);        
        for(int i=0; i<numCo; co[i++].start());
    } //[m][s] main

} //{c} ADAMBufPC

