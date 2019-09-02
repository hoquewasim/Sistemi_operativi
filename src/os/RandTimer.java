package os;

/**{c}
 * software timer con tempo casuale
 * @author M.Moro DEI UNIPD
 * @version 1.00 2003-10-04
 * @version 2.00 2005-10-07 package os
 */
 
public class RandTimer extends Timer
{
    
    private long minPeriod;
      // periodo minimo del timer
    private long maxPeriod;
      // periodo minimo del timer
      
    /**[c]
     * definisce il timer associando il metodo
     * di callback e i due periodi limite
     * @param cb  callback
     * @param minP  periodo minimo
     * @param maxP  periodo massimo
     */
    public RandTimer(TimerCallback cb, long minP, long maxP)
    {
        super(cb, 0L);
        minPeriod = minP;
        maxPeriod = maxP;
    } //[c]
    
    /**[m]
     * imposta i valori del timer -
     * ridefinisce quello base per
     * periodo casuale
     */
    protected void setTimer()
    {
        period = Util.randVal(minPeriod, maxPeriod);
        delay = period;
    }

    /**[m]
     * Conversione a stringa -
     * fornisce lo stato del timer
     * @return la stringa
     */
    public String toString()
    {
        return "Random "+super.toString()+
          " minPeriod="+minPeriod+
          " maxPeriod="+maxPeriod;
    } //[m] toString

} //{c} RandTimer

