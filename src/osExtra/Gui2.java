package osExtra;

import java.awt.*;
import java.awt.event.*;
import os.Sys;

/**{c}
 * Gui2 -
 * esempio 2 di interfaccia grafica come Frame
 *
 * @version 1.00 2003/10/11
 * @version 2.00 2005-10-07 package osExtra
 * @version 2.01 2005-10-21 show() -&gt; setVisible(true);
 * @author M.Moro DEI UPD
 */

public class Gui2 extends Frame 
implements ActionListener
{
    // componenti elementari
    Button button = new Button("Schiaccia!");
    TextArea textarea = new TextArea(
          "Area di testo modificabile\nsu piu` linee.\n", 6, 40);
    int cnt = 1;
    int cnta = 1;
    int cntp = 1;

    /**[c]
     * costruttore base, aggiunge al frame tutti i
     * componenti previsti
     */
    public Gui2() {
        super("Esempio di componenti grafiche 1");
        setLayout(new FlowLayout(FlowLayout.LEFT));


        add(button);
        add(textarea);
        button.addActionListener(this);
        setSize(new Dimension(500, 400));
        // [2.01]
        // show();
        setVisible(true);
    } //[c] Gui2

    /**[m]
     * implementazione di ActionListener
     * @see ActionListener
     */
    public void actionPerformed(ActionEvent e) 
    {
        
        // evento del bottone
        textarea.append("Pressione n. "+(cnt++)+"\n");
        System.out.println("(actionPerformed "+
          (cnta++)+") Thread: ["+
          Thread.currentThread() + "] Group ["+
          Thread.currentThread().getThreadGroup()+ "]");
    } //[m] actionPerformed
    
    /**[m]
     * disegno grafico
     * @see Frame
     */
    public void paint(Graphics g)
    {
        System.out.println("(paint "+
          (cntp++)+") Thread: ["+
          Thread.currentThread() + "] Group ["+
          Thread.currentThread().getThreadGroup()+ "]");
        if ((cnt % 4) == 0)
        {
            cnt++;
            System.out.print("Battere RET:");
            Sys.in.readLine();
        }
        g.setColor(Color.red);
        g.fillOval(100,200,30,50);
    }

    /**[m][s]
     * metodo di attivazione
     * @param args  non usato
     */
    public static void main(String[] args) 
    {
        System.out.println("All'inizio active="+Thread.activeCount());
        Gui2 me = new Gui2();
        System.out.println("(main) Thread: ["+
          Thread.currentThread() + "] Group ["+
          Thread.currentThread().getThreadGroup()+ "]");
        System.out.println("Battere Ctrl-C per terminare");
        // dopo la parte iniziale, il main termina
        System.out.println("active="+Thread.activeCount());
        Thread[] ts = new Thread[Thread.activeCount()];
        int tn = Thread.enumerate(ts);
        System.out.println("Thread nel sistema:");
        for(int i=0; i<tn; i++)
            System.out.println(ts[i]);
    } //[m][s] main

} //{c} Gui2
