package os;

/**{c}{a}
 * rappresenta un'interfaccia hardware per
 * un dispositivo di uscita
 * @see Device
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2004-11-12
 * @version 2.00 2005-10-07 package os
 * @version 2.01 2005-11-18 alcuni elementi pubblici
 */

public abstract class OutputDevice extends Device
{
    /**[m][a]
     * scrittura dati
     * @param d  area da scrivere
     */
    public abstract void write(byte d[]);

    /**[m]
     * @see Device
     */
    public void init()
    {  status=READY; }
    
} //{c}{a} OutputDevice
