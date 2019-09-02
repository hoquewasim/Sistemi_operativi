package os;

import java.io.*;
 
/**{c}
 * Pipeline di caratteri su file (nuovo) - 
 * protezione con semafori di tipo SimpleSemaphore2 
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-05-07
 * @version 2.00 2003-09-30 package Os
 * @version 2.01 2005-10-07 package os
 * @version 2.02 2019-05-25 deprecated eliminato
 */

public class PipeFile implements Buffer
{
    private SimpleSemaphore2 charsAval;
      // caratteri disponibili
    private File pipe;
      // il file che fa da pipeline
    private FileReader fin;
    private FileWriter fout;
      // Stream di I/O
    private int maxChars;
      // massimo del semaforo numerico
      
      
    /**[c]
     * inizializza al numero di elementi indicati
     * @param filename  nome file temporaneo per pipe
     * @param mc  capacita` del pipe
     */
    public PipeFile(String filename, int mc)
    {
        pipe = new File(filename);
        if (pipe.exists())
        {
            String ret = Sys.in.readLine("Il file "+filename+
              " esiste gia`: si vuol cancellare (y/N)? ");
            if (ret.length() != 0 &&
              Character.toUpperCase(ret.charAt(0)) == 'Y')
                pipe.delete();
            else
            {
                Sys.err.println("** Il file non deve esistere!");
                System.exit(0);
            }
        }
        try
        {
            pipe.createNewFile();
        }
        catch (IOException e)
        {
            Sys.out.println("Errore creazione pipe: "+e);
            System.exit(0);
        }
        pipe.deleteOnExit();
 
        charsAval = new SimpleSemaphore2(0, maxChars=mc);
        try
        {
            fin = new FileReader(pipe);
        }
        catch (FileNotFoundException e)
        { // non si verifica
        }
        try
        {
            fout = new FileWriter(pipe);
        }
        catch (IOException e)
        { 
            Sys.out.println("Errore pipe output: "+e);
            System.exit(0);
        }
    } //[c] 
    
    /**[m]
     * prelievo del dato
     * @return dato prelevato
     */
    public Object read()
    {
        charsAval.p();  // un dato e` disponibile?
    	// la mutua esclusione su I/O e` gia` garantita
        
        char ret = '?';
        try
        {
            ret = (char)fin.read();
        }
        catch (IOException e)
        { 
            Sys.out.println("Errore lettura da pipe:"+e);
        }
// v2.02 deprecated        return new Character(ret);
        return Character.valueOf(ret);
    }

    /**[m]
     * deposito del dato
     * @param d  dato da memorizzare
     */
    public void write(Object d)
    {
        if (size() == maxChars)
        {
            Sys.err.println("** pipe pieno, perso carattere!");
            return;
        }
        try
        {
            fout.write(((Character)d).charValue());
            fout.flush();
        }
        catch (IOException e)
        { 
            Sys.out.println("Errore scrittura su pipe:"+e);
        }
        charsAval.v();
    }

    /**[m}
     * dati memorizzati
     * @return numero dati memorizzati
     */
    public int size()
    { return charsAval.value(); }
    
    /**[m}
     * spazio allocato
     * @return numero elementi allocati
     */
    public int dimen()
    { return maxChars; }
    
    /**[m]
     * Chiusura dei canali e della finestra
     */
    public void close()
    {
        try
        {
            fin.close();
            fout.close();
        } 
        catch(IOException e) {}
    }

} //{c} PipeFile

