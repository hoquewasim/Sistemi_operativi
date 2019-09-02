package os;

/**{c}
 * classe di supporto -
 * memorizza un codice di errore
 * di I/O (I/O Request Block)
 * 
 * @author M.Moro DEI UNIPD
 * @version 1.00 2003-10-18
 * @version 2.00 2005-10-07 package os
 * @version 2.01 2005-11-18 public
 */
public class IOErr
{
    public int errcode = DeviceDesc.NOERR;
    public String errDesc;

} //{c} IOErr
