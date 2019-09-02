package os;

import java.awt.*;

/**{c}
 * Animatore filosofi -
 * ricavato da esempio della Sun
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-05-06
 * @version 2.00 2003-10-02 package Os
 * @version 2.01 2005-10-07 package os
 * @version 2.02 2005-10-21 show() -&gt; setVisible(true);
 * @version 2.03 2006-10-19 codifiche stato public
 */


/*
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

public class PhilAnim extends Frame
{
    // stati dei filosofi
    public static final int THINKSTATUS = 0;
    public static final int RIGHTSTATUS = 1;
    public static final int BOTHSTATUS = 2;
    public static final int LEFTSTATUS = 3;

//    private static final double MARGIN = 10.0f;
    private double x[], y[]; // posizione filosofi
    private int numPhil;  // numero filosofi
    private int freeForks;  // numero di forchette libere
    private int eating;  // numero filosofi che mangiano
    private Image[] imgs = new Image[4];  // immagini

//    private double spacing;
    private boolean deadlock = false;  // stato di deadlock

    private int[] philStatus;  // stato dei filosofi

    /**[c]
     * animatore filosofi
     * @param header  intestazione
     * @param np  numero filosofi
     */
    public PhilAnim(String header, int np) {
        super(np+" filosofi con "+header);
        numPhil = freeForks = np;
        philStatus = new int[numPhil];

        Toolkit kit = Toolkit.getDefaultToolkit();
        // immagini
        imgs[0] = kit.getImage("os/hungryduke.gif");
        imgs[1] = kit.getImage("os/rightspoonduke.gif");
        imgs[2] = kit.getImage("os/bothspoonsduke.gif");
        imgs[3] = kit.getImage("os/leftspoonduke.gif");

//        spacing = imgs[0].getWidth(this) + MARGIN;
        x = new double[numPhil];
        y = new double[numPhil];
        philStatus = new int[numPhil];
        // posizionamento circolare dei filosofi
        double radius = 130.0;
        double centerAdj = 155.0;
        double radians;

/* for a straight line
        y = MARGIN;
*/
        for (int i = 0; i < numPhil; i++) {
/* for a straight line
            x = i * spacing;
*/
            radians = i*(2.0 * Math.PI /(double)numPhil);
            x[i] = Math.sin(radians) * radius + centerAdj;
            y[i] = Math.cos(radians) * radius + centerAdj;
            philStatus[i] = THINKSTATUS;
        }
        setSize(400, 400);
        setLocation(300, 300);
        // [2.02]
        // show();
        setVisible(true);
    }

    public void paint(Graphics g) {
        g.setColor(Color.lightGray);
        g.fillRect(0, 0, getSize().width, getSize().height);
        update(g);
    } //[m] paint

    public void update(Graphics g) {
        for (int i = 0; i < numPhil; i++) {
            g.setColor(Color.lightGray);
            g.fillRect((int)x[i], (int)y[i], imgs[0].getWidth(this), imgs[0].getHeight(this)+25);
            g.drawImage(imgs[philStatus[i]], (int)x[i], (int)y[i], this);
            g.setColor(Color.black);
            g.drawString("phil"+i, ((int)(x[i])+8), ((int)(y[i])+imgs[0].getHeight(this)+13));
            if(philStatus[i] == THINKSTATUS)
            {
//                g.setColor(Color.black);
                g.drawString("Mmm!", ((int)(x[i])+8), ((int)(y[i])+imgs[0].getHeight(this)+23));
            }
            else if(philStatus[i] == BOTHSTATUS)
            {
                g.setColor(Color.red);
                g.drawString("GNAM!", ((int)(x[i])+8), ((int)(y[i])+imgs[0].getHeight(this)+23));
            }
            if (deadlock)
            {
                g.setColor(Color.red);
                Font old = g.getFont();
                Font f = new Font("Arial", Font.BOLD, 30);
                g.setFont(f);
                g.drawString("DEADLOCK!", 40, 390);
                g.setFont(old);
            }
            g.setColor(Color.lightGray);
            g.fillRect(360, 300, 390, 390);
            Font old = g.getFont();
            Font f = new Font("Arial", Font.BOLD, 30);
            g.setFont(f);
            g.setColor(Color.red);
            g.drawString(Integer.toString(eating), 360, 350);
            g.setColor(Color.green);
            g.drawString(Integer.toString(freeForks), 360, 390);
            g.setFont(old);
        } // for
    } //[m] update

    /**[m]
     * notifica cambiamento filosofo
     * @param idPh  indice filosofo
     * @param status  nuovo stato
     */
    public void notifyEvent(int idPh, int status)
    {
        int oldForks = (philStatus[idPh]==THINKSTATUS) ? 0 :
          ((philStatus[idPh]==BOTHSTATUS) ? 2 : 1);
        int newForks = (status==THINKSTATUS) ? 0 :
          ((status==BOTHSTATUS) ? 2 : 1);
        freeForks -= newForks-oldForks;
        if (newForks==2 && oldForks!=2)
            eating++;
        else if (oldForks==2 && newForks!=2)
            eating--;
        philStatus[idPh] = status;
        repaint();
    }

    /**[m]
     * notifica deadlock
     */
    public void deadlock()
    {
        deadlock = true;
        repaint();
    }

    /**[m][s]
     * main di collaudo
     * @param args  non ustao
     */
    public static void main(String [] args)
    {

        PhilAnim pa = new PhilAnim("Prova", 4);
        try {
        Thread.sleep(1000); } catch(InterruptedException e) {}
        pa.notifyEvent(0, RIGHTSTATUS);
        try {
        Thread.sleep(1000); } catch(InterruptedException e) {}
        pa.notifyEvent(1, LEFTSTATUS);
        try {
        Thread.sleep(1000); } catch(InterruptedException e) {}
        pa.notifyEvent(2, BOTHSTATUS);
        try {
        Thread.sleep(1000); } catch(InterruptedException e) {}
        pa.notifyEvent(3, BOTHSTATUS);
        try {
        Thread.sleep(1000); } catch(InterruptedException e) {}
        pa.notifyEvent(0, THINKSTATUS);

    } //[m][s] main
} //{c} PhilAnim
