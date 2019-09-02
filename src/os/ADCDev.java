package os;

import java.io.*;

/**{c}
 * rappresenta un'interfaccia hardware per
 * un ADC -
 * la simulazione prevede la lettura
 * dei campioni da un file da indicare in fase di costruzione
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2004-11-12
 * @version 2.00 2005-10-07 package os
 * @version 2.01 2012-05-09 terminazione del thread aggiunta
 * @version 2.02 2015-04-22 yield aggiunto
 */

class ADCDev extends InputDevice
{
    private int buffer = 0;  // campione letto, 16 bit
    private BufferedReader in;  // file per la simulazione
    
    /**{c}
     * thread che rappresenta il dispositivo fisico -
     * e' in grado di leggere singoli campioni e trasferirli
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
         * riceve i byte dal file specificato e,
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
                    // BUSY, acquisisce campione
                    try {
                        buffer = Integer.parseInt(in.readLine());
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
            System.out.println("ADCDev HT terminato!");
        } //[m] run
        
    } //{c} HwTh

    /**[c]
     * attiva il thread collegato
     * @param re  file di input per la simulazione
     */
    ADCDev(BufferedReader re)
    {
        in=re;
        Thread t = new HwTh();
        t.start();
    } //[c]
    
    /**[m]
     * lettura carattere
     * @param d  area dove scrivere
     */
    public void read(byte d[])
    {
        d[0] = (byte)(buffer&0xff); // LSBy
        d[1] = (byte)((buffer>>8)&0xff); // MSBy
        status = BUSY;
          // si prepara per il prossimo
    } //[m] read
        
    /**[m][a]
     * @see Device
     */
    public String toString()
    {
        return "ADCDev: buf["+buffer+
          "] intMask="+intMask;
    } //[m] toString

} //{i} ADCInDev
