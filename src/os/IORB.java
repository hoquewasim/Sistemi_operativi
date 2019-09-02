package os;

/**{c}
 * classe di supporto -
 * memorizza un descrittore per un'operazione
 * di I/O (I/O Request Block)
 * 
 * @author M.Moro DEI UNIPD
 * @version 1.00 2003-10-18
 * @version 2.00 2005-10-07 package os
 * @version 2.01 2005-11-18 public
 */
public class IORB
{
    public byte data[];    // posizione dei dati
    public int qty;        // quantita' in byte
    int ioMode;     // modo
    Thread id;      // thread richiedente
    public Semaphore sr;   // semaforo 'richiesta servita'
    public IOErr err;      // gestione errore
    
    /**[c]
     * costruttore base 
     * @param d  dati
     * @param q  quantita
     * @param m  I/O mode
     * @param t  thread
     * @param s  semaforo
     * @param e  gestione errore
     */
    public IORB(byte d[], int q, int m, Thread t, 
      Semaphore s, IOErr e)
    {
        data=d;
        qty = q;
        ioMode = m;
        id = t;
        sr = s;
        err = e;
    } //[c]

} //{c} IORB
