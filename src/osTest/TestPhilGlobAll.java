package osTest;

import os.Semaphore;
import os.PhilAnim;
import os.PhilGlobAll;
import os.Sys;
import os.Util;

/**{c}
 * test filosofi con allocazione globale
 * e con semafori provati
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-05-03
 * @version 2.00 2003-10-03 package OsTest
 * @version 2.01 2005-10-07 package os e osTest
 */

public class TestPhilGlobAll
{
    private static boolean forks[];
      // occupazione forchetta
    private static boolean philSusp[];
      // filosofo sospeso sul semaforo privato
    private static Semaphore philSem[];
      // semafori privati
    private static Semaphore mutex;
      // mutua esclusione
    private static Thread phils[];
      // filosofi
    private static int numPh;
      // numero filosofi
    private static PhilAnim phan;
      // l'animatore
    
    /**[m][s]
     * main di collaudo
     * @param args  non usato
     */
    public static void main(String[] args) 
    {
    	numPh = Sys.in.readInt("Test PhilGlobAll: battere numero filosofi:");
    	forks = new boolean[numPh];
        philSusp = new boolean[numPh];
        philSem = new Semaphore[numPh];
        mutex = new Semaphore(1);
        phan = new PhilAnim("allocazione globale", numPh);
        Util.sleep(2000);
        for(int i=0; i<numPh; i++) {
            forks[i] = true;
            philSusp[i] = false;
        }
        phils = new PhilGlobAll[numPh];
        for(int i=0; i<numPh; i++)
            phils[i] = new PhilGlobAll("phil"+i, i, 3000, 8000, 
              forks, philSusp, philSem, mutex, phan);
        System.err.println("** Battere Ctrl-C per terminare!");
        for(int i=0; i<numPh; phils[i++].start());
    } //[m][s] main
    
} //{c} TestPhilGlobAll

