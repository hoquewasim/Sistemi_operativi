package os;

import java.io.IOException;

/**{c}
 * rappresenta un'interfaccia hardware per
 * una linea seriale parte output -
 * la simulazione prevede la scrittura
 * su Sys.out che e' lo standard output bufferizzato
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2004-11-12
 * @version 2.00 2005-10-07 package os
 * @version 2.01 2012-05-09 terminazione del thread aggiunta
 * @version 2.02 2015-04-22 yield aggiunto
 */

class SerialLineOutDev extends OutputDevice
{
    private byte buffer = 0;
    
    /**{c}
     * thread che rappresenta il dispositivo fisico -
     * e' in grado di scrivere singoli byte dal
     * buffer, attivando al termine la routine di servizio
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
         * emette i byte su Sys.out e,
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
                if(status == BUSY)
                {
                    Sys.out.write(buffer);
                    Sys.out.flush();
                    Util.sleep(200);  // simula durata trasferimento
                    status = READY;
                }
                // attiva isr, e' senz'altro != BUSY
                if (intMask)
                    dh.isr();
/* 2015-04-22: M.Moro occorre forzare il RR
*/            
                yield();                    
            } //while
            System.out.println("SerialLineOutDev HT terminato!");
        } //[m] run
        
    } //{c} HwTh

    /**[c]
     * attiva il thread collegato
     */
    SerialLineOutDev()
    {
        Thread t = new HwTh();
        t.start();
    } //[c]
    
    /**[m]
     * scrittura carattere
     * @param d  area dove leggere
     */
    public void write(byte d[])
    {
        buffer = d[0];
        status = BUSY;
          // si prepara per il prossimo
    } //[m] write
        
    /**[m]
     * @see Device
     */
    public String toString()
    {
        return "SerialLineOutDev: buf=["+buffer+
          "] intMask="+intMask;
    } //[m] toString

} //{i} SerialLineInDev
