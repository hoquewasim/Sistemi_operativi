package os.ada;

import os.Util;
import os.Sys;
import os.Timeout;

/**{c}
 * test implementazione RV di Ada
 * 4 operazioni con guardie casuali
 * @author M.Moro DEI UNIPD
 * @version 1.00 2010-12-12
*/

public class ADAOper
{

    private static final String addStr = "add";
      // nome del selettore di somma
    private static final String subStr = "sub";
      // nome del selettore di sottrazione
    private static final String mulStr = "mul";
      // nome del selettore di moltiplicazione
    private static final String divStr = "div";
      // nome del selettore di divisione
    private static final String srvStr = "calc";
      // nome del server
    private static final String[] calStr = new String[4];
        
    static {
        calStr[0] = "add";
        calStr[1] = "sub";
        calStr[2] = "mul";
        calStr[3] = "div";
        } // static
          // nome del selettore di interesse

    /**{c}
     * contenitore per operandi
     */
    private class OpndObj
    {
        int op1,op2;
        
        /**[c]
         */
        OpndObj(int o1, int o2)
        {
            op1 = o1;
            op2 = o2;
        }
    } //{c} OpndObj

    /**{c}
     * contenitore per risultato
     */
    private class ResObj
    {
        int res;
        
        /**[c]
         */
        ResObj(int res)
        {
            this.res = res;
        }
    } //{c} ResObj

    /**{c}
     * thread server
     */
    private class CalcServ extends ADAThread
    {
        /**[c]
         * thread server per il test RV, 
         * @param name  nome del thread
         */
        public CalcServ(String name)
        {
            super(name);
        } //[c]
         
        /**[m]
         * test server di calcolo
         */
        public void run()
        {
            // il server ha 4 entry, uno per operazione
            // con guadie casuali
            
            // select unico
            Select sel = new Select();
            
            // entry add, choice=0
            sel.add(new Guard()
              {
                public boolean when()
                {
                    boolean ret = (Util.randVal(0, 1)==1);
System.out.println("------------- guard add ret="+ret);
                    return ret;
                } //[m] evaluate
              } //{c} <anonim>
              , addStr,
              new Entry()
              {
                public Object exec(Object inp)
                {
System.out.println("[[[entry add");        
                    return new ResObj(((OpndObj)inp).op1+
                      ((OpndObj)inp).op2);
                }
              } //{c} <anonim>
              );
    
            // entry sub, choice=1
            sel.add(new Guard()
              {
                public boolean when()
                {
                    boolean ret = (Util.randVal(0, 1)==1);
System.out.println("------------- guard sub ret="+ret);
                    return ret;
                } //[m] evaluate
              } //{c} <anonim>
              , subStr,
              new Entry()
              {
                public Object exec(Object inp)
                {
System.out.println("[[[entry sub");        
                    return new ResObj(((OpndObj)inp).op1-
                      ((OpndObj)inp).op2);
                }
              } //{c} <anonim>
              );

            // entry mul choice=2
            sel.add(new Guard()
              {
                public boolean when()
                {
                    boolean ret = (Util.randVal(0, 1)==1);
System.out.println("------------- guard mul ret="+ret);
                    return ret;
                } //[m] evaluate
              } //{c} <anonim>
              , mulStr,
              new Entry()
              {
                public Object exec(Object inp)
                {
System.out.println("[[[entry mul");        
                    return new ResObj(((OpndObj)inp).op1*
                      ((OpndObj)inp).op2);
                }
              } //{c} <anonim>
              );
    
            // entry div choice=3
            sel.add(new Guard()
              {
                public boolean when()
                {
                    boolean ret = (Util.randVal(0, 1)==1);
System.out.println("------------- guard div ret="+ret);
                    return ret;
                } //[m] evaluate
              } //{c} <anonim>
              , divStr,
              new Entry()
              {
                public Object exec(Object inp)
                {
System.out.println("[[[entry div");        
                    return new ResObj(((OpndObj)inp).op1/
                      ((OpndObj)inp).op2);
                }
              } //{c} <anonim>
              );
              
            while (true)
            {
                try {
                    int choice = sel.accept();
                    System.out.println("]]] choice="+choice+
                      " ("+sel.choice2Str(choice)+")");
                    
                } catch (AllGuardsClosedException e)
                {
                    System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ guardie chiuse->"+e+
                      "<-");
                }
            }
        } //[m] run     
    
    } //{c} CalcServ

    /**{c}
     * thread client chiamante
     */
    private class CalcCaller extends ADAThread
    {
        ADAThread serv;
          // thread servente
        private int minDelay, maxDelay;
          // ritardo minimo e massimo
        
        /**[c]
         * thread client per il test RV, 
         * @param name  nome del thread
         * @param srv  thread server
         * @param mind  delay minimo
         * @param maxd  delay massimo
         */
        public CalcCaller(String name, ADAThread srv, int mind, int maxd)
        {
            super(name);
            serv = srv;
            minDelay = mind;
            maxDelay = maxd;
        } //[c]
        
        /**[m]
         * test client
         */
        public void run()
        {

            // entry call
            int cnt = 1;

            while(true)
            {
                Util.rsleep(minDelay, maxDelay);
                int val1 = Util.randVal(-1000, 1000);
                int val2 = Util.randVal(-1000, 1000);
                int oper;
                do {
                    oper = Util.randVal(0, 3);
                    System.out.println("+++++ "+getName()+" cnt="+cnt+" oper="+oper+
                      " val2="+val2);
                } while(oper == 3 && val2 == 0);
                  // per evotare divisione per zero
                
                String calS = calStr[oper];
                System.out.println("+++ "+getName()+" cnt="+cnt+" Chiama "+calS+" con valori v1="+
                  val1+" v2="+val2);
                
                // impacchetta i due operandi
                CallOut ret = serv.entryCall(new OpndObj(val1, val2), 
                  calS, 2000);
                  // chiamata di RV
                if (ret.getTimeout()==Timeout.EXPIRED)
                    System.out.println("---- "+getName()+" cnt="+(cnt++)+" timeout EXPIRED");
                else
                    System.out.println("+++ "+getName()+" cnt="+(cnt++)+
                      " risultato res="+((ResObj)ret.getParams()).res+
                      " timeout="+(ret.getTimeout()==Timeout.INTIME ? "INTIME" : 
                      ret.getTimeout()));
            } // while
        } //[m] run
                    
    } // {c} CalcCaller 
        
    /**[m][s]
     * main di collaudo
     * @param args  not used
     */
    public static void main(String[] args) 
    {
    	int numPr = Sys.in.readInt("Test ADA RV: battere chiamanti:");
        ADAOper rv = new ADAOper();
 
        Thread srv = rv.new CalcServ(srvStr);
        Thread pr[] = new Thread[numPr];
        for(int i=1; i<=numPr; i++)
            pr[i-1] = rv.new CalcCaller("c"+i, (ADAThread)srv, 
            3000, 5000);
        System.err.println("** Battere Ctrl-C per terminare!");
        srv.start();
        for(int i=0; i<numPr; pr[i++].start());
    } //[m][s] main

} //{c} ADAOper

