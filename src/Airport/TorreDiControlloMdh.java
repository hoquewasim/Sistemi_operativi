package Airport;

import os.Monitor;

public class TorreDiControlloMdh extends TorreDiControllo {

    // non potendo estendere monitor dichiaro una
    private Monitor tower = new Monitor();
    private Monitor.Condition accA = tower.new Condition();
    private Monitor.Condition accB = tower.new Condition();
    private Monitor.Condition attAt = tower.new Condition();



    @Override
    public void richAutorizAtterraggio(int io) {
        tower.mEnter();
        riAutAt++;
        System.out.println("L'aereo A" + io + " richiede atterraggio");
        stampaSituazioneAeroporto();
        if(occupateA+occupateB!=0)
            attAt.cWait();
        riAutAt--;
        occupateA = 1;
        att = true;
        System.out.println("$$$ L'aereo A " + io + " In fase di atterraggio");
        stampaSituazioneAeroporto();
        tower.mExit();



    }

    @Override
    public void freniAttivati(int io) {

        tower.mEnter();
        occupateA = 0; occupateB = 1;
        System.out.println("$$$$$$ L'aereo A"+io+" TOCCA TERRA, FRENA ");
        stampaSituazioneAeroporto();
        tower.mExit();



    }

    @Override
    public void inParcheggio(int io) {
        tower.mEnter();
        occupateB = 0;
        att= false;
        contaAtt++;
        System.out.println("$$$$$$$$ L'aereo A"+io+" LIBERA LA PISTA E PARCHEGGIA");
        stampaSituazioneAeroporto();
        if(riAutAt>=1)
            attAt.cSignal();
        else if(riAccPi>=2){
            accA.cSignal(); accA.cSignal();}
        else if(riAccPi>=1)
            accA.cSignal();
        tower.mExit();




    }

    public void richAccessoPista(int io) {
        tower.mEnter();
        riAccPi++;               // segnala la volontà di decollare
        System.out.println("** L'aereo D"+io+" ^^^^^^^^ RICHIESTA PISTA PER DECOLLO");
        stampaSituazioneAeroporto();
        if (riAutAt != 0 || att || occupateA >= 2)
            accA.cWait();        // atterraggio (in vista) o pista occupata attende
        riAccPi--; occupateA++;  // una zona A libera, la occupa
        System.out.println("**** L'aereo D"+io+ " SI PREPARA AL DECOLLO");
        stampaSituazioneAeroporto();
        tower.mExit();
    }

    public void richAutorizDecollo(int io) {
        tower.mEnter();
        riAutDe++;             // segnala la volontà di decollare
        System.out.println("****** L'aereo D"+io+" RICHIEDE AUTORIZZAZIONE DECOLLO");
        stampaSituazioneAeroporto();
        // deve entrare in zona B
        if(occupateB >= 2)
            accB.cWait();        // zona B occupata, deve attendere
        occupateA--; occupateB++;    // zona B libera - libera zona A e occupa zona B
        riAutDe--;
        System.out.println("******** L'aereo D"+io+" IN FASE DI DECOLLO ");
        stampaSituazioneAeroporto();
        if (riAutAt==0 && riAccPi>=1)
            // nessun atterraggio in vista libera 1 aereo in decollo
            accA.cSignal();
        tower.mExit();
    }

    public void inVolo(int io) {
        tower.mEnter();
        occupateB--;                // libera zona B
        contaDec++;
        System.out.println("********** L'aereo D"+io+ " HA PRESO IL VOLO!!!!! ");
        stampaSituazioneAeroporto();
        if (riAutDe>=1)
            // c'e` un decollo da autorizzare libera zona A e occupa zona B
            accB.cSignal();
        else if (riAutAt>=1 && occupateA+occupateB==0)
            // atterraggio in vista, pista libera autorizza atterraggio
            attAt.cSignal();
        tower.mExit();
    }
}
