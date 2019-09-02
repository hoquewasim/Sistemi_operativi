package osExtra;

import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;

/**{c}
 * AppletDiag2.java
 * esempio di applet elementare 
 * con diagnostica su stdout e costruttore
 *
 * @version 1.00 02/03/18
 * @version 2.00 2005-10-07 package osExtra
 * @author M.Moro DEI UPD
 */

public class AppletDiag2 extends Applet 
{
    private static int cnt0 = 1; // del costruttore
    private int cnt1 = 1;
    private int cnt2 = 1;
    private int cnt3 = 1;
    private int cnt4 = 1;
    private int cnt5 = 1;

    /**[m]
     * @see Applet
     */
    public AppletDiag2() 
    {
        super();
        System.out.println("Costruttore default cnt0=" + (cnt0++));
    } //[m] init

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
    } //[m] start

    /**[m]
     * @see Applet
     */
    public void stop() 
    {
        System.out.println("stop cnt3=" + (cnt3++));
    } //[m] stop

    /**[m]
     * @see Applet
     */
    public void destroy() 
    {
        System.out.println("destroy cnt4=" + (cnt4++));
    } //[m] stop

    /**[m]
     * @see Applet
     */
    public void paint(Graphics g) 
    {
        System.out.println("paint cnt5=" + cnt5);
        g.drawString(
          "Sovrapporre un'altra finestra e commutare", 5, 50);
        g.drawString("cnt5=" + (cnt5++), 5, 70);
    } //[m] paint

} //{c} AppletDiag2
