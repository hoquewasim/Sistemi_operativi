package osTest.ada;

import os.Util;
import os.Sys;
import os.ada.*;

/**{c}
 * buffer singolo con 2 semafori binari e RV di Ada
 * 
 * @author M.Moro DEI UNIPD
 * @version 1.00 2010-12-12
*/

public class TestADANSemSBuf
{

    private static final String empStr = "empty";
    private static final String fulStr = "full";
      // nome dei server
    private static final String pStr = "p";
      // nome del selettore di p()
    private static final String vStr = "v";
      // nome del selettore di v()
    private static final String valStr = "val";
      // nome del selettore di stato
      
    private int buf;

    /**{c}
     * thread client produttore
     */
    private class SemRVProd extends ADAThread
    {
        private int minDelay, maxDelay;
          // ritardo minimo e massimo
        
        /**[c]
         * thread client produttore per il test RV, 
         * @param name  nome del thread
         * @param mind  delay minimo
         * @param maxd  delay massimo
         */
        public SemRVProd(String name, int mind, int maxd)
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
               
                System.out.println("+++ "+getName()+" cnt="+cnt+
                  " invia: val="+val);
                  
                // empty.p();
                entryCall(null, empStr, pStr);
                buf = val;
                // full.v();
                entryCall(null, fulStr, vStr);
                
                System.out.println("+++ "+getName()+" cnt="+(cnt++)+
                " empty="+
                  entryCall(null, empStr, valStr).getParams()+
                  " full="+
                  entryCall(null, fulStr, valStr).getParams());
            } // while
        } //[m] run
                    
    } // {c} SemRVProd 
        
    /**{c}
     * thread client consumatore
     */
    private class SemRVCons extends ADAThread
    {
    
        private static final String srvStr = "server";
          // nome del server
        private static final String portStr = "get";
          // nome del selettore di interesse
        private int minDelay, maxDelay;
          // ritardo minimo e massimo
        
        /**[c]
         * thread client consumatore per semaforo RV, 
         * @param name  nome del thread
         * @param mind  delay minimo
         * @param maxd  delay massimo
         */
        public SemRVCons(String name, int mind, int maxd)
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
               
                System.out.println("+++ "+getName()+" cnt="+cnt+
                  " riceve");
                  
                // full.p();
                entryCall(null, fulStr, pStr);
                int val = buf;
                // emp.v();
                entryCall(null, empStr, vStr);
                
                System.out.println("+++ "+getName()+" cnt="+cnt+
                  " val="+val);
                System.out.println("+++ "+getName()+" cnt="+(cnt++)+
                  " empty="+
                  entryCall(null, empStr, valStr).getParams()+
                  " full="+
                  entryCall(null, fulStr, valStr).getParams());
            } // while
        } //[m] run
                    
    } // {c} SemRVCons
        
    /**[m][s]
     * main di collaudo
     */
    public static void main(String[] args) 
    {
    	int numPr = Sys.in.readInt("Test Semaforo ADA RV: battere numero produttori:");
        int numCo = Sys.in.readInt("Battere numero consumatori:");
        TestADANSemSBuf sbuf = new TestADANSemSBuf();
        Thread emp = new ADANSem(empStr, 1);
        Thread ful = new ADANSem(fulStr, 0);
        Thread pr[] = new Thread[numPr];
        for(int i=1; i<=numPr; i++)
            pr[i-1] = sbuf.new SemRVProd("p"+i, 3000, 5000);
        Thread co[] = new Thread[numCo];
        for(int i=1; i<=numCo; i++)
            co[i-1] = sbuf.new SemRVCons("c"+i, 3000, 5000);
        System.err.println("** Battere Ctrl-C per terminare!");
        emp.start();
        ful.start();
        for(int i=0; i<numPr; pr[i++].start());
        for(int i=0; i<numCo; co[i++].start());
    } //[m][s] main

} //{c} TestADANSemSBuf

