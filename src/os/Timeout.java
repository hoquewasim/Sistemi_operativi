package os;

/**{i}
 * Valori speciali relativi al timeout
 * @author M.Moro DEI UNIPD
 * @version 1.00 2003-10-01
 * @version 2.00 2005-10-07 package os
 */
 
public interface Timeout
{
    // valori speciali
    /** sincronizzazione immediata
     */
    public static final long IMMEDIATE = 1L;

    /** attesa senza timeout
     */
    public static final long NOTIMEOUT = 0L;

    /** timeout spirato
    */
    public static final long EXPIRED = 0L;

    /** timeout spirato: oggetto sentinella
    */
    public static Object EXPIREDOBJ = "EXPIRED";
    
    /** timeout non spirato
     */
    public static final long INTIME = 1L;

} //{i} Timeout

