package os;

/**{c}
 * Monitor di Hoare
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-04-17
 * @version 2.00 2003-10-03 package Os
 * @version 2.01 2003-11-22 Condition public
 * @version 2.02 2004-11-09 timeout con timer
 * @version 2.03 2005-10-07 package os
 * @version 2.04 2005-10-28 aggiunto Condition.queue()
 * @version 2.05 2014-05-02 gestione corretta del timeout su condition
 * @version 2.06 2014-05-06 nesting mEnter correttamente implementato
 * @version 2.07 2019-05-25 condizione di corsa su nesting del mutex
 */

public class Monitor
{
    private MutexSem mutex = new MutexSem();
    private Semaphore urgent = new CountSem(0, false);
      // con coda LIFO, rimane sempre a 0
    private int urgentCount=0;
      // processi sul semaforo urgent
    private long elapsed = Timeout.INTIME;
      // tempo rimasto dal timeout:
      // la semantica del Monitor di Hoare
      // garantisce che questa variabile
      // venga scritta dal timer e subito dopo letta
      // dal thread che si risveglia
    //2.05
    private Thread expired = null;
      // Thread (in mutex) con timeout spirato
      
    /**{c}
     * condition di Monitor
     * @author M.Moro DEI UNIPD
     * @version 1.0 2002-04-17
     */
    public class Condition
    {
       private Semaphore cond = new Semaphore(0,1);
         // semaforo binario d'attesa, sempre rosso
       private int condCount=0;
         // conteggio thread in attesa

        /**[m]
         * costruttore pubblico
         */
        public Condition()
        {
        }        
          
        /**[m]
         * wait sul condition
         */
        public void cWait()
        {
            // 2.06 recupero livello nesting
            int recoverLevel= mutex.getVal()-1;
              // livello da recuperare
            condCount++;
            if( urgentCount>0 )
            {
                // 2.06 cede mutex
                // ritorna a livello 1 (uno)
                for (int i=1; i<=recoverLevel; i++)
                    mutex.v();
                urgent.v();
            }
            else
            {
                // 2.06 esce mutex
                // ritorna a livello 0 (zero)
                for (int i=1; i<=recoverLevel+1; i++)
                    mutex.v();
            }
            cond.p();
            // 2.05 se timeout spirato, non è questo il thread da risvegliare
            while (elapsed==Timeout.EXPIRED)
            {
                cond.v(); // risveglia un altro
                cond.p();
            }
            // 2.06 recupero livello nesting
            mutex.setOwner();
            // ripristina livello
            for (int i=1; i<=recoverLevel; i++)
                mutex.p();
            condCount--;
        } //[m] cWait

        /**[m]
         * wait sul condition con timeout
         * @param timeout  massimo tempo di attesa in ms
         *                 NOTIMEOUT senza timeout
         *                 IMMEDIATE se sincronizzazione immediata richiesta
         * @return tempo rimasto rispetto al timeout -
         *         timeout se non sospensivo o INTIME -
         *         INTIME&lt;ret&le;timeout se v() arriva in tempo
         *         EXPIRED se spirato
        */
        public long cWait(long timeout)
        {
            elapsed = timeout;  // ultimo timeout, comunque != EXPIRED
            // 2.05 thread con timeout
            final Thread me = Thread.currentThread();
            
            Timer tm = new Timer(new TimerCallback()
            {
                // classe anonima di callback del timer
                /**[m]
                 * @see TimerCallback#call
                 */
                public boolean call()
                {
                    // timeout scaduto
                    mEnter();
                    elapsed = Timeout.EXPIRED;
                    // 2.05 thread con timeout scaduto
                    expired = me;
                    cSignal();
                    elapsed = Timeout.INTIME;
                    mExit();
                    // 2.05 termina il timer
                    return true;
                } //[m] call

                /**[m]
                 * @see TimerCallback#toString
                 */
                public String toString()
                { return "Monitor timeout Timer"; }
            }, timeout);
            
            tm.start();  // attiva il timer
            // 2.06 recupero livello nesting
            int recoverLevel= mutex.getVal()-1;
              // livello da recuperare
            // 2.05 gestione corretta del timeout
            // attesa sul Condition
            condCount++;
            if( urgentCount>0 )
            {
                // 2.06 cede mutex
                // ritorna a livello 1
                for (int i=1; i<=recoverLevel; i++)
                    mutex.v();
                urgent.v();
            }
            else
            {
                // 2.06 esce mutex
                // ritorna a livello 0
                for (int i=1; i<=recoverLevel+1; i++)
                    mutex.v();
            }


            while(true) {
                cond.p();
                // al risveglio potrebbe essere pervenuto
                // un signal da un thread normale o dal timer
                // se timeout spirato
                // caso signal normale
                if (elapsed != Timeout.EXPIRED)
                {
                    // non scaduto, signal normale
                    elapsed=tm.getVal();  // tempo rimasto
                    tm.cancel();  // disattiva timer
                    // 2.06 recupero livello nesting
                    mutex.setOwner();
                    // ripristina livello
                    for (int i=1; i<=recoverLevel; i++)
                        mutex.p();
                    condCount--;
                    return elapsed;
                } 
            
                // 2.05 se timeout spirato, non è detto
                // che sia questo il thread da risvegliare
                if (expired!=me)
                    cond.v(); // risveglia un altro
                      // e poi ricicla, sospendendosi di nuovo
                else
                {
                    // 2.06 recupero livello nesting
                    mutex.setOwner();
                    // ripristina livello
                    for (int i=1; i<=recoverLevel; i++)
                        mutex.p();
                    condCount--;
                    return elapsed;
                }
            } //for
        } //[m] cWait(timeout)
        
        /**[m]
         * signal sul condition
         */
        public void cSignal()
        {
            if (condCount>0)
            {
                // ci sono thread in attesa sul condition
                urgentCount++;
                // 2.06 cede mutex
                int recoverLevel = mutex.getVal()-1;
                  // livello da recuperare
                // ritorna a livello 1
                for (int i=1; i<=recoverLevel; i++)
                    mutex.v();
                cond.v();  // risveglio
                urgent.p();  // attesa del risvegliante
                mutex.setOwner();
                // ripristina livello
                for (int i=1; i<=recoverLevel; i++)
                    mutex.p();
                urgentCount--;
            }
        } //[m] cSignal

        /**[m]
         * thread in attesa 
         * @return numero di thread in attesa sul condition
        */
        public int queue()
        {  return condCount; }
        
    } //{c} Condition

    /**[m]
     * ingresso del monitor
     */
    public void mEnter()
    {
        mutex.p();
    }

    /**[m]
     * uscita dal monitor
     */
    public void mExit()
    {
        // 2.06 gestione livello di nesting
        if (mutex.getVal()>1)
        {
            mutex.v();
            return;
        }
        
        if (urgentCount > 0)
        {
            // 2.07 questo thread deve subito perdere il possesso del mutex
            // prima di risvegliare quello in urgent, altrimenti proseguendo puo'
            // arrivare a eseguire un p() che incrementa il lockcount
            // prima che il risvegliato riesca a ottenere l'ownership 
            // questo provoca una situazione di errore che solleva l'eccezione
            // legata al non posesso
            mutex.setOwner(null);

            urgent.v();
        }
        else
            mutex.v();
    }

} //{c} Monitor
