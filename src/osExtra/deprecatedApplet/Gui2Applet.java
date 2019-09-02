package osExtra;

import java.awt.*;
import java.awt.event.*;

/**{c}
 * Gui2Applet -
 * esempio 2 di interfaccia grafica come Applet
 *
 * @version 1.00 02/03/12
 * @version 2.00 2005-10-07 package osExtra
 * @author M.Moro DEI UPD
 */

public class Gui2Applet extends java.applet.Applet
implements ActionListener
{
    // componenti elementari
    Button button = new Button("Schiaccia!");
    TextArea textarea = new TextArea(
          "Area di testo modificabile\nsu piu` linee.\n", 6, 40);
    int cnt = 1;
    int cnta = 1;
    int cntp = 1;

    public void init() {
        System.out.println("(init) Thread: ["+
          Thread.currentThread() + "] Group ["+
          Thread.currentThread().getThreadGroup()+ "]");
        System.out.println("active="+Thread.activeCount());
        Thread[] ts = new Thread[Thread.activeCount()];
        int tn = Thread.enumerate(ts);
        System.out.println("Thread nel sistema:");
        for(int i=0; i<tn; i++)
            System.out.println(ts[i]);

        setLayout(new FlowLayout(FlowLayout.LEFT));

        add(button);
        add(textarea);
        button.addActionListener(this);
    }

    public void start() 
    {
        System.out.println("start " + Thread.currentThread());
    }
    
    public void stop() 
    {
        System.out.println("stop " + Thread.currentThread());
    } //[m] stop

    public void actionPerformed(ActionEvent e) 
    {
        // evento del bottone
        textarea.append("Pressione n. "+(cnt++)+"\n");
        System.out.println("(actionPerformed "+
          (cnta++)+") Thread: ["+
          Thread.currentThread() + "] Group ["+
          Thread.currentThread().getThreadGroup()+ "]");
    } //[m] actionPerformed
    
    public void paint(Graphics g)
    {
        System.out.println("(paint "+
          (cntp++)+") Thread: ["+
          Thread.currentThread() + "] Group ["+
          Thread.currentThread().getThreadGroup()+ "]");
        g.setColor(Color.red);
        g.fillOval(100,200,30,50);
    }

} //{c} Gui2Applet
