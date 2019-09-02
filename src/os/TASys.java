package os;

import java.awt.Frame;
import java.io.*;


/**{c}
 * un sistema a 3 canali su TextArea
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-05-05
 * @version 2.00 2003-09-30 package Os
 * @version 2.01 2005-10-07 package os
 * @version 2.02 2005-10-21 show() -&gt; setVisible(true);
 */

public class TASys {
    // dimensioni frame, offset posizione
    private static final int FRAMEW = 600;
    private static final int FRAMEH = 300;
    private static final int FRAMEXOFFSET = 60;
    private static final int FRAMEYOFFSET = 100;
    
    private static int cnt = 0; 
      // per posizionamento finestre
    private static String prot = "Oggetto mutex";
      // protezione per cnt
    private Frame fr;
      // il frame di interfaccia
    // i 3 canali
    /** canale di input
     */
    public SysRS in;
    /** canale di output
     */
    public PrintWriter out;
    /** canale di output d'errore
     */
    public PrintWriter err;


    /**[c]
     * creazione della finestra con textarea
     */
    public TASys()
    {
        TextArea textArea = new TextArea(5);
        
        // crea un Frame con un TextArea
        fr = new Frame("Thread "+Thread.currentThread().getName());
        fr.add(textArea);
        fr.setSize(FRAMEW, FRAMEH);
        synchronized (prot)
        {
            fr.setLocation((cnt%4)*FRAMEXOFFSET, (cnt/4)*FRAMEYOFFSET);
            cnt++;
        }
        // [2.02]
        // fr.show();
        fr.setVisible(true);
        out = err = new PrintWriter(new TextAreaOutputStream(textArea), true);
          // con autoflush
        TextAreaInputStream tais = new TextAreaInputStream(textArea);
        in = new SysRS(tais, err);
    }
    
    /**[m]
     * Chiusura dei canali e della finestra
     */
    public void close()
    {
        try
        {
            in.close();
        } 
        catch(IOException e) {}
        out.close();
        fr.dispose();
    }
    
} //{c} TASys
