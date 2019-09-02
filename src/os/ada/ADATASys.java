package os.ada;

import java.awt.Frame;
import java.io.*;
import os.TASys;


/**{c}
 * un ADA server su TextArea --
 * in questo caso l'oggetto ADAThread non coincide
 * con l'oggetto che rappresenta il thread di Java --
 * bisogna che il nome del thread Java coincida con il
 * nome fornito in costruzione di ADAThread
 * @author M.Moro DEI UNIPD
 * @version 1.00 2010-12-17
 */


public class ADATASys extends TASys implements Runnable 
{
    private static final String tasysServStr = "tasysserver";
      // nome del selettore di print(String)
    private static final String printStr = "print";
      // nome del selettore di print(String)
    private static final String printlnStr = "println";
      // nome del selettore di println(String)
    private ADAThread serv = new ADAThread(tasysServStr);
      // il thread server
    
        /**[m]
         * test server output di stringa su TASys
         */
        public void run()
        {
            // select unico
            ADAThread.Select sel = serv.new Select();
            
            // entry print, choice=0, non condizionato
            sel.add(printStr, 
              new Entry()
              {
                public Object exec(Object inp)
                {
                    // inp e' la stringa da emettere
                    out.print((String)inp);
                    return null;
                }
              } //{c} <anonim>
              ); 
    
            // entry println, choice=1, non condizionato
            sel.add(printlnStr, 
              new Entry()
              {
                public Object exec(Object inp)
                {
                    // inp e' la stringa da emettere
                    out.println((String)inp);
                    return null;
                }
              } //{c} <anonim>
              ); 
    
            while (true)
                sel.accept();
        } //[m] run     
    
} //{c} ADATASys
