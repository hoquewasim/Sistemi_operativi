package os.ada;

/**{i}
 * interfaccia per una guardia di rendez-vous
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2010-12-12
 */

public interface Guard
{
    /**[m][a]
     * valutazione della guardia
     * @return true se guardia aperta
     */
    boolean when();

} //{i} Guard
      
