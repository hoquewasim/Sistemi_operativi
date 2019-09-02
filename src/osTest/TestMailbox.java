package osTest;

import os.Mailbox;
import os.TASys;
import os.Timeout;

/**{c}
 * test uso di Mailbox
 * @author M.Moro DEI UNIPD
 * @version 1.00 2003-10-03
 * @version 2.00 2005-10-07 package os e osTest
 */

public class TestMailbox
{
    private static final int MAXMSGS = 4;
      // capacita` del mailbox
    private static final long TIMEOUT = 5000;
      // timeout di attesa
    
    private Mailbox mbx = new Mailbox(MAXMSGS);
      // il mailbox
            
    /**{c}
     * thread inviante
     * @author M.Moro DEI UNIPD
     * @version 1.0 2003-10-03
     */
    private class Sender extends Thread
    {
        
        /**[c]
         * thread invio 
         * @param name  nome del thread
         */
        public Sender(String name)
        {
            super(name);
        }
        
        /**[m]
         * test: produce messaggio
         */
        public void run()
        {
            TASys mySys = new TASys();
              // frame di I/O
            System.out.println("Attivato thread "+getName());
            mySys.out.println("Sender attivato");
            while(true)
            {
                String mtx = mySys.in.readLine("Batti stringa (exit per uscire): ");
                mbx.put(mtx); // non fa la copia
                if (mtx.equals("exit"))
                {
                    System.out.println("** Sender terminato!");
                    mySys.close();
                    return;
                }
            } //while
        } //[m] run
                    
    } // {c} Sender
        
    /**{c}
     * thread ricevitore
     * @author M.Moro DEI UNIPD
     * @version 1.0 2003-10-03
     */
    private class Receiver extends Thread
    {
        
        /**[c]
         * thread ricazione
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
            TASys mySys = new TASys();
              // frame di I/O
            System.out.println("Attivato thread "+getName());
            mySys.out.println("Receiver, attende stringhe");
            while(true)
            {
                String str;
                do
                {
                    str = (String)mbx.get(TIMEOUT);
                    if (str == Timeout.EXPIREDOBJ)
                    {
                        mySys.out.print("** timeout!\n");
                        mySys.out.flush();
                    }
                        
                } while(str == Timeout.EXPIREDOBJ);
                mySys.out.println(str); 
                if (str.length() >= 4 && 
                  str.substring(0, 4).equals("exit"))
                {
                    System.out.println("** Receiver terminato!");
                    mySys.close();
                    return;
                }
            } //while
        } //[m] run
                    
    } // {c} Receiver
        
    /**[m][s]
     * main di collaudo
     * @param args non usato
     */
    public static void main(String[] args) 
    {
    	System.err.println("Test Mailbox");
        TestMailbox tm = new TestMailbox();
        System.err.println("Ora apre una finestra per ogni thread");
        Thread snd = tm.new Sender("sender");
        Thread rcv = tm.new Receiver("receiver");
        snd.start();
        rcv.start();
        try
        {
            snd.join();
            rcv.join();
        }
        catch( InterruptedException e ){}
        System.err.println("** Terminato!");
        System.exit(0);
    } //[m][s] main
    
} //{c} TestMailbox

