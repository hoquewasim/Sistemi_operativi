package os;

/**{i}
 * interfaccia per callback di un timer
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2003-10-02
 * @version 2.00 2005-10-07 package os
 * @version 2.01 2014-05-02 la call può spegnere il timer
 
 */

public interface TimerCallback
{
    /**[m][a]
     * chiama la funzione di callback
     * @return true se il timer deve spegnersi
     */
    boolean call();

    /**[m][a]
     * stringa descrittiva
     * @return stringa
     */
    String toString();

} //{i} TimerCallback
