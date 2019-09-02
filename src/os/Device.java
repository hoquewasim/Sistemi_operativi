package os;

/**{c}{a}
 * rappresenta la parte comune 
 * per un'interfaccia hardware di
 * un dispositivo
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2004-11-12
 * @version 2.00 2005-10-07 package os
 * @version 2.01 2005-11-18 alcuni elemnti public
 * @version 2.02 2012-05-09 terminazione aggiunta
 */

public abstract class Device
{
    // stati del dispositivo
    public final static int IDLE = 0;
    public final static int READY = 1;
    public final static int BUSY = 2;
      
    protected int status = IDLE;
      // stato del dispositivo 'fisico'
    protected int errcode = DeviceDesc.NOERR;
      // codice d'errore
    protected boolean intMask = false;
      // interrupt mask
    protected DeviceHandler dh = null;
      // il DH associato
/* 2012-05-09: M.Moro terminazione aggiunta */
    protected boolean toBeStopped = false;  

    /**[m]
     * abilita il dispositivo ad interrompere
     */
    public void enable()
    { intMask = true; }

    /**[m][a]
     * disabilita il dispositivo ad interrompere
     */
    public void disable()
    { intMask = false; }

    /**[m]
     * collegamento al device handler associato
     * @param d  DH associato
     */
    public void install(DeviceHandler d)
    {  dh = d; }
    
    /**[m]
     * il device handler installato
     * @return l'istanza specifica di DH
     */
    public DeviceHandler handler()
    {  return dh; }
    
    /**[m]
     * per tornare il codice d'errore
     * @return codice d'errore
     */
    public int error()
    {  return errcode; }
    
    /**[m]
     * accensione del dispositivo
     */
    public abstract void init();
    
    /**[m]
     * clear del dispositivo
     */
    public void clear()
    {  status=IDLE; }
    
    /**[m]
     * terminazione del dispositivo
     */
    public void toStop()
    { toBeStopped = true; }
    
    /**[m][a]
     * stringa descrittiva
     * @return stringa
     */
    public abstract String toString();

} //{c}{a} Device
