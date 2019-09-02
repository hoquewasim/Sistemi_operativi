package Airport;
import os.*;


public class AereoCheAtterra extends Thread {
    private int io ;
    private TorreDiControllo tc;

    /**[c]
     * @param io  indice del volo
     * @param tc  rif. torre di controllo
     */
    public AereoCheAtterra(int io , TorreDiControllo tc) {
        this.io=io;
        this.tc=tc;
    }

    public void run() {
        Util.rsleep(5000, 8000);		// Aereo in volo
        tc.richAutorizAtterraggio(io);    // Aereo fa richiesta di aterraggio
        Util.rsleep(1000, 4000);		// Quando ha ottenuto la risposta si allinea con pista
        tc.freniAttivati(io);
        Util.rsleep(1000, 4000);		// rulla sino all'uscita
        tc.inParcheggio(io);			// libera la pista
    }

} // class AereoCheAtterra
