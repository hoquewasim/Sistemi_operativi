package os.ada;

import os.Timeout;

/**{c}
 * fornisce i parametri d'uscita 
 * d'interesse anche per le applicazioni
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2010-12-12
 */

public class CallOut
{
    // queste informazioni sono utili al server
    Thread th;
      // caller thread
    int idx;
      // caller's call index (1..n)
    long timeout;
      // reply timeout (residuo, EXPIRED o INTIME)
      
    /**
     * parametri d'uscita effettivi impacchettati
     */
    Object params;
      // parametri d'uscita effettivi
      
    /**[c]
     * parametri d'uscita
     * @param th  caller's thread
     * @param idx  eguale a quello di CallIn
     * @param tm  timeout
     * @param pms  parametri d'input della chiamata
     */
    CallOut(Thread th, int idx, long tm, Object pms)
    {
        this.th=th;
        this.idx=idx;
        timeout=tm;
        params = pms;
    } //[c]
    
    /**[c]
     * parametro d'uscita nullo
     * @param th  caller's thread
     * @param idx  eguale a quello di CallIn
     * @param tm  timeout
     */
    CallOut(Thread th, int idx, long tm)
    {
        this(th, idx, tm, null);
    } //[c]
    
    /**[c]
     * parametro d'uscita con timeout non specificato
     * @param th  caller's thread
     * @param idx  eguale a quello di CallIn
     * @param pms  parametri d'input della chiamata
     */
    CallOut(Thread th, int idx, Object pms)
    {
        this(th, idx, Timeout.INTIME, pms);
    } //[c]
    
    /**[m]
     * il timeout
     * @return timeout
     */
    public long getTimeout()
    {
        return timeout;
    } //[m] getTimeout
    
    /**[m]
     * i paramtri d'uscita
     * @return oggetto d'uscita
     */
    public Object getParams()
    {
        return params;
    } //[m] getParams
    
} //{i} CallOut
      
