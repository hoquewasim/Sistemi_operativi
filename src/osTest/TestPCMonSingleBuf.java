package osTest;

import os.Buffer;
import os.MonSingleBuf;
 
/**{c}
 * test produttore/i-consumatore/i
 * su buffer singolo controllato da Monitor
 * @author M.Moro DEI UNIPD
 * @version 1.0 2002-04-17
 * @version 2.00 2003-10-03 package OsTest 
 * @version 2.01 2005-10-07 package os e osTest
 */

public class TestPCMonSingleBuf extends TestPC
{
    /**[m][s]
     * main di collaudo
     * @param args non usato
     */
    public static void main(String[] args) 
    {
    	System.out.println("P/C su singolo buffer come Monitor");
    	Buffer buf = new MonSingleBuf();
    	test(buf);
    } //[m][s] main
    
} //{c} TestPCMonSingleBuf

