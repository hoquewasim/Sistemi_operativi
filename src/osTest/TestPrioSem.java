package osTest;

import os.PrioSem;
import os.Util;

/**{c}
 * test semaforo con priorita`
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-05-04
 * @version 2.00 2003-10-02 package OsTest
 * @version 2.01 2005-10-07 package os e osTest
 */

public class TestPrioSem
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
        PrioSem sem;  // semaforo con priorita`
        int prio;  // priorita`
        
        /**[c]
         * thread per il test semaforo con peso
         * @param name  nome del thread
         * @param s  semaforo
         * @param p  priorita`
         */
        public TTSemTh(String name, PrioSem s, int p)
        {
            super(name);
            sem = s;
            prio = p;
        }
        
        /**[m]
         * test: attesa con priorita`
         */
        public void run()
        {
            Util.rsleep(100, 400);
            System.out.println("Attivato thread "+getName());
            System.out.println(getName()+" p("+prio+")");
            sem.p(prio);
            System.out.println("+++ "+getName()+" termina attesa");
        }
    } // {c} TTSemTh
        

    /**[m][s]
     * main di collaudo
     * @param args  non usato
     */
    public static void main(String[] args) 
    {
    	System.out.println("Test su semaforo con priorita`");
        PrioSem s = new PrioSem(0, 6);
        Thread th[] = new Thread[4];
        for(int i=1; i<=4; i++)
            th[i-1] = new TTSemTh("t"+i, s, i);
        for(int i=0; i<4; th[i++].start());
        Util.sleep(2000);
        System.out.println("** coda di thread in ordine di priorita`");
        for(int i=0; i<s.queue(); i++)
            System.out.println(
              s.waitingThread(i).getName());
        System.out.println("** 5 v()");
        s.v();
        s.v();
        s.v();
        s.v();
        s.v();
        Util.sleep(2000);
        System.out.println("!! fine ->"+s);
        for(int i=0; i<s.queue(); i++)
            System.out.println(
              s.waitingThread(i).getName()+" attende ancora");
    } //[m][s] main
    
} //{c} TestPrioSem

