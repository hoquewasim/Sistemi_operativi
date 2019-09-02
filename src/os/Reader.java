package os;

/**{c}
 * lettore -
 * sinonimo di Consumer 
 * @author M.Moro DEI UNIPD
 * @version 1.0 2002-04-17
 * @version 2.00 2003-10-03 package Os
 * @version 2.01 2005-10-07 package os
 */
 
public class Reader extends Consumer
{
    /**[c]
     * @see Consumer#Consumer(String name, Buffer b, int mind, int maxd)
     */
    public Reader(String name, Buffer b, int mind, int maxd)
    {
    	super(name, b, mind, maxd);
    } 
    
    /**[c]
     * @see Consumer#Consumer(String name, Buffer b)
     */
    public Reader(String name, Buffer b)
    {
        super(name, b);
    } 
    
} //{c} Reader

