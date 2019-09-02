package os.ada;

/**{c}
 * classe di supporto per esempio
 * ADA con classe che non estende ADAThread 
 * 
 * @author M.Moro DEI UNIPD
 * @version 1.00 2010-12-19
 */

class IncDec
{
    private int val = 0;
    
    /**[m]
     * incremento
     * @return valore incrementato 
     */
    protected int inc() { return ++val; }
    
    /**[m]
     * decremento
     * @return valore decrementato 
     */
    protected int dec() { return --val; }
    
    /**[m]
     * valore
     * @return valore 
     */
    protected int val() { return val; }

} //{c} IncDec

