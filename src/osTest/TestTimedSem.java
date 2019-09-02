package osTest;

import java.util.Date;
import os.Semaphore;
import os.Util;
 
/**{c}
 * test semaforo con timeout
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-04-19
 * @version 2.00 2003-10-01 package OsTest
 * @version 2.01 2005-10-07 package os e osTest
 */

public class TestTimedSem
{
    private static final long FACTOR = 2000L;
      // intervallo di discriminazione
    private static long startTime;
      // istante iniziale
    
    /**{c}
     * thread di test
     * @author M.Moro DEI UNIPD
     * @version 1.0 2002-04-19
     */
    private static class TTSemTh extends Thread
    {
        Semaphore sem;  // semaforo con timeout
        long timeout;  // attesa max su p()
        long delay;    // ritardo per p()
        
        /**[c]
         * thread per il test semaforo con timeout
         * @param name  nome del thread
         * @param s  semaforo
         * @param tm  timeout
         * @param d  ritardo chiamata p()
         */
        public TTSemTh(String name, Semaphore s, long tm, long d)
        {
            super(name);
            sem = s;
            timeout = tm;
            delay = d;
        }
        
        /**[m]
         * test: attesa con timeout
         */
        public void run()
        {
            System.out.println("Attivato thread "+getName());
            Util.sleep(delay);
            long now = (System.currentTimeMillis()-startTime) /
              FACTOR * FACTOR;
                // ezzera cifre meno significative
            System.out.println(getName()+" attende in "+now+
              " timeout="+timeout);
            long ret = sem.p(timeout) / FACTOR * FACTOR;
            now = (System.currentTimeMillis()-startTime) /
              FACTOR * FACTOR;
            System.out.println("+++ "+getName()+" termina attesa in "+
            now+" ret="+ret);
        }
    } // {c} TTSemTh
        

    /**[m][s]
     * main di collaudo
     * @param args  non usato
     */
    public static void main(String[] args) 
    {
    	System.out.println("Test su semaforo con timeout");
    	Semaphore s = new Semaphore(false);
        Thread th[] = new Thread[4];
        long tm[] = {5L*FACTOR, 7L*FACTOR, FACTOR, 3*FACTOR};
          // timeout
        long d[] = {0L, FACTOR, 2L*FACTOR, 4L*FACTOR};
        for(int i=1; i<=4; i++)
            th[i-1] = new TTSemTh("t"+i, s, tm[i-1], d[i-1]);
        startTime = System.currentTimeMillis();
        for(int i=0; i<4; th[i++].start());
        Util.sleep(6L*FACTOR);
        System.out.println("** v() in "+(6L*FACTOR));
        s.v();
        // attende terminazione
        for(int i=0; i<4; i++)
            try{ th[i].join(); }
            catch( InterruptedException e ){}
        System.out.println("!! fine ->"+s);
    } //[m][s] main
    
} //{c} TestTimedSem

