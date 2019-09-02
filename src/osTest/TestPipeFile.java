package osTest;

import os.SimpleSemaphore2;
import os.PipeFile;
import os.TASys;

/**{c}
 * test uso di pipe su file
 * con semaforo numerico SimpleSemaphore2 
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-05-07
 * @version 2.00 2003-09-30 package OsTest
 * @version 2.01 2005-10-07 package os e osTest
 * @version 2.02 2019-05-25 deprecated eliminati
 */

public class TestPipeFile
{
//    private static final int MAXCHARS = 1000;
    private static final int MAXCHARS = 50;
      // capacita` del pipe
      // bassa per prova per far vedere che perde caratteri
    
    private PipeFile pipe = new PipeFile("$$PipeFile", MAXCHARS);
      // il pipe su file
            
    /**{c}
     * thread inviante
     * @author M.Moro DEI UNIPD
     * @version 1.0 2002-05-07
     */
    private class Sender extends Thread
    {
        
        /**[c]
         * thread invio per il test PipeFile
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
                for (int i=0; i<mtx.length(); i++)
// 2.02 deprecated                    pipe.write(new Character(mtx.charAt(i)));
//                pipe.write(new Character('\n'));
                    pipe.write(Character.valueOf(mtx.charAt(i)));
                pipe.write(Character.valueOf('\n'));
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
     * @version 1.0 2002-05-07
     */
    private class Receiver extends Thread
    {
        
        /**[c]
         * thread ricazione per il test PipeFile
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
                char c;
                StringBuffer str = new StringBuffer();
                do
                    str.append(c = ((Character)pipe.read()).charValue());
                while (c != '\n');
                mySys.out.print(str); 
                  // non fa il flush perche` non e' println
                mySys.out.flush();
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
     * @param args  non usato
     */
    public static void main(String[] args) 
    {
    	System.err.println("Test PipeFile con SimpleSemaphore2 numerico");
        TestPipeFile pf = new TestPipeFile();
        System.err.println("Ora apre una finestra per ogni thread");
        Thread snd = pf.new Sender("sender");
        Thread rcv = pf.new Receiver("receiver");
        snd.start();
        rcv.start();
        try
        {
            snd.join();
            rcv.join();
        }
        catch( InterruptedException e ){}
        pf.pipe.close();
        System.err.println("** Terminato!");
        System.exit(0);
    } //[m][s] main
    
} //{c} TestPipeFile

