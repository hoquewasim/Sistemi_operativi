package osExtra;

import java.awt.*;
import java.awt.event.*;
import java.util.Date;

/**{c}
 * Now -
 * esempio Frame di clock giornaliero
 *
 * @version 1.00 02/03/11
 * @version 2.00 2005-10-07 package osExtra
 * @version 2.01 2005-10-21 show() -&gt; setVisible(true);
 * @author M.Moro DEI UPD
 */

public class Now extends Frame 
{
    Font fo = new Font("TimesRoman",Font.BOLD, 24);
    Date now;

    /**[c]
     * costruttore base, aggiunge al frame tutti i
     * componenti previsti
     */
    public Now() {
        super("Clock giornaliero");
        setSize(new Dimension(400, 200));
        // [2.01]
        // show();
        setVisible(true);
        while(true)
        {
            now = new Date();
            repaint();
            try { Thread.sleep(1000); }
            catch(InterruptedException e) {}
        }
    } //[c] Now

    /**[m]
     * disegno grafico
     * @see Frame
     */
    public void paint(Graphics g)
    {
        g.setFont(fo);
        if (now != null)        
            g.drawString(now.toString(), 10, 50);
    }

    /**[m][s]
     * metodo di attivazione
     * @param args  non usato
     */
    public static void main(String[] args) 
    {
        Now me = new Now();
    } //[m][s] main

} //{c} Now
