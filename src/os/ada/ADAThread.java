package os.ada;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;

import os.Semaphore;
import os.Util;
import os.Timeout;

/**{c}
 * classe base per un task ADA
 * sia client che server o entrambi
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2010-12-13
 * @version 1.01 2012-06-10 Bug fix nelle chiamate server.entryCall
 */
 
public class ADAThread extends Thread
{
    private static ConcurrentMap<String, Port<CallIn>> portReg =
      new ConcurrentHashMap<String, Port<CallIn>>();
      // registro dei port dei server
    private static ConcurrentMap<String, Semaphore> mutexReg =
      new ConcurrentHashMap<String, Semaphore>();
      // registro dei mutex dei server
    private static int selCnt = 1;
      // conteggio selettori unnamed
      
    private Semaphore mutex;
      // mutex generale 
    private int nestingLev = 0;
      // livello di nesting degli accept
    private Semaphore servPriv;
      // attesa del server nel select
    private Semaphore clntPriv1;  
    private Semaphore clntPriv2;  
      // attesa del client sulle due sincronizzazioni
    private int callInCount = 1;
      // conteggio messaggi di call
    private Port<CallOut> rep;
      // port per la risposta
      
    private void init(String name)
    {
        // e' (anche) un server
        if (mutexReg.containsKey(name))
            // server con lo stesso nome gia' creato
            throw new ServerExistsException(name);
        servPriv = new Semaphore(0, 1);
          // semaforo privato del server
        mutex = new Semaphore(1);
          // mutua esclusione a protezione
          // dei port di attesa del server
        mutexReg.put(name, mutex);
          // registrazione del mutex di protezione

        // e' (anche) un client
        clntPriv1 = new Semaphore(0, 1);
        clntPriv2 = new Semaphore(0, 1);
          // semafori di attesa 1 e 2
        rep = new Port<CallOut>(clntPriv2);
          // port di risposta
    } // [m] init
    
    /**[c]
     * thread ADA client e/o server
     * @param name  nome del thread
     * @throws ServerExistsException  se server gia' creato
     *         con quel nome
     */
    public ADAThread(String name)
    {
        super(name);
        init(name);
    } //[c]

    /**[c]
     * thread ADA client e/o server, Runnable
     * @param runTh  la classe che implementa Runnable
     * @param name  nome del thread
     * @throws ServerExistsException  se server gia' creato
     *         con quel nome
     */
    public ADAThread(Runnable runTh, String name)
    {
        super(runTh, name);
        init(name);
    } //[c]

    /**{c}
     * gruppo di selettori
     */
    public class Select
    {
        private static final long MAXDELAY = 0x7fffffffL;
          // valore iniziale del delay minimo 
        private static final int ACCEPT_TYPE = 0;
        private static final int CONSTDELAY_TYPE = 1;
        private static final int VARDELAY_TYPE = 2;
        private static final int ELSE_TYPE = 0;
              // tipi di selettore
        private int choice = -1;
          // indice scelto del selettore 
        private boolean elsePresent = false;
          // l'else part e' presente nel gruppo di selettori
        private int elseChoice = -1;
          // indice selettore corrispondente ad else
        private ArrayList<SelElem> selList = new ArrayList<SelElem>();
          // lista degli entry del gruppo
        private ArrayList<Integer> openList = new ArrayList<Integer>();
          // lista degli indici delle guardie aperte
        private ArrayList<Integer> waitList = new ArrayList<Integer>();
          // lista degli indici degli entry aperti in attesa

        /**{c}is
         * un selettore
         */
        class SelElem
        {
            int type;
              // tipo di selettore
            Guard grd;
              // la guardia
            String pName;
              // il nome del selettore
            Port<CallIn> prt;
              // port di selettore
            Entry ent;
              // esecutore dell'entry
            long delay;
              // timeout costante
            Delay varDelay;
              // timeout variabile
            
            /**[c]
             * un selettore di tipo accept
             * @param grd  guardia
             * @param pName  nome del selettore
             * @param prt  port del selettore
             * @param ent  esecutore dell'entry
             */
            SelElem(Guard grd, String pName, Port<CallIn> prt, Entry ent)
            {
                type = ACCEPT_TYPE;
                this.grd = grd;
                this.pName = pName;
                this.prt = prt;
                this.ent = ent;
                delay = Timeout.NOTIMEOUT;
                varDelay = null;
                
            } //{c}
            
            /**[c]
             * un selettore di tipo delay costante
             * @param grd  guardia
             * @param del  delay
             */
            SelElem(Guard grd, long del)
            {
                type = CONSTDELAY_TYPE;
                this.grd = grd;
                pName = "_constdelay"+(selCnt++);
                delay = del;
                prt = null;
                ent = null;
                varDelay = null;
            } //{c}
            
            /**[c]
             * un selettore di tipo delay variabile
             * @param grd  guardia
             * @param del  delay variabile
             */
            SelElem(Guard grd, Delay del)
            {
                type = VARDELAY_TYPE;
                this.grd = grd;
                pName = "_vardelay"+(selCnt++);
                varDelay = del;
                delay = Timeout.NOTIMEOUT;
                prt = null;
                ent = null;
            } //{c}

            /**[c]
             * un selettore di tipo else
             */
            SelElem()
            {
                type = ELSE_TYPE;
                pName = "_else";
                delay = Timeout.NOTIMEOUT;
                grd = null;
                prt = null;
                ent = null;
                varDelay = null;
            } //{c}
            
            /**[m]
             * conversione a stringa
             * @return il nome del selettore
             */
            public String toString()
            {
                return pName;
            }
        } //{c} SelElem
    
        /**[c]
         */
        public Select()
        {
        } //[c]

        /**[m]
         * conversione a stringa
         * @return nomi dei selettori del gruppo
         */
        public String toString()
        {
            String s = "<select [ ";
            ListIterator<SelElem> li = selList.listIterator();
            boolean first = true;
            while (li.hasNext())
            {
                SelElem el = li.next();
                if (!first)
                    s += ", ";
                s += el.toString();
                first=false;
            }
            return s+" ]>";
        } //[c]

        /**[m]
         * aggiunta di un entry accept al gruppo
         * @param grd  la guardia dell'entry
         * @param prtStr  nome del port di accumulo delle chiamate
         *             all'entry
         * @param ent  esecuzione dell'entry
         */
        public void add(Guard grd, String prtStr, Entry ent)
        {
            String prtName = Thread.currentThread().getName()+
            "."+prtStr;
              // qualifica il nome con quello del server
            Port<CallIn> prt = portReg.get(prtName);
              // acquisisce dal registro il port corrispondente
            if (prt == null)
            {
                // nuovo port
                prt = new Port<CallIn>(servPriv);
                  // port del server
                portReg.put(prtName, prt);
                  // aggiunge il nuovo port al registro
            }
            selList.add(new SelElem(grd, prtName, prt, ent));
              // aggiunta del selettore
        } //[m] add [accept]
    
        /**[m]
         * aggiunta al gruppo di un entry accept non condizionato
         * @param prtStr  nome del port di accumulo delle chiamate
         *             all'entry
         * @param ent  esecuzione dell'entry
         */
        public void add(String prtStr, Entry ent)
        {
            add(null, prtStr, ent);
        }  //[m] add [accept senza guardia]
        
        /**[m]
         * aggiunta al gruppo di un entry accept a corpo nullo
         * @param grd  la guardia dell'entry
         * @param prtStr  nome del port di accumulo delle chiamate
         *             all'entry
         */
        public void add(Guard grd, String prtStr)
        {
            add(grd, prtStr, null);
        }  //[m] add [accept a corpo nullo]
        
        /**[m]
         * aggiunta al gruppo di un entry accept a corpo nullo
         * e non condizionato
         * @param prtStr  nome del port di accumulo delle chiamate
         *                all'entry
         *                se null o "", assimilato ad add()
         *          
         */
        public void add(String prtStr)
        {
            if (prtStr == null || prtStr.equals(""))
                add();
            add(null, prtStr, null);
        }  //[m] add [accept a corpo nullo senza guardia]
        
        /**[m]
         * aggiunta di un entry delay costante al gruppo
         * @param grd  la guardia dell'entry
         * @param del  delay
         */
        public void add(Guard grd, long del)
        {
            selList.add(new SelElem(grd, del));
              // aggiunta del selettore
        } //[m] add [const delay]
    
        /**[m]
         * aggiunta al gruppo di un entry delay costante 
         * non condizionato
         * @param del  delay
         */
        public void add(long del)
        {
            add(null, del);
        } //[m] add [const delay senza guardia]
    
        /**[m]
         * aggiunta di un entry delay variabile al gruppo
         * @param grd  la guardia dell'entry
         * @param del  delay
         */
        public void add(Guard grd, Delay del)
        {
            selList.add(new SelElem(grd, del));
              // aggiunta del selettore
        } //[m] add [var delay]
    
        /**[m]
         * aggiunta al gruppo di un entry delay variabile 
         * non condizionato
         * @param del  delay
         */
        public void add(Delay del)
        {
            add(null, del);
        } //[m] add [var delay senza guardia]
    
        /**[m]
         * aggiunta di un entry else al gruppo
         */
        public void add()
        {
            if (elsePresent)
                throw new MultiElseException("selettore="+selList.size());
            elsePresent=true;
            elseChoice = selList.size();
              // corrisponde all'indice di inserimento
            selList.add(new SelElem());
              // aggiunta del selettore
        } //[m] add [else]
    
        /**[m]
         * attivazione del selettore
         * @return indice della scelta effettuata
         *         tra gli accept aperti
         * @throws AllGuardsClosedException  tutte le guardie sono chiuse
         *         e non c'e' una opzione else
         */
        public int accept()
        {
            long minDelay = MAXDELAY;
              // minimo tra i delay indicati
            int choiceMin = -1;
              // scelta corrispondente al min delay
            int i;

            // controlla livello nesting degli accept
            if (nestingLev == 0)
                mutex.p();
            nestingLev++;
// System.out.println("??1 server in mutex, selList size="+selList.size());

            // valuta le guardie e costruisce la lista
            // degli indici di guardie aperte
            // ed intoltre calcola il minimo timeout
            for (i=0; i<selList.size(); i++)
            {
                SelElem sel = selList.get(i);
                if (sel.grd==null ||  // guardia assente
                  sel.grd.when()) // guardia aperta
                {
                    // la guardia è aperta
                    long timeout;
                    openList.add(i);
                      // aggiunge indice entry
                    if (sel.type == CONSTDELAY_TYPE &&
                      sel.delay < minDelay)
                    {
                        minDelay = sel.delay;
                        choiceMin = i;
                    }
                    else if (sel.type == VARDELAY_TYPE &&
                      (timeout = sel.varDelay.timeout()) < minDelay)
                    {
                        minDelay = timeout;
                        choiceMin = i;
                    }
                }
            }
            

// System.out.println("??2 server in mutex, openList size="+openList.size());
            if (openList.size()==0)
            {
                // tutte le guardie sono chiuse e conseguentemente
                // manca anche l'else part (che ha virtualmente
                // una guardia nulla)
                nestingLev--;
                if (nestingLev==0)
                    mutex.v();
                throw new AllGuardsClosedException(toString());
            }
            
            // degli entry accept aperti valuta quali hanno
            // richieste pendenti, costruendo la lista di
            // indici di questi
            for (i=0; i<openList.size(); i++)
            {
                int j = openList.get(i);
                if (selList.get(j).prt!=null && 
                  // potrebbe essere else o delay
                  selList.get(j).prt.size()>0)
                {
                    // c'e' una call sull'entry aperto
                    waitList.add(j);
                      // aggiunge indice entry
                }
            }

// System.out.println("??3 waitList size="+waitList.size());
            if (waitList.size()>0)
            {
                // un accept e' immediatamente possibile
                choice = Util.randVal(0, waitList.size()-1);
                  // scelta casuale tra quelli in attesa
//System.out.println("??4  choice="+choice);
//System.out.println("??4a waitList choice="+waitList.get(choice));
                SelElem chosen = 
                  selList.get(choice=waitList.get(choice));
                  // l'entry selezionato
                CallIn inp = chosen.prt.remove();  
                  // entrae parametri d'ingresso dal port
                inp.wait1.v();
                  // invia evento 1 al client
                CallOut out;
                if (chosen.ent==null)
                    // corpo nullo, solo sincronizzazione
                    out = new CallOut(inp.th, inp.idx, null);
                else
                    out = new CallOut(inp.th, inp.idx, 
                      chosen.ent.exec(inp.params));
                      // esegue entry
// System.out.println("????? tipo risposta="+
// out.getClass().getName());
                inp.reply.add(out);
                  // invia risposta
                inp.wait2.v();
                  // invia evento 2 al client
                openList.clear();
                waitList.clear();
                  // svuota liste temporanee
            }
            else
            {
                // un accept non e' immediatamente possibile
                if (elsePresent)
                {
                    // else part senza attesa
                    nestingLev--;
                    if (nestingLev == 0)
                        mutex.v();
                    openList.clear();
                    waitList.clear();
                      // svuota liste temporanee
                    return elseChoice;
                }
                for (i=0; i<openList.size(); i++)
                    if (selList.get(openList.get(i)).prt != null)
                      // effettivamente un accept
                        selList.get(openList.get(i)).prt.waiting=true;
                          // attende su ciascuna guardia aperta di un accept
                // qui, anche se nestingLev>0, deve liberare
                // il mutex per consentire al client di proseguire
                mutex.v();
//System.out.println("??5 si mette in attesa timeout="+minTimeout);
                if (servPriv.p(minDelay)==Timeout.EXPIRED)
                {
//System.out.println("??6 timeout spirato");
                    // timeout
                    // condizione di corsa: un client potrebbe
                    // aver comunque accodato ed inviato il signal
                    // con leggero ritardo
                    mutex.p();
                    for (i=0; i<openList.size(); i++)
                        if (selList.get(openList.get(i)).prt != null)
                          // effettivamente un accept
                            selList.get(openList.get(i)).prt.waiting=false;
                              // non piu' in attesa
                    servPriv.p(Timeout.IMMEDIATE);
                      // elimina il signal se ancora presente
                      // ma comunque lascia la richiesta pendente
                      // nel port
                    nestingLev--;
                    if (nestingLev==0)
                        mutex.v();
                    openList.clear();
                    waitList.clear();
                      // svuota liste temporanee
                    return choiceMin;
                      // ritorna la prima delle scelte 
                      // con il timeout minimo
                }
                // in questo caso ha ereditato dal client la mutua esclusione 
                // e il client ha segnalato l'entry attivato
                // ponendo a false il corrispondente flag waiting
                for (i=0; i<openList.size(); i++)
                {
                    if (selList.get(openList.get(i)).prt != null)
                      // effettivamente un accept
                    {
                        if (! selList.get(openList.get(i)).prt.waiting)
                            // e' arrivata la richiesta su questo port
                            choice = openList.get(i);
                        selList.get(openList.get(i)).prt.waiting=false;
                          // non piu' in attesa
                    }
                }
//System.out.println("??7 si e' risvegliato choice="+choice);
                SelElem chosen = selList.get(choice);
                  // l'entry selezionato
//System.out.println("??7 selelem=<"+chosen+">");
                CallIn inp = chosen.prt.remove();  
                  // entrae parametri d'ingresso
                // il client attende comunque l'evento 1 
                inp.wait1.v();
                  // invia evento 1 al client
                CallOut out;
                if (chosen.ent==null)
                    // corpo nullo
                    out = new CallOut(inp.th, inp.idx, null);
                else
                    out = new CallOut(inp.th, inp.idx, 
                      chosen.ent.exec(inp.params));
                      // esegue entry
// System.out.println("????? tipo risposta="+
// out.getClass().getName());
                inp.reply.add(out);
                  // invia risposta
                inp.wait2.v();
                  // invia evento 2 al client
                openList.clear();
                waitList.clear();
                  // svuota liste temporanee
            
            } // if (waitList.size()>0)
            nestingLev--;
            if (nestingLev==0)
                mutex.v();
            return choice;
              // scelta effettuata
        } //[m] accept
     
        /**[m]
         * ultima scelta operata (0..waitList.size()-1)
         * @return indice dell'ultimo select scelto
         */
        public int choice()
        {
            return choice;
        } //[m] choice
        
        /**[m]
         * nome del selettore
         * @param choice indice delselettore
         * @return stringa che rappresenta il selettore
         *         !!SELETTORE ILLEGALE!! se indice non valido
         */
        public String choice2Str(int choice)
        {
            if (choice<0 || choice>=selList.size())
                return "!! SELETTORE ILLEGALE !!";
            return selList.get(choice).pName;
        } //[m] choice2Str
        
    } //{c} Select

    /**[m]
     * stato di un port del server
     * @param prtStr  nome dell' entry
     * @return il numero di messaggi nel port associato
     * @throws InvalidPortNameException  nome servizio non trovato
     */
    public int entryCount(String prtStr)
    {
        String prtName = Thread.currentThread().getName()+
        "."+prtStr;
          // qualifica il nome con quello del server
        Port<CallIn> prt = portReg.get(prtName);
          // acquisisce dal registro il port corrispondente
        if (prt == null)
            throw new InvalidPortNameException(prtName);
        return prt.size();
    } //[m] entryCount

    /**[m]
     * attivazione del Call
     * @param inParams  parametri d'ingresso impacchettati
     * @param servStr  nome del server thread
     * @param destStr  nome dell'accept chiamato
     * @param timeout  timeout
     * @return i parametri di uscita impacchettati che
     *         comprendono il ritorno di timeout 
     *         timeout se non sospensivo o INTIME -
     *         INTIME&lt;ret&le;timeout se la prima sincronizzazione
     *         arriva in tempo
     *         EXPIRED se spirato
     * @throws InvalidPortNameException  nome servizio non trovato
     */
    public CallOut entryCall(Object inParams, String servStr,
      String destStr, long timeout)
    {
        int i;
        long ret = Timeout.INTIME;
        int inCnt = -1;
        String destName = servStr+"."+destStr;
          // nome del port qualificato
        
        Port<CallIn> dest = portReg.get(destName);
          // acquisisce port
        if (dest==null)
            throw new InvalidPortNameException(destName);
        // il server esiste senz'altro se ha trovato il port
        Semaphore mtx = mutexReg.get(servStr);
          // acquisisce il relativo mutex
        mtx.p();
        
        // impostazione del contenitore dei parametri di ingresso
        CallIn inp = new CallIn(inParams);
        inp.th = Thread.currentThread();
        inp.idx = inCnt = callInCount++;
        inp.dest = dest;
        inp.mtx = mtx;
        inp.wait1 = clntPriv1;
        inp.wait2 = clntPriv2;
        inp.reply = rep;
        inp.isImmediate = dest.waiting;
          // server in attesa
            
        dest.add(inp);
          // accoda i parametri di ingresso nel port del selettore
          // indicato 
        if (dest.waiting)
        {
// System.out.println("^^^1 "+Thread.currentThread().getName()+
// " call, ha rilevato server in attesa");            
            // il server e' in attesa (anche) su questo port
            // segnala di aver inviato la richiesta su quel port
            // cede la mutua esclusione
            // e risveglia il server
            dest.waiting=false;
            dest.priv.v();
// System.out.println("^^^2 "+Thread.currentThread().getName()+
// " call, attende prima sinc");            
            // la mutua esclusione e' stata ceduta
            clntPriv1.p();
              // attesa untimed, il v() arriva senz'altro
              // se il server e' 'vivo'
// System.out.println("^^^3 "+Thread.currentThread().getName()+
// " call, attende seconda sinc");            
            clntPriv2.p();
            // ha la risposta sul port di risposta 
            CallOut co = rep.remove();
            co.timeout = ret;
            return co;
        }

        // il server non sta aspettando
        mtx.v();
// System.out.println("^^^4 "+Thread.currentThread().getName()+
// " call, attende prima sinc");            
        if ((ret=clntPriv1.p(timeout))== Timeout.EXPIRED)
        {
            // a causa di una condizione di corsa
            // puo' darsi che il server abbia fatto in tempo
            // ad estrarre il messaggio che quindi
            // potrebbe esser stato eliminato dal port
// System.out.println("^^^5 "+Thread.currentThread().getName()+
// " call, timeout prima sinc");            
            mtx.p();
            // cerca messaggio da estrarre
            ListIterator<CallIn> li = dest.listIterator();
            while (li.hasNext())
            {
                CallIn ci = li.next();
                if (ci.th==Thread.currentThread() && 
                  ci.idx == inCnt)
                {
                    // trovato,lo estrae
                    li.remove();
                    mtx.v();
// System.out.println("^^^6 "+Thread.currentThread().getName()+
// " call, timeout ritorna");            
                    return new CallOut(ci.th, ci.idx, ret, null);
                      // ritorna EXPIRED
                }
            }
            // il messaggio e' stato estratto dal server
            mtx.v();
        }
        // la prima sincronizzazione e' avvenuta
// System.out.println("^^^7 "+Thread.currentThread().getName()+
// " call, attende seconda sinc");            
        clntPriv2.p();
        // ha la risposta sul port 
        CallOut co = rep.remove();
        co.timeout = ret;
        return co;
    } //[m] entryCall

    /**[m]
     * attivazione del Call senza timeout
     * @param inParams  parametri d'ingresso impacchettati
     * @param servStr  nome del server thread
     * @param destStr  nome dell'accept chiamato
     * @return i parametri di uscita impacchettati che
     *         comprendono il ritorno di timeout 
     *         timeout se non sospensivo o INTIME -
     *         INTIME&lt;ret&le;timeout se la prima sincronizzazione
     *         arriva in tempo
     *         EXPIRED se spirato
     * @throws InvalidPortNameException  nome servizio non trovato
     */
    public CallOut entryCall(Object inParams, String servStr,
      String destStr)
    {
        return entryCall(inParams, servStr, destStr, Timeout.NOTIMEOUT);
    } //[m] entryCall

    /**[m]
     * attivazione del Call applicata ad un thread
     * @param inParams  parametri d'ingresso impacchettati
     * @param destStr  nome dell'accept chiamato
     * @param timeout  timeout
     * @return i parametri di uscita impacchettati che
     *         comprendono il ritorno di timeout 
     *         timeout se non sospensivo o INTIME -
     *         INTIME&lt;ret&le;timeout se la prima sincronizzazione
     *         arriva in tempo
     *         EXPIRED se spirato
     * @throws InvalidPortNameException  nome servizio non trovato
     */
    public CallOut entryCall(Object inParams, String destStr, long timeout)
    {
//v1.01        return entryCall(inParams, this.getName(), destStr, timeout);
        return ((ADAThread)(Thread.currentThread())).entryCall(inParams, this.getName(), destStr, timeout);

          // this e' l'oggetto che rappresenta il server
    } //[m] entryCall
    
    /**[m]
     * attivazione del Call applicata ad un thread senza timeout
     * @param inParams  parametri d'ingresso impacchettati
     * @param destStr  nome dell'accept chiamato
     * @return i parametri di uscita impacchettati che
     *         comprendono il ritorno di timeout 
     *         timeout se non sospensivo o INTIME -
     *         INTIME&lt;ret&le;timeout se la prima sincronizzazione
     *         arriva in tempo
     *         EXPIRED se spirato
     * @throws InvalidPortNameException  nome servizio non trovato
     */
    public CallOut entryCall(Object inParams, String destStr)
    {
//v1.01        return entryCall(inParams, this.getName(), destStr);
        return ((ADAThread)(Thread.currentThread())).entryCall(inParams, this.getName(), destStr);
          // this e' l'oggetto che rappresenta il server
    } //[m] entryCall
    
    /**[m]
     * attivazione ritardata del Call
     * @param inParams  parametri d'ingresso impacchettati
     * @param servStr  nome del server thread
     * @param destStr  nome dell'accept chiamato
     * @return il parametro da passare nella successiva chiamata
     *         replyWait
     * @throws InvalidPortNameException  nome servizio non trovato
     */
    public CallIn delayedEntryCall(Object inParams, String servStr,
      String destStr)
    {
        int i;
        int inCnt = -1;
        String destName = servStr+"."+destStr;
          // nome del port qualificato
        
        Port<CallIn> dest = portReg.get(destName);
          // acquisisce port
        if (dest==null)
            throw new InvalidPortNameException(destName);
        // il server esiste senz'altro se ha trovato il port
        Semaphore mtx = mutexReg.get(servStr);
          // acquisisce il relativo mutex
        mtx.p();
        
        // impostazione del contenitore dei parametri di ingresso
        CallIn inp = new CallIn(inParams);
        inp.th = Thread.currentThread();
        inp.idx = inCnt = callInCount++;
        inp.mtx = mtx;
        inp.wait1 = new Semaphore(0,1);
        inp.wait2 = new Semaphore(0,1);
        inp.reply = new Port<CallOut>(inp.wait2);
          // la forma delayed richiede questi 3 elementi dinamici
        inp.isImmediate = dest.waiting;
          // server in attesa
            
        dest.add(inp);
          // accoda i parametri di ingresso nel port del selettore
          // indicato 
        if (dest.waiting)
        {
// System.out.println("^^^1 "+Thread.currentThread().getName()+
// " call, ha rilevato server in attesa");            
            // il server e' in attesa (anche) su questo port
            // segnala di aver inviato la richiesta su quel port
            // cede la mutua esclusione
            // e risveglia il server
            dest.waiting=false;
            dest.priv.v();
// System.out.println("^^^2 "+Thread.currentThread().getName()+
// " call, attende prima sinc");            
            // la mutua esclusione e' stata ceduta
            return inp;
        }

        // il server non sta aspettando
        mtx.v();
// System.out.println("^^^4 "+Thread.currentThread().getName()+
// " call, attende prima sinc");
        return inp;            
    } //[m] delayedEntryCall

    /**[m]
     * attivazione ritardata del Call applicata ad un thread
     * @param inParams  parametri d'ingresso impacchettati
     * @param destStr  nome dell'accept chiamato
     * @return il parametro da passare nella successiva chiamata
     *         replyWait
     * @throws InvalidPortNameException  nome servizio non trovato
     */
    public CallIn delayedEntryCall(Object inParams, String destStr)
    {
//v1.01        return delayedEntryCall(inParams, this.getName(), destStr);
        return ((ADAThread)(Thread.currentThread())).delayedEntryCall(inParams, this.getName(), destStr);
          // this e' l'oggetto che rappresenta il server
    } //[m] delayedEntryCall
    
    /**[m]
     * attesa della risposta ritardata
     * @param inp  descrittore par. di ingresso
     *             tornato da delayedEntryCall
     * @param timeout  timeout
     * @return i parametri di uscita impacchettati che
     *         comprendono il ritorno di timeout 
     *         timeout se non sospensivo o INTIME -
     *         INTIME&lt;ret&le;timeout se la prima sincronizzazione
     *         arriva in tempo
     *         EXPIRED se spirato
     */
    public static CallOut replyWait(CallIn inp, long timeout)
    {
        long ret = Timeout.INTIME;

        if (inp.isImmediate)
        {
            // era un rv immediatamente possibile
            inp.wait1.p();
              // attesa untimed, il v() arriva senz'altro
              // se il server e' 'vivo'
// System.out.println("^^^3 "+Thread.currentThread().getName()+
// " call, attende seconda sinc");            
            inp.wait2.p();
            // ha la risposta sul port di risposta 
            CallOut co = inp.reply.remove();
            co.timeout = ret;
            return co;
        }
        // occorre attendere
        if ((ret=inp.wait1.p(timeout))== Timeout.EXPIRED)
        {
            // a causa di una condizione di corsa
            // puo' darsi che il server abbia fatto in tempo
            // ad estrarre il messaggio che quindi
            // potrebbe esser stato eliminato dal port
// System.out.println("^^^5 "+Thread.currentThread().getName()+
// " call, timeout prima sinc");            
            inp.mtx.p();
            // cerca messaggio da estrarre
            ListIterator<CallIn> li = inp.dest.listIterator();
            while (li.hasNext())
            {
                CallIn ci = li.next();
                if (ci.th==inp.th && 
                  ci.idx == inp.idx)
                {
                    // trovato,lo estrae
                    li.remove();
                    inp.mtx.v();
// System.out.println("^^^6 "+Thread.currentThread().getName()+
// " call, timeout ritorna");            
                    return new CallOut(ci.th, ci.idx, ret, null);
                      // ritorna EXPIRED
                }
            }
            // il messaggio e' stato estratto dal server
            inp.mtx.v();
        }
        // la prima sincronizzazione e' avvenuta
// System.out.println("^^^7 "+Thread.currentThread().getName()+
// " call, attende seconda sinc");            
        inp.wait2.p();
        // ha la risposta sul port 
        CallOut co = inp.reply.remove();
        co.timeout = ret;
        return co;
    } //[m] replyWait

    /**[m]
     * attesa della risposta ritardata senza timeout
     * @param inp  descrittore par. di ingresso
     *             tornato da delayedEntryCall
     * @return i parametri di uscita impacchettati che
     *         comprendono il ritorno di timeout 
     *         timeout se non sospensivo o INTIME -
     *         INTIME&lt;ret&le;timeout se la prima sincronizzazione
     *         arriva in tempo
     *         EXPIRED se spirato
     */
    public static CallOut replyWait(CallIn inp)
    {
        return replyWait(inp, Timeout.NOTIMEOUT);
    } //[m] replyWait

} //{c} ADAThread
