package os.ada;

import os.Util;
import os.ada.ADAThread.Select;

/**{c}
 * esempio di RV di Ada con classe che estende altra classe
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2010-12-19
 * @version 1.01 2019-05-25  deprecated eliminato
*/

public class ADAIncDec
{
    static final String srvStr = "incdecserv";
      // nome del server
    static final String incStr = "inc";
      // nome del selettore di inc
    static final String decStr = "dec";
      // nome del selettore di dec
    static final String valStr = "val";
      // nome del selettore di valore
    private static final int MAXVAL = 5;

    class ADAIncDecServ extends IncDec implements Runnable
    {
        private ADAThread th;
          // RV di supporto

        /**[c]
         * thread server RV che non estende ADAThread,
         * @param name  nome del thread
         */

        /**[m]
        * imposta thread RV di supporto
        * @param th l'istanza di ADAThread di supporto
        */
        public void setTh(ADAThread th)
        {
            this.th = th;
        } // [m] setTh

        /**[m]
        * server di inc/dec
         */
        public void run()
        {
// System.out.println("Server attivo, imposta select");
        
            // select unico
            Select sel = th.new Select();

            // entry inc, choice=0
            sel.add(new Guard()
              {
                public boolean when()
                {
                    return val()<MAXVAL;
                } //[m] evaluate
              } //{c} <anonim>
              , incStr,
              new Entry()
              {
                public Object exec(Object inp)
                {
                    // parametri in-out non significativi
                    inc();
                    return null;
                }
              } //{c} <anonim>
              );

            // entry dec, choice=1
            sel.add(new Guard()
              {
                public boolean when()
                {
                    return val()>-MAXVAL;
                } //[m] evaluate
              } //{c} <anonim>
              , decStr,
              new Entry()
              {
                public Object exec(Object inp)
                {
                    // parametri in-out non significativi
                    dec();
                    return null;
                }
              } //{c} <anonim>
              );

            // entry val choice=2
            sel.add(// val sempre possibile
              valStr,
              new Entry()
              {
                public Object exec(Object inp)
                {
// v.1.01 deprecated                    return new Integer(val());
                    return Integer.valueOf(val());
                }
              } //{c} <anonim>
              );

            while (true)
                sel.accept();
        } //[m] run

    } //{c} IncDecRVServ

    /**[m][s]
     * main di collaudo
     * @param args  not used
     */
    public static void main(String[] args) 
    {
        ADAIncDec rv = new ADAIncDec();
        ADAIncDecServ srv = rv.new ADAIncDecServ();
        ADAThread th = new ADAThread(srv, srvStr);
        srv.setTh(th);
        th.start();  // parte il server
        Util.sleep(2000);
        
        System.err.println("** Inc");
        CallOut ret = th.entryCall(null, srvStr, incStr);
                  // chiamat di RV
        System.err.println("** Val");
        ret = th.entryCall(null, srvStr, valStr);
        System.err.println("** Val="+(Integer)ret.getParams());
        System.err.println("** Dec");
        ret = th.entryCall(null, srvStr, decStr);
                  // chiamat di RV
        System.err.println("** Val");
        ret = th.entryCall(null, srvStr, valStr);
        System.err.println("** Val="+(Integer)ret.getParams());
        Util.sleep(2000);
        System.exit(0);
        
    } //[m][s] main

    
} //{c} ADAIncDec
