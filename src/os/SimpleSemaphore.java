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
 
public class SimpleSemaphore
{
    /** valore del semaforo
     */
    protected int value;
    /** numero thread in attesa
     */
    protected int waiting = 0;
    private int maxvalue;
      // valore massimo (di inizializzazione)
      
    /**[c]
     * costruttore base per un semaforo numerico 
     * @param v  valore iniziale del semaforo
     * @param mv  valore massimo del semaforo
     * @throws InvalidValueException  se mv &lt; 1,
     * v negativo, o v &gt; mv
     */
    public SimpleSemaphore(int v, int mv) 
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
    public SimpleSemaphore(int v) 
    {  this(v, v); }

    /**[c]
     * costruttore per semaforo binario
     * @param b  valore iniziale del semaforo binario
     */
    public SimpleSemaphore(boolean b) 
    { this(b ? 1 : 0, 1); }

    /**[m][y]
     * operazione p (wait)
     */
    public synchronized void p() 
    {
        if (value <=0) 
        {
            try 
            { 
                waiting++;
                wait();  // attende
                // waiting--;
                // lo deve fare v()
                // per non rischiare che n v()
                // successivi, con n>waiting 
                // facciano perdere l'incremento 
                // del semaforo
            }
            catch(InterruptedException e)
            {
                waiting--;
            }
        }
        else
            value--;
    } //[m] p
    
    /**[m][y]
     * operazione v (signal)
     */
    public synchronized void v() {
        if (value == maxvalue)
            // NOP
            return;
        if(value == 0 && waiting != 0)
        {
            waiting--;
            notify(); 
        }
        else
            value++;
        //!! L’ordine di risveglio e’ arbitrario
    } //[m] v

    /**[m]
     * valore del semaforo
     * @return il valore del semaforo (&ge;0)
     */
    public int value() 
    { return value; }
    
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
        return "SimpleSemaphore value="+value()+
          " maxvalue="+maxvalue+" waiting="+
          queue();
    }
    
} //{c} SimpleSemaphore
