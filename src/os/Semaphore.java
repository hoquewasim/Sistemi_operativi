package os;

import java.util.Vector;
 
/**{c}
 * Semaforo numerico, include il caso binario -
 * la coda associata al semaforo puo` essere FIFO
 * o LIFO
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-05-04
 * @version 2.00 2003-10-01 package Os
 * @version 2.01 2005-10-07 package os
 * @version 2.02 2008-10-14 utilizzati generics in Vector
 */
 
public class Semaphore
{

    /** valore del semaforo &ge; 0
     */
    protected int value;

    /** thread in attesa &ge; 0
     */
    protected int waitNum = 0;

    /** valore massimo (di inizializzazione)
     */
    protected int maxvalue;

    /** lista FIFO dei thread in attesa
     */
//v2.02 old protected Vector waiting = new Vector();
	 protected Vector<WaitingThread> waiting = new Vector<WaitingThread>();
	 
	 
    /** true se gestione della coda come FIFO,
     *  altrimenti LIFO
     */
    protected boolean isFifo;

    /** il possessore se privato
     */
    protected Thread owner = null;
      
    /**[c]
     * costruttore base per un semaforo numerico 
     * @param v  valore iniziale del semaforo
     * @param mv  valore massimo del semaforo
     * @param fifo  true se FIFO, altrimenti LIFO
     * @throws InvalidValueException  se mv &lt; 1,
     * v negativo, o v &gt; mv
     */
    public Semaphore(int v, int mv, boolean fifo) 
    {
        if (mv < 1)
            throw new InvalidValueException("Valore massimo del semaforo "+mv);
        if (v < 0 || v > mv)
            throw new InvalidValueException("Valore iniziale del semaforo "+v);
        value = v;
        maxvalue = mv; 
        isFifo = fifo;
    } //[c]

    /**[c]
     * costruttore base per un semaforo numerico FIFO
     * @param v  valore iniziale del semaforo
     * @param mv  valore massimo del semaforo
     */
    public Semaphore(int v, int mv) 
    {  this(v, mv, true); }

    /**[c]
     * costruttore per un semaforo numerico FIFO
     * @param v  valore iniziale del semaforo
     * @throws InvalidValueException  se v negativo
     */
    public Semaphore(int v) 
    {  this(v, v); }

    /**[c]
     * costruttore per semaforo binario
     * @param b  valore iniziale del semaforo binario
     */
    public Semaphore(boolean b) 
    { this(b ? 1 : 0, 1); }

    /**[c]
     * costruttore per semaforo binario PRIVATO
     */
    public Semaphore() 
    {
    	this(false); 
    	  // inizializzato a rosso
    	owner = Thread.currentThread();
    }

    /**[m][y]
     * operazione p (wait)
     */
    public synchronized void p() 
    {
        Thread curTh = Thread.currentThread();
    	if (owner != null && owner != curTh)
    	    // esegue p() un thread che non e` l'owner
    	    throw new InvalidThreadException
    	      ("Non possiede il semaforo: "+curTh.getName()+
               " che e` di:"+owner.getName());
        if(value==0)
        {
            // deve attendere nella coda
            waitNum++;
            if (isFifo)
			      waiting.add(new WaitingThread(curTh, false)); 
               // aggiunta in coda (FIFO)
            else
					waiting.add(0,new WaitingThread(curTh, false)); 
               // aggiunta in testa (LIFO)
            while(true)
            {
                try{ wait(); }
                catch( InterruptedException e ){}
                // se si deve risvegliare questo thread
                // di norma e` il primo ma nel caso
                // di coda LIFO può 'intrufolarsi'
                // in testa un nuovo thread che si sospende,
                // quindi bisogna cercarlo in un ciclo
                for (int i=0; i<waiting.size(); i++)
                {
//v2.02 old   	 WaitingThread wt = WaitingThread)waiting.elementAt(i);
                    WaitingThread wt = waiting.elementAt(i);
                    if (wt.th == curTh)
                        // trovato
                        if (wt.wakenUp)
                        {
                            // e` proprio da risvegliare
                           // waiting.remove(i);
									 waiting.remove(i);
// lo fa v()!!!!                            waitNum--;
                            // il valore del semaforo e`
                            // gia` a posto
                            return;
                        }
                        else 
                            // non era da risvegliare
                            break; // for
                    else ;
                } // for
            } // while
        } // if (value==0)
        else
            value--;
    } //[m] p()
    
    /**[m][y]
     * operazione v (signal)
     */
    public synchronized void v() 
    {
        if (value == maxvalue)
            // NOP se raggiunto il valore massimo
            return;
        if (waitNum>0)
        {                           
            for (int i=0; i<waiting.size(); i++)
            {
//v2.02 old     WaitingThread wt = (WaitingThread)waiting.elementAt(i);
                WaitingThread wt = waiting.elementAt(i);

                if (!wt.wakenUp)
                {
                    // trovato il primo da risvegliare
                    wt.wakenUp=true;
                    waitNum--;
                    break; // for
                } // if
            }
            notifyAll();
        }
        else
            value++;
    } //[m] v

    /**[m][y]
     * operazione p (wait) con timeout
     * @param timeout  massimo tempo di attesa in ms
     *                 NOTIMEOUT senza timeout
     *                 IMMEDIATE se sincronizzazione immediata richiesta
     * @return tempo rimasto rispetto al timeout -
     *         timeout se non sospensivo o INTIME -
     *         INTIME&lt;ret&le;timeout se v() arriva in tempo
     *         EXPIRED se spirato
     */
    public synchronized long p(long timeout) 
    {
        if (timeout == Timeout.NOTIMEOUT)
            // ricondotta a normale p()
        {
            p();
            return Timeout.INTIME;
        }
        Thread curTh = Thread.currentThread();
        if (owner != null && owner != curTh)
            // esegue p() un thread che non e` l'owner
            throw new InvalidThreadException
              ("Non possiede il semaforo: "+curTh.getName()+
               " che e` di:"+owner.getName());
        if(value==0)
        {
            if (timeout == Timeout.IMMEDIATE)
                // sincronizzazione immediata non possibile
                return Timeout.EXPIRED;
            // deve attendere nella coda
            long exp = System.currentTimeMillis()+timeout;
              // istante di scadenza
            waitNum++;
            if (isFifo)
                waiting.addElement
                  ( new WaitingThread(curTh, false));

            else
                waiting.add
             (0, new WaitingThread(curTh, false));
            long diffTime = timeout;  
              // tempo effettivo di attesa
            while(true)
            {
                if (diffTime > 0)
                    try{ wait(diffTime); }
                    catch( InterruptedException e ){}
                diffTime = exp-System.currentTimeMillis();

                // se si deve risvegliare questo thread per signal,
                // di norma e` il primo ma nel caso
                // di coda LIFO può 'intrufolarsi'
                // in testa un nuovo thread che si sospende,
                // quindi bisogna cercarlo in un ciclo
              //for (int i=0; i<waiting.size(); i++)
					 for (int i=0; i<waiting.size(); i++)

                {
//v2.02 old    WaitingThread wt = (WaitingThread)waiting.elementAt(i);
                    WaitingThread wt = waiting.elementAt(i);
                    if (wt.th == curTh)
                    {
                        // trovato
                        if (wt.wakenUp)
                        {
                            // risveglio per signal
                            waiting.remove(i);
// lo fa v()                            waitNum--;
                            // il valore del semaforo e`
                            // gia` a posto
                            return diffTime > 0L ? diffTime : 
                              Timeout.INTIME;
                             // comunque >0
                        }
                        else if (diffTime <= 0)
                        {
                            // risveglio per timeout
                            waiting.remove(i);
                            waitNum--;
                            // il valore del semaforo e`
                            // gia` a posto
                            return Timeout.EXPIRED;
                              // timeout spirato
                        }
                        else
                            // non era da risvegliare
                            break; // for
                    }
                    else ;
                } // for
            } // while
        } // if (value==0)
        else
            value--;
        return timeout;
          // p non sospensivo
    } //[m] p(long timeout)
    
    /**[m]
     * valore del semaforo
     * @return il valore del semaforo (&ge;0)
     */
    public int value() 
    {
        return value;
    }
    
    /**[m]
     * numero dei thread in attesa
     * @return totale thread in attesa
     */
    public int queue() 
    {
        return waitNum;
    }
    
    /**[m][y]
     * thread in attesa
     * @param pos  posizione nel FIFO (0..waiting.size()-1)
     * @return riferimento al thread nel FIFO se pos legale
     *         null altrimenti
     */
    public synchronized Thread waitingThread(int pos) 
    {
        if (pos >= waiting.size())
            return null;
//v2.02 old         return ((WaitingThread)waiting.elementAt(pos)).th;
        return (waiting.elementAt(pos)).th;		  
    }
    
    /**[m]
     * possessore del semaforo
     * @return nome del possessore
     */
    public String ownedBy() 
    {
        return owner == null ? "nessuno" : 
          owner.getName();
    }
    
    /**[m]
     * Conversione a stringa
     * @return la stringa
     */
    public String toString()
    {
        return "Semaphore value="+value()+
          " maxvalue="+maxvalue+" queue="+
          queue()+" FIFO="+isFifo+" owner=["+
          ((owner==null) ? "NOOWNER" : owner.getName())+"]";
    }
    
} //{c} Semaphore

