package osExtra;

import java.applet.*;
import java.awt.*;

/**{c}
 * ColorScribble
 * esempio di disegno senza memoria
 * con scelta del colore da parametro HTML
 *
 * @version 1.00 99/11/13
 * @version 2.00 2005-10-07 package osExtra
 * @author D.Flanagan O'Really
 */

// This example is from the book "Java in a Nutshell, Second Edition".
// Written by David Flanagan.  Copyright (c) 1997 O'Reilly & Associates.
// You may distribute this source code for non-commercial purposes only.
// You may study, modify, and use this example for any purpose, as long as
// this notice is retained.  Note that this example is provided "as is",
// WITHOUT WARRANTY of any kind either expressed or implied.

public class ColorScribble extends Scribble1 {
  // Read in two color parameters and set the colors.
  public void init() {
    super.init();
    Color foreground = getColorParameter("foreground");
    Color background = getColorParameter("background");
    if (foreground != null) this.setForeground(foreground);
    if (background != null) this.setBackground(background);
  }

  // Read the specified parameter.  Interpret it as a hexadecimal
  // number of the form RRGGBB and convert it to a color.
  protected Color getColorParameter(String name) {
    String value = this.getParameter(name);
    try { return new Color(Integer.parseInt(value, 16)); }
    catch (Exception e) { return null; }
  }

  // Return information suitable for display in an About dialog box.
  public String getAppletInfo() {
    return "ColorScribble v. 0.02.  Written by David Flanagan.";
  }

  // Return info about the supported parameters.  Web browsers and applet
  // viewers should display this information, and may also allow users to
  // set the parameter values.
  public String[][] getParameterInfo() { return info; }

  // Here's the information that getParameterInfo() returns.
  // It is an array of arrays of strings describing each parameter.
  // Format: parameter name, parameter type, parameter description
  private String[][] info = {
    {"foreground", "hexadecimal color value", "foreground color"},
    {"background", "hexadecimal color value", "background color"}
  };
}
