package os.ada;

import os.Util;

/**{c}
 * esempio della sincronizzazione di 3 task in ADA
 * 
 * @author M.Moro DEI UNIPD
 * @version 1.00 2011-01-04
 * @version 1.01 2019-05-25 deprecated aliminato
*/

public class ADA3Task implements ADA3TaskStr
{
    /**{c}
     * thread di sincronizzazione
     */
    private class Sync extends ADAThread
    {
        Object readVal; // valore copiato
        
        /**[c]
         * @param name  nome del thread
         */
        public Sync(String name)
        {
            super(name);
        } //[c]
        
        public void run()
        {

            // 3 accept isolati
            Select rrsel = new Select();
            final Select stsel = new Select();
            final Select ersel = new Select();
    
            // entry REQUEST_READ
            rrsel.add(RRSTR,
              new Entry()
              {
                public Object exec(Object inp)
                {
                    // parametro di input non significativo
System.out.println("--1 in rr, start");
                    stsel.accept();
System.out.println("--2 in rr, er");
                    ersel.accept();
System.out.println("--3 in rr, completato");
                    return readVal;
                }
              } //{c} <anonima>
              );
    
            // entry START
            stsel.add(STSTR, null);  // solo sincronizzazione
    
            // entry END_READ
            ersel.add(ERSTR,
              new Entry()
              {
                public Object exec(Object inp)
                {
                    // parametro di output  non significativo
                    readVal = inp;
                    return null;
                }
              } //{c} <anonim>
              );
    
            while (true)
            {
                rrsel.accept();
                System.out.println("-- REQUEST_READ completato");
           }
        } //[m] run
        
    } //{c} Sync
    
    /**{c}
     * thread di lettura
     */
    private class Reader extends ADAThread
    {
        /**[c]
         * @param name  nome del thread
         */
        public Reader(String name)
        {
            super(name);
        } //[c]
        
        public void run()
        {
            System.out.println("!! inizio reader");
            while (true)
            {
System.out.println("!!1 start");
                entryCall(null, SYNCSTR, STSTR);
                // simula lettura di un valore con numero casuale
                int val = Util.randVal(1,100);
System.out.println("!!2 end read val="+val);
// v1.01 deprecated
//                entryCall(new Integer(val), SYNCSTR, ERSTR);
                entryCall(Integer.valueOf(val), SYNCSTR, ERSTR);
                Util.sleep(2000L);
            } // for
        } //[m] run

    } //{c} Reader
            
    /**{c}
     * thread utente
     */
    private class User extends ADAThread
    {
        /**[c]
         * @param name  nome del thread
         */
        public User(String name)
        {
            super(name);
        } //[c]
        
        public void run()
        {
            System.out.println("++ inizio user");
            for (int i=1; i<=10; i++)
            {
                CallOut ret = entryCall(null, SYNCSTR, RRSTR);
                System.out.println("++++ letto: "+
                  ((Integer)ret.getParams()).intValue());
                Util.sleep(1000L);
            } // for
            System.exit(0);
        } //[m] run

    } //{c} User
            
    /**[m][s]
     * main di collaudo
     * @param args  not used
     */
    public static void main(String[] args) 
    {
        ADA3Task a3t = new ADA3Task();
        Thread sync = a3t.new Sync(SYNCSTR);
        Thread reader = a3t.new Reader(READERSTR);
        Thread user = a3t.new User(USERSTR);
        sync.start();
        Util.sleep(1000); // cosi' il server inizializza tutti gli entry
        reader.start();
        user.start();
    } //[m][s] main

} //{c} ADA3Task

