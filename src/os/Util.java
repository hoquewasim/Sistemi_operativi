package os;

/**{c}
 * metodi di utilita`
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-04-17
 * @version 2.00 2003-10-04 package Os
 * @version 2.01 2005-10-07 package os
 * @version 2.02 2005-11-28 corretta l'imprecisione formula lineare
 */
public class Util 
{
    /**[m][s]
     * sospende il thread corrente
     * @param ms  attesa in ms
     */
	public static void sleep(long ms)
	{ 
	    try {
	        Thread.sleep(ms);
	    }
	    catch(InterruptedException e) {}
    }
    
    /**[m][s]
     * sospende il thread corrente per un tempo casuale
     * @param minMs  attesa minima in ms
     * @param maxMs  attesa massima in ms
     */
    public static void rsleep(long minMs, long maxMs)
	{ 
	    long diff = maxMs-minMs;
	    try {
	        Thread.sleep(
	          (long)(Math.random()*((double)diff+1.0d))+minMs);
	    }
	    catch(InterruptedException e) {}
    }
    
    /**[m][s]
     * sospende il thread corrente per un tempo casuale
     * @param ms  attesa massima in ms
     */
    public static void rsleep(long ms)
	{ rsleep(0, ms); }
	
    /**[m][s]
     * valore casuale nell'intervallo fornito
     * @param minVal  valore minimo
     * @param maxVal  valore massimo
     * @return Valore casuale
     */
    public static int randVal(int minVal, int maxVal)
    { 
        int diff = maxVal-minVal;
        return (int)(Math.random()*((double)diff+1.0d))+minVal;
    }
    
    /**[m][s]
     * valore casuale nell'intervallo fornito
     * @param minVal  valore minimo
     * @param maxVal  valore massimo
     * @return Valore casuale
     */
    public static long randVal(long minVal, long maxVal)
    { 
        long diff = maxVal-minVal;
        return (long)(Math.random()*((double)diff+1.0d))+minVal;
    }
    
    /**[m][s]
     * prova della regola lineare di distribuzione
     * @param args  non usato
     */
    public static void main(String args[])
    { 
    	int min = Sys.in.readInt("Battere valore minimo:");
    	int max = Sys.in.readInt("Battere valore massimo:");
    	int num = Sys.in.readInt("Battere numero campioni:");
        int arr[] = new int[max-min+1];
        for (int i=0; i<num; i++)
        {
            int ran = randVal(min,max);
            if (ran<min || ran >max)
                System.out.println("*********** ran="+ran);
            else
                arr[ran-min]++;
        }
        System.out.println("Statistica (valore, campioni, percentuale):");
        for (int i=0; i<max-min+1; i++)
            System.out.println(""+(min+i)+" "+arr[i]+" "+(((double)arr[i])*100/(double)num)+"%");
    }
    
} //{c} Util
