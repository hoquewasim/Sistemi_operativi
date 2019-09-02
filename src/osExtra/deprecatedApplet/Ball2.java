package osExtra;

import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;

/**{c}
 * Ball2
 * esempio di interazione con mouse con thread
 *
 * @version 1.00 98/12/08
 * @version 1.01 99/09/30 -
 * Thread.stop() deprecato, sostituito
 * con stopFlag
 * @version 2.00 2005-10-07 package osExtra
 * @author L.Sanson CPR Padova
 * @author M.Moro DEI UPD
 */

public class Ball2 extends Ball 
  implements Runnable 
{
    
    final static Color TARGET_COLOR = new Color(32, 64, 128);
    final static Color CRASH_COLOR = Color.yellow;
    final static int CRASH_WIDTH = BALL_WIDTH * 4;
    final static int CRASH_HEIGHT = BALL_HEIGHT * 4;

    int x2, y2;
    int stepX, stepY;
    int score;
    Thread target;
/*>> 1.1 Thread.stop deprecato
    variabile valutata in run()
*/
    private boolean stopFlag = false;
/*<< */
    
    /**[m]
     * @see Runnable
     */
    public void run() 
    {
        for (;;) {
            if (x2 >= getSize().width || x2 <= 0) stepX*=-1;
            if (y2 >= getSize().height || y2 <= 0) stepY*=-1;
            x2+=stepX;
            y2+=stepY;
            if (stopFlag)
                // fermato
                return;
            try {
                Thread.currentThread().sleep(20);
            }
            catch (InterruptedException e) {}
            repaint();
        } //for
    } //[m] run

    /**[m]
     * @see Applet
     */
    public void start() 
    {
        super.start();
        score = 0;
        x2 = y2 = 10;
        stepX = stepY = 5;
/*>> 1.1 Thread.stop deprecato
*/
        stopFlag = false;
/*<< */
        target = new Thread(this);
        target.start();
    } //[m] start

    /**[m]
     * @see Applet
     */
    public void stop() 
    {
/*>> 1.1 Thread.stop deprecato
     sostituito con una variabile valutata periodicamente
     in run()
        target.stop();
*/
        stopFlag = true;
/*<< */
    } //[m] stop

    /**[m]
     * @see Applet
     */
    public void paint(Graphics g) 
    {
        super.paint(g);
        g.setColor(TARGET_COLOR);
        g.fillOval(x2, y2, BALL_WIDTH, BALL_HEIGHT);
        if (Math.abs(x - x2) < BALL_WIDTH && 
          Math.abs(y - y2) < BALL_HEIGHT) {
            g.setColor(CRASH_COLOR);
            g.fillOval(x, y, CRASH_WIDTH, CRASH_HEIGHT);
            score++;
        }
        g.drawString("Score: " + 
          Integer.toString(score), 80, 50);
    } //[m] paint

    /**[m][s]
     * metodo di attivazione
     * @param arg  non usato
     */
    public static void main(String arg[]) 
    {
        Ball2Frame app = new Ball2Frame();
    } //[m][s] main

} //{c} Ball2

/**{c}
 * frame di supporto per Ball2
 */
class Ball2Frame extends Frame 
{
    Ball applet;
    CtrlWindow ctrlWindow = new CtrlWindow();

    /**[c]
     * costruttore di default
     */
    public Ball2Frame() 
    {
        super("-Ball2-");
        addWindowListener(ctrlWindow);
        setSize(new Dimension(500, 300));
        applet = new Ball2();
        add(applet);
        applet.init();
        setVisible(true);
        applet.start();
    } //[c] Ball2Frame

    /**{c}{l}
     * classe interna che implementa WindowListener
     * @see WindowListener
     */
    class CtrlWindow extends WindowAdapter 
    {

        /**[m]
         * @see WindowListener
         */
        public void windowClosing(WindowEvent e) 
        {
            System.exit(0);
        } //[m] windowClosing

    } //{c}{l} CtrlWindow

} //{c} Ball2Frame
