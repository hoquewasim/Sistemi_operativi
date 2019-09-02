package os;

/**{c}
 * pipe - 
 * mailbox per uno stream di byte 
 * 2 semafori con p e v multipli e un mutex di protezione 
 * @author M.Moro DEI UNIPD
 * @version 1.00 2003-10-03
 * @version 2.00 2005-10-07 package
 */
 
public class Pipe
{
    private CountSem spaceAval;
      // locazioni disponibili
    private CountSem dataAval;
      // dati disponibili
    private MutexSem mutex = new MutexSem();
      // mutua esclusione
    private MutexSem readMutex = new MutexSem();
      // mutua esclusione per i lettori
    private MutexSem writeMutex = new MutexSem();
      // mutua esclusione per gli scrittori
    private byte data[];
      // buffer dati
    private int numEl, head=0, tail=0;
      // numero elementi, dove si legge, dove si scrive
      
      
    /**[c]
     * inizializza al numero di elementi indicati
     * @param n  numero elementi del buffer
     */
    public Pipe(int n)
    {
    	numEl = n;
    	spaceAval = new CountSem(numEl);
        dataAval = new CountSem(0);
        data = new byte[numEl];
    } //[c]
    
    /**[m]
     * prelievo dati
     * @param num  numero di byte da leggere
     * @param dest  array di destinazione
     */
    public void read(int num, byte dest[])
    {
        readMutex.p();
          // sequenzializza i lettori
        int readIdx = 0;
        while (num > 0)
        {
            int readNum = Math.min(num, dataAval.value());
            if (readNum == 0)
            {
                // per evitare il deadlock
                // attende un solo byte disponibile
                dataAval.p();
                mutex.p();  // mutex
                dest[readIdx++] = data[head];
                head = ++head % numEl;
                num--;
                mutex.v();
                spaceAval.v();
                  // segnala spazio disponibile
            }
            else
            {
                // si possono leggere piu` byte
                dataAval.p(readNum);
                // dati disponibile
                mutex.p();  // mutex
                // potendosi intrufolare uno scrittore
                // i dati disponibili potrebbero essere
                // di piu` di readNum: poco male
                int first = (numEl-head > readNum) ? 
                  readNum : numEl-head;
                System.arraycopy(data, head, dest, readIdx, first);
                head = (head+first) % numEl;
                readIdx += first;
                  // prima parte
                if (first < readNum)
                {
                    System.arraycopy(data, head, dest, readIdx, 
                      readNum-first);
                    head = (head+readNum-first) % numEl;
                    readIdx += readNum-first;
                      // seconda parte
                }
                num -= readNum;
                mutex.v();
                spaceAval.v(readNum);  // segnala spazio disponibile
            } // if(readNum)
        } // while(num)
        readMutex.v();
    } //[m] read

    /**[m]
     * deposito dei dati
     * @param num  numero byte da memorizzare
     * @param src  array sorgente
     */
    public void write(int num, byte src[])
    {
        writeMutex.p();
          // sequenzializza gli scrittori
        int writeIdx = 0;
        while (num > 0)
        {
            int writeNum = Math.min(num, spaceAval.value());
            if (writeNum == 0)
            {
                // per evitare il deadlock
                // attende un solo byte disponibile
                spaceAval.p();
                mutex.p();  // mutex
                data[tail] = src[writeIdx++];
                tail = ++tail % numEl;
                num--;
                mutex.v();
                dataAval.v();
                  // segnala dato disponibile
            }
            else
            {
                // si possono scriverepiu` byte
                spaceAval.p(writeNum);
                // spazio disponibile
                mutex.p();  // mutex
                // potendosi intrufolare un lettore
                // gli spazi disponibili potrebbero essere
                // di piu` di writeNum: poco male
                int first = (numEl-tail > writeNum) ? 
                  writeNum : numEl-tail;
                System.arraycopy(src, writeIdx, data, tail, first);
                tail = (tail+first) % numEl;
                writeIdx += first;
                // prima parte
                if (first < writeNum)
                {
                    System.arraycopy(src, writeIdx, data, tail, 
                      writeNum-first);
                    head = (head+writeNum-first) % numEl;
                    writeIdx += writeNum-first;
                      // seconda parte
                }
                num -= writeNum;
                mutex.v();
                dataAval.v(writeNum);  
                  // segnala dati disponibili
            } // if(readNum)
        } // while(num)
        writeMutex.v();
    } //[m] write

    /**[m}
     * byte memorizzati
     * @return numero byte memorizzati
     */
    public int size()
    { return dataAval.value(); }
    
    /**[m}
     * spazio allocato
     * @return numero elementi allocati
     */
    public int dimen()
    { return numEl; }
    
} //{c} Pipe

