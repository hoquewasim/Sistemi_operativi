package os;

/**{i}
 * interfaccia per buffer
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-04-21
 * @version 2.00 2003-09-30 package Os
 * @version 2.01 2005-10-07 package os
 */

public interface Buffer
{
    /**[m][a]
     * lettura di un dato
     * @return il dato letto
     */
    Object read();

    /**[m][a]
     * scrittura di un dato
     * @param d  il dato da memorizzare
     */
    void write(Object d);

    /**[m][a]
     * dati memorizzati
     * @return numero dati memorizzati
     */
    int size();
    
    /**[m][a]
     * spazio allocato
     * @return numero elementi allocati
     */
    int dimen();
    
} //{i} Buffer
