package os.ada;

/**{e}
 * MultiElseException.java
 * eccezione di opzione else multipla -
 * non controllata
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2010-12-14
 */
public class MultiElseException extends RuntimeException 
{  
    /**[c]
     * costruttore base 
     * @param err  stringa descrittiva
     */
    public MultiElseException(String err) 
    {
        super("Else multiplo: " + err);
    } //[c] MultiElseException

} //{c} MultiElseException
