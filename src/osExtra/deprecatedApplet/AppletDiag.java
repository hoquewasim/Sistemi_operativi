package osExtra;

import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;

/**{c}
 * AppletDiag.java
 * esempio di applet elementare con diagnostica su stdout
 *
 * @version 1.00 98/12/08
 * @version 2.00 2005-10-07 package osExtra
 * @author M.Moro DEI UPD
 */

public class AppletDiag extends Applet 
{
    private static int cnt1 = 1;
    private static int cnt2 = 1;
    private static int cnt3 = 1;
    private static int cnt4 = 1;

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
    public void paint(Graphics g) 
    {
        System.out.println("paint cnt4=" + cnt4);
        g.drawString(
          "Sovrapporre un'altra finestra e commutare", 5, 50);
        g.drawString("cnt4=" + (cnt4++), 5, 70);
    } //[m] paint

} //{c} AppletDiag
