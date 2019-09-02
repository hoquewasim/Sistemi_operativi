package osTest;

import os.Monitor; 
import os.Util; 
import os.Timeout; 
 
/**{c}
 * test per timeout nel Monitor
 * @author M.Moro DEI UNIPD
 * @version 1.00 2004-11-09
 * @version 2.00 2005-10-07 package os e osTest
 */
 
public class TestTimedMon extends Monitor
{
    private Condition w = new Condition();
    private int data;
      
    /**{c}
     * thread inviante
     */
    private class TTMTh1 extends Thread
    {
        
        /**[m]
         * test: invio senza attesa
         */
        public void run()
        {
            int cnt = 1;
            
            System.out.println("Attivato sender");
            while(true)
            {
                Util.rsleep(2000, 8000);
                System.out.println("Sender invia "+cnt);
                send(cnt++);
            }
        } // run
    }//{c} TTMTh1

    /**{c}
     * thread ricevente con timeout
     */
    private class TTMTh2 extends Thread
    {
        
        /**[m]
         * test: attesa con timeout
         */
        public void run()
        {
            int cnt=1;
            
            System.out.println("Attivato receiver");
            while(true)
            {
                Util.rsleep(1000, 4000);
                long tmo = Util.randVal(1000L, 4000L);
                System.out.println("Ricezione "+
                  (cnt++)+" tmo="+tmo);
                long ret = receive(tmo);
                if (ret==Timeout.EXPIRED)
                    System.out.println("Ricezione SPIRATA **, ret="+ret);
                else
                    System.out.println("Ricevuto Ok, ret="+ret);
            }
        } // run
    }//{c} TTMTh2
                
    /**[m]
     * invio senza attesa
     * @param d  dato da inviare
     */
    public void send(int d)
    {
        mEnter();
        data=d;
        w.cSignal();
        mExit();
    } //[m] send
    
    /**[m]
     * ricezione con timeout
     * @param timeout  in ms
     * @return residual timeout
     */
    public long receive(long timeout)
    {
        mEnter();
        long ret = w.cWait(timeout);
        if (ret != Timeout.EXPIRED)
            System.out.println("->"+data+"<-");
        mExit();
        return ret;
    } //[m] send
    
    /**[m][s]
     * main di collaudo
     * @param args  non usato
     */
    public static void main(String[] args) 
    {
        TestTimedMon tm = new TestTimedMon();
        tm.new TTMTh1().start();
        tm.new TTMTh2().start();
    } //[m][s] main
    
} //{c} TestTimedMon

