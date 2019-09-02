package osTest;

import os.Buffer;
import os.Sys;
import os.Reader;
import os.Writer;
 
/**{c}
 * test lettori/scrittori
 * funzione generica
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-04-18
 * @version 2.00 2003-10-03 package OsTest
 * @version 2.01 2005-10-07 package os e osTest
 */
 
public class TestRW
{
    /**[m][s]
     * funzione generica per collaudo
     * @param buf  buffer su cui operare
     */
    public static void test(Buffer buf) 
    {
    	int numRe = Sys.in.readInt("Battere numero lettori:");
    	Thread re[] = new Thread[numRe];  // lettori
    	for(int i=1; i<=numRe; i++)
    	    re[i-1] = new Reader("r"+i, buf, 3000, 5000);
    	      // lettore come consumatore
    	int numWr = Sys.in.readInt("Battere numero scrittori:");
    	Thread wr[] = new Thread[numWr];  // scrittori
    	for(int i=1; i<=numWr; i++)
    	    wr[i-1] = new Writer("w"+i, buf, 10000, 14000); // scrittori. lenti
    	      // scrittore come produttore
    	System.err.println("** Battere Ctrl-C per terminare!");
    	for(int i=0; i<numWr; wr[i++].start());
    	for(int i=0; i<numRe; re[i++].start());
    } //[m][s] main
    
} //{c} TestRW

