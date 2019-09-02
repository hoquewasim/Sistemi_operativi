package os;

import java.io.*;

/**{c}
 * conteggio righe di output -
 * aggiunge a PrintWriter il conteggio delle linee di
 * uscita con attesa ogni MAXLINES linee -
 * l'attesa ha luogo, con relativo invito, 
 * solo se il canale di input e` aperto -
 * si assume che PrintWriter chiami println()
 * al termine delle varie versioni di println con
 * parametro/i
 * @see PrintWriter
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-04-17
 * @version 2.00 2003-09-30 package Os
 * @version 2.01 2005-10-07 package Os
 */

public class SysPS extends PrintWriter
{
    private int lines;
        // contatore linee di output
    private int maxLines;
        // numero di linee successive senza attesa
    private InputStream ins;
        // input per RETURN

    /**[c]
     * costruttore base
     * @param out  stream di output
     * @param in   stream di input
     * @param max  massimo numero di righe prima dell'attesa
     */
    public SysPS(OutputStream out, InputStream in, int max)
    {
        super(out, true);
        lines = 1;
        ins = in;
        maxLines = max;
    } //[c] SysPS

    /**[m]
     * effettua il controllo con attesa dopo il numero
     * di righe prefissato 
     */
    private void control()
    {
        if (lines >= maxLines)
        {
            try
            {
                ins.available();
                  // stampa avviso solo se l'input
                  // e` disponibile
                super.print(
                  "-- Continua -- (battere RETURN)");
                super.flush();
                if (ins.read() == '\r')
                    ins.read();  // salta \n
            }
            catch(IOException e)
            {
                // nop
            } 
            lines = 1;
        }
        else
            lines++;
    } //[m] control

    /**[m]
     * aggiunge il controllo prima del fine riga 
     */
    public void println()
    {
        control();
        super.println();
    }

    /**[m]
     * controlla se la stringa e` divisa in piu` righe
     * @param s  stringa da emettere
     */
    public void println(String s)
    {
        int start = 0;
        while (start < s.length())
        {
            int last = s.indexOf('\n', start);
            if (last == -1)
            {
                // ultima porzione
                super.println(s.substring(start));
                  // chiama anche println() che chiama control
                start = s.length();
            } //if
            else
            {
                if (last >0 && s.charAt(last-1) == '\r')
                    // elimina CR
                    super.println(s.substring(start, last-1));
                else
                    super.println(s.substring(start, last));
                start = last+1;
            } //else
        } //while
    }

    /**[m]
     * consente di ricondurre alla versione stringa
     * nel caso di un oggetto generico
     * @param obj  oggetto con descrizione da emettere
     */
    public void println(Object obj)
    {
        println(obj.toString());
    }

} //{c} SysPS


