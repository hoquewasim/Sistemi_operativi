package osTest;

import os.SimpleMonitor; 
import os.Util; 
import os.Sys; 
 
/**{c}
 * test allocatore globale 1/2 risorse 
 * come SimpleMonitor
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-04-21
 * @version 2.00 2003-10-03 package OsTest 
 * @version 2.01 2005-10-07 package os
 */
 
public class TestAllSimpleMon extends SimpleMonitor
{
    private Condition wait1 = new Condition();
      // attesa per una risorsa
    private Condition wait2 = new Condition();
      // attesa per due risorse
    private int avalRes;
      // risorse disponibili
      
    /**[c]
     * allocatore 1-2 risorse
     * @param res  risorse iniziali
     */
    public TestAllSimpleMon(int res)
    {
        avalRes = res;
        System.out.println("         <"+avalRes+"> risors"+(avalRes==1 ? "a" : "e"));
    } 
    
    /**{c}
     * thread di test di allocazione
     * @author M.Moro DEI UNIPD
     * @version 1.0 2002-04-21
     */
    private static class TTAllTh extends Thread
    {
        TestAllSimpleMon alloc12;  // allocatore
        int minDelay, maxDelay;
          // range di tempo d'attesa
        
        /**[c]
         * thread per il test allocatore 1-2
         * @param name  nome del thread
         * @param a  allocatore
         * @param mind  minima attesa
         * @param maxd  massima attesa
         */
        public TTAllTh(String name, TestAllSimpleMon a, int mind, int maxd)
        {
            super(name);
            alloc12 = a;
            minDelay = mind;
            maxDelay = maxd;
        }
        
        /**[m]
         * test: attesa con timeout
         */
        public void run()
        {
            System.out.println("Attivato thread "+getName());
            while(true)
            {
                Util.rsleep(minDelay, maxDelay);
                int choice = Util.randVal(1, 2);
                switch(choice)
                {
                  case 1:
                    System.out.println(getName()+" chiede 1 risorsa");
                    alloc12.get1();
                    System.out.println("+++1 "+getName()+" ottenuta 1 risorsa");
                    Util.rsleep(minDelay, maxDelay);
                    System.out.println("---1 "+getName()+" restituisce 1 risorsa");
                    alloc12.put(1);
                    break;
                  case 2:
                    System.out.println(getName()+" chiede 2 risorsa");
                    alloc12.get2();
                    System.out.println("+++2 "+getName()+" ottenute 2 risorse");
                    Util.rsleep(minDelay, maxDelay);
                    System.out.println("---2 "+getName()+" restituisce 2 risorse");
                    alloc12.put(2);
                    break;
                }
            } // while
        } //[m] run
                    
    } // {c} TTAllTh
        
    /**[m]
     * acquisisce 1 risorsa
     */
    public synchronized void get1()
    {
        if (avalRes >= 1 && wait2.queue()==0)
            // ci sono abbastanza risorse e si da`
            // priorita` a quelli che chiedono 2 risorse
        {
            avalRes--;
            System.out.println("         <"+avalRes+"> risors"+(avalRes==1 ? "a" : "e"));
            return;
        }
        // deve attendere
        wait1.cWait();
        // quando si risveglia la risorsa e`
        // gia` allocata;
        // si deve far cosi` perche` non c'e`
        // garanzia che al risveglio rientri
        // per primo nel monitor
    }
    
    /**[m]
     * acquisisce 2 risorsa
     */
    public synchronized void get2()
    {
        if (avalRes >= 2)
            // ci sono abbastanza risorse 
        {
            avalRes-=2;
            System.out.println("         <"+avalRes+"> risors"+(avalRes==1 ? "a" : "e"));
            return;
        }
        // deve attendere
        wait2.cWait();
        // quando si risveglia le risorse sono
        // gia` allocate;
        // si deve far cosi` perche` non c'e`
        // garanzia che al risveglio rientri
        // per primo nel monitor
    }
    
    /**[m]
     * restituisce le risorse acquisite
     * @param res  risorse restituite
     */
    public synchronized void put(int res)
    {
        avalRes += res;
        System.out.println("         <"+avalRes+"> risors"+(avalRes==1 ? "a" : "e"));
        int waiting2 = wait2.queue();
        while (avalRes >= 2 && waiting2>0)
        {
            avalRes -= 2;
            System.out.println("         <"+avalRes+"> risors"+(avalRes==1 ? "a" : "e"));
            wait2.cSignal();
              // il signal non libera immediatamente chi attende
            waiting2--;
        }
        int waiting1 = wait1.queue();
        while (avalRes >= 1 && waiting1>0)
        {
            avalRes -= 1;
            System.out.println("         <"+avalRes+"> risors"+(avalRes==1 ? "a" : "e"));
            wait1.cSignal();
              // il signal non libera immediatamente chi attende
            waiting1--;
        }
    } //[m] put
        

    /**[m][s]
     * main di collaudo
     * @param args  non usato
     */
    public static void main(String[] args) 
    {
    	int numRes = Sys.in.readInt("Test SimpleMonitor con allocatore 1-2: battere numero risorse:");
        TestAllSimpleMon ta = new TestAllSimpleMon(numRes);
        int numTh = Sys.in.readInt("Battere numero thread:");
        Thread th[] = new Thread[numTh];
        for(int i=1; i<=numTh; i++)
            th[i-1] = new TTAllTh("a"+i, ta, 3000, 5000);
        System.err.println("** Battere Ctrl-C per terminare!");
        for(int i=0; i<numTh; th[i++].start());
    } //[m][s] main
    
} //{c} TestAllSimpleMon

