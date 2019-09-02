package os.ada;

/**{c}
 * lettori-scrittori in Ada con prenotazione
 * dello scrittore
 * @author M.Moro DEI UNIPD
 * @version 1.00 2010-12-12
 * @version 1.01 2019-05-25  deprecated eliminato
*/

public class ADARW extends ADAThread implements ADARWStr
{

    private ADARWDb db;
      // il data base condiviso
    private int readers = 0;
      // conteggio lettori
    private boolean writers = false;
      // scrittore pendente


    /**[c]
     * thread server per server RW
     * @param name  nome del thread
     * @param db  variabile condivisa 
     */
    public ADARW(String name, ADARWDb db)
    {
        super(name);
        this.db = db;
    } //[c]

    /**[m]
     * RW server
     */
    public void run()
    {
        // il server ha gli usuali 4 entry, piu'
        // stato che restituisce il valore dei contatori

        // select unico
        Select sel = new Select();

        // entry inizio_lettura, choice=0
        sel.add(new Guard()
          {
            public boolean when()
            {
                return ! writers;
            }
          } //{c} <anonim>
          , STARTREADSTR,
          new Entry()
          {
            public Object exec(Object inp)
            {
                // parametri non significativi
                readers++;
                return null;
            }
          } //{c} <anonim>
          );

        // entry fine_lettura, choice=1
        sel.add(STOPREADSTR,
          new Entry()
          {
            public Object exec(Object inp)
            {
                // parametri non significativi
                readers--;
                return null;
            }
          } //{c} <anonim>
          );

        // entry richiesta_scrittura, choice=2
        sel.add(new Guard()
          {
            public boolean when()
            {
                return ! writers;
            }
          } //{c} <anonim>
          , REQWRITESTR,
          new Entry()
          {
            public Object exec(Object inp)
            {
                // parametri non significativi
                writers=true;
                return null;
            }
          } //{c} <anonim>
          );

        // entry scrittura, choice=3
        sel.add(new Guard()
          {
            public boolean when()
            {
                return readers==0;
            } 
          } //{c} <anonim>
          , COMPWRITESTR,
          new Entry()
          {
            public Object exec(Object inp)
            {
                // inp e' di tipo generico
                db.write(inp);
                return null;
            }
          } //{c} <anonim>
          );

        // entry stato, choice=4
        sel.add(
          STATSTR,
          new Entry()
          {
            public Object exec(Object inp)
            {
// v.1.01 deprecated                return new Integer(writers ? -readers : readers);
                return Integer.valueOf(writers ? -readers : readers);
            }
          } //{c} <anonim>
          );

        while (true)
        {
           int choice = sel.accept();
           switch(choice)
           {
             case 3: // COMPLETE_WRITING
               writers = false;
               break;
             default:
               // nulla
           }
       } // while
   } //[m] run

} //{c} ADARW
