package os;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.StreamTokenizer;
import java.io.IOException;
import java.io.EOFException;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Enumeration;
import java.util.Vector;

/**{c}
 * rappresentazione di un grafo di precedenza -
 * main di collaudo
 *
 * @author M.Moro DEI UNIPD
 * @version 1.00 2003-10-11
 * @version 1.01 2005-10-05 package os e output completo su file in italiano
 * @version 1.02 2009-10-14 utilizzati i generics per Vector
 * @version 1.03 2019-05-25 eliminate deprecazioni
 */

public class PGraph
{
    private int num;
      // numero nodi del grafo
    private Object pr[];
      // oggetti che descrivono i nodi
      // del grafo, tipicamente (sotto)processi
    private boolean graph[][];
      // matrice di adiacenza del grafo
      // graph[i][j]== true se c'e'
      // un arco orientato da pr[i] a pr[j]

    static class VertexDesc
    {
        int weight;
          // peso del nodo
        String name;
          // nome del nodo

        /**[c]
         * inizializza il descrittore
         * @param w  peso
         * @param n  nome
         */
        public VertexDesc(int w, String n)
        {
            weight = w;
            name = n;
        }

        /**[m]
         * conversione a stringa
         * @return le due info associate
         */
        public String toString()
        { return "peso="+weight+" nome="+name; }

    } //{c} VertexDesc

    /**[c]
     * inizializza le matrici
     * @param numV  numero nodi
     * @param p  oggetti che descrivono i nodi del grafo
     */
    public PGraph(int numV, Object p[])
    {
        num = numV;
        pr = p;
        graph = new boolean[numV][numV];
    }

    /**[m]
     * ricerca l'indice di un nodo
     * @param name  nome del nodo
     * @return l'indice del nodo se trovato,
     *         -1 altrimenti
     */
    public int findIdx(String name)
    {
        // ricerca lineare
        // non val la pena far di meglio
        int i;
        for (i=0;
          i<num && (! ((VertexDesc)pr[i]).name.equals(name)); i++);
        return (i<num) ? i : -1;
    } //[m] findIdx

    /**[m]
     * crea e inizializza da file un PGraph
     * @param in  file d'ingresso (anche stdin)
     * @return PGraph creato
     * @throws IOException  errore open
     */
    public static PGraph loadGraph(InputStream in)
      throws IOException
    {
        Reader re = new BufferedReader(new
          InputStreamReader(in));
        StreamTokenizer sIn = new StreamTokenizer(re);
        sIn.parseNumbers();
        if (sIn.nextToken() == StreamTokenizer.TT_EOF ||
          sIn.ttype != StreamTokenizer.TT_NUMBER)
            // errore
            return null;
        // numero nodi
        int numV = (int)sIn.nval;
        VertexDesc vertex[] = new VertexDesc[numV];
        if (in == System.in)
            System.out.println(
  "Battere peso e nome di ciascun nodo (tot="+numV+"):");
        for (int i=0; i<numV; i++)
        {
            // legge nodi
            if (sIn.nextToken() == StreamTokenizer.TT_EOF)
                return null;
            int weight = (int)sIn.nval;
            if (sIn.nextToken() == StreamTokenizer.TT_EOF)
                return null;
            vertex[i] = new VertexDesc(weight, sIn.sval);
            System.out.println("-- nodo "+sIn.sval+" con peso="+
              weight);
        }

        PGraph retG = new PGraph(numV, vertex);

        // legge matrice di adiacenza
        if (in == System.in)
            System.out.println(
  "Battere archi (coppie nodi ordinate) (fine=Ctrl-Z)");
        try
        {
            while(sIn.nextToken() != StreamTokenizer.TT_EOF)
            {
                if (sIn.ttype != StreamTokenizer.TT_EOF)
                {
                    int row = retG.findIdx(sIn.sval);
                    if (row == -1)
                    {
                        // errore nome
                        System.out.println(sIn.sval +
                          " non nodo: non inserito");
                        continue;
                    }
                    if (sIn.nextToken() == StreamTokenizer.TT_EOF)
                        // fine file, esce
                        break;
                    int col = retG.findIdx(sIn.sval);
                    if (col == -1)
                    {
                        // errore nome
                        System.out.println(sIn.sval +
                          " non nodo: non inserito");
                        continue;
                    }
                    retG.graph[row][col] = true; // imposta arco
                    System.out.println("arco (indici) "+row+", "+col);
                } //if
                else
                    // fine file, esce
                    break;
            } //while
        } //try
        catch (EOFException e)
        {
            // termina il ciclo
        } //catch(EOF)
        return retG;
    } // loadGraph

    /**[m]
     * salva un PGraph su file
     * @param out  file di output
     */
    public void saveGraph(PrintWriter out)
    {
        out.println("// file generato da PGraph");
        out.println("//   numero dei nodi");
        out.println(num);
        out.println("//   peso e nome dei nodi");
        for (int i=0; i<num; i++)
            out.println(((VertexDesc)pr[i]).weight+" "+
              ((VertexDesc)pr[i]).name);
        out.println("//   archi orientati");
        for (int i=0; i<num; i++)
        {
            for (int j=0; j<num; j++)
            {
                if (graph[i][j])
                {
                    // c'e` l'arco da i a j
                    out.println(((VertexDesc)pr[i]).name+" "+
                      ((VertexDesc)pr[j]).name);
                }
            }
        }
    } // saveGraph

    /**[m]
     * numero nodi
     * @return nodi
     */
    public int size()
    { return num; }

    /**[m]
     * grado d'ingresso di un nodo
     * @param ver  indice del nodo
     * @return il suo grado di ingresso
     *         (numero archi entranti)
     */
    public int inDegree(int ver)
    {
        // per calcolare il grado d'input del nodo ver
        // si contano gli elementi true della colonna ver
        int cnt = 0;
        for (int row=0; row<num; row++)
            if (graph[row][ver])
                cnt++;
         return cnt;
    } //[m] inDegree

    /**[m]
     * grado d'uscita di un nodo
     * @param ver  indice del nodo
     * @return il suo grado di uscita
     *         (numero archi uscenti)
     */
    public int outDegree(int ver)
    {
        // per calcolare il grado d'output del nodo ver
        // si contano gli elementi true della riga ver
        int cnt = 0;
        for (int col=0; col<num; col++)
            if (graph[ver][col])
                cnt++;
         return cnt;
    } //[m] outDegree

    /**[m]
     * un iteratore per i nodi predecessori
     * @param ver  indice del nodo
     * @return l'iteratore dei predecessori
     */
    public Enumeration parents(int ver)
    {
//v1.02 old  Vector par = new Vector();	 
        Vector<Integer> par = new Vector<Integer>();
          // vettore espandibile di supporto
        for (int row=0; row<num; row++)
            if(graph[row][ver])
                // row e` predecessore
//v1.02 old         par.add(new Integer(row));
              par.add(row);
        return par.elements();
    } //[m] parents

    /**[m]
     * calcola il tempo complessivo di esecuzione
     * considerando il peso come un tempo
     * @param ver  nodo di partenza
     * @return tempo totale fino all'inizio
     */
    public int execTime(int ver)
    {
        int retT = 0;
        Enumeration pars = parents(ver);
        while(pars.hasMoreElements())
        {
            int cur =
              ((Integer)pars.nextElement()).intValue();
              // predecessore corrente
            int tmp = execTime(cur);
            if (tmp > retT)
                // percorso piu` lungo
                retT = tmp;
        } // while
        return retT+((VertexDesc)pr[ver]).weight;
    } //[m] execTime

    /**[m]
     * suddivide il grafo per livelli
     * rispettando le precedenze
     * @return lista ordinata per livello
     *         ogni elemento e` una lista degli
     *         indici dei nodi di quel livello +
     *         il grado di parallelismo dopo
     *         quel livello
     *         
     */
//v1.02 old                 public Vector levels()
	 public  Vector<Vector<Integer>> levels()
    {
        // algoritmo simile a quello di
        // ordinamento topologico del grafo		  		  		  
		  
          // 2 code di supporto iniz. vuote                   
//v1.02 old	   Vector queues[] = new Vector[2];																		       
//           queues[0] = new Vector();                      
//           queues[1] = new Vector();                      	    
       	Vector<Integer> queue1 = new  Vector<Integer>();
        Vector<Integer> queue2 = new  Vector<Integer>();	 

        int inCnt[] = new int[num];
        int inCntTmp[] = new int[num];
        int outCnt[] = new int[num];
          // inDegree, outDegree dei nodi
//v1.02 old   Vector retLevels = new Vector();
        Vector<Vector<Integer>> retLevels = new Vector<Vector<Integer>>();

          // vettore di vettori che rappresentano 
          // i successivi livelli

        for (int i=0; i<num; i++)
        {
		  
            // calcola inDegree di ogni nodo e
            // mette in coda il/i nodi iniziali
            // del grafo (inDegree=0)
            if ((inCntTmp[i] = inCnt[i] = inDegree(i)) == 0)			
//v1.02 old           queues[0].add(new Integer(i));			
                queue1.add(i);
            outCnt[i] = outDegree(i);
				
        } // for
        int curQue = 0;
          // una delle due code alternativamente
        int curLev = 0;
          // livello corrente
        while(true)
        {
//v1.02 old       if (queues[curQue].size() == 0)
            if ((curQue==0 ? queue1.size() : queue2.size()) == 0)
                // concluso
                break;
//v1.02 old       retLevels.add(new Vector());
            retLevels.add(new Vector<Integer>());
              // aggiunge livello
//v1.02 old		   for(int j=0; j<queues[curQue].size(); j++)
            for(int j=0; j<(curQue == 0 ? queue1.size() : queue2.size()); 
              j++)
            {
                // nodi da esaminare
//v1.02 old           Integer curVertex = (Integer)queues[curQue].elementAt(j);
                Integer curVertex = (curQue == 0 ? queue1.elementAt(j) : queue2.elementAt(j));
                  // legge nodo da trattare
//v1.02 old				 ((Vector)retLevels.elementAt(curLev)).add(curVertex);
                (retLevels.elementAt(curLev)).add(curVertex);
                  // aggiunge il nodo al livello corrente
                int curIdx = curVertex.intValue();
                for(int i=0; i<num; i++)
                {
                    // decrementa inDegree dei nodi successori
                    if (graph[curIdx][i])
                        if (--inCntTmp[i] == 0)
                            // nodo del prossimo livello
//v1.02 old                       queues[(curQue+1)%2]).add(new Integer(i));
                            if((curQue+1)%2 == 0) 
                                queue1.add(i); 
                            else
                                queue2.add(i);									 
                } // for
            } // for queues
//v1.02 old       int parDegree = queues[curQue].size();
            int parDegree = (curQue == 0 ? queue1.size() : queue2.size());				
              // grado di parallelismo del livello corrente
//v1.02 old       while(queues[curQue].size() != 0)
            while((curQue == 0 ? queue1.size() : queue2.size()) != 0)				
            {
//v1.02 old           Integer curVertex = (Integer)queues[curQue].remove(0);
                Integer curVertex = (curQue == 0 ? queue1.remove(0) : queue2.remove(0));					 
                  // estrae nodo da trattare
                int curIdx = curVertex.intValue();
                boolean splitted = false;
                for(int i=0; i<num; i++)
                {
                    // valuta se ci sono successori che non sono nel
                    // livello immediatamente successivo
//v1.02 old               if (graph[curIdx][i] && ! queues[(curQue+1)%2].contains(new Integer(i)))
                    if (graph[curIdx][i] && ! ((curQue+1)%2 == 0 ? queue1.contains(i) : queue2.contains(i)))						  
                    {
                        // il successore non e' al livello successivo:
                        // il nodo viene duplicato nel livello successivo
                        // e ripristinato il conteggio d'ingresso del nodo successore
                        inCntTmp[i]++;
                        if (! splitted)
                        {
                            // reinserito nella coda del livello successivo
//v1.02 old                       queues[(curQue+1)%2].add(new Integer(curIdx));
                            if((curQue+1)%2 == 0) 
                                queue1.add(curIdx); 
                            else
                                queue2.add(curIdx);	
                            splitted = true;
                        }
                    } // if
                } // for i
            } // for j
//v1.02 old 		((Vector)retLevels.elementAt(curLev)).add(new Integer(parDegree));
// v1.03 old            (retLevels.elementAt(curLev)).add(new Integer(parDegree));
            (retLevels.elementAt(curLev)).add(Integer.valueOf(parDegree));
              // aggiunge alla lista, come ultimo elemento, il grado di
              // parallelismo  del livello
            curQue = ++curQue % 2;
            curLev++;
        } // while true
        return retLevels;
    } //[m] levels

    /**[m][s]
     * metodo di collaudo
     * @param args  [0] file di input
     * @exception IOException  se file non trovato
     */
    public static void main(String[] args)
      throws IOException
    {
        InputStream in;

        if (args.length == 0)
        {
            // default, stdin
            in = System.in;
            System.out.println("PGraph: Battere num. vertici:");
        }
        else
            in = new FileInputStream(args[0]);
        PGraph myGraph = PGraph.loadGraph(in);

        for (int i=0; i<myGraph.size(); i++)
            System.out.println("nodo i="+i+" "+myGraph.pr[i]);

        for (int i=0; i<myGraph.size(); i++)
        {
            for (int j=0; j<myGraph.size(); j++)
            {
                System.out.print(" "+myGraph.graph[i][j]);
            }
            System.out.println(" ");
        }

        // si assume che il grafo sia chiuso
        // cerca il processo finale
        int last;
        for(last=0; last<myGraph.size(); last++)
            if (myGraph.outDegree(last)==0)
                break;
        int time = -1;
        if (last < myGraph.size())
        {
            time = myGraph.execTime(last);
            System.out.println("tempo di esecuzione="+time);
        }
        else
            System.out.println(
              "!! il sistema non ha un processo finale!");

        File out;
        if (args.length < 2)
            out = new File("$$PGraph.txt");
        else
            out = new File(args[1]);
            
        FileWriter fwout;
        PrintWriter pwout = null;;

        if (out.exists())
        {
            String ret = Sys.in.readLine(
              "Il file "+ ((args.length < 2) ? "$$PGraph.txt" : args[1])+
              " esiste gia`: si vuol cancellare (y/N)? ");
            if (ret.length() != 0 &&
              Character.toUpperCase(ret.charAt(0)) == 'Y')
                out.delete();
            else
            {
                System.err.println("** Il file non deve esistere!");
                System.exit(0);
            }
        }
        try
        {
            out.createNewFile();
        }
        catch (IOException e)
        {
            System.out.println("Errore creazione file di output: "+e);
            System.exit(0);
        }

        try
        {
            fwout = new FileWriter(out);
            pwout = new PrintWriter(fwout);
        }
        catch (IOException e)
        {
            System.out.println("Errore file output: "+e);
            System.exit(0);
        }
        myGraph.saveGraph(pwout);
        Vector levels = myGraph.levels();
        pwout.println("Livelli del grafo");
        int maxParD = -1;
        for (int i=0; i<levels.size(); i++)
        {
            int j;
            pwout.print("Livello "+i+" nodo/i:");
            for (j=0; j<((Vector)levels.elementAt(i)).size()-1; j++)
            {
                int vertex = ((Integer)((Vector)levels.elementAt(i)).elementAt(j)).intValue();
                pwout.print(" "+((VertexDesc)myGraph.pr[vertex]).name);
                ((VertexDesc)myGraph.pr[vertex]).name += "'";
            } // for j
            int parDeg = ((Integer)((Vector)levels.elementAt(i)).elementAt(j)).intValue();
            pwout.println(" (grado di paral. del livello="+parDeg+")");
            if (parDeg > maxParD)
                maxParD = parDeg;
        } // for i
        pwout.println("Massimo grado di parallelismo="+maxParD);
        if (time != -1)
            pwout.println("Tempo di esecuzione="+time);
//        pwout.flush();
        pwout.close();
    } //[c][s] main


} //{c} PGraph
