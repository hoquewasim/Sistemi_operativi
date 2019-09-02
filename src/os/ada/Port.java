package os.ada;

import java.util.ArrayList;
import java.util.Queue;
import os.Semaphore;

/**{c}
 * coda asincrona molti a uno
 *
 * @param <M>  classe degli elementi nella coda
 * @author M.Moro DEI UNIPD
 * @version 1.00 2010-12-12
 */

public class Port<M> extends ArrayList<M> implements Queue<M>
{
    boolean waiting = false;
      // un thread attende su questo port
    Semaphore priv;
      // semaforo privato di attesa
      
    /**[c]
     * un port
     * @param pr  il semaforo di sincronizzazione
     */
    public Port(Semaphore pr)
    {
        priv = pr;
    }

    /**[m]
     * il primo della coda
     * @return il primo della coda
     */
    public M element()
    {
        return get(0);
    } //[m] element

    /**[m]
     * inserisce richiesta
     * @return true (nessuna limitazione di spazio)
     * @see java.util.ArrayList#add(Object el)
     */
    public boolean offer(M inParams)
    {
        add(inParams);  
          // non ci sono restrizioni di spazio
        return true;
    } //[m] offer

    /**[m]
     * il primo della coda
     * @return il primo della coda se presente;
     *         null se coda vuota
     * @see java.util.ArrayList#get(int index)
     */
    public M peek()
    {
        if (size()>0)
            return get(0);
        else
            return null;
    } //[m] peek

    /**[m]
     * estrae il primo della coda
     * @return il primo della coda se presente;
     *         null se coda vuota
     * @see java.util.ArrayList#remove(int index)
     */
    public M poll()
    {
        if (size()>0)
            return remove(0);
        else
            return null;
    } //[m] poll

    /**[m]
     * estrae il primo della coda
     * @return il primo della coda
     * @see java.util.ArrayList#remove(int index)
     */
    public M remove()
    {
        return remove(0);
    } //[m] remove

    /**[m]
     * estrae un elemento
     * @param i  indice dell'elemento da estrarre
     * @return elemento estratto
     * @see java.util.ArrayList#remove(int index)
     */
    public M extract(int i)
    {
        return remove(i);
    }

} //{c} Port<M>
      
