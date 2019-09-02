package osExtra;

import java.awt.*;
import java.awt.event.*;
import java.util.Date;

/**{c}
 * NowApplet -
 * esempio errato di clock giornaliero come Applet
 *
 * @version 1.00 02/03/12
 * @version 2.00 2005-10-07 package osExtra
 * @author M.Moro DEI UPD
 */

public class NowApplet extends java.applet.Applet 
{
    Font fo = new Font("TimesRoman",Font.BOLD, 24);
    Date now;

    public void init() 
    {
        System.out.println("init " + Thread.currentThread());
    } //[m] init

    public void start() 
    {
        System.out.println("start " + Thread.currentThread());
        while(true) {
            now = new Date();
            repaint();
            try { Thread.sleep(1000); }
            catch(InterruptedException e) {}
        }
    } //[m] start

    public void stop() 
    {
        System.out.println("stop " + Thread.currentThread());
    } //[m] stop

    public void paint(Graphics g) 
    {
        System.out.println("paint " + Thread.currentThread());
        g.setFont(fo);
        g.drawString(now.toString(),10,50);
    } //[m] paint

} //{c} NowApplet
