package os;

import java.util.*;

/**{c}
 * regione critica condizionale -
 * la valutazione della condizione
 * viene fatta chiamando il metodo
 * evaluate() di un oggetto che implementa
 * RegionCondition -
 * la regione e` innestabile ed effettua
 * a tal proposito un controllo in modo che
 * il nesting abbia correttamente luogo
 * per livelli crescenti (come l'allocazione
 * gerarchica) -
 * per il controllo viene mantenuta nel sistema
 * un'unica tabella hash che memorizza, per ogni
 * thread che usa le regioni, uno stack dei successivi
 * livelli di regione innestati -
 * l'ingresso ad una regione e` ammesso
 * se il livello di quest'ultima e` &gt;
 * del livello corrente del thread (NOLEVEL se
 * fuori da qualsiasi regione) -
 * l'uscita e` corretta se avviene da una regione
 * che ha il medesimo livello corrente del thread -
 * l'implementazione evita di risvegliare tutti i
 * i thread che attendono di entrare: quelli in attesa
 * all'uscita di un thread si risvegliano in sequenza
 * fino a che uno, verificata come vera l'eventuale
 * condizione, puo` entrare
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2003-10-14
 * @version 2.00 2005-10-07 package os
 * @version 2.01 2005-10-21 aggiunto il metodo enterWhen(condition, timeout)
 * @version 2.02 2005-10-28 risolto correttamente il problema del mantenimento
 *                          dell'ordine di accodamento su cond
 * @version 2.03 2009-10-14 usati generics per Map e Stack
 * @version 2.04 2013-06-25 aggiunto metodo count
 * @version 2.05 2019-05-25 eliminate deprecazioni
 */


public class Region
{
    private static final int NOLEVEL = -1;
      // livello non indicato
//v2.03 old  private static Map regMap = new HashMap();
    private static Map<String,RegThread> regMap = new HashMap<String,RegThread>();	 
      // set di thread con stack dei livelli;
      // dimensione e load factor di default;
      // hashcode dalle chiavi che sono i nomi 
      // dei thread
      // metodi della mappa NON sincronizzati
    private int level;
      // livello di nesting della regione
    private int npt = 0;
      // numero di processi che devono
      // valutare il test
    private int npr = 0;
      // numero di processi residui che devono
      // rivalutare il test
    private boolean toFinish = false;
      // posta a true per riposizionare i thread nella coda
      // una volta che uno di questi e` stato estratto
      // e ha verificato come vera la condizione
    private Semaphore mutex = new Semaphore(true);
      // per la mutua esclusione
      // non va bene un MutexSem perche`
      // p() e v() possono essere eseguiti da thread
      // diversi
    private Semaphore cond = new Semaphore(false);
      // per l'attesa della condizione
    private Semaphore urgent = new Semaphore(false);
      // per l'attesa del thread che ha trovata soddisfatta la condizione
      // in modo che i seguenti in coda ritornino nella posizione
      // corretta
      
    /**[c]
     * costruttore base: definisce il livello della regione
     * @param newLevel  livello di nesting
     */
    public Region(int newLevel)
    { level = newLevel; }
    
    /**{c}
     * classe di supporto -
     * memorizza una coppia thread name - stack regioni
     * in cui e` entrato
     * @author M.Moro DEI UNIPD
     * @version 1.00 2003-10-14
     */
    class RegThread
    {
//v2.03 old     Stack regStack = new Stack();
        Stack<Integer> regStack = new Stack<Integer>();

          // stack del nesting delle regioni
    
        /**[c]
         * costruttore base 
         * @param curLev  livello iniziale
         */
        RegThread(int curLev) 
//v2.03 old	  { regStack.push(new Integer(curLev)); }
        { regStack.push(curLev); }

        /**[m]
         * entra in una nuova regione 
         * @param curLev  nuovo livello
         */
        void pushLevel(int curLev)
//v2.03 old	  { regStack.push(new Integer(curLev)); }		  
        { regStack.push(curLev); }

        /**[m]
         * esce da una regione 
         */
        void popLevel()
        { regStack.pop(); }
        
        /**[m]
         * livello regione corrente
         * @return il livello sul top dello stack,
         *         NOLEVEL se stack vuoto
         */
        int curLevel()
        { return regStack.empty() ?
//v2.03 old     NOLEVEL : ((Integer)regStack.peek()).intValue(); }
          NOLEVEL : regStack.peek(); }

    } //{c} regThread
        
    /**[m]
     * entra con verifica in una regione innestata
     */
    private void enterLevel()
    {
        RegThread ret;
        String name = Thread.currentThread().getName();
          // nome del thread corrente come chiave
//v2.03 old   if ((ret = (RegThread)regMap.get(name)) == null)
        if ((ret = regMap.get(name)) == null)		  
        {
            // nuovo descrittore da inserire
          regMap.put(name, ret = new RegThread(level));           
            return;
        } //if
        // regThread trovato
        int lastLevel = ret.curLevel();
        if (lastLevel != NOLEVEL &&
          lastLevel >= level)
            // nesting errato
            throw new RegionNestingException(
              "Region.enter thread "+ name+ " in level "+level+
              " from level "+lastLevel);
        // aggiunge livello corrente
        ret.pushLevel(level);
     } //[m] enterLevel
        
    /**[m]
     * esce dall'ultima regione innestata
     */
    private void exitLevel()
    {
        RegThread ret;
        String name = Thread.currentThread().getName();
          // nome del thread corrente come chiave
//v2.03 old   if ((ret = (RegThread) regMap.get(name)) == null ||			 
        if ((ret = regMap.get(name)) == null ||
          ret.curLevel() != level)
            throw new RegionNestingException(
              "Region.leave thread "+name+
              " in level "+((ret==null || ret.curLevel()==NOLEVEL) 
              ? "NOLEVEL" :
//v2.05 old              (new Integer(ret.curLevel())).toString())+
              (Integer.valueOf(ret.curLevel())).toString())+
              " from level "+level);
        // level ok, rimosso
        ret.popLevel();
    } //[m] exitLevel
        
    /**[m]
     * entra nella regione con condizione
     * @param condition  RegionCondition per la valutazione
     *                  della condizione,
     *                  se null equivale a enter()
     */
    public void enterWhen(RegionCondition condition)
    {
        enterLevel();
          // verifica livelli e se ok aggiunge il corrente
        mutex.p();
        // non avendo garanzia sull'ordine di esecuzione,
        // attende che tutti i risvegliati siano tornati in coda
        while(cond.queue() != npt)
            Thread.yield();
        npt++;
        npr++;
        while(toFinish || (condition != null && ! condition.evaluate()))
        {
            if (--npr != 0)
                // processi residui che devono rivalutare
                cond.v();
            else
            {
                // sono stati tutti risvegliati
                if (toFinish)
                {
                    // libera il thread che deve proseguire
                    toFinish = false;
                    urgent.v();
                }
                else
                    // fa entrare eventualmente altri
                    mutex.v();
            }
            // condizione falsa, sospendi
            cond.p();
            // al risveglio, possiede gia` il mutex
            // non avendo garanzia sull'ordine di esecuzione,
            // attende che il risvegliante sia tornato in coda
            while(cond.queue() != npt-1)
                Thread.yield();
        } // while
        // condizione vera
        npr--;
        npt--;
        // prima di uscire definitivamente dall'attesa per la condizione,
        // fa riposizionare i thread che sono dopo di lui nella coda
        if (npr != 0)
        {
            toFinish = true;
            cond.v();
            urgent.p();
        }
    } //[m] enterWhen

    /**[m]
     * entra nella regione con condizione e timeout
     * @param condition  RegionCondition per la valutazione
     *                  della condizione,
     *                  se null equivale a enter()
     * @param t  timeout
     * @return tempo rimasto rispetto al timeout -
     *         timeout se non sospensivo o INTIME -
     *         INTIME&lt;ret&le;timeout se riesce ad entrare in tempo
     *         EXPIRED se spirato
     */
    public long enterWhen(RegionCondition condition, long t)
    {
        long ret = mutex.p(t);
        if (ret == Timeout.EXPIRED)
            // prima attesa spirata
            return ret;
        // effettivamente entrato
        enterLevel();
          // verifica livelli e se ok aggiunge il corrente
        // non avendo garanzia sull'ordine di esecuzione,
        // attende che tutti i risvegliati siano tornati in coda
        while(cond.queue() != npt)
            Thread.yield();
        npt++;
        npr++;
        while(toFinish || (condition != null && ! condition.evaluate()))
        {
            if (--npr != 0)
                // processi residui che devono rivalutare
                cond.v();
            else
            {
                // sono stati tutti risvegliati
                if (toFinish)
                {
                    // libera il thread che deve proseguire
                    toFinish = false;
                    urgent.v();
                }
                else
                    // fa entrare eventualmente altri
                    mutex.v();
            }
            // condizione falsa, sospendi
            ret = cond.p(ret);
              // qui c'e' una piccola imprecisione perche'
              // il parametro ret andrebbe decurtato del ritardo
              // introdotto dall'esecuzione delle istruzioni 
              // di questo ciclo, errore sostanzialmente trascurabile
            if (ret == Timeout.EXPIRED)
            {
                // attesa interna spirata
                npt--;
                exitLevel();
                  // verifica livelli e se ok toglie il corrente
                return ret;
            }
            // al normale risveglio, possiede gia` il mutex
            // non avendo garanzia sull'ordine di esecuzione,
            // attende che il risvegliante sia tornato in coda
            while(cond.queue() != npt-1)
                Thread.yield();
        } // while
        // condizione vera
        npr--;
        npt--;
        // prima di uscire definitivamente dall'attesa per la condizione,
        // fa riposizionare i thread che sono dopo di lui nella coda
        if (npr != 0)
        {
            toFinish = true;
            cond.v();
            urgent.p();
        }
        return ret;
    } //[m] enterWhen (condition, timeout)
    
    /**[m]
     * entra nella regione senza condizione
     */
    public void enterWhen()
    { 
        enterLevel();
          // verifica livelli e se ok aggiunge il corrente
        mutex.p(); 
    } //[m] enterWhen
        

    /**[m]
     * entra nella regione senza condizione con timeout
     * @param t  timeout
     * @return tempo rimasto rispetto al timeout -
     *         timeout se non sospensivo o INTIME -
     *         INTIME&lt;ret&le;timeout se riesce ad entrare in tempo
     *         EXPIRED se spirato
     */
    public long enterWhen(long t)
    {
        long ret = mutex.p(t);
        if (ret != Timeout.EXPIRED)
            // effettivamente entrato
            enterLevel();
              // verifica livelli e se ok aggiunge il corrente
        return ret;
    } //[m] enterWhen(t)
        
    /**[m]
     * esce dalla regione
     */
    public void leave()
    {
        npr = npt;
          // i residui sono tutti quelli sotto test
          // perche` probabilmente qualcosa e` cambiato
          // essendo l'applicazione uscita dalla regione
          // critica
        if (npr == 0)
            // nessun residuo, esce effettivamente
            mutex.v();
        else
            // risveglia qualcuno
            cond.v();
        exitLevel();
          // verifica livelli e se ok toglie il corrente
    } //[m] leave
    
    /**[m]
     * numero di thread nella coda condizione -
     * il metodo non e' thread-safe: chiamato mentre
     * un altro thread sta eseguendo un enterWhen o leave
     * può dare risultati incerti; eventualmente inserire
     * la chiamata tra un enterWhen e un leave
     * @return numero thread accodati
     */
    public int count()
    { return cond.queue(); }
    
} //{c} Region