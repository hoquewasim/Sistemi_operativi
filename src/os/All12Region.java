package os;

/**{c}
 * allocatore del banchiere 1-2
 * mediante regioni condizionali
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2003-10-15
 * @version 2.00 2005-10-07 package os
 */

public class All12Region extends Region
{
    private static final int ALL12LEV = 0;
      // livello di nesting della regione
    private int free;
      // risorse da amministrare
    private int np11;  // prenotazione 1 sola ris.
    private int np22; // prenotazione 2 risorse insieme
    private int np21a; // prenotazione 1^ di 2 risorse
    private int np21b; // prenotazione 2^ di 2 risorse
      
    /**[c]
     * costruttore base: definisce il numero delle risorse
     * @param num  numero iniziale delle risorse
     */
    public All12Region(int num)
    { 
        super(ALL12LEV);
        free = num;
    } //[c]
    
    /**[m]
     * acquisisce 1 risorsa sola
     */
    public void alloc11()
    { 
        enterWhen();
        np11++;  // prenota
        leave();
        // preleva
        enterWhen(new RegionCondition()
        // classe anonima di valutazione
        {
            public boolean evaluate()
            { 
                return free>=1 && np22==0 && 
                  np21a==0 && np21b == 0; 
                    // si da` priorita` alla
                    // richiesta di 2 risorse
            }
        } // {c} <anonim>
        );
        free--;  // alloca
        np11--;  // elimina prenotazione
        leave();
    }

    /**[m]
     * acquisisce 2 risorse insieme
     */
    public void alloc22()
    { 
        enterWhen();
        np22++;  // prenota
        leave();
        // preleva
        enterWhen(new RegionCondition()
        // classe anonima di valutazione
        {
            public boolean evaluate()
            { 
                return free>=2 && 
                  np21a==0 && np21b == 0; 
                    // si da` priorita` alle
                    // richieste separate per 2 risorse
            }
        } // {c} <anonim>
        );
        free-=2;  // alloca
        np22--;  // elimina prenotazione
        leave();
    }

    /**[m]
     * acquisisce 1^ risorsa di 2
     */
    public void alloc21a()
    { 
        enterWhen();
        np21a++;  // prenota
        leave();
        // preleva
        enterWhen(new RegionCondition()
        // classe anonima di valutazione
        {
            public boolean evaluate()
            { 
                return free>=2 && 
                  np21b == 0; 
                    // si da` priorita` alle
                    // seconde richieste di 1
                    // risorsa di 2
            }
        } // {c} <anonim>
        );
        free--;  // alloca
        np21a--;  // elimina prenotazione
        leave();
    }

    /**[m]
     * acquisisce 2^ risorsa di 2
     */
    public void alloc21b()
    { 
        enterWhen();
        np21b++;  // prenota
        leave();
        // preleva
        enterWhen(new RegionCondition()
        // classe anonima di valutazione
        {
            public boolean evaluate()
            { return free>=1; }
        } // {c} <anonim>
        );
        free--;  // alloca
        np21b--;  // elimina prenotazione
        leave();
    }

    /**[m]
     * restituisce 1 o due risorse
     * @param numRes  1 o 2
     */
    public void free(int numRes)
    { 
        enterWhen();
        free += numRes;
        leave();
    }

    /**[m]
     * risorse libere
     * @return numero risorse libere &ge;0
     */
    public int free()
    { return free; }

} //{c} All12Region