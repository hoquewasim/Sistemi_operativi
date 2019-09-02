package osTest;

import java.io.*;
import os.MachineConfig;
import os.IOChannel;
import os.DeviceDesc;
import os.Util;

/**{c}
 * test simulazione I/O
 * @author M.Moro DEI UNIPD
 * @version 1.00 2004-11-21
 * @version 2.00 2005-10-07 package os e osTest
 */
 
public class TestIO
{
    
    /**{c}
     * thread di test
     */
    private class User extends Thread
    {
        private MachineConfig mach; // il sistema
        // buffer
        private byte[] bufin  = new byte[200];
        private byte[] bufout = new byte[200];
        // canali
        private IOChannel sliIoc ;
        private IOChannel sloIoc ;

        /**[c]
         * Utente che usa il canale
         * @param iAm  nome del thread
         * @param mach  il sistema di riferimento
         */
        public User(String iAm, MachineConfig mach)
        {
            super(iAm);
            this.mach=mach;
        }
        
        /**[m]
         * codice del thread utente
         */
        public void run()
        {
            System.out.println("Attivato "+getName());
 
            // ogni thread apre i propri canali
            try {
                // apre un canale d'input
                sliIoc = new IOChannel("SLI_"+getName(),
                  DeviceDesc.SERIALLINE_CLASS, 
                  mach.getDevDesc(MachineConfig.SERIALLINEIN_DEV),
                  mach);
                // apre un canale d'output
                sloIoc = new IOChannel("SLO_"+getName(),
                  DeviceDesc.SERIALLINE_CLASS, 
                  mach.getDevDesc(MachineConfig.SERIALLINEOUT_DEV),
                  mach);
            } catch(IOException e) 
            {
                System.out.println(" **!! "+getName()+" errore apertura canali:"+e);
            } 
            
            // legge 3 volte 4 byte
            for(int i=1;i<=3;i++)
            {
                try { 
                    System.out.println(" ** "+getName()+
                      " si mette in attesa di input da tastiera "+i+" **");
                    sliIoc.read(4, bufin);
                } catch(IOException e) 
                {
                    System.out.println(" !! "+getName()+" errore input:"+e);
                } 
                System.out.println(getName()+"."+i+" THREAD ricevuto da serial line input ->"+
                bufin[0]+" "+bufin[1]+" "+bufin[2]+" "+bufin[3]+"<-");
            } 
             
            sliIoc.close();
            System.out.println(" ** "+getName()+" CHIUSO CANALE INPUT ** ");
           
            // emette 4 volte 6 byte casuali (cifre in ASCII)
            for(int j=1;j<=4;j++)
            {
                for(int i=0; i<6; i++)
                    bufout[i] = (byte)Util.randVal(48, 57);
                try {
                    System.out.println(" ** "+getName()+
                      " si mette in attesa di output su stdout "+j+" **");
                    sloIoc.write(6, bufout);
                }
                catch(IOException e)
                {
                    System.out.println(" !! "+getName()+" errore output:"+e);
                } 
                System.out.println("-->"+getName()+"."+j+" inviato su serial line output");
              }
              
            sloIoc.close();
            System.out.println(" ** "+getName()+" CHIUSO CANALE OUTPUT ** ");
             
        } //[m] run
                    
    } // {c} User
    
    /**[m][s]
     * metodo di collaudo
     * @param args  non usato
     * @throws IOException  errore open
     * @throws InterruptedException interruzione
     */
    public static void main(String[] args)
      throws IOException, InterruptedException
    {
        MachineConfig thisSys = new MachineConfig();
        TestIO test = new TestIO();
        Thread th[] = new Thread[4];
        IOChannel slo;
        byte[] buf = new byte[100];

        // crea e attiva utenti
        for (int i=0; i<=3; i++)
            (th[i] = test.new User("th"+(i+1), thisSys)).start();

        Util.sleep(8000); // lascia creare i canali ai thread
            
        while(true) {
            try {
                // apre un canale d'output ESCLUSIVO
                slo = new IOChannel("SLO_Main",
                  DeviceDesc.SERIALLINE_CLASS, 
                  thisSys.getDevDesc(MachineConfig.SERIALLINEOUT_DEV),
                  true, thisSys);
                break;
            } catch(IOException e)
            {
                // ricicla mentre gli altri thread
                // usano il device in modo 'common'
                if(!e.toString().endsWith("not available!"))
                    // non l'eccezione attesa, propaga
                    throw e;
                Util.sleep(1000);
            }
        }
        String end = "!! TestIO terminato !!\r\n";
        for (int i=0; i<end.length(); i++)
            buf[i] = (byte)end.charAt(i);
        slo.write(end.length(), buf);
    }

} //{c} TestIO

