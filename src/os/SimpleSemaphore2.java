package os;

/**{c}
 * semplice semaforo numerico, 
 * include il caso binario
 * non garantisce il FIFO di attesa
 * dei thread sospesi
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-05-07
 * @version 2.00 2003-09-30 package Os
 * @version 2.01 2005-10-07 package os
*/
public class SimpleSemaphore2
{
    /** valore del semaforo
     */
    protected int value;
    private int maxvalue;
      // valore massimo (di inizializzazione)
    private int waiting = 0;
      // solo per documentazione, vedi toString
      
    /**[c]
     * costruttore base per un semaforo numerico 
     * @param v  valore iniziale del semaforo
     * @param mv  valore massimo del semaforo
     * @throws InvalidValueException  se mv &lt; 1,
     * v negativo, o v &gt; mv
     */
    public SimpleSemaphore2(int v, int mv) 
    {
        if (mv < 1)
            throw new InvalidValueException("Valore massimo del semaforo"+mv);
        if (v < 0 || v > mv)
            throw new InvalidValueException("Valore iniziale del semaforo"+v);
        value = v;
        maxvalue = mv; 
    } //[c]

    /**[c]
     * costruttore per un semaforo numerico 
     * @param v  valore iniziale del semaforo
     * @throws InvalidValueException  se v negativo
     */
    public SimpleSemaphore2(int v) 
    {  this(v, v); }

    /**[c]
     * costruttore per semaforo binario
     * @param b  valore iniziale del semaforo binario
     */
    public SimpleSemaphore2(boolean b) 
    { this(b ? 1 : 0, 1); }

    /**[m][y]
     * operazione p (wait)
     */
    public synchronized void p() 
    {
        while (value <=0) {
          try 
          { 
              waiting++;
              wait(); 
              waiting--;
          } // attende
          catch(InterruptedException e){}
        }
        value--;
    } //[m] p
    
    /**[m][y]
     * operazione v (signal)
     */
    public synchronized void v() {
        if (value == maxvalue)
            // NOP
            return;
        ++value;
        notify(); 
        //!! L’ordine di risveglio e’ arbitrario
    } //[m] v
    
    /**[m]
     * valore del semaforo
     * @return il valore del semaforo (&ge;0)
     */
    public int value() 
    {
        return value;
    }
    
    /**[m]
     * numero dei processi in attesa
     * @return processi in attesa
     */
    public int queue() 
    { return waiting; }
    
    /**[m]
     * Conversione a stringa
     * @return la stringa
     */
    public String toString()
    {
        return "SimpleSemaphore2 value="+value()+
          " maxvalue="+maxvalue+" waiting="+
          queue();
    }
    
} //{c} SimpleSemaphore2
