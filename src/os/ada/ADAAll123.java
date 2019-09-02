package os.ada;

/**{c}
 * Allocatore 123 in Ada
 * server
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2010-12-22
 * @version 1.01 2019-05-25  deprecated eliminati
*/

public class ADAAll123 extends ADAThread implements ADAAll123Str
{

    private static final long SERVTMO = 2000L;
      // timeout vari
    private int risorse;
      // risorse libere

    /**[c]
     * thread server allocatore 1-2-3 RV,
     * @param name  nome del thread
     * @param ris  risorse iniziali
     */
    public ADAAll123(String name, int ris)
    {
        super(name);
        risorse = ris;
    } //[c]

    /**[m]
     * allocatore 1-2-3
     */
    public void run()
    {
        // select unico
        Select sel = new Select();

        // entry preleva3, choice=0
        sel.add(new Guard()
          {
            public boolean when()
            {
                return risorse>=3;
            } //[m] evaluate
          } //{c} <anonim>
          , prel3Str,
          new Entry()
          {
            public Object exec(Object inp)
            {
                // parametro di input non significativo
                risorse -= 3;
// v1.01 deprecated                return new Integer(risorse);
                return Integer.valueOf(risorse);
            }
          } //{c} <anonim>
          );

        // entry preleva2, choice=1
        sel.add(new Guard()
          {
            public boolean when()
            {
                return risorse>=2 && entryCount(prel3Str)==0;
                  // priorita' alla richiesta superiore
            } //[m] evaluate
          } //{c} <anonim>
          , prel2Str,
          new Entry()
          {
            public Object exec(Object inp)
            {
                // parametro di input non significativo
                risorse -= 2;
// v1.01 deprecated                return new Integer(risorse);
                return Integer.valueOf(risorse);
            }
          } //{c} <anonim>
          );

        // entry preleva 1, choice=2
        sel.add(new Guard()
          {
            public boolean when()
            {
                return risorse>=1 && entryCount(prel3Str)==0 && 
                  entryCount(prel2Str)==0;
                  // priorita' alle richieste superiori
            } //[m] evaluate
          } //{c} <anonim>
          , prel1Str,
          new Entry()
          {
            public Object exec(Object inp)
            {
                // parametro di input non significativo
                risorse -= 1;
// v1.01 deprecated                return new Integer(risorse);
                return Integer.valueOf(risorse);
            }
          } //{c} <anonim>
          );

        // entry rilascia, choice=3
        sel.add(rilaStr,
          new Entry()
          {
            public Object exec(Object inp)
            {
                // parametro di input risorse rilasciate 1..3
                int ris = ((Integer)inp).intValue();
                risorse += ris;
// v1.01 deprecated                return new Integer(risorse);
                return Integer.valueOf(risorse);
            }
          } //{c} <anonim>
          );

        // entry stato, choice=4
        sel.add(new Guard()
          {
            public boolean when()
            {
                return risorse<=3;
            } //[m] when
          } //{c} <anonim>
          , SERVTMO  // delay costante
        );

        while (true)
        {
            int choice = sel.accept();
            System.out.println("[[[entry "+sel.choice2Str(choice));
            switch(choice)
            {
              case 4:
                System.out.println("[[!!!! SERVER TIMEOUT risorse="+risorse);
                break;
              default:
                // nulla
            }
        }
    } //[m] run

} //{c} ADAAll123
