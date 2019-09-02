package os;

import java.util.*;

/**{c}
 * classe di supporto -
 * memorizza una coppia thread id - coda canali
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2004-11-11
 * @version 2.00 2005-10-07 package os
 * @version 2.01 2005-11-18 public
 * @version 2.02 2010-03-03 usati generics
 */
public class ChannelThread
{
    public static final int IN_CH = 0;
    public static final int OUT_CH = 1;
    Thread th;  // thread id
    int chNum[] = new int[2];
      // numero progressivo del canale
      // 0 input
      // 1 output
    List<IOChannel> channels = new LinkedList<IOChannel>();
      // coda dei canali definiti
      // associati al thread

    /**[c]
     * costruttore base 
     * @param t  thread id
     */
    public ChannelThread(Thread t)
    {
       th=t;
       chNum[IN_CH] = 
       chNum[OUT_CH] = 0;
         // valori iniziali di conteggio
         // progessivo
   }
} //{c} ChannelThread
    

      
    
