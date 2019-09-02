package os;

/**{i}
 * interfaccia per buffer con ricerca e cancellazione
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-04-28
 * @version 2.00 2003-10-03 package Os
 * @version 2.01 2005-10-07 package os
 */

public interface SBuffer extends Buffer
{
    /** posizione non valida
     */
    static final int NOPOS = -1;
      
    /**[m][a]
     * elemento in posizione
     * @param pos  poszione elemento (0..siz()-1) 
     * @return oggetto in posizione se posizione valida,
     *         null altrimenti
     */
    Object elem(int pos);
    
    /**[m][a]
     * ricerca per chiave
     * @param key  chiave ricercata
     * @return posizione elemento (0..siz()-1) se trovato,
     *         NOPOS altrimenti
     */
    int find(Object key);
    
    /**[m][a]
     * ricerca ed estrazione per chiave
     * @param key  chiave ricercata
     * @return oggetto estratto se chiave trovata,
     *         null altrimenti
     */
    Object extract(Object key);
    
} //{i} SBuffer
