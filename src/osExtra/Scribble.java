package osExtra;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

/**{c}
 * Scribble
 * esempio di frame di disegno senza memoria
 *
 * @version 1.00 02/03/11
 * @version 2.00 2005-10-07 package osExtra
 * @version 2.02 2005-10-21 show() -&gt; setVisible(true);
 * @author D.Flanagan O'Really
 */

// This example is from the book "Java in a Nutshell, Second Edition".
// Written by David Flanagan.  Copyright (c) 1997 O'Reilly & Associates.
// You may distribute this source code for non-commercial purposes only.
// You may study, modify, and use this example for any purpose, as long as
// this notice is retained.  Note that this example is provided "as is",
// WITHOUT WARRANTY of any kind either expressed or implied.

public class Scribble extends Frame
                      implements MouseListener,  MouseMotionListener {
  private int last_x, last_y;

  public Scribble() {
    // Tell this frame what MouseListener and MouseMotionListener
    // objects to notify when mouse and mouse motion events occur.
    // Since we implement the interfaces ourself, our own methods are called.
    super("Scribble");
    this.addMouseListener(this);
    this.addMouseMotionListener(this);

    // Another event listener, as inner class.
    this.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) { System.exit(0); }
    });



    setSize(new Dimension(400, 300));
    // [2.01]
    // show();
    setVisible(true);
  }

  // A method from the MouseListener interface.  Invoked when the
  // user presses a mouse button.
  public void mousePressed(MouseEvent e) {
    last_x = e.getX();
    last_y = e.getY();
  }

  // A method from the MouseMotionListener interface.  Invoked when the
  // user drags the mouse with a button pressed.
  public void mouseDragged(MouseEvent e) {
    Graphics g = this.getGraphics();
    int x = e.getX(), y = e.getY();
    g.drawLine(last_x, last_y, x, y);
    last_x = x; last_y = y;
  }

  // The other, unused methods of the MouseListener interface.
  public void mouseReleased(MouseEvent e) {;}
  public void mouseClicked(MouseEvent e) {;}
  public void mouseEntered(MouseEvent e) {;}
  public void mouseExited(MouseEvent e) {;}

  // The other method of the MouseMotionListener interface.
  public void mouseMoved(MouseEvent e) {;}

    /**[m][s]
     * metodo di attivazione
     * @param args  non usato
     */
    public static void main(String[] args) 
    {
        Scribble me = new Scribble();
    } //[m][s] main

}
