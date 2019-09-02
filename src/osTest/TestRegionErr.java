package osTest;

import os.Region;
import os.RegionCondition;
import os.RegionNestingException;
import os.Timeout;
import os.Util;
import os.Sys;

/**{c}
 * test per regione critica innestata:
 * si puo' commettere un errore se non
 * si soddisfa la regola del livello crescente 
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2003-10-15
 * @version 2.00 2005-10-07 package os e osTest
 */

public class TestRegionErr
{
    private static final int TRLEVA = 11;
    private static final int TRLEVB = 22;
      // livelli di nesting della regione
    private Region regA = new Region(TRLEVA);
    private Region regB = new Region(TRLEVB);
      // regioni in nesting
      
    /**[m]
     * ingresso
     * @param inOrder  true se prima A e poi B
     * @return true
     */
    public boolean enter(boolean inOrder)
    { 
        if (inOrder)
        {
            if (regA.enterWhen(10000) == Timeout.EXPIRED)
                return false;
            System.out.println("         "+
            Thread.currentThread().getName()+" entrato in A");
            if (regB.enterWhen(10000) == Timeout.EXPIRED)
                return false;
            System.out.println("         "+
            Thread.currentThread().getName()+" entrato in B");
        }
        else
        {
            if (regB.enterWhen(10000) == Timeout.EXPIRED)
                return false;
            System.out.println("         "+
            Thread.currentThread().getName()+" entrato in B");
            if (regA.enterWhen(10000) == Timeout.EXPIRED)
                return false;
            System.out.println("         "+
            Thread.currentThread().getName()+" entrato in A");
        }
        return true;
    } //[m] enter

    /**[m]
     * uscita
     * @param inOrder  true se prima B e poi A
     */
    public void exit(boolean inOrder)
    { 
        if (inOrder)
        {
            regB.leave();
            System.out.println("         "+
            Thread.currentThread().getName()+" --- uscito da B");
            regA.leave();
            System.out.println("         "+
            Thread.currentThread().getName()+" --- uscito da A");
        }
        else
        {
            regA.leave();
            System.out.println("         "+
            Thread.currentThread().getName()+" --- uscito da A");
            regB.leave();
            System.out.println("         "+
            Thread.currentThread().getName()+" --- uscito da B");
        }
    } //[m] exit
    
    
    /**{c}
     * thread di test di allocazione
     * @author M.Moro DEI UNIPD
     * @version 1.0 2003-10-13
     */
    private static class TTAllTh extends Thread
    {
        TestRegionErr err;  // allocatore
        int minDelay, maxDelay;
          // range di tempo d'attesa
        
        /**[c]
         * thread per il test allocatore con regione critica
         * @param name  nome del thread
         * @param e  oggetto prova
         * @param mind  minima attesa
         * @param maxd  massima attesa
         */
        public TTAllTh(String name, TestRegionErr e, int mind, int maxd)
        {
            super(name);
            err = e;
            minDelay = mind;
            maxDelay = maxd;
        }
        
        /**[m]
         * test: attesa con timeout
         */
        public void run()
        {
            System.out.println("Attivato thread "+getName());
          try 
          {  
            while(true)
            {
                Util.rsleep(minDelay, maxDelay);
                System.out.println("---1 "+getName()+" entra in ordine");
                if (err.enter(true))
                    System.out.println("+++1 "+getName()+" dentro innestato");
                else                    
                    System.out.println("+++1a "+getName()+" NON ENTRATO");
                Util.rsleep(minDelay, maxDelay);
                System.out.println("---2 "+getName()+" esce in ordine");
                err.exit(true);

                Util.rsleep(minDelay, maxDelay);
                System.out.println("---1 "+getName()+" entra fuori ordine");
                if (err.enter(false))
                    System.out.println("+++1 "+getName()+" dentro innestato");
                else                    
                    System.out.println("+++1a "+getName()+" NON ENTRATO");
                Util.rsleep(minDelay, maxDelay);
                System.out.println("---2 "+getName()+" esce fuori ordine");
                err.exit(false);
            } // while
          } catch(RegionNestingException e)
            {
                System.out.println("!! err-->"+e);
                return; // termina
            }
                
        } //[m] run

    } // {c} TTAllTh

    /**[m][s]
     * main di collaudo
     * @param args  non usato
     */
    public static void main(String[] args) 
    {
        Sys.out.println("Prova regioni innestate con errore");
        TestRegionErr tr = new TestRegionErr();
        int numTh = Sys.in.readInt("Battere numero thread:");
        Thread th[] = new Thread[numTh];
        for(int i=1; i<=numTh; i++)
            th[i-1] = new TTAllTh("a"+i, tr, 1000, 2000);
        System.err.println("** Battere Ctrl-C per terminare!");
        for(int i=0; i<numTh; th[i++].start());
    } //[m][s] main

} //{c} TestRegionErr