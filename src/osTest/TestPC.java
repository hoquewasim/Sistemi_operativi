package osTest;

import os.Buffer;
import os.Producer;
import os.Consumer;
import os.Sys;
 
/**{c}
 * test produttore/i-consumatore/i
 * funzione generica
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-04-18
 * @version 2.00 2003-10-01 package OsTest
 * @version 2.01 2005-10-07 package os e osTest
 */

public class TestPC
{
    /**[m][s]
     * funzione generica per collauda
     * @param buf  buffer su cui operare
     */
    public static void test(Buffer buf) 
    {
    	int numPr = Sys.in.readInt("Battere numero produttori:");
    	Thread pr[] = new Thread[numPr];  // produttori
    	for(int i=1; i<=numPr; i++)
    	    pr[i-1] = new Producer("p"+i, buf, 500, 600); // prod. veloci
    	int numCo = Sys.in.readInt("Battere numero consumatori:");
    	Thread co[] = new Thread[numCo];  // consumatori
    	for(int i=1; i<=numCo; i++)
    	    co[i-1] = new Consumer("c"+i, buf);
    	System.err.println("** Battere Ctrl-C per terminare!");
    	for(int i=0; i<numPr; pr[i++].start());
    	for(int i=0; i<numCo; co[i++].start());
    } //[m][s] main
    
} //{c} TestPC

