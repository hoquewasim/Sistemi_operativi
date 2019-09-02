package os;

/**{c}
 * buffer multiplo FIFO senza protezione 
 * autoespandente, con ricerca
 * @author M.Moro DEI UNIPD
 * @version 1.00 2002-04-28
 * @version 2.00 2003-10-03 package Os
 * @version 2.01 2005-10-07 package os
 */
 
public class FifoSBuf implements SBuffer
{
    private Object data[];
      // buffer dati
    private int numEl, numData=0, head=0, tail=0;
      // numero elementi, dati memorizzati,
      // dove si legge, dove si scrive
      
      
    /**[c]
     * inizializza al numero di elementi indicati
     * @param n  numero iniziale elementi del buffer
     */
    public FifoSBuf(int n)
    {
    	numEl = n;
        data = new Object[numEl];
    } 
    
    /**[m]
     * espansione del buffer per raddoppio
     * @return dato prelevato, null se buffer vuoto
     */
    private void expand()
    {
        Object newBuf[] = new Object[numEl<<1];
          // nuovo buffer
        for (int i=0; i<numEl; newBuf[i] = data[i], i++);
        numEl <<= 1;  // raddoppio
        // head e tail rimangono inalterati
    } //[m] expand

    /**[m]
     * prelievo del dato
     * @return dato prelevato, null se buffer vuoto
     */
    public Object read()
    {
        if (numData == 0)
            // buffer vuoto
            return null;
        Object ret = data[head];
        data[head] = null;
        head = (++head) % numEl;
        numData--;
        return ret;
    }

    /**[m]
     * deposito del dato
     * @param d  dato da memorizzare
     */
    public void write(Object d)
    {
        if (numData == numEl)
            // non c'e` spazio, espansione
            expand();
        data[tail] = d;
        tail = (++tail) % numEl;
        numData++;
    }

    /**[m}
     * dati memorizzati
     * @return numero dati memorizzati
     */
    public int size()
    { return numData; }
    
    /**[m}
     * alementi allocati
     * @return numero elementi allocati
     */
    public int dimen()
    { return numEl; }
    
    /**[m]
     * elemento in posizione
     * @param pos  posizione elemento (0..siz()-1) 
     * @return oggetto in posizione se posizione valida,
     *         null altrimenti
     */
    public Object elem(int pos)
    {
        if (pos<0 || pos>=numData)
            // posizione illegale
            return null;
        return data[(head+pos)%numEl];
    } //[m] elem
    
    /**[m]
     * ricerca per chiave
     * @param key  chiave ricercata (oggetto inserito)
     * @return posizione relativa elemento (0..size()-1) 
     *         se trovato,
     *         NOPOS altrimenti
     */
    public int find(Object key)
    {
        int pos=head;
        for(int i=0; i<numData; i++, pos=++pos%numEl)
            if(data[pos] == key)
                // trovato
                return i;
        return NOPOS;
    } //[m] find
    
    /**[m]
     * ricerca ed estrazione per chiave
     * @param key  chiave ricercata (oggetto inserito)
     * @return oggetto estratto se chiave trovata,
     *         null altrimenti
     */
    public Object extract(Object key)
    {
        int pos;
        if ((pos = find(key)) == NOPOS)
            // non trovato
            return null;
        // estrazione
        // si sa in questo caso che l'oggetto coincide
        // con la chiave
        for(int dest=(head+pos)%numEl,
          src=(head+pos+1)%numEl; pos<numData-1; pos++,
          dest=src, src=++src%numEl)
            data[dest]=data[src];
        // da retrarre tail
        tail=(--tail+numEl)%numEl;
        numData--;
        return key;
    } //[m] extract
            

} //{c} FifoSBuf

