package os;

/**{c}{a}
 * rappresenta un'interfaccia hardware per
 * un dispositivo di ingresso
 * @see Device
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2004-11-12
 * @version 2.00 2005-10-07 package os
 */

public abstract class InputDevice extends Device
{
    /**[m][a]
     * lettura dati
     * @param d  area dove leggere
     */
    public abstract void read(byte d[]);

    /**[m]
     * @see Device
     */
    public void init()
    {  status=BUSY; }
    
} //{c}{a} InputDevice
