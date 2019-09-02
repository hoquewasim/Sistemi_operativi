package osTest;

import os.All12Region; 
import os.Region;
import os.RegionCondition;
import os.Util;
import os.Sys; 

/**{c}
 * test allocatore globale 1/2 risorse 
 * mediante regioni critiche
 * @author M.Moro DEI UNIPD
 * @version 1.00 2003-10-14
 * @version 2.00 2005-10-07 package os e osTest
 */

public class TestAll12Reg
{
    private All12Region allReg;
      // allocatore 12
      
    /**[c]
     * allocatore 1-2 risorse
     * @param res  risorse iniziali
     */
    public TestAll12Reg(int res)
    {
        allReg = new All12Region(res);
        System.out.println("         <"+allReg.free()+"> risorse");
    } 
    
    /**{c}
     * thread di test di allocazione
     * @author M.Moro DEI UNIPD
     * @version 1.0 2002-04-21
     */
    private class TTAllTh extends Thread
    {
        int minDelay, maxDelay;
          // range di tempo d'attesa
        
        /**[c]
         * thread per il test allocatore 1-2
         * @param name  nome del thread
         * @param mind  minima attesa
         * @param maxd  massima attesa
         */
        public TTAllTh(String name, int mind, int maxd)
        {
            super(name);
            minDelay = mind;
            maxDelay = maxd;
        }
        
        /**[m]
         * test: attesa con timeout
         */
        public void run()
        {
            System.out.println("Attivato thread "+getName());
            int choice = Util.randVal(1, 3);
            while(true)
            {
                Util.rsleep(minDelay, maxDelay);
                switch(choice)
                {
                  case 1:
                    System.out.println(getName()+" chiede 1 risorsa");
                    allReg.alloc11();
                    System.out.println("+++1 "+getName()+" ottenuta 1 risorsa");
                    Util.rsleep(minDelay, maxDelay);
                    System.out.println("---1 "+getName()+" restituisce 1 risorsa");
                    allReg.free(1);
                    break;
                  case 2:
                    System.out.println(getName()+" chiede 2 risorse");
                    allReg.alloc22();
                    System.out.println("+++2 "+getName()+" ottenute 2 risorse");
                    Util.rsleep(minDelay, maxDelay);
                    System.out.println("---2 "+getName()+" restituisce 2 risorse");
                    allReg.free(2);
                    break;
                  case 3:
                    System.out.println(getName()+" chiede 1^ di 2 risorse");
                    allReg.alloc21a();
                    System.out.println("+++2 "+getName()+" ottenuta 1^ di 2 risorse");
                    Util.rsleep(minDelay, maxDelay);
                    System.out.println(getName()+" chiede 2^ di 2 risorse");
                    allReg.alloc21b();
                    System.out.println("+++2 "+getName()+" ottenute 2^ di 2 risorse");
                    Util.rsleep(minDelay, maxDelay);
                    System.out.println("---2 "+getName()+" restituisce 2 risorse");
                    allReg.free(2);
                    break;
                }
            } // while
        } //[m] run
                    
    } // {c} TTAllTh

    /**[m][s]
     * main di collaudo
     * @param args  non usato
     */
    public static void main(String[] args) 
    {
        int numRes = Sys.in.readInt("Test Alloc. 1-2 con reg. critiche: battere numero risorse:");
        TestAll12Reg ta = new TestAll12Reg(numRes);
        int numTh = Sys.in.readInt("Battere numero thread:");
        Thread th[] = new Thread[numTh];
        for(int i=1; i<=numTh; i++)
            th[i-1] = ta.new TTAllTh("a"+i, 3000, 5000);
        System.err.println("** Battere Ctrl-C per terminare!");
        for(int i=0; i<numTh; th[i++].start());
    } //[m][s] main
    
} //{c} TestAll12Reg

