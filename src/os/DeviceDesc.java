package os;

import java.util.*;

/**{c}
 * classe di supporto -
 * descrittore di un dispositivo
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2004-11-11
 * @version 2.00 2005-10-07 package os
 * @version 2.01 2005-11-18 aggiunto DMA_CLASS, qualche elemento public
 * @version 2.02 2010-03-03 usati generics
 */
 
public class DeviceDesc
{
    // stati dei dispositivi
    final static int UNAVAILABLE = 0;
    final static int FREE = 1;      // non occupato
    final static int COMMON = 2;    // comune a piu' thread
    final static int LOCKED = 3;    // occupato da un thread

    // modi dei dispositivi
    public final static int INPUT = 0;
    public final static int OUTPUT = 1;
      
    // classi (tipi) dei dispositivi
    public final static int SERIALLINE_CLASS = 0;
    public final static int ADC_CLASS = 1;
    public final static int DMA_CLASS = 2;

    // codici d'errore
    public final static int NOERR = 0;         // nessun errore
    public final static int GENERICERR = 1;    // errore generico
        
    String id;      // identificatore del dispositivo
    public int mode;       // modo di operazione
    int devClass;   // tipo del dispositivo (CDROM, floppy. ecc)
    DeviceType type;// info specifiche
    public Device dev;     // dispositivo 'fisico'
    public List<IORB> iorbQueue = new LinkedList<IORB>();  // coda richieste
    public MutexSem prot = new MutexSem();     // protezione mutex
    public Semaphore ar = new Semaphore(0, Integer.MAX_VALUE);    // semaforo (numerico) richiesta attiva
    public Semaphore co = new Semaphore(false);// semaforo operazione completa
    

    // informazioni correnti
    int status = FREE;  // stato del dispositivo
    int numTh = 0;      // numero thread che hanno aperto
    Thread th;          // thread che lo usa attualmente se unico
    
    /**[c]
     * costruttore base 
     * @param n  id
     * @param m  modo
     * @param dc tipo 
     * @param y  info
     * @param d  device 'fisico'
     */
    public DeviceDesc(String n, int m, int dc, DeviceType y, Device d)
    {
        id = n;
        mode = m;
        devClass = dc;
        type = y;
        dev = d;
    } //[c]
    
    /**[m]
     * stringa descrittiva
     * @return descrizione
     */
    public String toString()
    {
        return "DeviceDesc "+id+" ("+dev+")";
    } //[m] toString

} //{c} DeviceDesc
