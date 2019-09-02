package osExtra;

import java.rmi.*;
import java.util.Date;

/**{i}
 * remote date:
 * esempio d'uso di RMI con data remota -
 * interfaccia metodo remotizzato
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2003-10-20
 * @version 2.00 2005-10-07 package osExtra
 */

public interface RemDate extends Remote 
{
    /**[m][a]
     * acquisisce data corrente
     * @return la data acquisita remotamente
     * @see Remote
     */
    Date getDate () 
      throws RemoteException;

    /**[m][a]
     * unbind dell'oggetto
     * @see Remote
     */
    void close () 
      throws RemoteException;
}
