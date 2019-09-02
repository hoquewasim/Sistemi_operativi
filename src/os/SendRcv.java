package os;

import java.util.*;
import java.lang.reflect.*;
 
/**{c}
 * Classe di controllo che
 * realizza send-receive diretto
 * sincrono o asincrono 
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-05-04
 * @version 2.00 2003-10-03 package Os
 * @version 2.01 2005-10-07 package os
 * @version 2.02 2009-10-14 usati generics per Hastable
 */

public class SendRcv
{
    private static final int INITMSGS = 8;
    // dimensione iniziale di ogni coda di messaggi
   
//v2.02 old   private Hashtable dict;
    private Hashtable<String,RcvThread> dict;
	 
      // dizionario descrittori di thread

    /**[c]
     * alloca dizionario dei descrittori
     */      
    public SendRcv()
    {
//v2.02 old   dict = new Hashtable();	 
        dict = new Hashtable<String,RcvThread>();
    }
        
    /**[m]
     * registrazione del thread con creazione
     * dei canali di I/O su frame
     * @return frame di I/O
     */      
    public TASys register()
    {
        Thread th = Thread.currentThread();
          // il thread che si registra
        String name = th.getName();
        if (!dict.containsKey(name))
        {
            // non ancora registrato
            TASys sys = new TASys();
              // frame di I/O
            Semaphore priv = new Semaphore();
              // semaforo privato di attesa
            Semaphore mutex = new Semaphore(true);
              // mutex;
            FifoSBuf msgs = new FifoSBuf(INITMSGS);
              // la coda dei messaggi ricevuti per questo thread
            dict.put(name, new RcvThread(th, priv, mutex, msgs, sys));
              // registra descrittore nel dizionario
            sys.out.println("Registrato come:"+name);
            return sys;
        }
        // gia` registrato
//v2.02 old   return ((RcvThread)dict.get(name)).sys;        
        return dict.get(name).sys;
    } //[m] register
    
    /**[m]
     * prelievo di un messaggio inviato
     * @param sender  nome thread da cui ricevere
     * @return messaggio prelevato
     *         null se sender==receiver e non ci
     *         sono msg autospediti precedenti
     */
    public Object receive(String sender)
    {
        String myName = Thread.currentThread().getName();
//v2.02 old   RcvThread myDesc = (RcvThread)dict.get(myName);
        RcvThread myDesc = dict.get(myName);
          // il descrittore del ricevente
        if (myDesc == null)
            throw new InvalidThreadException(myName+" non registato");
//v2.02 old   RcvThread sndDesc = (RcvThread)dict.get(sender);
        RcvThread sndDesc = dict.get(sender);		  
          // il descrittore del mittente
        if (sndDesc == null)
        {
            myDesc.sys.out.println("^^ "+myName+" sender "+sender+" non registrato");
            return null;
        }

        myDesc.mutex.p();
        // cerca un messaggio dal mittente
        FifoSBuf msgs = myDesc.msgs;
        for (int i=0; i<msgs.size(); i++)
        {
            MsgDesc found = (MsgDesc)msgs.elem(i);
            if (found.sender.equals(sender))
            {
                // trovato
                myDesc.sys.out.println("** "+myName+" rcv estrae msg");
                msgs.extract(found);
                if (found.sync)
                    found.priv.v();
                      // risposta sincrona al mittente
                myDesc.mutex.v();
                return found.msg;
            } //if
        }
        // non trovato, deve attendere
        if (myName.equals(sender))
            // sender e receiver coincidono, non puo` attendere
        {
            myDesc.sys.out.println("^^ "+myName+" sender==receiver, NOP");
            myDesc.mutex.v();
            return null;
        }
            
        myDesc.sys.out.println("** "+myName+" rcv deve attendere invio immediato");
        myDesc.waiting = true;
        myDesc.sender = sender;
        myDesc.mutex.v();
        myDesc.priv.p();
        
        // ora il messaggio e` nel descrittore
        return myDesc.msg;
    } //[m] receive
        
    /**[m]
     * invio di un messaggio
     * @param msg  messaggio inviato (viene clonato)
     * @param receiver  nome thread che ricevere
     * @param sync  true se sincrono
     * @return true se ricevente trovato
     */
    public boolean send(CMsg msg, String receiver, boolean sync)
    {
        String myName = Thread.currentThread().getName();
//v2.02 old   RcvThread myDesc = (RcvThread)dict.get(myName);
        RcvThread myDesc = dict.get(myName);
		  
          // il descrittore del mittente
        if (myDesc == null)
            throw new InvalidThreadException(myName+" non registato");
//v2.02 old   RcvThread rcvDesc = (RcvThread)dict.get(receiver);
        RcvThread rcvDesc = dict.get(receiver);
		  
          // il descrittore del ricevente			 
        if (rcvDesc == null)
        {
            // ricevente non trovato
            myDesc.sys.out.println("^^ "+myName+" receiver "+receiver+" non registrato");
            return false;
        }

        Object msgCl = msg.clone();
        rcvDesc.mutex.p();
        if (rcvDesc.waiting && rcvDesc.sender.equals(myName))
        {
            // il ricevitore sta aspettando da questo thread:
            // invio diretto
            myDesc.sys.out.println("^^ "+myName+" send invio immediato");
            rcvDesc.waiting = false;
            rcvDesc.msg = msgCl;
            // ok sia sincrono che asincrono
            rcvDesc.mutex.v();
            rcvDesc.priv.v();
            return true;
        } // if
        
        // deve accodare
        myDesc.sys.out.println("^^ "+myName+" send accodamento");
        if (myName.equals(receiver))
            // sender e receiver coincidono, nosync
        {
            myDesc.sys.out.println("^^ "+myName+" sender==receiver, nosync");
            sync = false;
        }
            
        MsgDesc msgDesc = new MsgDesc(myName, msgCl, sync, myDesc.priv);
            // prepara descrittore messaggio
        rcvDesc.msgs.write(msgDesc);
        rcvDesc.mutex.v();
        if (sync)
            // attende sincronizzazione
        {
            myDesc.sys.out.println("^^ "+myName+" attende sync");
            myDesc.priv.p();
        }
    	return true;
    }

} //{c} SendRcv
