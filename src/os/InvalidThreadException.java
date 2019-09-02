package os;

/**{e}
 * InvalidThreadException.java
 * eccezione di thread illegale -
 * non controllata
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-04-18
 * @version 2.00 2003-09-30 package Os
 * @version 2.01 2005-10-07 package os
 */
public class InvalidThreadException extends RuntimeException 
{  
    /**[c]
     * costruttore base 
     * @param err  stringa descrittiva
     */
    public InvalidThreadException(String err) 
    {
        super("Thread illegale: " + err);
    } //[c] InvalidThreadException

} //{c} InvalidThreadException
