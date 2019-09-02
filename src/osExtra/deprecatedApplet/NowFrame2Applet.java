package osExtra;

import java.awt.*;
import java.awt.event.*;

/**{c}
 * NowFrame2Applet
 * attivazione di Now come applet -
 * fa vedere l'esigenza dei thread
 *
 * @version 1.00 02/03/12
 * @version 2.00 2005-10-07 package osExtra
 * @author M.Moro DEI UPD
 */

public class NowFrame2Applet 
  extends java.applet.Applet
{
    private Frame f;

    public void init() 
    {
        f = new Now();
    } //[m] init

} //{c} NowFrame2Applet
