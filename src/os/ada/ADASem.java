package os.ada;

import os.Util;

/**{c}
 * esempio Semaforo del capitolo 13 del libro
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2010-12-30
*/

public class ADASem
{
    class Semaforo extends ADAThread
    {
        /**[c]
         * thread server Box
         * @param name  nome del thread
         */
        public Semaforo(String name)
        {
            super(name);
        } //[c]

        /**[m]
        * server semaforo
         */
        public void run()
        {
        
            Select selwait = new Select();
            selwait.add("wait");
            Select selsignal = new Select();
            selsignal.add("signal");

            while (true)
            {
                selwait.accept();
                selsignal.accept();
            }
        } //[m] run

    } //{c} Semaforo

    /**[m][s]
     * main di collaudo
     * @param args  not used
     */
    public static void main(String[] args) 
    {
        ADASem s = new ADASem();
        ADAThread sem = s.new Semaforo("MUTEX");
        sem.start();  // parte il server
        Util.sleep(2000);
          // attende allocazione dei servizi
        
        System.err.println("** wait");
        sem.entryCall("MUTEX", "wait");
        System.err.println("** in sezione critica");
        Util.sleep(2000);
        System.err.println("** signal");
        sem.entryCall("MUTEX", "signal");
        System.err.println("** fuori");
        Util.sleep(2000);
        System.exit(0);
        
    } //[m][s] main

    
} //{c} ADASem
