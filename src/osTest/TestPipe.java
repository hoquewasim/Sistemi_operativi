package osTest;

import os.Pipe;
import os.Util;

/**{c}
 * test pipe di byte
 * @author M.Moro DEI UNIPD
 * @version 1.00 2003-10-03
 * @version 2.00 2005-10-07 package os e osTest
 * @version 2.01 2014-05-07 ora termina
 */

public class TestPipe
{
    private static final int MAXBYTES = 10;
      // capacita` del pipe
    private static final int REQ = 6;
      // richiesta del ricevitore
    private static final int MAXWRITE = MAXBYTES-REQ;
      // richiesta del ricevitore
    
    private Pipe pi = new Pipe(MAXBYTES);
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
            byte data[] = new byte[30];
            int num=0;
            System.out.println("Attivato thread "+getName());
            for (int cnt=1; cnt<=10; cnt++)
            {
//                int num = Util.randVal(1, MAXWRITE);
                num = Util.randVal(1, 30);
                for(int i=0; i<num; i++)
                    data[i] = (byte)((int)'0'+num);
                for(int i=num; i<30; i++)
                    data[i] = (byte)'0';
                System.out.println("++ num="+num+
                  " scrive->"+(new String(data))+
                  "<- (elem nel pipe)="+pi.size());
                pi.write(num, data);
                System.out.println("++++ num="+num+
                  " scritto->"+(new String(data))+
                  "<- (elem nel pipe)="+pi.size());
                Util.rsleep(500, 2000);
            } //while
            data[0]='*';
            pi.write(6, data);
            System.out.println("++++ num="+num+
              " scritto->"+(new String(data))+
               "<- (elem nel pipe)="+pi.size()+
               " E TERMINA!");
            
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
            byte data[] = new byte[6];
            System.out.println("Attivato thread "+getName());
            while(true)
            {
                String s;
                for(int i=0; i<data.length; data[i++] = 0);
//                pi.read(REQ, data);
                pi.read(6, data);
                System.out.println("------------->"+
                  (s=new String(data))+"<-");
                if (s.contains("*"))
                    break;
            } //while
        } //[m] run
                    
    } // {c} Receiver
        
    /**[m][s]
     * main di collaudo
     * @param args  non usato
     */
    public static void main(String[] args) 
    {
    	System.err.println("Test Pipe");
        TestPipe tp = new TestPipe();
        System.err.println("** Battere Ctrl-C per terminare!");
        Thread snd = tp.new Sender("sender");
        Thread rcv = tp.new Receiver("receiver");
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

