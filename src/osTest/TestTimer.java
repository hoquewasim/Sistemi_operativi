package osTest;

import java.awt.Frame;
import java.awt.Color;
import java.awt.Canvas;
import java.awt.Button;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import os.Timer;
import os.TimerCallback;

/**{c}
 * test del software timer
 * @author M.Moro DEI UNIPD
 * @version 1.00 2003-09-30
 * @version 2.00 2005-10-07 package os e osTest
 * @version 2.01 2005-10-21 show() -&gt; setVisible(true); call che puo' terminare
 */

public class TestTimer extends Frame
{
    private static final Color COLOR1 = new Color(0xFF, 0xBB, 0x11);
    private static final Color COLOR2 = new Color(0x33, 0x90, 0xDF);
    private Color backColor = COLOR1;
    private static final long MINPERIOD = 300;
    private static final long MAXPERIOD = 3000;
    
    private long period = MAXPERIOD;
    
    private Canvas cv = new MyCanvas();
    private Button button = new Button("Premere per sospendere e riattivare");
    private boolean butStatus = false; // stato pressione bottone
    private Timer tm;
    
    /**{c}
     * canvas di disegno
     * @author M.Moro DEI UNIPD
     * @version 1.0 2003-10-002
     */
    private class MyCanvas extends Canvas
    {
        
        /**[m]
         * disegno con le caratteristiche impostate
         */
        public void paint(Graphics g)
        {
            setBackground(backColor);
            if (tm == null)
                g.drawString("Click per cambiare periodo",
                  100, 100);
            else
                g.drawString("Click per cambiare periodo="+
                  tm.getPeriod()+" ms", 100, 100);
        } //[m] paint
                    
    } // {c} MyCanvas
        
    /**[c]
     * costruttore base dell'interfaccia
     */
    public TestTimer()
    {
        super("Esempio con timer");
        setLayout(new FlowLayout(FlowLayout.LEFT));
        
        button.addActionListener(new ActionListener()
        {
            // classe anonima
            public void actionPerformed(ActionEvent e)
            {
                if (butStatus)
                {
                    butStatus = false;
                    System.out.println(
                      "        TestTimer   R I A T T I V A:\n"+tm);
                    tm.start();  // restart
                }
                else
                {
                    // sospende
                    butStatus = true;
                    System.out.println(
                      "        TestTimer   S O S P E N D E\n"+tm);
                    tm.freeze();
                }
            } //[m] actionPerformed
            
        } );
        add(button);
        
        cv.addMouseListener(new MouseAdapter()
        {
            // classe anonima
            public void mouseClicked(MouseEvent e)
            {
                period *= 0.7;
                if (period < MINPERIOD)
                    period = MAXPERIOD;
                tm.setPeriod(period);
                cv.repaint();
            } //[m] mouseClicked
        } );
        cv.setSize(400,200);
        add(cv);
        
        // ascoltatore per la finestra
        addWindowListener(new WindowAdapter()
        {
            /**[m]
             * @see WindowListener
             */
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            } //[m] windowClosing
        });
        
        setSize(new Dimension(600, 300));
        // [2.01]
        // show();
        setVisible(true);
        
        tm = new Timer(new TimerCallback()
        {
            // classe interna di callback
            /**[m]
             * @see TimerCallback#call
             */
            // 2.01 ora la callback può spegnere il timer
//            public void call()
            public boolean call()
            {
                // commuta il background
                if (backColor == COLOR1) 
                    backColor = COLOR2;
                else
                    backColor = COLOR1;
                tm.retrigger();
                cv.repaint();
                return true;
            } //[m] call
            
            /**[m]
             * @see TimerCallback#toString
             */
            public String toString()
            { return "Cambia colore"; }
            
        }, period);
        
        tm.start();
     } //[c]
        
    /**[m][s]
     * main di collaudo
     * @param args  non usato
     */
    public static void main(String[] args) 
    {
        System.out.println("TestTimer: apre finestra su cui fare click");
        Frame f = new TestTimer();
    } //[m][s] main
    
} //{c} TestTimer

