package os.ada;

/**{e}
 * ServerExistsException.java
 * eccezione di server con quel nome gia' attivato -
 * non controllata
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2010-12-14
 */
public class ServerExistsException extends RuntimeException 
{  
    /**[c]
     * costruttore base 
     * @param err  stringa descrittiva
     */
    public ServerExistsException(String err) 
    {
        super("Server gia' attivato: " + err);
    } //[c] ServerExistsException

} //{c} ServerExistsException
