package osExtra;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.net.*;

/**{c}
 * SelFile
 * applet di selezione con il mouse
 *
 * @version 1.00 02/03/13
 * @version 2.00 2005-10-07 package osExtra
 * @author M.Moro DEI UPD
 */

public class SelFile extends Applet  
  implements Runnable, MouseListener
{
    private Thread runner;
    private String str1, str2, str3, str;
      // i 3 nomi di file da prelevare e la concat.
    private URL loadUrl1 = null, loadUrl2 = null, loadUrl3 = null;
    private int xpos;  // posizione orizzontale del testo
    private int ypos;  // posizione verticale del testo
    private int xdiff = 3;  // pixel di spostamento orizzontale
    private int delay = 50;  // ms di aggiornamento
    private int length;  // lunghezza del testo
    private Font outFont = new Font("SansSerif", Font.BOLD, 20);
    private Color bk = Color.black;  // colore background
    private Color fo = Color.red;    // colore foreground
    private boolean stopFlag = false;

    public void init()
    {
        str1 = getParameter("File1");
        str2 = getParameter("File2");
        str3 = getParameter("File3");
        str = str1+"  "+str2+"  "+str3;
        setBackground(bk);
        xpos = getSize().width + 10;
        ypos = (int)(0.5 * getSize().height);
        FontMetrics fm = getFontMetrics(outFont);
        length = fm.stringWidth(str);
          // lunghezza rapportata al font
        try {
            loadUrl1 = new URL(getDocumentBase(), str1);
            loadUrl2 = new URL(getDocumentBase(), str2);
            loadUrl3 = new URL(getDocumentBase(), str3);
        }
        catch(MalformedURLException ee) { } 
        addMouseListener(this);
    }

    public void start() 
    {
        stopFlag = false;
        runner = new Thread(this);
        runner.start();
    } //[m] start

    public void stop() 
    {
        stopFlag = true;
    } //[m] stop
    
    public void run() 
    {
        while(true)
        {
            if (stopFlag)
                // fermato
                return;
            repaint();
            xpos-=xdiff;
            if (xpos <= -(length)) {
              // ricomincia
                xpos=getSize().width+10;
                ypos = (int)(0.5 * getSize().height);
            }
            try { Thread.sleep(delay); }
            catch (InterruptedException e) { }
        }
    } //[m] run

// public void update(Graphics g) { paint(g); }

    public void paint(Graphics g) {
/*        
          g.setColor(bk);
          g.fillRect(0,0,getSize().width, getSize().height);
*/          
          g.setColor(fo);
          g.setFont(outFont);
          g.drawString(str, xpos, ypos);
    }

    public void mouseClicked(MouseEvent e) 
    {
System.out.println("Clicked on "+e.getX()+ "-" + e.getY()+ 
  " xpos="+xpos + " ypos="+ ypos);
    	int posx = e.getX();  // posizione x
    	int posy = e.getY();  // posizione y
        FontMetrics fm = getFontMetrics(outFont);
        int length1 = fm.stringWidth(str1);
        int length2 = fm.stringWidth(str2);
        int length3 = fm.stringWidth(str3);
        int pos2 = fm.stringWidth(str1+"  ");
        int pos3 = fm.stringWidth(str1+"  "+str2+"  ");

        if (posy < ypos-fm.getHeight() || posy > ypos)
            // fuori dell'altezza dei caratteri
            return;
        AppletContext ac = getAppletContext();
        if (posx>=xpos && posx<=xpos+length1)
        {
            // prima voce
            System.out.println("Prima voce");
            ac.showDocument(loadUrl1);
        } 
        else if (posx>=xpos+pos2 && posx<=xpos+pos2+length2) 
        {
            // seconda voce
            System.out.println("Seconda voce");
            ac.showDocument(loadUrl2);
        } 
        else if (posx>=xpos+pos3 && posx<=xpos+pos3+length3)
        {
            // terza voce
            System.out.println("Terza voce");
            ac.showDocument(loadUrl3);
        } 
    }    
    public void mouseEntered(MouseEvent e) 
    {
    	fo = Color.green;
    }    

    public void mouseExited(MouseEvent e) 
    {
    	fo = Color.red;
    }    
    public void mousePressed(MouseEvent e) {}    
    public void mouseReleased(MouseEvent e) {}    

}