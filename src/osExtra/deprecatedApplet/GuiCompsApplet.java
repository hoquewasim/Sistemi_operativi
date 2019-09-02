package osExtra;

import java.awt.*;
import java.awt.event.*;

/**{c}
 * GuiCompsApplet
 * attivazione di GuiComps come applet
 *
 * @version 1.00 98/12/08
 * @version 2.00 2005-10-07 package osExtra
 * @author M.Moro DEI UPD
 */

public class GuiCompsApplet 
  extends java.applet.Applet
{
    private Frame f;

    public void init() 
    {
        f = new GuiComps();
    } //[m] init

} //{c} GuiCompsApplet
