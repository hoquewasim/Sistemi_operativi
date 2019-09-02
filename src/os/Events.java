package os;

import java.util.*;
 
/**{c}
 * gestione degli eventi associati ai task 
 * @author M.Moro DEI UNIPD
 * @version 1.00 2003-10-01 
 * @version 2.00 2005-10-07 package os
 * @version 2.01 2011-03-03 generics used
 */

public class Events
{
    private static Map<String, EventThread> evMap = 
      new HashMap<String, EventThread>();
      // set di thread con eventi
      // dimensione e load factor di default
      // hashcode dalle chiavi che sono i nomi 
      // dei thread
      // metodi della mappa NON sincronizzati
      
    /* [m][y]
     * eventuale inserimento descrittore degli eventi
     * @param th  thread da cercare
     * @return descrittore degli eventi
     */
    private static synchronized 
      EventThread getEventThread(Thread th) 
    {
        EventThread ret;
        String name = th.getName();
          // nome del thread usato come chiave
        if ((ret = (EventThread) evMap.get(name)) == null)
        {
            // nuovo descrittore da inserire
            evMap.put(name, ret = new EventThread(th));
        } //if
        return ret;
    } // [m][y] getEventThread

    /**[m]
     * operazione di attesa di uno o più
     * degli eventi precisati dalla maschera -
     * se nessun evento e` gia` stato ricevuto,
     * il thread attende il primo che arriva 
     * tra di quelli definiti dalla maschera -
     * prima di ritornare cancella tutti gli eventi 
     * ricevuti
     * @param mask  maschera di attesa (OR)
     * @return maschera degli eventi ricevuti,
     *         0 se attesa interrotta
     */
    public static int wait(int mask) 
    {
        Thread curTh = Thread.currentThread();
        EventThread et = getEventThread(curTh);
        return et.wait(mask);
    } //[m][y] wait

    /**[m]
     * operazione di attesa di uno o più
     * degli eventi precisati dalla maschera
     * con timeout - 
     * se nessun evento e` gia` stato ricevuto,
     * il thread attende il primo che arriva in tempo
     * tra di quelli definiti dalla maschera -
     * se il timeout spira, ritorna gli eventi
     * comunque ricevuti (che saranno, in questo caso,
     * non corrispondenti a nessuno della maschera) -
     * prima di ritornare cancella tutti gli eventi 
     * ricevuti
     * @param mask  maschera di attesa (OR)
     * @param timeout  tempo di scadenza
     *                 Timeout.NOTIMEOUT attesa normale
     *                 Timeout.IMMEDIATE sincronizzazione immediata
     * @return maschera degli eventi ricevuti,
     *         0 se attesa interrotta,
     *         gli eventi arrivati se timeout
     */
    public static int wait(int mask, long timeout) 
    {
        Thread curTh = Thread.currentThread();
        EventThread et = getEventThread(curTh);
        return et.wait(mask, timeout);
    } //[m][y] wait

    /**[m]
     * operazione di attesa di uno o più
     * degli eventi precisati dalla maschera -
     * se nessun evento e` gia` stato ricevuto,
     * il thread attende il primo che arriva 
     * tra di quelli definiti dalla maschera -
     * prima di ritornare cancella tutti gli eventi 
     * catturati dalla maschera, lasciando
     * inalterati gli altri
     * @param mask  maschera di attesa (OR)
     * @return maschera degli eventi ricevuti,
     *         0 se attesa interrotta
     */
    public static int waitSingle(int mask) 
    {
        Thread curTh = Thread.currentThread();
        EventThread et = getEventThread(curTh);
        return et.waitSingle(mask);
    } //[m] waitSingle

    /**[m]
     * operazione di attesa di uno o più
     * degli eventi precisati dalla maschera
     * con timeout - 
     * se nessun evento e` gia` stato ricevuto,
     * il thread attende il primo che arriva in tempo 
     * tra di quelli definiti dalla maschera -
     * se il timeout spira, ritorna 0 e non cancella
     * altri segnali ricevuti -
     * prima di ritornare cancella tutti gli eventi 
     * catturati dalla maschera, lasciando
     * inalterati gli altri
     * @param mask  maschera di attesa (OR)
     * @param timeout  tempo di scadenza
     *                 Timeout.NOTIMEOUT attesa normale
     *                 Timeout.IMMEDIATE sincronizzazione immediata
     * @return maschera degli eventi ricevuti,
     *         0 se attesa interrotta o timeout
     */
    public static int waitSingle(int mask, long timeout) 
    {
        Thread curTh = Thread.currentThread();
        EventThread et = getEventThread(curTh);
        return et.waitSingle(mask, timeout);
    } //[m] waitSingle

    /**[m]
     * operazione di attesa di più
     * eventi precisati dalla maschera -
     * se l'insieme degli eventi della maschera 
     * non e` gia` stato ricevuto,
     * il thread attende la compresenza degli eventi -
     * prima di ritornare cancella tutti gli eventi 
     * catturati dalla maschera, lasciando
     * inalterati gli altri
     * @param mask  maschera di attesa (AND)
     * @return maschera degli eventi ricevuti,
     *         0 se attesa interrotta
     */
    public static int waitAll(int mask) 
    {
        Thread curTh = Thread.currentThread();
        EventThread et = getEventThread(curTh);
        return et.waitAll(mask);
    } //[m] waitAll

    /**[m]
     * operazione di attesa di più
     * eventi precisati dalla maschera
     * con timeout - 
     * se l'insieme degli eventi della maschera 
     * non e` gia` stato ricevuto,
     * il thread attende per il tempo fissato -
     * se il timeout spira, ritorna 0 e non cancella
     * i segnali ricevuti -
     * prima di ritornare cancella tutti gli eventi 
     * effettivamente catturati dalla maschera, lasciando
     * inalterati gli altri
     * @param mask  maschera di attesa (AND)
     * @param timeout  tempo di scadenza
     *                 Timeout.NOTIMEOUT attesa normale
     *                 Timeout.IMMEDIATE sincronizzazione immediata
     * @return maschera degli eventi ricevuti,
     *         0 se attesa interrotta o timeout
     */
    public static int waitAll(int mask, long timeout) 
    {
        Thread curTh = Thread.currentThread();
        EventThread et = getEventThread(curTh);
        return et.waitAll(mask, timeout);
    } //[m] waitAll

    
    /**[m]
     * operazione di invio di eventi ad un thread
     * @param mask  maschera degli eventi inviati
     * @param th  thread di destinazione
     */
    public static void signal(int mask, Thread th) 
    {
        EventThread et = getEventThread(th);
        et.signal(mask);
    } //[m] signal

    /**[m]
     * legge gli eventi ricevuti da un thread
     * @param th  thread degli eventi da leggere
     * @return maschera degli eventi ricevuti
     */
    public static int getEvents(Thread th) 
    {
        return getEventThread(th).events;
          // la sincronizzazione non serve per la lettura
    } //[m] getEvents

    /**[m]
     * legge e cancella gli eventi ricevuti da un thread
     * @param th  thread degli eventi da cancellare
     * @return maschera degli eventi ricevuti
     */
    public static int clearEvents(Thread th) 
    {
        EventThread et = getEventThread(th);
        return et.clearEvents();
    } //[m] clearEvents

} //{c} Events
