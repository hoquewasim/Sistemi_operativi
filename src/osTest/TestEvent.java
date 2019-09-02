package osTest;

import os.Events;
import os.Util;

/**{c}
 * test su eventi associati ai thread
 * @author M.Moro DEI UNIPD
 * @version 1.00 2003-10-01
 * @version 2.00 2005-10-07 package os e osTest
 */

public class TestEvent
{
    static private Thread dest;
      // thread di destinazione
      
    /**{c}
     * thread inviante
     * @author M.Moro DEI UNIPD
     * @version 1.0 2003-10-01
     */
    private static class Sender extends Thread
    {
            
        /**[c]
         * invia evento alternato
         * @param name  nome del thread
         */
        public Sender(String name)
        {
            super(name);
        }
        
        /**[m]
         * test: invia evento
         */
        public void run()
        {
            System.out.println("Attivato thread inviante "+getName());
            int ev = 1; // bit 0;
            int cnt = 1;
            while(true)
            {
                System.out.println("$$ inviante cnt="+(cnt++)+" ev="+ev);                
                Events.signal(ev, dest);
                // inverte maschera
                ev ^= 3;
                Util.rsleep(250, 3000);
            } //while
        } //[m] run
                    
    } // {c} Sender
        
    /**{c}
     * thread ricevitore
     * @author M.Moro DEI UNIPD
     * @version 1.0 2003-10-01
     */
    private static class Receiver extends Thread
    {
        
        /**[c]
         * attende 2 eventi 
         * @param name  nome del thread
         */
        public Receiver(String name)
        {
            super(name);
        }
        
        /**[m]
         * test: produce messaggio
         */
        public void run()
        {
            final int RCVMASK = 3; // maschera bit 0 e 1
            System.out.println("Attivato thread ricevitore "+getName());
            while(true)
            {
                System.out.println("-- ricevitore wait");
                int received = Events.wait(RCVMASK);
                System.out.println("-------- ricevitore retmask="+received);
                Util.sleep(2000);
            } //while
        } //[m] run
                    
    } // {c} Receiver
        

    /**[m][s]
     * main di collaudo
     * @param args  non usato
     */
    public static void main(String[] args) 
    {
        System.err.println("TestEvent: Battere Ctrl-C per terminare");
        Thread sender = new Sender("sender");
        dest = new Receiver("receiver");
        sender.start();
        dest.start();
    } //[m][s] main
    
} //{c} TestEvent

