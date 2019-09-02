package os.ada;

/**{c}
 * semaforo numerico in Ada
 * 
 * @author M.Moro DEI UNIPD
 * @version 1.00 2010-12-12
 * @version 1.01 2019-05-25 deprecated eliminato
*/

public class ADANSem extends ADAThread implements ADANSemStr
{
    private int semVal;
      // valore del semaforo
        
    /**[c]
     * thread server per semaforo numerico 
     * @param name  nome del thread
     * @param semInit valore iniziale del semaforo
     */
    public ADANSem(String name, int semInit)
    {
        super(name);
        semVal = semInit;
    } //[c]
         
    /**[m]
    * server di semaforo numerico
     */
    public void run()
    {
        // select unico
        Select sel = new Select();
            
        // entry p, choice=0
        Guard grd0 = new Guard()
          {
            public boolean when()
            {
                return semVal>=1;
            } //[m] evaluate
          } //{c} <anonim>
          ;
        Entry ent0 = new Entry()
          {
            public Object exec(Object inp)
            {
                // parametri in-out non significativi
                semVal--;
                return null;
            }
          } //{c} <anonim>
          ;
        sel.add(grd0, pStr, ent0); 
    
        // entry v, choice=1
        Entry ent1 = new Entry()
          {
            public Object exec(Object inp)
            {
                // parametri in-out non significativi
                semVal++;
                return null;
            }
          } //{c} <anonim>
          ;
        sel.add(vStr, ent1);
          // il v e' sempre possibile

        // entry val choice=2
        Entry ent2 = new Entry()
          {
            public Object exec(Object inp)
            {
// v1.01 deprecated                return new Integer(semVal);
                return Integer.valueOf(semVal);
            }
          } //{c} <anonim>
          ;
        sel.add(valStr, ent2);
          // val sempre possibile
    
        while (true)
            sel.accept();
    } //[m] run     
    
} //{c} ADANSem

