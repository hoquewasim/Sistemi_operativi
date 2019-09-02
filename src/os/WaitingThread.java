package os;

/**{c}
 * classe di supporto -
 * memorizza una coppia thread id - risvegliato
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-04-22
 * @version 2.00 2003-10-02 package Os
 * @version 2.01 2005-10-07 package os
 */
class WaitingThread
{
    Thread th;  // thread id
    boolean wakenUp;  // se risvegliato
    int qty;  // quantita` richiesta
    int prio;  // priorita` dell'attesa
               // valori bassi, priorita` piu` elevata
    static final int MIN_PRIO = Integer.MAX_VALUE;
    static final int MAX_PRIO = 1;
      // limiti priorita`
    
    /**[c]
     * costruttore base 
     * @param t  thread id
     * @param w  risvegliato
     * @param q  quantita` richiesta
     * @param p  priorita`
     */
    WaitingThread(Thread t, boolean w, int q, int p)
    {
        th=t;
        wakenUp = w;
        qty = q;
        prio = p;
    }

    /**[c]
     * costruttore base 
     * @param t  thread id
     * @param w  risvegliato
     * @param q  quantita` richiesta
     */
    WaitingThread(Thread t, boolean w, int q)
    {
        this(t, w, q, MIN_PRIO);
    }

    /**[c]
     * costruttore base 
     * @param t  thread id
     * @param w  risvegliato
     */
    WaitingThread(Thread t, boolean w)
    {
        this(t, w, 1, MIN_PRIO);
    }

} //{c} WaitingThread
