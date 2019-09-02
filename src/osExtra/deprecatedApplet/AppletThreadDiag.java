package osExtra;

import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;

/**{c}
 * AppletThreadDiag
 * esempio di applet elementare eseguito come
 * thread con diagnostica su stdout (sulla 
 * finestra di console java nel caso di un browser) -
 * l'esecuzione come thread garantisce l'aggiornamento
 * periodico della finestra
 *
 * @version 1.00 98/12/08
 * @version 1.01 99/09/30 -
 * Thread.stop() deprecato, sostituito
 * con stopFlag
 * @version 2.00 2005-10-07 package osExtra
 * @author M.Moro DEI UPD
 */

public class AppletThreadDiag extends Applet 
implements Runnable 
{
    private static int cnt1 = 1;
    private static int cnt2 = 1;
    private static int cnt3 = 1;
    private static int cnt4 = 1;
    private static int cnt5 = 1;
    private Thread runner;
/*>> 1.1 Thread.stop deprecato
    variabile valutata in run()
*/
    private boolean stopFlag = false;
/*<< */

    /**[m]
     * @see Applet
     */
    public void init() 
    {
        System.out.println("init cnt1=" + (cnt1++));
    } //[m] init

    /**[m]
     * @see Applet
     */
    public void start() 
    {
        System.out.println("start cnt1=" + (cnt2++));
        if (runner == null)
        {
            // crea il thread che fa il repaint periodico
/*>> 1.1 Thread.stop deprecato
*/
            stopFlag = false;
/*<< */
            runner = new Thread(this);
            runner.start();
              // attiva il thread (il metodo run())
        }
    } //[m] start

    /**[m]
     * @see Applet
     */
    public void stop() 
    {
        System.out.println("stop cnt3=" + (cnt3++));
        if (runner != null)
        {
            // distrugge il thread di repaint
/*>> 1.1 Thread.stop deprecato
     sostituito con una variabile valutata periodicamente
     in run()
            runner.stop();
*/
            stopFlag = true;
/*<< */
            runner = null;
        }
    } //[m] stop

    /**[m]
     * @see Runnable
     */
    public void run() 
    {
        // metodo principale del thread
/*>> 1.1 Thread.stop deprecato
        while (true)
*/
        while (!stopFlag)
/*<< */
        {
            System.out.println("repaint cnt5=" + (cnt5++));
            repaint();
            try
            {
                Thread.sleep(4000);
            }
            catch (InterruptedException e)
            {
            }
        } // while
    } //[m] run

    /**[m]
     * @see Applet
     */
    public void paint(Graphics g) 
    {
        System.out.println("paint cnt4=" + cnt4);
        g.drawString("cnt4=" + (cnt4++), 5, 50);
    }

} //{c} AppletThreadDiag
