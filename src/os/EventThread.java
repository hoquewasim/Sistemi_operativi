package os;

/**{c}
 * classe di supporto -
 * memorizza una coppia thread - eventi ricevuti
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-04-22
 * @version 2.00 2003-10-01 package Os
 * @version 2.01 2005-10-07 package os
 */
class EventThread
{
    Thread th;  // thread id
    int events;  // eventi ricevuti
    
    /**[c]
     * costruttore base 
     * @param t  thread id
     */
    EventThread(Thread t)
    {
        th=t;
        events = 0;
    }

    /**[m][y]
     * Si assume che il thread chiamante sia quello
     * del descrittore
     * @see Events#wait(int)
     */
    synchronized int wait(int mask) 
    {
        int retMask = 0;
        try 
        { 
            while ((retMask = mask & events) == 0)
            {
                // deve attendere
                super.wait();
            } 
        } catch(InterruptedException e)
        {
        }
        events = 0;
          // azzera tutti gli eventi
        return retMask;
    } //[m][y] wait

    /**[m][y]
     * Si assume che il thread chiamante sia quello
     * del descrittore
     * @see Events#wait(int,long)
     */
    synchronized int wait(int mask, long timeout) 
    {
        if (timeout == Timeout.NOTIMEOUT)
            // ricondotta a normale wait()
            return wait(mask);

        int retMask;
        if((retMask = mask & events) == 0)
        {
            if (timeout == Timeout.IMMEDIATE)
                // sincronizzazione immediata non possibile
            {
                // cancella e ritorna tutti gli eventi
                retMask = events;
                events = 0;
                return retMask;
            }
            // deve attendere
            long exp = System.currentTimeMillis()+timeout;
              // istante di scadenza
            long diffTime = timeout;  
              // tempo effettivo di attesa
            while(true)
            {
                if (diffTime > 0)
                    try{ wait(diffTime); }
                    catch( InterruptedException e ){}
                diffTime = exp-System.currentTimeMillis();
                if ((retMask = mask & events) != 0)
                {
                    // cattura gli eventi e 
                    // cancella tutti
                    events = 0;
                    return retMask;
                }
                else if (diffTime <= 0)
                {
                    // risveglio per timeout
                    // cancella e ritorna tutti gli eventi
                    retMask = events;
                    events = 0;
                    return retMask;
                }
            } // while
        } // if (retMask==0)
        // non attende
        events = 0;
          // azzera tutti gli eventi
        return retMask;
    } //[m][y] wait

    /**[m][y]
     * Si assume che il thread chiamante sia quello
     * del descrittore
     * @see Events#waitSingle(int)
     */
    synchronized int waitSingle(int mask) 
    {
        int retMask = 0;
        try 
        { 
            while ((retMask = mask & events) == 0)
            {
                // deve attendere
                super.wait();
            } 
        } catch(InterruptedException e)
        {
        }
        events &= ~retMask;
          // azzera gli eventi catturati
        return retMask;
    } //[m][y] waitSingle

    /**[m][y]
     * Si assume che il thread chiamante sia quello
     * del descrittore
     * @see Events#waitSingle(int, long)
     */
    synchronized int waitSingle(int mask, long timeout) 
    {
        if (timeout == Timeout.NOTIMEOUT)
            // ricondotta a normale wait()
            return waitSingle(mask);

        int retMask;
        if((retMask = mask & events) == 0)
        {
            if (timeout == Timeout.IMMEDIATE)
                // sincronizzazione immediata non possibile
                return 0;
            // deve attendere
            long exp = System.currentTimeMillis()+timeout;
              // istante di scadenza
            long diffTime = timeout;  
              // tempo effettivo di attesa
            while(true)
            {
                if (diffTime > 0)
                    try{ wait(diffTime); }
                    catch( InterruptedException e ){}
                diffTime = exp-System.currentTimeMillis();
                if ((retMask = mask & events) != 0)
                {
                    events &= ~retMask;
                      // azzera gli eventi catturati
                    return retMask;
                }
                else if (diffTime <= 0)
                {
                    // risveglio per timeout
                    return 0;
                }
            } // while
        } // if (retMask==0)
        // non attende
        events &= ~retMask;
          // azzera gli eventi catturati
        return retMask;
    } //[m][y] waitSingle

    /**[m][y]
     * Si assume che il thread chiamante sia quello
     * del descrittore
     * @see Events#waitAll(int)
     */
    synchronized int waitAll(int mask) 
    {
        int retMask = 0;
        try 
        { 
            while ((retMask = mask & events) != mask)
            {
                // deve attendere
                super.wait();
            } 
        } catch(InterruptedException e)
        {
        }
        events &= ~retMask;
          // azzera gli eventi catturati
        return retMask;
    } //[m][y] waitAll

    /**[m][y]
     * Si assume che il thread chiamante sia quello
     * del descrittore
     * @see Events#waitAll(int, long)
     */
    synchronized int waitAll(int mask, long timeout) 
    {
        if (timeout == Timeout.NOTIMEOUT)
            // ricondotta a normale wait()
            return waitAll(mask);

        int retMask;
        if((retMask = mask & events) != mask)
        {
            if (timeout == Timeout.IMMEDIATE)
                // sincronizzazione immediata non possibile
                return 0;
            // deve attendere
            long exp = System.currentTimeMillis()+timeout;
              // istante di scadenza
            long diffTime = timeout;  
              // tempo effettivo di attesa
            while(true)
            {
                if (diffTime > 0)
                    try{ wait(diffTime); }
                    catch( InterruptedException e ){}
                diffTime = exp-System.currentTimeMillis();
                if ((retMask = mask & events) == mask)
                {
                    events &= ~retMask;
                      // azzera gli eventi catturati
                    return retMask;
                }
                else if (diffTime <= 0)
                {
                    // risveglio per timeout
                    return 0;
                }
            } // while
        } // if (retMask!=mask)
        // non attende
        events &= ~retMask;
          // azzera gli eventi catturati
        return retMask;
    } //[m][y] waitAll

    
    /**[m][y]
     * @see Events#signal(int, Thread)
     */
    synchronized void signal(int mask) 
    {
        events |= mask;
          // OR degli eventi inviati
        notify();
          // risveglia l'eventuale (unico) processo in attesa
    } //[m][y] signal

    /**[m][y]
     * @see Events#clearEvents(Thread)
     */
    synchronized int clearEvents() 
    {
        int retMask = events;
        events = 0;
        return retMask;
    } //[m][y] clearEvents

} //{c} EventThread
