package os.ada;

/**{e}
 * AllGuardsClosedException.java
 * eccezione di guardie tutte chiuse -
 * non controllata
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2010-12-14
 */
public class AllGuardsClosedException extends RuntimeException 
{  
    /**[c]
     * costruttore base 
     * @param err  stringa descrittiva
     */
    public AllGuardsClosedException(String err) 
    {
        super("Guardie tutte chiuse: " + err);
    } //[c] AllGuardsClosedException

} //{c} AllGuardsClosedException
