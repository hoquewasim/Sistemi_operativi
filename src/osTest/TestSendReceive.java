package osTest;

import os.SendRcv;
import os.CMsg;
import os.TASys;
import os.Sys;

/**{c}
 * test send-receive diretto
 * sincrono o asincrono 
 * @author M.Moro DEI UNIPD
 * @version 1.0 2002-05-04
 * @version 2.00 2003-10-03 package OsTest
 * @version 2.01 2005-10-07 package os e osTest
 */

public class TestSendReceive
{
    SendRcv control = new SendRcv();
            
    /**{c}
     * numero clonabile
     * @author M.Moro DEI UNIPD
     * @version 1.0 2002-05-04
     */
    private class NMsg extends CMsg
    {
        int msg;  // da clonare
        
        public String toString()
        { return Integer.toString(msg); }
    }
    
    /**{c}
     * thread invio/ricezione
     * @author M.Moro DEI UNIPD
     * @version 1.0 2002-05-04
     */
    private class Runner extends Thread
    {
        /**[c]
         * thread per il test send-receive
         * @param name  nome del thread
         */
        public Runner(String name)
        {  super(name); }
        
        /**[m]
         * test: produce messaggio
         */
        public void run()
        {
            NMsg myMsg = new NMsg();
            TASys mySys = control.register();
              // si registra
            System.out.println("Attivato thread "+getName());
            while(true)
            {
                String mtx = mySys.in.readLine("Invia o riceve (I,r)? ");
                boolean invia = true;
                if (mtx.length() != 0 &&
                  Character.toUpperCase(mtx.charAt(0)) == 'R')
                    invia = false;
                if(invia)
                {
                    // invio
                    while(true)
                    {
                        try 
                        {
                            myMsg.msg = mySys.in.readInt(
                              "Battere numero da inviare ");
                            break;
                        }
                        catch(NumberFormatException e)
                        {
                            Sys.err.println("Non numero: "+e);
                        }
                    } // while
                    String sy = mySys.in.readLine("Sincrono o asincrono (S,a)? ");
                    boolean sync = true;
                    if (sy.length() != 0 &&
                      Character.toUpperCase(sy.charAt(0)) == 'A')
                        sync = false;
                    while(true)
                    {
                        String receiver = mySys.in.readLine("Battere a chi invia: ");
                        if (control.send(myMsg, receiver, sync))
                            // ok
                            break;
                    } // while
                }
                else
                {
                    // ricezione
                    String sender = mySys.in.readLine("Battere da chi si vuol ricevere: ");
                    Object rcv = control.receive(sender);
                    if (rcv != null)
                        mySys.out.println("*** ricevuto msg: "+rcv);
                } // if
            } //while
        } //[m] run
                        
                    
    } // {c} Runner
        
    /**[m][s]
     * main di collaudo
     * @param args  non usato
     */
    public static void main(String[] args) 
    {
    	int numTh = Sys.in.readInt("Test Send-Receive: battere numero thread:");
        TestSendReceive sr = new TestSendReceive();
        Thread th[] = new Thread[numTh];
        System.err.println("Ora apre una finestra per ogni thread");
        for(int i=1; i<=numTh; i++)
            th[i-1] = sr.new Runner("t"+i);
        System.err.println("** Battere Ctrl-C per terminare!");
        for(int i=0; i<numTh; th[i++].start());
    } //[m][s] main
    
} //{c} TestSendReceive

