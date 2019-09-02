package osTest;

import os.TASys;
import os.Timeout;

/**{c}
 * test TASys finestre di I/O
 * @author M.Moro DEI UNIPD
 * @version 1.00 2003-10-03
 * @version 2.00 2005-10-07 package os e osTest
 */

public class TestTASys
{
    /**[m][s]
     * main di collaudo
     * @param args  non usato
     */
    public static void main(String[] args) 
    {
        int cnt = 1;
        System.err.println("Test TASys");
        TASys mySys1 = new TASys();
        TASys mySys2 = new TASys();
          // frame di I/O
        mySys1.out.println("Finestra di input");
        mySys2.out.println("Finestra di eco");
        while(true)
        {
            String mtx = mySys1.in.readLine(
              "Battere un stringa terminata da CR (exit per terminare):");
            mySys1.out.println("letto->"+mtx+"<-");
            mySys2.out.println("Stringa "+(cnt++)+" =>"+mtx+"<=");
            if (mtx.equals("exit"))
            {
                System.out.println("** terminato!");
                mySys1.close();
                mySys2.close();
                return;
            }
        } //while
    } //[m][s] main
    
} //{c} ProTASys

