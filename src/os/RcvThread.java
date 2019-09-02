package os;

/**{c}
 * classe di supporto -
 * memorizza un descrittore di thread ricevitore
 * per l'esempio sned-receive
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-05-05
 * @version 2.00 2003-10-03 package Os
 * @version 2.01 2005-10-07 package os
 */
class RcvThread
{
    Thread th;  // thread id
    Semaphore priv;  // semaforo privato
    Semaphore mutex;  // mutex
    FifoSBuf msgs;  // fifo dei messaggi
    boolean waiting;  // se in attesa di ricevere msg
    String sender;  // il nome del mittente
    Object msg; // messaggio diretto
    TASys sys;  // canali di I/O
    
    /**[c]
     * costruttore base 
     * @param t  thread id
     * @param p  semaforo privato
     * @param m  mutex
     * @param b  buffer messaggi ricevuti
     * @param s  sys
     */
    RcvThread(Thread t, Semaphore p, Semaphore m,
      FifoSBuf b, TASys s)
    {
        th=t;
        priv = p;
        mutex = m;
        msgs = b;
        waiting = false;
        sys = s;
    } //[c]

} //{c} RcvThread
