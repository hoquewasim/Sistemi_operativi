package os.ada;

/**{c}
 * multi buffer server in Ada
 * 
 * @author M.Moro DEI UNIPD
 * @version 1.00 2010-12-12
 * @version 1.01 2019-05-25 deprecated eliminato
*/

public class ADAMBuf extends ADAThread
{
    private static final long SERVTMO = 1000L;
      // timeout del loop

    private Object[] buf;  // il buffer circolare
    int size;              // dimensione del buffer
    int numEl=0;           // numero corrente di elementi presenti
    int head=0, tail=0;    // puntatori

    /**[c]
     * thread server per il multibuffer
     * @param name  nome del thread
     * @param size  dimensione buffer
     */
    public ADAMBuf(String name, int size)
    {
        super(name);
        this.size = size;
        buf = new Object[size];
    } //[c]

    public void run()
    {
        // il server ha gli usuali 2 entry, GET e PUT

        // select unico
        Select sel = new Select();

        // entry GET, choice=0
        sel.add(new Guard()
          {
            public boolean when()
            {
                return numEl>0;  // buffer non vuoto
            } 
          } //{c} <anonima>
          , "GET",
          new Entry()
          {
            public Object exec(Object inp)
            {
                // parametro di input non significativo
                return get();
            }
          } //{c} <anonima>
          );

        // entry PUT, choice=1
        sel.add(new Guard()
          {
            public boolean when()
            {
                return numEl<size;  // buffer non pieno
            }
          } //{c} <anonima>
          , "PUT",
          new Entry()
          {
            public Object exec(Object inp)
            {
                put(inp);
                return null;  // nessun parametro di uscita
            }
          } //{c} <anonima>
          );
 
        // entry NUM, choice=2
        sel.add(// sempre aperta
          "NUM",
          new Entry()
          {
            public Object exec(Object inp)
            {
                // parametro di input non significativo
// v1.01 deprecated                return new Integer(numEl);
                return Integer.valueOf(numEl);
            }
          } //{c} <anonim>
          );

        // entry num, choice=3
        sel.add(new Guard()
          {
            public boolean when()
            {
                return numEl>=size-2;  // 2 elementi liberi o meno
            } //[m] when
          } //{c} <anonima>
          , SERVTMO  // delay costante
        );

        while (true)
        {
            int choice = sel.accept();
            System.out.println("[[[entry "+sel.choice2Str(choice));
            switch(choice)
            {
              case 3:
                System.out.println("Attenzione numEl="+numEl);
                break;
              default:
                // nulla
            }
        }
    } //[m] run

    /**[m]
     * procedura dell'entry get
     * @return l'oggetto estratto dalla coda circolare
     */
    private Object get()
    {
        Object ret = buf[head];
        head = (head+1)%size;
        numEl--;
        return ret;
    } //[m] get
 
    /**[m]
     * procedura dell'entry put
     * @param val  l'oggetto da inserire nella coda circolare
     */
    private void put(Object val)
    {
        buf[tail] = val;
        tail = (tail+1)%size;
        numEl++;
    } //[m] put

} //{c} ADAMBuf
