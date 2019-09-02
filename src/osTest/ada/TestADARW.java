package osTest.ada;

import os.Util;
import os.Sys;
import os.Timeout;
import os.ada.*;

/**{c}
 * test lettori-scrittori in ADA 
 * @author M.Moro DEI UNIPD
 * @version 1.00 2011-01-03
 * @version 1.01 2019-05-25  deprecated eliminati
*/

public class TestADARW implements ADARWStr
{

    private static final long LETTMO = 1000L;
    private static final long SCRITMO = 500L;
      // timeout vari
      
    private class Db implements ADARWDb
    {
        private Object buf; // db
        
        public Db(Object val)
        {
            write(val);
        }
        
        public Object read()
        {
            return buf;
        }
 
        public void write(Object val)
        {
            buf=val;
        }
    } //{c} DB
    
// v1.01 deprecated    Db db = new Db(new Integer(200));
    Db db = new Db(Integer.valueOf(200));
      // db locale
       
    /**{c}
     * thread client scrittore
     */
    private class ADARWWrite extends ADAThread
    {
        private int minDelay, maxDelay;
          // ritardo minimo e massimo
        
        /**[c]
         * thread client scrittore per il test RW, 
         * @param name  nome del thread
         * @param mind  delay minimo
         * @param maxd  delay massimo
         */
        public ADARWWrite(String name, int mind, int maxd)
        {
            super(name);
            minDelay = mind;
            maxDelay = maxd;
        } //[c]
        
        /**[m]
         * test scrittore
         */
        public void run()
        {
            int cnt = 1;

            while(true)
            {
                Util.rsleep(minDelay, maxDelay);
                int val = Util.randVal(1, 100);
               
                System.out.println("+++ "+getName()+" cnt="+cnt+
                  " tenta scrittura di val="+val);

                // prenotazione                
                entryCall(null, SERVSTR,REQWRITESTR);
                // scrittura
// 1.01 deprecated                Object obj = new Integer(val);
                Object obj = Integer.valueOf(val);
                while(true) 
                {
                    CallOut co = entryCall(obj, SERVSTR, 
                      COMPWRITESTR, SCRITMO);
                    if (co.getTimeout() == Timeout.EXPIRED)
                    {
                        co = entryCall(null, SERVSTR, 
                          STATSTR);
                        System.out.println("++++++++++++++++++ "+
                          getName()+" cnt="+cnt+
                          " timeout scrittori="+
                          (((Integer)co.getParams())<0)+" lettori="+
                          Math.abs((Integer)co.getParams()));
                        
                      
                    }
                    else 
                    {
                        System.out.println("+++ "+getName()+" cnt="+cnt+
                          " SCRITTO val="+val);
                        break;
                    }
                } // while (true)
            } // while
        } //[m] run
                    
    } // {c} ADARWWrite
        
    /**{c}
     * thread client lettore
     */
    private class ADARWRead extends ADAThread
    {
        private int minDelay, maxDelay;
          // ritardo minimo e massimo
        
        /**[c]
         * thread client lettore per il test RW, 
         * @param name  nome del thread
         * @param mind  delay minimo
         * @param maxd  delay massimo
         */
        public ADARWRead(String name, int mind, int maxd)
        {
            super(name);
            minDelay = mind;
            maxDelay = maxd;
        } //[c]
        
        /**[m]
         * test lettore
         */
        public void run()
        {
            int cnt = 1;

            while(true)
            {
                Util.rsleep(minDelay, maxDelay);
                System.out.println("+++ "+getName()+" cnt="+cnt+
                  " deve leggere");

                // inizio                
                entryCall(null, SERVSTR, STARTREADSTR);
                int val = ((Integer)db.read()).intValue();
                Util.sleep(LETTMO);
                  // durata lettura
                entryCall(null, SERVSTR, STOPREADSTR);
                System.out.println("+++ "+getName()+" cnt="+cnt+
                  " letto val="+val);
            } // while
        } //[m] run
                    
    } // {c} ADARWRead
        
    /**[m][s]
     * main di collaudo
     */
    public static void main(String[] args) 
    {
        int numPr = Sys.in.readInt("Test ADA RW: battere numero scrittori:");
        int numCo = Sys.in.readInt("Battere numero lettori:");
        TestADARW rw = new TestADARW();

        Thread srv = new ADARW(SERVSTR, rw.db);
        Thread pr[] = new Thread[numPr];
        for(int i=1; i<=numPr; i++)
            pr[i-1] = rw.new ADARWWrite("w"+i, 3000, 5000);
        Thread co[] = new Thread[numCo];
        for(int i=1; i<=numCo; i++)
            co[i-1] = rw.new ADARWRead("r"+i, 3000, 5000);
        System.err.println("** Battere Ctrl-C per terminare!");
        srv.start();
        Util.sleep(1000); // cosi' il server inizializza tutti gli entry
        for(int i=0; i<numPr; pr[i++].start());
        for(int i=0; i<numCo; co[i++].start());
    } //[m][s] main

} //{c} TestADARW

