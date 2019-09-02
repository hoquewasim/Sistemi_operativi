package osExtra;

import java.util.Date;
import java.rmi.*;
import os.Sys;

/**{c}
 * remote date:
 * esempio d'uso di RMI con data remota -
 * file di test
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2003-10-20
 * @version 2.00 2005-10-07 package os e osExtra
 * @version 2.01 2013-05-08 generalizz. indirizzo
 */

public class TestRemDate 
{
    /**[m][s]
     * main di collaudo
     * @param args  indirizzo
     */
    public static void main(String args[]) 
    {
        long t1=0,t2=0;
        Date d1 = new Date();
        RemDate remDate = null;
        System.out.println("TestRemDate: tenta lookup su: "+
		    ((args.length == 0) ?
              "//localhost/RemDateImpl" :
              "//"+args[0]+"/RemDateImpl"));
        try {
            /* La chiamata si riferisce
               al caso di cliente e server sulla
               stessa macchina.
               In generale la chiamata remota e`
               del tipo:
               Naming.lookup("rmi://<URL macchina>/RemDateImpl");
            */
//              Naming.lookup("rmi:///RemDateImpl");
/* v2.01            remDate = (RemDate) 
              Naming.lookup("///RemDateImpl");
*/              
            remDate = (RemDate)((args.length == 0) ?
              Naming.lookup("//localhost/RemDateImpl") :
              Naming.lookup("//"+args[0]+"/RemDateImpl"));
            d1 = remDate.getDate();
            t1 = remDate.getDate().getTime();
            t2 = remDate.getDate().getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Prima chiamata: "+d1);
        System.out.println("Seconda chiamata, time: "+t1);
        System.out.println("Terza chiamata, time: "+t2);
        System.out.println(
          "Questa chiamata RMI impiega " + (t2-t1) + 
          " millisecondi");
        String unb = Sys.in.readLine("Unbind l'oggetto (y,N)? ");
        if (unb.length() != 0 &&
          Character.toUpperCase(unb.charAt(0)) == 'Y')
            try {
                remDate.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
    } //[m][s] main
    
} //{c} TestRemDate
