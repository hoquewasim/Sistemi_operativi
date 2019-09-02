package osExtra;

import java.awt.*;
import java.awt.event.*;

/**{c}
 * Gui1 -
 * esempio 1 di interfaccia grafica come Frame
 *
 * @version 1.00 2003/10/11
 * @version 2.00 2005-10-07 package osExtra
 * @version 2.01 2005-10-21 show() -&gt; setVisible(true);
 * @author M.Moro DEI UNIPD
 */

public class Gui1 extends Frame 
implements ActionListener
{
    // componenti elementari
    Button button = new Button("Schiaccia!");
    TextArea textarea = new TextArea(
          "Area di testo modificabile\nsu piu` linee.\n", 6, 40);
    int cnt = 1;

    /**[c]
     * costruttore base, aggiunge al frame tutti i
     * componenti previsti
     */
    public Gui1() {
        super("Esempio di componenti grafiche 1");
        setLayout(new FlowLayout(FlowLayout.LEFT));

        add(button);
        add(textarea);
        button.addActionListener(this);
        setSize(new Dimension(500, 400));
        // [2.01]
        // show();
        setVisible(true);
    } //[c] Gui1

    /**[m]
     * implementazione di ActionListener
     * @see ActionListener
     */
    public void actionPerformed(ActionEvent e) 
    {
        // evento del bottone
        textarea.append("Pressione n. "+(cnt++)+"\n");
    } //[m] actionPerformed
    
    /**[m]
     * disegno grafico
     * @see Frame
     */
    public void paint(Graphics g)
    {
        g.setColor(Color.red);
        g.fillOval(100,200,30,50);
    }

    /**[m][s]
     * metodo di attivazione
     * @param args  non usato
     */
    public static void main(String[] args) 
    {
        Gui1 me = new Gui1();
        System.out.println("Battere Ctrl-C per terminare");
        // dopo la parte iniziale, il main termina
    } //[m][s] main

} //{c} Gui1
