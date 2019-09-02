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

import os.RandTimer;
import os.TimerCallback;
import os.Util;

/**{c}
 * test del software timer casuale
 * @author M.Moro DEI UNIPD
 * @version 1.00 2003-10-04
 * @version 2.00 2005-10-07 package os e osTest
 * @version 2.01 2005-10-21 show() -&gt; setVisible(true); call che puo' terminare
 */

public class TestRandTimer extends Frame
{
    private static final Color COLOR1 = new Color(0xFF, 0xBB, 0x11);
    private static final Color COLOR2 = new Color(0x33, 0x90, 0xDF);
    private Color backColor = COLOR1;
    private static final long MINPERIOD = 300;
    private static final long MAXPERIOD = 3000;
    
    private Canvas cv = new MyCanvas();
    private Button button = new Button("Premere per sospendere e riattivare");
    private boolean butStatus = false; // stato pressione bottone
    private RandTimer tm;
    
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
                g.drawString("Periodo",
                  100, 100);
            else
                g.drawString("Periodo="+
                  tm.getPeriod()+" ms", 100, 100);
        } //[m] paint
                    
    } // {c} MyCanvas
        
    /**[c]
     * costruttore base dell'interfaccia
     */
    public TestRandTimer()
    {
        super("Esempio con timer casuale");
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
                      "        TestTimer   S O S P E N D E:\n"+tm);
                    tm.freeze();
                }
            } //[m] actionPerformed
            
        } );
        add(button);
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
        
        tm = new RandTimer(new TimerCallback()
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
            
        }, 400, 4000);
        
        tm.start();
        while(true)
        {
            long oldPeriod = tm.getPeriod();
            while(tm.getPeriod() == oldPeriod)
                Util.sleep(200);
            cv.repaint();
        } // while
    } //[c]
        
    /**[m][s]
     * main di collaudo
     * @param args  non usato
     */
    public static void main(String[] args) 
    {
        System.out.println("TestTimer: apre finestra con periodo variabile");
        Frame f = new TestRandTimer();
    } //[m][s] main
    
} //{c} TestRandTimer

