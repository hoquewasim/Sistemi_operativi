package osTest;

import os.Semaphore;
import os.PhilAnim;
import os.PhilGerAll;
import os.Sys;
import os.Util;

/**{c}
 * test filosofi con acquisizione gerarchica
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-05-06
 * @version 2.00 2003-10-03 package OsTest
 * @version 2.01 2005-10-07 package os e osTest
 */

public class TestPhilGerAll
{
    private static Semaphore forks[];
      // un semaforo per forchetta
    private static Thread phils[];
      // i thread filosofi
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
    	numPh = Sys.in.readInt("Test PhilGerAll: battere numero filosofi:");
    	forks = new Semaphore[numPh];
        phan = new PhilAnim("allocazione gerarchica", numPh);
        Util.sleep(4000);
        for(int i=0; i<numPh; i++)
            forks[i] = new Semaphore(1);
        phils = new PhilGerAll[numPh];
        for(int i=0; i<numPh; i++)
            phils[i] = new PhilGerAll("phil"+i, i, 3000, 8000, forks, phan);
        System.err.println("** Battere Ctrl-C per terminare!");
        for(int i=0; i<numPh; phils[i++].start());
    } //[m][s] main
    
} //{c} TestPhilGerAll

