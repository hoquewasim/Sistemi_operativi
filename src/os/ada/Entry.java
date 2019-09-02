package os.ada;

/**{i}
 * interfaccia per un entry-accept
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2010-12-12
 */

public interface Entry
{
    /**
      port name per clausola else
     */
    public static final String ELSECLAUSE = null;
         
    /**[m][a]
     * esecuzione dell'entry
     * @param inParams  parametri d'ingresso
     * @return parametri d'uscita
     */
    Object exec(Object inParams);

} //{i} Entry
      
