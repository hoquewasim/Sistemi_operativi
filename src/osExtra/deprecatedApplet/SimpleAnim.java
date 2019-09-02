package osExtra;

import java.awt.*;
import java.applet.*;
import java.net.URL;
import java.lang.*;

/**{c}
 * SimpleAnim
 * applet di semplice animazione con 3 gif
 *
 * @version 1.00 02/03/13
 * @version 2.00 2005-10-07 package osExtra
 * @author M.Moro DEI UPD
 */

public class SimpleAnim extends Applet implements Runnable
{
Image images[];
int x=0, y=0;
Thread th;
int numImg=4, idx;
int time;

    public void init()
    {
        images=new Image[numImg+1];
        for(int e=0; e<numImg-1;e++) {
            images[e]=getImage(getCodeBase(), "OsExtra/homo"+(e+1)+".gif");
            System.out.println("carica "+"homo"+(e+1)+".gif");
        }
        images[numImg-1]=getImage(getCodeBase(), "OsExtra/homo1.gif");
        System.out.println("img[0]="+images[0]);
        System.out.println("img[1]="+images[1]);
        System.out.println("img[2]="+images[2]);
    }
	
    public void start()
    {
        if(th==null)
        {
	        th=new Thread(this);
	        th.start();
        }
    }

    public void run()
    {    
        boolean right=true;
        idx=0;
        while(true)
        {
            idx++;
            if(idx>numImg-1)
	        idx=0;
            if(right==true)
	            x=x+10;
            else 
	            x=x-10;
            if(x>100)
	            right=false;
            if(x<0)
	            right=true;
            repaint();
            try {
	            th.sleep(220);
            } catch(InterruptedException e) { }
        }
    }

    public void stop()
    {
        th=null;
    }

    public void paint(Graphics g)
    {
        g.drawImage(images[idx],x,y,this);
    }
}