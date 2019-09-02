package osTest;

import os.Buffer;
import os.SyncSingleBuf;
 
/**{c}
 * test produttore/i-consumatore/i
 * su buffer singolo
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-04-17
 * @version 2.00 2003-10-01 package OsTest
 * @version 2.01 2005-10-07 package os osTest
 */

public class TestPCSingleBuf extends TestPC
{
    /**[m][s]
     * main di collaudo
     * @param args  non usato
     */
    public static void main(String[] args) 
    {
    	System.out.println("Prod./Cons. su singolo buffer");
    	Buffer buf = new SyncSingleBuf();
    	test(buf);
    } //[m][s] main
    
} //{c} TestPCSingleBuf

