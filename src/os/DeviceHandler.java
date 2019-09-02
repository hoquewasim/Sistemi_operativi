package os;

/**{c}{a}
 * Device handler generico
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2004-11-11
 * @version 2.00 2005-10-07 package os
 * @version 2.01 2005-11-18 public class 
 * @version 2.02 2012-05-09 terminazione aggiunta
 */

public abstract class DeviceHandler extends Thread
{
    protected DeviceDesc dDesc;   // descrittore del device associato
    protected byte[] data;        // riferimento ad array destinazione
    protected int toBeMoved;      // byte ancora da trasferire
    protected int pos=0;          // posizione di lettura
    protected int errcode = DeviceDesc.NOERR;  // codice d'errore
    protected boolean toBeStopped = false;  // per uccidere lo handler
    
    /**[m]
     * terminazione del dispositivo
     */
    public void toStop()
    { 
        toBeStopped = true; 
        dDesc.ar.v();
    }
    
    /**[m][a]
     * routine di servizio
     */
    public abstract void isr();

} //{i} DeviceHandler
