package os;

import java.io.*;

/**{c}
 * sostituisce System per usare la classe SysPS
 * e SysRS
 * @see SysPS
 * @see SysRS
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-04-17
 * @version 2.00 2003-09-30 package Os
 * @version 2.01 2005-10-07 package os
 */

public class Sys {
    /** canale di input
     */
    public static SysRS in;
    /** canale di output
     */
    public static SysPS out;
    /** canale di output d'errore
     */
    public static SysPS err;
    
    /** gruppi di linee separate
     */
    public static final int MAXLINES = 16;

    // blocco statico di inizializzazione
    static
    {
        err = new SysPS(System.err, System.in, MAXLINES);
        in = new SysRS(System.in, err);
        out = new SysPS(System.out, System.in, MAXLINES);
    }

    /**[m][s]
     * metodo di collaudo
     * @param args  non usato
     */
    public static void main(String[] args)
    {
        for (int i=0; i<20 ; i++)
            Sys.out.println("Riga " + i + "\r\nRiga " + i + 
              "\r\nRiga " + i);
        Sys.in.prompt("Battere return:");
        Sys.in.readLine();
        while (true)
        {
            try
            {
                int iVal = Sys.in.readInt("Battere valore intero:");
				Sys.out.println("Letto " + iVal);
				break;
            }
            catch (NumberFormatException e)
            {
            }
        } // while
        while (true)
        {
            try
            {
                double dVal = Sys.in.readDouble("Battere valore double:");
				Sys.out.println("Letto " + dVal);
				break;
            }
            catch (NumberFormatException e)
            {
            }
        } // while
    } //[m] main

} //{c} Sys
