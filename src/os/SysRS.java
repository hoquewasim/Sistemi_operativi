package os;

import java.io.*;

/**{c}
 * supporto di input -
 * aggiunge a Reader alcune funzioni di utilita` -
 * ricavata dalla classe Console di corejava [Hor]
 * @see InputStreamReader
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-04-17
 * @version 2.00 2003-09-30 package Os
 * @version 2.01 2005-10-07 package os
 * @version 2.02 2005-10-21 aggiunt readLong
 */

public class SysRS extends InputStreamReader
{
    private PrintWriter out;

    /**[c]
     * costruttore base
     * @param in  stream di input
     * @param o  stream di output
     */
    public SysRS(InputStream in, PrintWriter o)
    {
        super(in);
        out = o;
    } //[c] SysRS
    

    
    /**[m]
     * output di stringa di prompt
     * @param pr  stringa di prompt
     */
    public void prompt(String pr)
    {
        out.print(pr + " ");
        out.flush();
    } //[m] prompt

    /**[m]
     * lettura di una linea + prompt
     * @param pr  stringa di prompt
     * @return la linea letta
     */
    public String readLine(String pr)
    {  
        prompt(pr);
        return readLine();
    }

    /**[m]
     * lettura di una linea
     * @return la linea letta
     */
    public String readLine()
    {  
        int ch;
        String r = "";
        boolean done = false;

        while (!done)
        {  
            try
            {  
                ch = read();
                if (ch < 0 || (char)ch == '\n')
                    done = true;
                else if ((char)ch != '\r')
                    r = r + (char) ch;
            }
            catch(java.io.IOException e)
            {  
                done = true;
            }
      } // while
      return r;
   } //[m] readLine
    
    /**[m]
     * acquisizione di un valore intero
     * @param pr  stringa di prompt
     * @return intero acquisito
     * @exception NumberFormatException
     * se la linea non rappresenta un intero
     */
    public int readInt(String pr)
      throws NumberFormatException
    {  
        prompt(pr);
        return Integer.valueOf
          (readLine().trim()).intValue();
    } //[m] readInt

    /**[m]
     * acquisizione di un valore intero lungo
     * @param pr  stringa di prompt
     * @return intero long acquisito
     * @exception NumberFormatException
     * se la linea non rappresenta un intero
     */
    public long readLong(String pr)
      throws NumberFormatException
    {  
        prompt(pr);
        return Long.valueOf
          (readLine().trim()).longValue();
    } //[m] readLong

    /**[m]
     * acquisizione di un valore double
     * @param pr  stringa di prompt
     * @return double acquisito
     * @exception NumberFormatException
     * se la linea non rappresenta un double
     */
    public double readDouble(String pr)
      throws NumberFormatException
    {  
        prompt(pr);
        return Double.valueOf
          (readLine().trim()).doubleValue();
    } //[m] readInt

} //{c} SysRS


