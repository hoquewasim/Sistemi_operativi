package osTest;

import os.Buffer;
import os.RWMonSingleBuf;;
 
/**{c}
 * test lettori-scrittori
 * su buffer singolo controllato da Monitor
 * @author M.Moro DEI UNIPD
 * @version 1.0 2002-04-18
 * @version 2.00 2003-10-03 package OsTest
 * @version 2.01 2005-10-07 package os e osTest
 */

public class TestRWMonSingleBuf extends TestRW
{
    /**[m][s]
     * main di collaudo
     * @param args  non usato
     */
    public static void main(String[] args) 
    {
        System.out.println("R/W su singolo buffer come Monitor");
        System.err.println("** Battere Ctrl-C per terminare!");
        Buffer buf = new RWMonSingleBuf();
    	test(buf);
    } //[m][s] main
    
} //{c} TestRWMonSingleBuf

