package osTest;

import os.CountSem;
import os.Timeout;
import os.Util;

/**{c}
 * test semaforo con peso
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-05-04
 * @version 2.00 2003-10-01 package OsTest
 * @version 2.01 2005-10-07 package os e osTest
 */

public class TestCountSem
{
    /**{c}
     * thread di test
     * @author M.Moro DEI UNIPD
     * @version 1.0 2002-04-19
     */
    private static class TTSemTh extends Thread
    {
        CountSem sem;  // semaforo con timeout
        int qty;  // quantita` su p()
        
        /**[c]
         * thread per il test semaforo con peso
         * @param name  nome del thread
         * @param s  semaforo
         * @param q  quantita`
         */
        public TTSemTh(String name, CountSem s, int q)
        {
            super(name);
            sem = s;
            qty = q;
        }
        
        /**[m]
         * test: attesa con peso
         */
        public void run()
        {
            System.out.println("Attivato thread "+getName());
            long tmo = Util.randVal(500L, 4000L);
            System.out.println(getName()+" p("+qty+", "+tmo+")");
            long ret = sem.p(qty,tmo);
            System.out.println("+++ "+getName()+" termina attesa di qty="+
              qty+" con ret="+ret+
              ((ret==Timeout.EXPIRED)?" EXPIRED" : " OK"));
        }
    } // {c} TTSemTh
        

    /**[m][s]
     * main di collaudo
     * @param args  non usato
     */
    public static void main(String[] args) 
    {
        System.out.println("Test su semaforo con peso");
        System.err.println("** Battere Ctrl-C per terminare!");
        CountSem s = new CountSem(6);
    	s.p(6);
        Thread th[] = new Thread[4];
        for(int i=1; i<=4; i++)
            th[i-1] = new TTSemTh("t"+i, s, i);
        for(int i=0; i<4; th[i++].start());
        Util.sleep(2000);
        System.out.println("** v(6)");
        s.v(6);
        Util.sleep(2000);
        System.out.println("!! fine ->"+s);
        for(int i=0; i<s.queue(); i++)
            System.out.println(
              s.waitingThread(i).getName()+" attende ancora");
    } //[m][s] main
    
} //{c} TestCountSem

