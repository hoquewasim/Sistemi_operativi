package osExtra;

import java.applet.*;
import java.awt.*;

/**{c}
 * SliText1
 * esempio con Frame di testo che scorre
 *
 * @version 1.00 02/03/13
 * @version 2.00 2005-10-07 package osExtra
 * @version 2.01 2005-10-21 show() -&gt; setVisible(true);
 * @author M.Moro DEI UPD
 */

public class SliText1 extends Frame
{
    int xpos;  // posizione orizzontale del testo
    int ypos;  // posizione verticale del testo
    int xdiff = 2;  // pixel di spostamento orizzontale
    int delay = 40;  // ms di aggiornamento
    int length;  // lunghezza del testo
    String text = "Un testo che scorre!";
    Font outFont = new Font("SansSerif", Font.BOLD, 20);
    Color bk = Color.black;  // colore background
    Color fo = Color.red;    // colore foreground

    public SliText1() {
//        setBackground(Color.green);
        setBackground(bk);
        setSize(200, 400);
        // [2.01]
        // show();
        setVisible(true);

        xpos = getSize().width + 10;
        ypos = (int)(0.5 * getSize().height);
        FontMetrics fm = getFontMetrics(outFont);
        length = fm.stringWidth(text);
          // lunghezza rapportata al font
        
        while(true)
        {
           repaint();
           xpos-=xdiff;
           if (xpos <= -(length)) 
             // ricomincia
           xpos=getSize().width+10;
           try { Thread.sleep(delay); }
           catch (InterruptedException e) { }
       }
    }

    public void paint(Graphics g) {
          g.setColor(bk);
          g.fillRect(0,0,getSize().width, getSize().height);
          g.setColor(fo);
          g.setFont(outFont);
          g.drawString(text, xpos, ypos);
    }

/* Il normale update chiama paint dopo
aver eseguito il refresh dello sfondo
con il colore di background impostato: 
per evitare 'disturbi' il background 
deve essere dello stesso colore del 
riempimento eseguito dal paint qui sopra 
(provare a impostare, con setBackGround, 
un colore diverso per vedere l'effetto). 
Se si vuole evitare il refresh dello 
sfondo, si puo' ridefinire update nel
modo che segue:

    public void update(Graphics g) {
          paint(g); 
    }
*/
    
    public static void main(String args[])
    {  
	     System.out.println("Battere Ctrl-C per termonare!");
		  new SliText1(); 
    }        
}

