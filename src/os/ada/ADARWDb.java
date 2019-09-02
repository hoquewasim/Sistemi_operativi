package os.ada;

/**{i}
 * interfaccia per lettura/scrittura su db
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2010-12-12
 */

public interface ADARWDb
{
    /**[m][a]
     * lettura
     * @return valore letto
     */
    Object read();

    /**[m][a]
     * scrittura
     * @param val  valore da scrivere
     */
    void write(Object val);

} //{i} ADARWDb
      
