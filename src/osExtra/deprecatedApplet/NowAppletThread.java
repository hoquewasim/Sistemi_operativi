package osExtra;

import java.awt.*;
import java.awt.event.*;
import java.util.Date;

/**{c}
 * NowAppletThread -
 * esempio multithreading di clock giornaliero come Applet
 *
 * @version 1.00 02/03/12
 * @version 2.00 2005-10-07 package osExtra
 * @author M.Moro DEI UPD
 */

public class NowAppletThread extends java.applet.Applet 
implements Runnable
{
    Font fo = new Font("TimesRoman",Font.BOLD, 24);
    Date now;
    Thread runner;

    public void init() 
    {
        System.out.println("init " + Thread.currentThread());
        now = new Date();
    } //[m] init

    public void start() 
    {
        System.out.println("start " + Thread.currentThread());
/*        
codice piu' sicuro
        if (runner == null)
        {
            // crea una sola volta il thread che fa il repaint periodico
            runner = new Thread(this);
            runner.start();
              // attiva il thread (il metodo run())
        }
*/
        // versione semplificata
        (new Thread(this)).start();        
    } //[m] start

    public void run() 
    {
        System.out.println("run " + Thread.currentThread());
        // per aver tempo di guardare la console
        now = new Date();
        repaint();
        try { Thread.sleep(20000); }
        catch(InterruptedException e) {}
        while(true) {
            now = new Date();
            repaint();
            try { Thread.sleep(1000); }
            catch(InterruptedException e) {}
            System.out.println("run " + Thread.currentThread());
        }
    } //[m] run

    public void stop() 
    {
        System.out.println("stop " + Thread.currentThread());
        // il thread creato non viene 'ucciso' o fermato
    } //[m] stop

    public void paint(Graphics g) 
    {
        System.out.println("paint " + Thread.currentThread());
        g.setFont(fo);
        if (now != null)
            g.drawString(now.toString(),10,50);
    } //[m] paint

} //{c} NowAppletThread
