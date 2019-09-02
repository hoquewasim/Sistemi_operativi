package osExtra;

import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;

/**{c}
 * Ball
 * esempio di interazione con mouse
 *
 * @version 1.00 98/12/08
 * @version 2.00 2005-10-07 package osExtra
 * @author L.Sanson CPR Padova
 * @author M.Moro DEI UPD
 */
 
public class Ball extends Applet 
{
    final static int CLICK_STEP = 30;
    final static int MOUSE_STEP = 2;

    final static int BALL_WIDTH = 20;
    final static int BALL_HEIGHT = 20;

    final static Color BACKGROUND_COLOR = 
      new Color(64, 128, 192);
    final static Color BALL_COLOR = 
      new Color(192, 64, 64);

    int x ,y;

    Button cmdUp, cmdDown, cmdLeft, cmdRight;   

    CtrlAction ctrlAction = new CtrlAction();
    CtrlMouse ctrlMouse = new CtrlMouse();

    /**[m]
     * @see Applet
     */
    public void init() 
    {
        cmdUp = new Button("Alto");
        cmdDown = new Button("Basso");
        cmdLeft = new Button("Sinistra");
        cmdRight = new Button("Destra");

        cmdUp.addActionListener(ctrlAction);
        cmdDown.addActionListener(ctrlAction);
        cmdLeft.addActionListener(ctrlAction);
        cmdRight.addActionListener(ctrlAction);

        setLayout(new BorderLayout());
        add(cmdUp, "North");
        add(cmdDown, "South");
        add(cmdLeft, "West");
        add(cmdRight, "East");

        setBackground(BACKGROUND_COLOR);

        addMouseMotionListener(ctrlMouse);
        addMouseListener(ctrlMouse);
    } //[m] init

    /**[m]
     * @see Applet
     */
    public void start() 
    {
        x = getSize().width / 2;
        y = getSize().height / 2;
    } //[m] start
    
    /**[m]
     * @see Applet
     */
    public void paint(Graphics g) 
    {
        g.setColor(BALL_COLOR);
        g.fillOval(x, y, BALL_WIDTH, BALL_HEIGHT);
    } //[m] paint

    /**{c}{l}
     * classe interna che implementa MouseMotionListener 
     * @see MouseMotionListener 
     */
    class CtrlMouse extends MouseAdapter 
      implements MouseMotionListener 
    {

        /**[m]
         * @see MouseMotionListener
         */
        public void mouseMoved(MouseEvent e) 
        {
            if (x < e.getX()) x+=MOUSE_STEP;
            else x-=MOUSE_STEP;
            if (y < e.getY()) y+=MOUSE_STEP;
            else y-=MOUSE_STEP;
            repaint();
        } //[m] mouseMoved

        /**[m]
         * @see MouseMotionListener
         */
        public void mouseDragged(MouseEvent e) {}

        /**[m]
         * @see MouseMotionListener
         */
        public void mouseClicked(MouseEvent e) {
            x = e.getX();
            y = e.getY();
            repaint();
        } //[m] mouseClicked

    } //{c}{l} CtrlMouse

    /**{c}{l}
     * classe interna che implementa ActionListener
     * @see ActionListener
     */
    class CtrlAction implements ActionListener 
    {
        /**[m]
         * @see ActionListener
         */
        public void actionPerformed(ActionEvent e) 
        {
            Object obj = e.getSource();
            if (obj == cmdUp) y -= CLICK_STEP;
            else if (obj == cmdDown) y += CLICK_STEP;
            else if (obj == cmdLeft) x -= CLICK_STEP;
            else if (obj == cmdRight) x += CLICK_STEP;
            repaint();
        } //[m] actionPerformed

    } //{c}{l} CtrlAction

    /**[m][s]
     * metodo di attivazione
     * @param arg  non usato
     */
    public static void main(String arg[]) 
    {
        BallFrame app = new BallFrame();
    } //[m][s] main

} //{c} Ball

/**{c}
 * frame di supporto per Ball
 */
class BallFrame extends Frame 
{

    Ball applet;
    CtrlWindow ctrlWindow = new CtrlWindow();

    /**[c]
     * costruttore di default
     */
    public BallFrame() 
    {
        super("-Ball-");
        addWindowListener(ctrlWindow);
        setSize(new Dimension(500, 300));
        applet = new Ball();
        add(applet);
        applet.init();
        setVisible(true);
        applet.start();
    } //[c] BallFrame

    /**{c}{l}
     * classe interna che implementa WindowListener
     * @see WindowListener
     */
    class CtrlWindow extends WindowAdapter 
      implements WindowListener
    {
        /**[m]
         * @see WindowListener
         */
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        } //[m] windowClosing

    } //{c}{l} CtrlWindow

} //{c} BallFrame
