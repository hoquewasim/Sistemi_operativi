package os;

/**{c}
 * classe di supporto -
 * memorizza un descrittore di messaggio
 * per l'esempio send-receive
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-05-04
 * @version 2.00 2003-10-03 package Os
 * @version 2.01 2005-10-07 package os
 */
class MsgDesc
{
    String sender;  // il nome del mittente
    Object msg; // messaggio dati (clonato)
    boolean sync;  // se sincrono
    Semaphore priv;  // semaforo privato del mittente
    
    /**[c]
     * costruttore base 
     * @param s  nome inviante
     * @param m  messaggio dati
     * @param y  true se sincrono
     * @param p  semaforo privato
     */
    MsgDesc(String s, Object m, boolean y, Semaphore p)
    {
        sender = s;
        msg = m;
        sync = y;
        priv = p;
    } //[c]

} //{c} MsgDesc
