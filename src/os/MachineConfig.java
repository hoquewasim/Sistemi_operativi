package os;

import java.util.*;
import java.io.*;

/**{c}
 * configurazione di un sistema
 * con i dispositivi 'installati'
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2004-11-12
 * @version 2.00 2005-10-07 package os
 * @version 2.01 2011-03-03 usati generics
 * @version 2.01 2012-05-09 terminazione dei thread aggiunta
 */
 
public class MachineConfig
{
    // numero totale dei dispositivi installati
    public static final int NUMDEVICES = 3;

    // indici dei dispositivi
    public  static final int SERIALLINEIN_DEV = 0;
    public  static final int SERIALLINEOUT_DEV = 1;
    public  static final int ADC_DEV = 2;
    
    private DeviceDesc devs[] = new DeviceDesc[NUMDEVICES];
      // insieme dei device installati

    /* Anche se non necessaria, per uniformita`
       con il testo viene implementata la coda
       dei canali associata a ciascun thread.
       Cio` e` realizzato con un hash table di
       descrittori, uno per ogni thread, che
       memorizzano la associata coda di 
       descrittori di canali
     */
    private Map<String, ChannelThread> chMap = 
      new HashMap<String, ChannelThread>();
      // set di thread con canali
      // dimensione e load factor di default
      // hashcode dalle chiavi che sono i nomi 
      // dei thread
      // metodi della mappa NON sincronizzati

    private MutexSem prot = new MutexSem();
      // protezione mappa
      
    /**[c]
     * costruttore base 
     */
    public MachineConfig()
    {
        // per semplicita' la configurazione
        // e' hard-coded in questo costruttore
        // in alternativa si potrebbe ricavare da un file esterno

        // installa device e device handler
        
        // linea seriale input simulata su Sys.in
        Device dev = new SerialLineInDev();
        devs[SERIALLINEIN_DEV] = new DeviceDesc("Serial Line Input 0", DeviceDesc.INPUT, 
          DeviceDesc.SERIALLINE_CLASS, new SerialLineType(), dev);
        DeviceHandler dh = new SerialLineInDH(devs[SERIALLINEIN_DEV]);
        dev.install(dh);
        dh.start();
          
        // linea seriale output simulata su Sys.out
        dev = new SerialLineOutDev();
        devs[SERIALLINEOUT_DEV] = new DeviceDesc("Serial Line Output 0", DeviceDesc.OUTPUT, 
          DeviceDesc.SERIALLINE_CLASS, new SerialLineType(), dev);
        dh = new SerialLineOutDH(devs[SERIALLINEOUT_DEV]);
        dev.install(dh);
        dh.start();

        // ADC con campioni dal file indicato
        BufferedReader br;
        try
        {
            br=new BufferedReader(new FileReader("ADCSamples.dat"));
        }
        catch(IOException err)
        {
            System.out.println("Errore apertura file ADC");
            return;
        }
        dev = new ADCDev(br);
        devs[ADC_DEV] = new DeviceDesc("ADC 0", DeviceDesc.INPUT, 
          DeviceDesc.ADC_CLASS, new ADCType(), dev);
        dh = new ADCDH(devs[ADC_DEV]);
        dev.install(dh);
        dh.start();
        
    } //[c]
    
    /**[c]
     * terminatore 
     */
    public void toStop()
    {
        devs[SERIALLINEIN_DEV].dev.toStop();    
        devs[SERIALLINEIN_DEV].dev.handler().toStop();    
        devs[SERIALLINEOUT_DEV].dev.toStop();    
        devs[SERIALLINEOUT_DEV].dev.handler().toStop();    
        devs[ADC_DEV].dev.toStop();    
        devs[ADC_DEV].dev.handler().toStop();    
    } //[m] stop

    /* [m]
     * eventuale inserimento descrittore thread/canali
     * @param th  thread da cercare
     * @return il descrittore canali associato al thread
     */
    ChannelThread getChThread(Thread th) 
    {
        ChannelThread ret;
        String thName = th.getName();
          // nome del thread usato come chiave
        prot.p();
        if ((ret = (ChannelThread) chMap.get(thName)) == null)
        {
            // nuovo descrittore da inserire
            chMap.put(thName, ret = new ChannelThread(th));
        } //if
        prot.v();
        return ret;
    } // [m] getChThread

    /* [m]
     * estrae descrittore
     * @param idx  indice del dispositivo nella tabella centrale
     * @return il descrittore del dispositivo
     */
    public DeviceDesc getDevDesc(int idx) 
    {
        return devs[idx];
    }
    
    /**[m][s]
     * metodo di collaudo
     * @param args  non usato
     * @throws IOException  errore open
     */
    public static void main(String[] args)
      throws IOException
    {
        byte[] buf = new byte[100];
        
        MachineConfig thisSys = new MachineConfig();

        // apre un canale d'input
        IOChannel sliIoc = new IOChannel("ProvaSLI",
          DeviceDesc.SERIALLINE_CLASS, thisSys.getDevDesc(SERIALLINEIN_DEV),
          thisSys);

        // apre un canale d'output
        IOChannel sloIoc = new IOChannel("ProvaSLO",
          DeviceDesc.SERIALLINE_CLASS, thisSys.getDevDesc(SERIALLINEOUT_DEV),
          thisSys);

        // apre ADC
        IOChannel adcIoc = new IOChannel("ProvaADC",
          DeviceDesc.ADC_CLASS, thisSys.getDevDesc(ADC_DEV),
          thisSys);
          
        Semaphore sync = new Semaphore(false);

        // input, legge 4 byte
        IOErr err = sliIoc.doIO(DeviceDesc.INPUT, 4, buf, sync);
        System.out.println("** main si mette in attesa 1");
        sync.p();
        System.out.println("!! main ricevuto da serial line input ->"+
          buf[0]+" "+buf[1]+" "+buf[2]+" "+buf[3]+"<-");
        sliIoc.close();

        // output, emette 6 byte
        for(int i=0; i<6; i++)
            buf[i] = (byte)(i+(int)'A');
        err = sloIoc.doIO(DeviceDesc.OUTPUT, 6, buf, sync);
        System.out.println("** main si mette in attesa 2");
        sync.p();
        System.out.println("!! main inviato su serial line output");
        sloIoc.close();

        // ADC
        // 10 campioni da 16 bit
        err = adcIoc.doIO(DeviceDesc.INPUT, 20, buf, sync);
        System.out.println("** main si mette in attesa 3");
        sync.p();
        System.out.print("!! main ricevuto 10 campioni da ADC ->");
        for(int i=0; i<10; i++)
            System.out.print(" "+(buf[i*2]+(buf[i*2+1]<<8)));
        System.out.println("<-");
        adcIoc.close();
        
        // termina thread
        thisSys.toStop();

    }

} //{c} MachineConfig
