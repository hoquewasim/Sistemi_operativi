package osExtra;

import java.util.Date;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import os.Util;

/**{c}
 * remote date:
 * esempio d'uso di RMI con data remota -
 * implementazione dell'oggetto remoto
 * @author M.Moro DEI UNIPD
 * @version 1.00 2003-10-13
 * @version 2.00 2005-10-07 package osExtra
 * @version 2.01 2013-05-08 aggiunta disgnostica e generalizz. indirizzo
 */

public class RemDateImpl
  extends UnicastRemoteObject implements RemDate 
{
    private static boolean stop = false;
      // per terminare

    /**[c]
     * per dichiarare l'eccezione
     * @see UnicastRemoteObject
     */
    public RemDateImpl() throws RemoteException {}
    
    /**[m]
     * metodo remotizzato
     * @return data corrente
     */
    public Date getDate () throws RemoteException 
    { 
        System.out.println(
          "** RemDate: getDate() thread="+Thread.currentThread()+
            " isDaemon="+Thread.currentThread().isDaemon());
        tlist();
        return new Date(); 
    }

    /**[m]
     * metodo remotizzato
     */
    public void close () throws RemoteException 
    { 
        try {
            Naming.unbind("///RemDateImpl"); 
            System.out.println(
              "** RemDate: unbind eseguito!");
            tlist();
            stop=true;
        } catch(Exception e)
        { 
            System.out.println(e);
            e.printStackTrace();
        }
    } //[m] close
    
    private static void tlist()
    {
        Thread[] ta = new Thread[Thread.activeCount()];
        Thread.enumerate(ta);
        System.out.println("----------Thread list tot="+ta.length);
        for (int i=0; i<ta.length; i++)
            System.out.println("i="+i+" thread="+ta[i]+
            " isDaemon="+ta[i].isDaemon()+" group="+
              ta[i].getThreadGroup());
    }

    /**[m][s]
     * main di avvio
     * @param args  non usato
     */
    public static void main(String args[]) 
    {
        RemDateImpl date;
        try {
// se si vuole utilizzare un registro locale
// a questa applicazione
//            System.out.println("TestRemDate: crea rif. registro");
//            LocateRegistry.createRegistry(1099);
            System.out.println(
              "RemDate: crea oggetto remotizzato");
            date = new RemDateImpl();
            System.out.println("RemDate: bind oggetto");
            Naming.rebind("//localhost/RemDateImpl", date);
            tlist();
            System.out.println("Pronto per RMI sull'oggetto");
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }

        while (!stop)
        {
            Util.sleep(2000);
         }
        System.out.println("^^^^^^^^^ MAIN STA PER TERMINARE!");
        ThreadGroup tg;
        System.out.println("**** parent="+
          (tg=Thread.currentThread().getThreadGroup().getParent()));
        Thread[] ta = new Thread[tg.activeCount()];
        tg.enumerate(ta);
        System.out.println("Thread list tot="+ta.length);
        for (int i=0; i<ta.length; i++)
        {
            System.out.println("i="+i+" thread="+ta[i]+
            " isDaemon="+ta[i].isDaemon()+" group="+
              ta[i].getThreadGroup());
        }
        System.out.println("^^^^^^^^^ FRA 20 s MAIN TERMINA!");
        Util.sleep(20000);
        for (int i=0; i<ta.length; i++)
        {
            if (!ta[i].isDaemon())
                ta[i].interrupt();
        }
    } //[m][s] main
    
} //{c} RemDateImpl
