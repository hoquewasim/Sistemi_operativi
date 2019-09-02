package os;

/**{c}
 * Monitor in forma semplificata
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-05-08
 * @version 2.00 2003-10-03 package Os
 * @version 2.01 2005-10-07 package os
 */

public class SimpleMonitor
{
    /**{c}
     * condition di Monitor
     * @author M.Moro DEI UNIPD
     * @version 1.0 2002-04-17
     */
    protected class Condition
    {
        private int signalled = 0;
          // per distinguere i thread che
          // devono risvegliarsi, essendo relativi
          // a questa istanza di Condition
        private int waiting = 0;
          // per garantire che del segnale 
          // non rimanga memoria.
          // Si ricordi che, quando un thread esce dall'attesa
          // (wait) ricompete con tutti per il lock,
          // quindi potrebbe entrare un thread
          // che non era in attesa e che fa cWait
          // che deve essere sospensiva per quel thread

        /**[m]
         * costruttore pubblico
         */
        public Condition()
        {}        
          
        /**[m]
         * wait sul condition
         */
        public void cWait()
        {
            waiting++;
            do
                // si sospende comunque
                try { SimpleMonitor.this.wait(); }
                  // wait sul lock dell'oggetto SimpleMonitor
                  // associato a questo condition
                catch(InterruptedException e) {}
            while(signalled == 0);
              // ricicla se non e` da risvegliare
            // da risvegliare
            signalled--;
            // waiting--;
            // eseguito in cSignal
        } //[m] cWait

        /**[m]
         * signal sul condition
         */
        public void cSignal()
        {
            if (waiting != 0)
            {
                // c'e' qualche thread in attesa
                // su questo condition
                signalled++;
                waiting--;
                  // in modo da tener conto
                  // del numero di signal effettivamente
                  // eseguiti con processi da risvegliare
                SimpleMonitor.this.notifyAll();
            }
        
        } //[m] cSignal

        /**[m]
         * thread in attesa
         * @return numero di thread in attesa
         */
        public int queue()
        {
            return waiting;
        }
        
    } //{c} Condition

} //{c} SimpleMonitor
