package os;

/**{i}
 * interfaccia per la valutazione
 * di una condizione in una regione
 * critica condizionale
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2003-10-14
 * @version 2.00 2005-10-07 package os
 */

public interface RegionCondition
{
    /**[m][a]
     * valutazione della condizione
     * @return risultato della valutazione
     */
    boolean evaluate();

} //{i} RegionCondition
