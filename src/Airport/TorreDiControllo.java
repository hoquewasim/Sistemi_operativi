package Airport;
import os.*;

/** {a}{c}
  * TorreDiControllo.java
  * Problema Aeroporto -
  * Torre di controllo generalizzata
  * @version 1.00 2001-03-28
  * @version 2.00 2003-11-21
  * @author S.Cecchin Ist. Negrelli Feltre
  * @author M.Moro DEI UNIPD
  */

public abstract class TorreDiControllo {
  protected boolean att = false; 	// atterraggio in corso
  protected int occupateA = 0;  	// piste occupate in zona A
  protected int occupateB = 0;  	// piste occupate in zona B
  protected int riAutAt = 0;     	// richiesta di autorizzazione atterraggio
  protected int riAccPi = 0;     	// richiesta di accesso alla pista per decollo
  protected int riAutDe = 0;    	// richiesta di autorizzazione decollo
  protected int contaAtt = 0;    	// conteggio atterrati
  protected int contaDec = 0;   	// conteggio decollati

  /**[m]
    * stampa stato aeroporto
    */
  protected void stampaSituazioneAeroporto() {
    System.out.println("Atterrati = "+contaAtt+" Decollati  = "+contaDec);
    System.out.println("Situazione ZONA A:  aerei in pista  = "+occupateA);
    System.out.println(" di cui in attesa di autorizzazione = "+riAutDe);
    for(int p=0;p<2;p++)
      if(p<(occupateA))
        System.out.print("X");
      else
        System.out.print("O");
    System.out.println("");
    System.out.println("Situazione ZONA B:  aerei in pista  = "+occupateB);
    for(int q=0;q<2;q++)
      if(q<(occupateB))
        System.out.print("X");
      else
        System.out.print("O");
    System.out.println("");
    System.out.println("Richieste pista ATTERRAGGIO pendenti = "+ riAutAt);
    System.out.print("-");
    for(int e=0;e<riAutAt;e++)
      System.out.print("R");
    System.out.println("");
    System.out.println("Richieste pista DECOLLO pendenti = "+ riAccPi);
    System.out.print("-");
    for(int e=0;e<riAccPi;e++)
      System.out.print("D");
    System.out.println("");
    System.out.println("ATTERRAGGIO in corso = "+  (att? "si" : "no"));
    System.out.print("-");
    if (att)
      System.out.print("A");
    System.out.println("");
  }
  
  public abstract void richAutorizAtterraggio(int io); 
  public abstract void freniAttivati(int io); 
  public abstract void inParcheggio(int io); 
  public abstract void richAccessoPista(int io);
  public abstract void richAutorizDecollo(int io);
  public abstract void inVolo(int io);

} //{a}{c} TorredDiControllo
