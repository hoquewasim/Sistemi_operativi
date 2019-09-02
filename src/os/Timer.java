package os;

/**{c}
 * software timer 
 * @author M.Moro DEI UNIPD
 * @version 1.00 2003-10-02
 * @version 2.00 2005-10-07 package os
 * @version 2.01 2014-05-02 la call può spegnere il timer
 */
 
public class Timer extends Thread
{
    // stati del timer
    public static final int NOTSTARTED = 0;
    public static final int RUNNING = 1;
    public static final int STOPPED = 2;
    public static final int EXPIRED = 3;
    public static final int RESTARTED = 4;
    public static final int RETRIGGERED = 5;
    public static final int INVALID = 6;
    private static final String statusStr[] = {
      "NOTSTARTED", "RUNNING", "STOPPED",
      "EXPIRED", "RESTARTED", "RETRIGGERED",
      "INVALID"};

    
    private static int cnt = 1;
      // id del timer
    
    private TimerCallback callback;
      // metodo di callback associato al timer

    /** periodo del timer
     */
    protected long period;

    /** tempo d'attesa
     */
    protected long delay;
      // periodo di attesa
    private long exp;
      // istante di scadenza
    private int status;
      // satto del timer
      
    /**[c]
     * definisce il timer associando il metodo
     * di callback
     * @param cb  callback
     * @param tm  periodo iniziale
     */
    public Timer(TimerCallback cb, long tm)
    {
        super("Timer"+(cnt++));
        callback = cb;
        period = tm;
        delay = 0;
        status = NOTSTARTED;
    } //[c]
    
    /**[m][y]
     * avvio iniziale del timer
     */
    public synchronized void start()
    {
        if (status == NOTSTARTED)
            // avvio
            super.start();
        else if (status == STOPPED)
        {
            // riavvio
            status = RESTARTED;
            notify();
        }
    } //[m][y] start 

    /**[m][y]
     * sospensione del timer se running
     */
    public synchronized void freeze()
    {
        if (status == RUNNING)
        {
            // sospensione
            status = STOPPED;
            notify();
        }
    } //[m][y] stop

    /**[m][y]
     * reset e riavvio 
     */
    public synchronized void retrigger()
    {
        status = RETRIGGERED;
        notify();
    } //[m][y] retrigger

    /**[m][y]
     * reimposta periodo
     * @param tm  nuovo periodo del timer
     */
    public synchronized void setPeriod(long tm)
    { 
        period = tm;
        // il timer rimane running se lo e`
    }

    /**[m][y]
     * sospende e distrugge il timer 
     */
    public synchronized void cancel()
    {
        status = INVALID;
        notify();
    } //[m][y] cancel

    /**[m]
     * restituisce il periodo corrente
     * @return periodo
     */
    public long getPeriod()
    {  return period; }

    /**[m]
     * restituisce il tempo che manca alla scadenza
     * @return tempo mancante
     */
    public long getVal()
    {  
        return (status == RUNNING) ? 
          (exp-System.currentTimeMillis()) : 
          delay; 
    }

    /**[m]
     * restituisce lo stato
     * @return stato
     */
    public int getStatus()
    {  return status; }

    /**[m]
     * restituisce lo stato in forma di stringa
     * @return stato
     */
    public String getStatusStr()
    {  return statusStr[status]; }

    /**[m]
     * Conversione a stringa -
     * fornisce lo stato del timer
     * @return la stringa
     */
    public String toString()
    {
        return "Timer "+getName()+" status="+
          getStatusStr()+ " period="+period+
          " delay="+delay+" value="+getVal()+
          " callback=["+callback+"]";
    } //[m] toString
    
      /**[m]
     * imposta i valori del timer -
     * viene ridefinito per timer specializzati
     */
    protected void setTimer()
    {
        delay = period;
    }

    /**[m]
     * il thread timer vero e proprio
     */
    public synchronized void run()
    { 
        while(true)
        {
            switch(status) 
            {
              case NOTSTARTED:  // init
              case RETRIGGERED: // riavviato
                setTimer();
                if (period == 0)
                {
                    // == spento
                    status = EXPIRED;
                    break;
                }
                exp = System.currentTimeMillis()+period;
                status = RUNNING;
                try { wait(delay); }
                catch (InterruptedException e) {}
                break;
              case RUNNING: // scaduto
                status = EXPIRED;
                if (callback != null)
                    // 2.01 ora la callback può spegnere il timer
                    if (callback.call())
                      // attiva callback che puo` eseguire un retrigger
                        status = INVALID;
                delay = 0;
                break;
              case STOPPED: 
                // sospeso, conserva tempo mancante ed attende
                delay = exp - System.currentTimeMillis();
              case EXPIRED:
                try { wait(); }
                catch (InterruptedException e) {}
                break;
              case RESTARTED: // riattivato dopo stop
                exp = System.currentTimeMillis()+delay;
                status = RUNNING;
                try { wait(delay); }
                catch (InterruptedException e) {}
                break;
              default:
                status = INVALID;
              case INVALID:  // termina 
                return;
            } // switch
        } // while
    } //[m] run

} //{c} Timer

