package osTest;

import java.awt.Frame;
import java.awt.Button;
import java.awt.TextArea;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import os.Events;
import os.Util;

/**{c}
 * test su eventi associati ai thread
 * con timeout
 * @author M.Moro DEI UNIPD
 * @version 1.00 2003-10-02
 * @version 2.00 2005-10-07 package os e osTest
 * @version 2.01 2005-10-21 show() -&gt; setVisible(true);
 */

public class TestEvent1 extends Frame
implements ActionListener
{
    
    private static final int MASK0 = 1; 
      // maschera bit 0
    private static final int MASK1 = 2; 
      // maschera bit 1
    private static final int MASK2 = 4; 
      // maschera bit 1
    private static final int RCVMASK = MASK0 | MASK1; 
      // maschera bit 0 e 1

    private String str01 = "Ev0 a rcv1";
    private String str11 = "Ev1 a rcv1";
    private String str21 = "Ev2 a rcv1";
    private String str02 = "Ev0 a rcv2";
    private String str12 = "Ev1 a rcv2";
    private String str22 = "Ev2 a rcv2";
    private String str03 = "Ev0 a rcv3";
    private String str13 = "Ev1 a rcv3";
    private String str23 = "Ev2 a rcv3";
    private Button button01 = new Button(str01);
    private Button button11 = new Button(str11);
    private Button button21 = new Button(str21);
    private Button button02 = new Button(str02);
    private Button button12 = new Button(str12);
    private Button button22 = new Button(str22);
    private Button button03 = new Button(str03);
    private Button button13 = new Button(str13);
    private Button button23 = new Button(str23);
    private TextArea textarea = new TextArea();
      
    private Thread th1, th2, th3;  // thread generati
        
    /**{c}
     * thread ricevitore 1
     * @author M.Moro DEI UNIPD
     * @version 1.0 2003-10-02
     */
    private class Rcv1 extends Thread
    {
        
        /**[c]
         * thread ricezione
         * @param name  nome del thread
         */
        public Rcv1(String name)
        {
            super(name);
        }
        
        /**[m]
         * riceve eventi con wait e timeout
         */
        public void run()
        {
            System.out.println("Attivato thread ricevitore "+getName());
            while(true)
            {
                System.out.println("--1 ricevitore wait");
                int received = Events.wait(RCVMASK, 4000L);
//                System.out.println("--------1 ricevitore retmask="+received);
                if (received != RCVMASK)
                    textarea.append("--1 wait TIMEOUT rcvmask="+received+" curmask="+
                      Events.getEvents(th1)+"\n");
                else
                    textarea.append("--1 wait rcvmask="+received+" curmask="+
                      Events.getEvents(th1)+"\n");
                Util.rsleep(3500, 4000);
            } //while
        } //[m] run
                    
    } // {c} Rcv1
        
    /**{c}
     * thread ricevitore 2
     * @author M.Moro DEI UNIPD
     * @version 1.0 2003-10-02
     */
    private class Rcv2 extends Thread
    {
        
        /**[c]
         * thread ricezione
         * @param name  nome del thread
         */
        public Rcv2(String name)
        {
            super(name);
        }
        
        /**[m]
         * riceve eventi con waitSingle e timeout
         */
        public void run()
        {
            System.out.println("Attivato thread ricevitore "+getName());
            while(true)
            {
                System.out.println("--2 ricevitore wait");
                int received = Events.waitSingle(RCVMASK, 4000);
//                System.out.println("--------2 ricevitore retmask="+received);
                if (received == 0)
                    textarea.append("--2 waitSingle TIMEOUT rcvmask="+received+" curmask="+
                      Events.getEvents(th2)+"\n");
                else
                    textarea.append("--2 waitSingle rcvmask="+received+" curmask="+
                      Events.getEvents(th2)+"\n");
                Util.rsleep(3500, 4000);
            } //while
        } //[m] run
                    
    } // {c} Rcv2
        
    /**{c}
     * thread ricevitore 3
     * @author M.Moro DEI UNIPD
     * @version 1.0 2003-10-02
     */
    private class Rcv3 extends Thread
    {
        
        /**[c]
         * thread ricezione
         * @param name  nome del thread
         */
        public Rcv3(String name)
        {
            super(name);
        }
        
        /**[m]
         * riceve eventi con waitAll e timeout
         */
        public void run()
        {
            System.out.println("Attivato thread ricevitore "+getName());
            while(true)
            {
                System.out.println("--3 ricevitore wait");
                int received = Events.waitAll(RCVMASK, 4000);
//                System.out.println("--------3 ricevitore retmask="+received);
                if (received == 0)
                    textarea.append("--3 waitAll TIMEOUT rcvmask="+received+" curmask="+
                      Events.getEvents(th3)+"\n");
                else
                    textarea.append("--3 waitAll rcvmask="+received+" curmask="+
                      Events.getEvents(th3)+"\n");
                Util.rsleep(3500, 4000);
            } //while
        } //[m] run
                    
    } // {c} Rcv3
        
    /**[m]
     * implementazione di ActionListener
     * @see ActionListener
     */
    public void actionPerformed(ActionEvent e) 
    {
        String buttonCommand = e.getActionCommand();
        if(buttonCommand.equals(str01))
            Events.signal(MASK0, th1);
        else if(buttonCommand.equals(str11))
            Events.signal(MASK1, th1);
        else if(buttonCommand.equals(str21))
            Events.signal(MASK2, th1);
        else if(buttonCommand.equals(str02))
            Events.signal(MASK0, th2);
        else if(buttonCommand.equals(str12))
            Events.signal(MASK1, th2);
        else if(buttonCommand.equals(str22))
            Events.signal(MASK2, th2);
        else if(buttonCommand.equals(str03))
            Events.signal(MASK0, th3);
        else if(buttonCommand.equals(str13))
            Events.signal(MASK1, th3);
        else if(buttonCommand.equals(str23))
            Events.signal(MASK2, th3);
    } //[m] actionPerformed
    
    /**[c]
     * costruttore base, aggiunge al frame tutti i
     * componenti previsti
     */
    public TestEvent1() {
        super("Esempio eventi con timeout");
        
        // genera thread
        th1 = new Rcv1("th1");
        th2 = new Rcv2("th2");
        th3 = new Rcv3("th3");
        th1.start();
        th2.start();
        th3.start();

        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(button01);
        add(button11);
        add(button21);
        add(button02);
        add(button12);
        add(button22);
        add(button03);
        add(button13);
        add(button23);
        add(textarea);
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
        // ascoltatore per i bottoni
        button01.addActionListener(this);
        button11.addActionListener(this);
        button21.addActionListener(this);
        button02.addActionListener(this);
        button12.addActionListener(this);
        button22.addActionListener(this);
        button03.addActionListener(this);
        button13.addActionListener(this);
        button23.addActionListener(this);
        setSize(new Dimension(700, 400));
        // [2.01]
        // show();
        setVisible(true);
    } //[c]

    /**[m][s]
     * main di collaudo
     * @param args  non usato
     */
    public static void main(String[] args) 
    {
        System.out.println("TestEvent1: apre finestra");
        Frame f = new TestEvent1();
    } //[m][s] main
    
} //{c} TestEvent

