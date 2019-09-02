package os;

/**{e}
 * RegionNestingException.java
 * eccezione di livello di nesting nelle regioni
 * errato -
 * non controllata
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2003-10-14
 * @version 2.00 2005-10-07 package os
 */
public class RegionNestingException extends RuntimeException 
{  
    /**[c]
     * costruttore base 
     * @param err  stringa descrittiva
     */
    public RegionNestingException(String err) 
    {
        super("Region nesting: " + err);
    } //[c] RegionNestingException

} //{c} RegionNestingException
