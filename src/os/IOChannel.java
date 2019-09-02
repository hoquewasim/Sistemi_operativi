package os;

import java.util.*;
import java.io.IOException;
 
/**{c}
 * canale di I/O
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2004-11-21
 * @version 2.00 2005-10-07 package os
 * @version 2.01 2005-11-18 resa estensibile
 */
 
public class IOChannel
{
    private String name;
      // nome del canale
    private int devClass;
      // codice di tipologia del dispositivo
    protected DeviceDesc dev;
      // descrittore del dispositivo -
      // sostituisce il numero di dispositivo
      // per evitare di accedere ogni volta
      // all'array dei dispositivi del sistema -
      // null se canale chiuso
    private int num;
      // numero di canale per il thread 
      // che lo usa
    
    /**[c]
     * costruttore base 
     * @param n  nome del canale
     * @param c  classe del dispositivo
     * @param d  descrittore del dispositivo
     * @param locked  true se esclusivo, false altrimenti
     * @param mac  il sistema
     * @throws IOException  se canale chiuso o device occupato
     */
    public IOChannel(String n, int c, DeviceDesc d, boolean locked, MachineConfig mac) 
      throws IOException
    {
        name = n;
        devClass = c;
        // dev = d; eseguito in open
        open(d, locked);
        
        // l'oggetto descrittore del dispositivo
        // e' aperto nel modo specificato da locked
        // per il thread che sta aprendo il canale
        Thread th = Thread.currentThread();
        ChannelThread cht = mac.getChThread(th);
          // elemento descrittore dei canali per 
          // questo thread
        switch(d.mode)
        {
          case DeviceDesc.INPUT:
            num = (cht.chNum[ChannelThread.IN_CH])++;
            if (n == null)
                name = "I"+num;
            break;
          case DeviceDesc.OUTPUT:
            num = (cht.chNum[ChannelThread.OUT_CH])++;
            if (n == null)
                name = "O"+num;
            break;
        } // switch
        cht.channels.add(this);
    } //[c]

    /**[c]
     * costruttore per modo non esclusivo
     * @param n  nome del canale
     * @param c  classe del dispositivo
     * @param d  descrittore del dispositivo
     * @param mac  il sistema
     * @throws IOException  se canale chiuso o device occupato
     */
    public IOChannel(String n, int c, DeviceDesc d, MachineConfig mac) 
      throws IOException
    {  this(n, c, d, false, mac);  }

    /**[c]
     * costruttore con nome di default 
     * @param c  classe del dispositivo
     * @param d  descrittore del dispositivo
     * @param mac  il sistema
     * @throws IOException  se canale chiuso o device occupato
     */
    public IOChannel(int c, DeviceDesc d, MachineConfig mac) 
      throws IOException
    {
        this(null, c, d, mac);
    } //[c]
    
    /**[m]
     * (ri)apertura NON esclusiva del canale
     * @param d  device da associare al canale
     * @throws IOException  se canale chiuso o device occupato
     */
    public void open(DeviceDesc d) 
      throws IOException
    {
        open(d, false);
    } //[m] open

    /**[m]
     * (ri)apertura del canale con specifica del modo
     * @param d  device da associare al canale
     * @param locked true se esclusivo, false altrimenti
     * @throws IOException  se canale chiuso o device occupato
     */
    public void open(DeviceDesc d, boolean locked) 
      throws IOException
    {
        if(d.devClass != devClass)
            // dispositivo di classe errata
            throw new IOException("Channel "+ name +
              " device class different from channel class!");

        // mutex
        d.prot.p();
        if (d.status == DeviceDesc.LOCKED ||
          (d.status == DeviceDesc.COMMON && locked))
            // dispositivo non apribile
        {
            d.prot.v();
            throw new IOException("Channel "+ name +
              " device "+d+ " not available!");
        }
        dev = d;
        if (dev.status != DeviceDesc.COMMON)
        {
            // free
            dev.status = locked ? 
              DeviceDesc.LOCKED : DeviceDesc.COMMON;
            dev.th = Thread.currentThread();
        }
        if(++dev.numTh == 1)
            // accende il dispositivo fisico
            dev.dev.init();
        d.prot.v();
    } //[m] open

    /**[m]
     * chiusura del canale
     */
    public void close()
    {
        dev.prot.p();
        if (--dev.numTh == 0)
        {
            // nessun thread associato
            // clear del dispositivo
            dev.status = DeviceDesc.FREE;
            dev.th = null;
            dev.dev.clear();
        }
        dev.prot.v();
        dev = null;
    } //[m] close
        
    /**[m]
     * lettura da dispositivo di input -
     * chiamata dall'applicazione, fa da ponte
     * verso la DOIO
     * @param qty  quantita` da trasferire in byte
     * @param data  area dati
     * @throws IOException  se canale chiuso o modo illegale o
     *                      errore nell'operazione
     */
    public void read(int qty, byte data[]) throws IOException
    {
        // la chiamata dovrebbe essere una SVC
        IOErr err = doIO(DeviceDesc.INPUT, qty, data);
        if (err.errcode != DeviceDesc.NOERR)
            // errore
            throw new IOException("Channel "+ name + " read error: "+
              err.errDesc);
    } //[m] read

    /**[m]
     * lettura da dispositivo di input di un array di byte
     * di lunghezza predisposta dal chiamante -
     * chiamata dall'applicazione, fa da ponte
     * verso la DOIO
     * @param data  area dati
     * @throws IOException  se canale chiuso o modo illegale o
     *                      errore nell'operazione
     */
    public void read(byte data[]) throws IOException
    {
        // la chiamata dovrebbe essere una SVC
        IOErr err = doIO(DeviceDesc.INPUT, data.length, data);
        if (err.errcode != DeviceDesc.NOERR)
            // errore
            throw new IOException("Channel "+ name + " read error: "+
              err.errDesc);
    } //[m] read

    /**[m]
     * scrittura su dispositivo di output -
     * chiamata dall'applicazione, fa da ponte
     * verso la DOIO
     * @param qty  quantita` da trasferire in byte
     * @param data  area dati
     * @throws IOException  se canale chiuso o modo illegale o
     *                      errore nell'operazione
     */
    public void write(int qty, byte data[]) throws IOException
    {
        // la chiamata dovrebbe essere una SVC
        IOErr err = doIO(DeviceDesc.OUTPUT, qty, data);
        if (err.errcode != DeviceDesc.NOERR)
            // errore
            throw new IOException("Channel "+ name + " write error: "+
              err.errDesc);
    } //[m] write

    /**[m]
     * scrittura su dispositivo di output di un array di byte
     * di lunghezza predisposta dal chiamante -
     * chiamata dall'applicazione, fa da ponte
     * verso la DOIO
     * @param data  area dati
     * @throws IOException  se canale chiuso o modo illegale o
     *                      errore nell'operazione
     */
    public void write(byte data[]) throws IOException
    {
        // la chiamata dovrebbe essere una SVC
        IOErr err = doIO(DeviceDesc.OUTPUT, data.length, data);
        if (err.errcode != DeviceDesc.NOERR)
            // errore
            throw new IOException("Channel "+ name + " write error: "+
              err.errDesc);
    } //[m] write

    /**[m]
     * operazione di I/O
     * @param mode  modo dell'operazione
     * @param qty  quantita` da trasferire in byte
     * @param data  area dati
     * @param s  semaforo 'richiesta servita'
     * @return un oggetto per il codice d'errore
     * @throws IOException  se canale chiuso o modo illegale
     */
    public IOErr doIO(int mode, int qty, byte data[],
      Semaphore s) throws IOException
    {
        // controllo parametri
        if (dev == null)
            // canale chiuso
            throw new IOException("Channel "+ name +
              " not open!");
        if (mode == DeviceDesc.INPUT && 
          dev.mode ==  DeviceDesc.OUTPUT ||
          mode == DeviceDesc.OUTPUT && 
          dev.mode ==  DeviceDesc.INPUT)
            // modi incompatibili
            throw new IOException("Channel "+ name +
              ((mode == DeviceDesc.INPUT) ?
              " input" : " output")+
              " illegal operation!");
       
         // prepara I/O request block
         IOErr ret = new IOErr();
         IORB req = new IORB(data, qty, mode, 
           Thread.currentThread(), s, ret);

         // il metodo e' in gran parte rientrante:
         // e' necessario solo proteggere la coda di IORB
         dev.prot.p();
         dev.iorbQueue.add(req);
         dev.prot.v();

         // signal su 'richiesta attiva'
         dev.ar.v();
         return ret;
    } //[m] doIO

    /**[m]
     * operazione di I/O sincronizzata
     * @param mode  modo dell'operazione
     * @param qty  quantita` da trasferire in byte
     * @param data  area dati
     * @return un oggetto per il codice d'errore
     * @throws IOException  se canale chiuso o modo illegale
     */
    public IOErr doIO(int mode, int qty, byte data[]) 
      throws IOException
    {
        Semaphore s = new Semaphore(false);
        IOErr ret = doIO(mode, qty, data, s);
        s.p();
        return ret;
    } 
    
    /**[m]
     * Conversione a stringa
     * @return la stringa
     */
    public String toString()
    {
        return "IOChannel "+name+
          " num="+num+" devClass="+
          devClass+ " device "+
          dev;
    }
    
} //{c} IOChannel
