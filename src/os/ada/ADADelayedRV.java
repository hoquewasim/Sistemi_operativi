package os.ada;

import os.Util;
import os.Sys;
import os.Timeout;

/**{c}
 * esempio di delayed RV
 * 4 operazioni
 * @author M.Moro DEI UNIPD
 * @version 1.00 2010-12-21
 * @version 1.01 2019-05-25  deprecated eliminati
*/

public class ADADelayedRV
{

    private static final String addStr = "add";
      // nome del selettore di somma
    private static final String subStr = "sub";
      // nome del selettore di sottrazione
    private static final String mulStr = "mul";
      // nome del selettore di moltiplicazione
    private static final String divStr = "div";
      // nome del selettore di divisione
    private static final String calc1Str = "calc1";
    private static final String calc2Str = "calc2";
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
            sel.add(addStr,
              new Entry()
              {
                public Object exec(Object inp)
                {
// v1.01 deprecated                    return new Integer(((OpndObj)inp).op1+
//                      ((OpndObj)inp).op2);
                    return  Integer.valueOf(((OpndObj)inp).op1+
                      ((OpndObj)inp).op2);
                }
              } //{c} <anonim>
              );
    
            // entry sub, choice=1
            sel.add(subStr,
              new Entry()
              {
                public Object exec(Object inp)
                {
// v1.01 deprecated                    return new Integer(((OpndObj)inp).op1-
//                      ((OpndObj)inp).op2);
                    return Integer.valueOf(((OpndObj)inp).op1-
                      ((OpndObj)inp).op2);
                }
              } //{c} <anonim>
              );

            // entry mul choice=2
            sel.add(mulStr,
              new Entry()
              {
                public Object exec(Object inp)
                {
// v1.01 deprecated                    return new Integer(((OpndObj)inp).op1*
//                      ((OpndObj)inp).op2);
                    return Integer.valueOf(((OpndObj)inp).op1*
                      ((OpndObj)inp).op2);
                }
              } //{c} <anonim>
              );
    
            // entry div choice=3
            sel.add(divStr,
              new Entry()
              {
                public Object exec(Object inp)
                {
// v.1.01 deprecated                    return new Integer(((OpndObj)inp).op1/
//                      ((OpndObj)inp).op2);
                    return Integer.valueOf(((OpndObj)inp).op1/
                      ((OpndObj)inp).op2);
                }
              } //{c} <anonim>
              );
              
            while (true)
            {
                sel.accept();
            }
        } //[m] run     
    
    } //{c} CalcServ

    /**{c}
     * thread client chiamante
     */
    private class CalcCaller extends ADAThread
    {
        /**[c]
         * thread client per il test RV, 
         * @param name  nome del thread
         */
        public CalcCaller(String name)
        {
            super(name);
        } //[c]
        
        /**[m]
         * test client
         */
        public void run()
        {

            // entry call
            int cnt = 1;
            ADAThread srv1 = new CalcServ(calc1Str);
            ADAThread srv2 = new CalcServ(calc2Str);
            srv1.start();
            srv2.start();
            Util.sleep(2000);

            while(true)
            {
                Util.sleep(1000);
                int val1 = Util.randVal(0, 20);
                int val2 = Util.randVal(0, 20);
                int val3 = Util.randVal(0, 20);
                int val4 = Util.randVal(0, 20);
                System.out.println("+++ "+getName()+" cnt="+cnt+
                " val1="+val1+" val2="+val2+" val3="+val3+" val4="+val4);
                // impacchetta i due operandi
                CallIn ret1 = delayedEntryCall(new OpndObj(val1, val2), 
                  calc1Str, calStr[2]);
                CallIn ret2 = delayedEntryCall(new OpndObj(val3, val4), 
                  calc2Str, calStr[2]);
                  // chiamata delayed
                System.out.println("+++ "+getName()+" cnt="+cnt+
                " ha attivato le due somme e ora attende risultati");
                Util.sleep(1000);
                CallOut rep1 = replyWait(ret1, 2000);
                if (rep1.getTimeout()==Timeout.EXPIRED)
                {
                    System.out.println("---- "+getName()+" cnt="+(cnt++)+" timeout 1 EXPIRED");
                    CallOut rep2 = replyWait(ret2, 10);
                    continue;
                }
                CallOut rep2 = replyWait(ret2, 2000);
                if (rep2.getTimeout()==Timeout.EXPIRED)
                {
                    System.out.println("---- "+getName()+" cnt="+(cnt++)+" timeout 2 EXPIRED");
                    continue;
                }
                CallOut rep3 = entryCall(new OpndObj(
                  ((Integer)rep1.getParams()).intValue(),
                  ((Integer)rep2.getParams()).intValue()),
                  calc1Str, calStr[0], 2000);
                System.out.println("+++ "+getName()+" cnt="+(cnt++)+
                  " risultato res="+((Integer)rep3.getParams())+
                  " timeout="+(rep3.getTimeout()==Timeout.INTIME ? "INTIME" : 
                      rep3.getTimeout()));
            } // while
        } //[m] run
                    
    } // {c} CalcCaller 
        
    /**[m][s]
     * main di collaudo
     * @param args  not used
     */
    public static void main(String[] args) 
    {
        ADADelayedRV rv = new ADADelayedRV();
        Thread cli = rv.new CalcCaller("caller");
        System.err.println("** Battere Ctrl-C per terminare!");
        cli.start();
    } //[m][s] main

} //{c} ADADelayedRV

