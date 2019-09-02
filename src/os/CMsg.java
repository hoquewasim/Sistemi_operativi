package os;

/**{c}{a}
 * classe di supporto -
 * messaggio clonabile
 * per l'esempio send-receive
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-05-05
 * @version 2.00 2003-10-03 package Os
 * @version 2.01 2005-10-07 package os
 */
public abstract class CMsg implements Cloneable
{
    /**[m]
     * @see java.lang.Object#clone()
     */
    public Object clone()
    {
        try 
        {
            return super.clone();
        }
        catch(CloneNotSupportedException e)
        {
            return null; 
        }
    } // [m] clone
    
} //{c}{a} CMsg
