package os;

/**{c}
 * scrittore -
 * sinonimo di Producer 
 * @author M.Moro DEI UNIPD
 * @version 1.0 2002-04-17
 * @version 2.00 2003-10-03 package Os
 * @version 2.01 2005-10-07 package os
 */
 
public class Writer extends Producer
{
    /**[c]
     * @see Producer#Producer(String name, Buffer b, int mind, int maxd)
     */
    public Writer(String name, Buffer b, int mind, int maxd)
    {
        super(name, b, mind, maxd);
    } 
    
    /**[c]
     * @see Producer#Producer(String name, Buffer b)
     */
    public Writer(String name, Buffer b)
    {
        super(name, b);
    } 
    
} //{c} Writer

