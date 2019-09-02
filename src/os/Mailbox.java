package os;

/**{c}
 * mailbox su buffer multiplo -
 * in pratica e` una rinominazione
 * delle funzioni della classe SyncMultiBuf
 * @see SyncMultiBuf
 * @author M.Moro DEI UNIPD
 * @version 1.00 2003-10-03
 * @version 2.00 2005-10-07 package os
 */
 
public class Mailbox extends SyncMultiBuf
{
    /**[c]
     * inizializza al numero di elementi indicati
     * @param maxMsgs  massimo numero di messaggi accodabili
     */
    public Mailbox(int maxMsgs)
    { super (maxMsgs); }
    
    /**[m]
     * accoda un messaggio -
     * si noti che il messaggio non viene copiato
     * @param d  messaggio da accodare
     */
    public void put(Object d)
    { write(d); }

    /**[m]
     * accoda immediatamente un messaggio -
     * si noti che il messaggio non viene copiato -
     * la chiamata non e` mai sospensiva -
     * puo` essere chiamata anche da una
     * callback di un timer
     * @param d  messaggio da accodare
     * @return d se accodato, EXPIREDOBJ altrimenti
     */
    public Object putCond(Object d)
    { return write(d, Timeout.IMMEDIATE); }

    /**[m]
     * accoda una copia del messaggio
     * @param d  messaggio da accodare
     */
    public void putClone(CMsg d)
    { write(d.clone()); }

    /**[m]
     * accoda immediatamente una copia del messaggio -
     * la chiamata non e` mai sospensiva -
     * puo` essere chiamata anche da una
     * callback di un timer
     * @param d  messaggio da accodare
     * @return d se accodato, EXPIREDOBJ altrimenti
     */
    public Object putCloneCond(CMsg d)
    { return 
        (write(d.clone(), Timeout.IMMEDIATE) ==
          Timeout.EXPIREDOBJ) ? Timeout.EXPIREDOBJ :
          d; 
    }

    /**[m]
     * estrae un messaggio
     * @return messaggio estratto
     */
    public Object get()
    { return read(); }
        
    /**[m]
     * estrae un messaggio con timeout
     * @param timeout attesa massima
     * @return messaggio se estratto
     *         EXPIRED altrimenti
     */
    public Object get(long timeout)
    { return read(timeout); }
        
    /**[m]
     * estrae immediatamente un messaggio -
     * la chiamata non e` mai sospensiva -
     * puo` essere chiamata anche da una
     * callback di un timer
     * @return messaggio se estratto,
     *         EXPIRED altrimenti
     */
    public Object getCond()
    { return read(Timeout.IMMEDIATE); }
        
    /**[m]
     * rimuove i messaggi dalla coda
     * @return numero messaggi rimossi
     */
    public int clearMbx()
    { 
        int cnt = 0;
        
        while(getCond() != Timeout.EXPIREDOBJ)
          cnt++;
        return cnt; 
    } //[m] clearMbx
    
    /**[m]
     * numero di messaggi in coda
     * @return numero messaggi
     */
    public int msgCnt()
    { return size(); }
    
} //{c} Mailbox

