package os.ada;

/**{e}
 * InvalidPortNameException.java
 * eccezione di nome port illegale o non trovato -
 * non controllata
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2010-12-14
 */
public class InvalidPortNameException extends RuntimeException 
{  
    /**[c]
     * costruttore base 
     * @param err  stringa descrittiva
     */
    public InvalidPortNameException(String err) 
    {
        super("Nome Port illegale o non trovato: " + err);
    } //[c] InvalidPortNameException

} //{c} InvalidPortNameException
