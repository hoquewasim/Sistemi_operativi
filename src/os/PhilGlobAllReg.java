package os;

/**{c}
 * filosofo con allocazione globale -
 * si utilizza una regione critica
 * @author M.Moro DEI UNIPD
 * @version 1.00 2006-10-19
 */

public class PhilGlobAllReg extends Phil
{
    private static boolean forks[];
      // le forchette
    private static Region reg = new Region(0);
      // regione di controllo
    private static PhilAnim phan;
      // animatore

    /**[c]
     * filosofo con nome
     * @param name  nome del thread
     * @param id  indice del filosofo
     * @param mind  minima attesa
     * @param maxd  massima attesa
     */
    public PhilGlobAllReg(String name, int id, int mind, int maxd)
    { 
        super(name, id, mind, maxd); 
    } //[c]
        
    /**[m]
     * acquisizione forchette
     * @param idxPh  indice filosofo
     */
    public void getForks(final int idxPh)
    {
        reg.enterWhen(new RegionCondition() 
          {
            public boolean evaluate()
            {
                return forks[idxPh] && forks[(idxPh+1)%forks.length];
            }
          }
        );
        forks[idxPh]=false;
        forks[(idxPh+1)%forks.length] = false;
        reg.leave();
        System.out.println("Il filosofo "+getName()+" ha entrambe le forchette");
        phan.notifyEvent(idxPh, PhilAnim.BOTHSTATUS);
    } //[m] getForks
    
    /**[m]
     * rilascio forchette
     * @param idxPh  indice filosofo
     */
    public void putForks(int idxPh)
    {
        reg.enterWhen();
        forks[idxPh]=true;
        forks[(idxPh+1)%forks.length] = true;
        reg.leave();
        phan.notifyEvent(idxPh, PhilAnim.THINKSTATUS);
    } //[m] putForks
    
    /**[m][s]
     * main di collaudo
     * @param args  non usato
     */
    public static void main(String[] args) 
    {
    	int numPh = Sys.in.readInt("Test PhilGlobAllReg: battere numero filosofi:");
    	forks = new boolean[numPh];
        phan = new PhilAnim("allocazione globale", numPh);
        Util.sleep(2000);
        for(int i=0; i<numPh; i++)
            forks[i] = true;
        Phil phils[] = new PhilGlobAllReg[numPh];
        for(int i=0; i<numPh; i++)
            phils[i] = new PhilGlobAllReg("phil"+i, i, 3000, 8000);
        System.err.println("** Battere Ctrl-C per terminare!");
        for(int i=0; i<numPh; phils[i++].start());
    } //[m][s] main
    
} //{c} PhilGlobAllReg