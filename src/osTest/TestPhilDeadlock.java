package osTest;

import os.Semaphore;
import os.PhilAnim;
import os.PhilDeadlock;
import os.Util;
import os.Sys;

/**{c}
 * test filosofi con attesa circolare
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-05-06
 * @version 2.00 2003-10-02 package OsTest
 * @version 2.01 2005-10-07 package os e osTest
 */

public class TestPhilDeadlock
{
    private static Semaphore forks[];
      // un semaforo per forchetta
    private static Thread phils[];
      // i thread filosofi
    private static int numPh;
      // numero filosofi
    private static PhilAnim phan;
      // l'animatore
    
    /**{c}{s}
     * thread di controllo dello stato di deadlock
     */
    private static class DeadlockCheck
      extends Thread
    {
        
        /**[m]
         * esecutore controllo
         */
        public void run()
        {
loop:       while(true)
            {
                Util.sleep(5000L);
                System.err.println("???? ciclo controllo deadlock");
                
                // controllo se tutti i filosofi
                // sono in attesa della seconda forchetta
                for(int i=0; i<numPh; i++)
                {
                    Thread th = forks[i].waitingThread(0);
                    if (th == null)
                        continue loop;
                    if (th != phils[(i+numPh-1)%numPh])
                        // non attende come seconda forchetta
                        continue loop;
                } // for
                System.err.println("!!!!!!!!! DEADLOCK !!!!!!!!!");
                phan.deadlock();
                return;
            } // while
        } //[m] run
        
    } //{c} DeadlockCheck
                
        
    /**[m][s]
     * main di collaudo
     * @param args  non usato
     */
    public static void main(String[] args) 
    {
    	numPh = Sys.in.readInt("Test PhilDeadLock: battere numero filosofi:");
    	forks = new Semaphore[numPh];
    	phan = new PhilAnim("allocazione circolare", numPh);
        Util.sleep(2000);
        for(int i=0; i<numPh; i++)
            forks[i] = new Semaphore(1);
        phils = new PhilDeadlock[numPh];
        for(int i=0; i<numPh; i++)
            phils[i] = new PhilDeadlock("phil"+i, i, 3000, 8000, forks, phan);
        System.err.println("** Battere Ctrl-C per terminare!");
        for(int i=0; i<numPh; phils[i++].start());
        new DeadlockCheck().start();
    } //[m][s] main
    
} //{c} TestPhilDeadlock

