package os;

/**{c}
 * Cassetta postale
 * come SimpleMonitor
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-05-04
 * @version 2.00 2003-10-03 package Os
 * @version 2.01 2005-10-07 package os
 */
 
public class CassettaPostale extends SimpleMonitor
{
    private Object contenitore;
      // la cassetta
    private boolean messaggioDisponibile = false;
    private boolean contenitoreDisponibile = true;
      // flag di stato    
    private Condition scrittura = new Condition();
      // attesa per contenitore
    private Condition lettura = new Condition();
      // attesa per messaggio
      
    /**[m]
     * deposito di un messaggio nella casella -
     * NON effettua una copia del messaggio
     * @param lettera  il messaggio
     */
    public synchronized void deposita(Object lettera)
    {
        if (!contenitoreDisponibile)
            scrittura.cWait();
        contenitoreDisponibile = false;
        messaggioDisponibile = true; // vedi errata corrige
        contenitore = lettera;
        lettura.cSignal();
    } //[m] deposita
    
    /**[m]
     * prelievo di un messaggio dalla casella
     * @return il messaggio prelevato
     */
    public synchronized Object preleva()
    {
        if (!messaggioDisponibile)
            lettura.cWait();
        messaggioDisponibile = false;
        contenitoreDisponibile = true; // vedi errata corrige
        Object ret = contenitore;
        scrittura.cSignal();
        return ret;
    } //[m] preleva
    
} //{c} CassettaPostale

