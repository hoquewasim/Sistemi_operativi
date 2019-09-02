package os.ada;

import os.Util;

/**{c}
 * esempio Box del capitolo 13 del libro
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2010-12-30
 * @version 1.01 2019-05-25 deprecated eliminato
*/

public class ADABox
{
    class Box extends ADAThread
    {
        private Object v;
          // buffer

        /**[c]
         * thread server Box
         * @param name  nome del thread
         */
        public Box(String name)
        {
            super(name);
        } //[c]

        /**[m]
        * server di inc/dec
         */
        public void run()
        {
        
            Select sel1 = new Select();

            // entry put, senza guardia, choice=0
            sel1.add("put",
              new Entry()
              {
                public Object exec(Object inp)
                {
                    // parametro trattato in astratto
                    v = inp;
                    return null;
                }
              }
              );

            Select sel2 = new Select();

            // entry get, senza guardia, choice=0
            sel2.add("get",
              new Entry()
              {
                public Object exec(Object inp)
                {
                    // parametro trattato in astratto
                    return v;
                }
              }
              );

            while (true)
            {
                sel1.accept();
                sel2.accept();
            }
        } //[m] run

    } //{c} Box

    /**[m][s]
     * main di collaudo
     * @param args  not used
     */
    public static void main(String[] args) 
    {
        ADABox b = new ADABox();
        ADAThread th = b.new Box("BOX");
        th.start();  // parte il server
        Util.sleep(2000);
          // attende allocazione dei servizi
        
        System.err.println("** put");
// v1.01 deprecated        th.entryCall(new Integer(27), "BOX", "put");
        th.entryCall(Integer.valueOf(27), "BOX", "put");
        System.err.println("** get");
        CallOut ret = th.entryCall(null, "BOX", "get");
        System.err.println("** Val="+(Integer)ret.getParams());
        Util.sleep(2000);
        System.exit(0);
        
    } //[m][s] main

    
} //{c} ADABox
