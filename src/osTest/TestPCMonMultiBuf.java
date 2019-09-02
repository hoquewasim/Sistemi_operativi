package osTest;

import os.Sys;
import os.Buffer;
import os.MonMultiBuf;

/**{c}
 * test produttore/i-consumatore/i
 * su buffer multiplo come Monitor
 * @author M.Moro DEI UNIPD
 * @version 1.0 2002-04-18
 * @version 2.00 2003-10-03 package OsTest 
 * @version 2.01 2005-10-07 package os e osTest
 */
 
public class TestPCMonMultiBuf extends TestPC
{
    /**[m][s]
     * main di collaudo
     * @param args non usato
     */
    public static void main(String[] args) 
    {
    	int numEl = Sys.in.readInt("P/C su buffer multiplo come Monitor; battere numero elementi buffer:");
        Buffer buf = new MonMultiBuf(numEl);
        test(buf);
    } //[m][s] main
    
} //{c} TestPCMonMultiBuf

