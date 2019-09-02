package os;

import java.io.IOException;
//
import os.Util;

/**{c}
 * rappresenta un'interfaccia hardware per
 * una linea seriale parte input -
 * la simulazione prevede la lettura
 * da Sys.in che e' lo standard input bufferizzato
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2004-11-12
 * @version 2.00 2005-10-07 package os
 * @version 2.01 2012-05-09 terminazione del thread aggiunta
 */

class SerialLineInDev extends InputDevice
{
    private byte buffer = 0;
    
    /**{c}
     * thread che rappresenta il dispositivo fisico -
     * e' in grado di leggere singoli byte e trasferirli
     * nel buffer, attivando la routine di servizio
     * associata se il dispositivo e' abilitato ad interrompere
     */
    private class HwTh extends Thread
    {
        
        /**[c]
         * @param d  device handler associato
         */
        public HwTh(DeviceHandler d)
        {  dh = d; }
        
        /**[c]
         */
        public HwTh()
        {}
        
        /**[m]
         * riceve i byte da Sys.in e,
         * se interrupt abilitati, chiama
         * la routine di servizio
         */
        public void run()
        {
/* 2012-05-09: M.Moro terminazione aggiunta        
            while(true)
*/            
            while(!toBeStopped)
            {
                if (status == BUSY)
                {
                    // BUSY, acquisisce bytes
                    try {
                        buffer = (byte)Sys.in.read();
                    } catch(IOException dummy) 
                    {
                        errcode = DeviceDesc.GENERICERR;
                    }
                    //non controlla se BUSY; se e' ancora
                    // READY il byte sovrascrive uno precedente
                    status = READY;
                }
                // attiva isr, e' senz'altro != BUSY
                if (intMask)
                    dh.isr();
/* 2015-04-22: M.Moro occorre forzare il RR
*/            
                yield();                    
                // anche se il thread rimane pronto per un lungo periodo
                // (status!=BUSY intMask==false) il time-slice lo fa commutare
            } //while
            System.out.println("SerialLineInDev HT terminato!");
        } //[m] run
        
    } //{c} HwTh

    /**[c]
     * attiva il thread collegato
     */
    SerialLineInDev()
    {
        Thread t = new HwTh();
        t.start();
    } //[c]
    
    /**[m]
     * lettura carattere
     * @param d  area dove scrivere
     */
    public void read(byte d[])
    {
        d[0] = buffer;
        status = BUSY;
          // si prepara per il prossimo
    } //[m] read
        
    /**[m][a]
     * @see Device
     */
    public String toString()
    {
        return "SerialLineInDev: buf["+buffer+
          "] intMask="+intMask;
    } //[m] toString

} //{i} SerialLineInDev
