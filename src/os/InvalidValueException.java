package os;

/**{e}
 * InvalidValueException.java
 * eccezione di valore illegale -
 * non controllata
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-04-16
 * @version 2.00 2003-09-30 package Os
 * @version 2.01 2005-10-07 package os
 */
public class InvalidValueException extends RuntimeException 
{  
    /**[c]
     * costruttore base 
     * @param err  stringa descrittiva
     */
    public InvalidValueException(String err) 
    {
        super("Valore illegale: " + err);
    } //[c] InvalidValueException

} //{c} InvalidValueException
