package tsp;

import java.util.*;

public class Popolazione {
    private final int[][] matriceDistanze; //matrice delle distanze
    private final int numCitta; //numero di righe
    private final int numIndividui; //numero elementi della popolazione
    
    //individuals sono dei vettori contenenti l'ordine le città in ordine diverso 
    private ArrayList<int[]> individui;
    
    private final int[] vettoreDistanze; //vett[0] contiene il valore dell'individuo 0, ecc 
    private ArrayList<Integer> popIniziale; //sarà composto dal percorso ordinato
    private final static Random rand = new Random();

    int indiceDistanzaMin;


    public Popolazione(int cityCount, int individualsCount, int[][] distances) {
        this.matriceDistanze = distances;
        this.numCitta = cityCount;
        this.numIndividui = individualsCount;
        this.individui = new ArrayList<>();
        this.vettoreDistanze = new int[individualsCount];
        this.indiceDistanzaMin = 0;

        //a partire da list che è una lista ordinata delle città creo degli individui formati da vettori contenenti le città in ordine sparso
        popIniziale = new ArrayList<>();
        for (int i = 0; i < cityCount; i++) {
            popIniziale.add(i);
        }
    }

    public int[] creaIndividuo() {

        Collections.shuffle(popIniziale);
        int[] tab = new int[popIniziale.size()];
        for (int i = 0; i < popIniziale.size(); i++) {
            tab[i] = popIniziale.get(i);
        }
        return tab;
    }

    public void aggiungiIndividuo(int[] individual) {
        this.individui.add(individual);
    }

    public void creaPopolazione() {
        for (int i = 0; i < numIndividui; i++) {
            aggiungiIndividuo(creaIndividuo());
        }
    }

    public ArrayList<int[]> getIndividui() {
        return individui;
    }

    public int[] calcolaLunghezze() {
        for (int i= 0; i < individui.size(); i++) {
            vettoreDistanze[i] = 0;
            int[] individual = individui.get(i);
            for (int j = 0; j < numCitta-1; j++) {
                vettoreDistanze[i] += matriceDistanze[individual[j]][individual[j + 1]]; //distanza tra città i e i+1
            }
            //mi resta da aggiungere l'elemento riga ultima prima colonna
            vettoreDistanze[i] += matriceDistanze[individual[numCitta - 1]][individual[0]];
        }
        return vettoreDistanze;
    }

    public void tournament_select(int paramK) {
        ArrayList<int[]> nuoviIndividui = new ArrayList<>();
        int minLength = getMax(vettoreDistanze);
        int minRand = 0;

        for(int i=0; i < numIndividui; i++) {
            for (int j = 0; j < paramK; j++) {
                int random = rand.nextInt(numIndividui);
                if (vettoreDistanze[random] < minLength) {
                    minLength = vettoreDistanze[random];
                    minRand = random;
                }
            }
            nuoviIndividui.add(individui.get(minRand));
        }

        this.individui = nuoviIndividui;
    }
    

    private static int getMax(int[] array) {
        int maxValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > maxValue) {
                maxValue = array[i];
            }
        }
        return maxValue;
    }

    
    public void pmxCrossing(int parameter) {
        for (int i = 0; i < numIndividui; i++) {
            int random = rand.nextInt(100);
            if (random <= parameter) {
                int index2;
                while ((index2 = rand.nextInt(numIndividui)) == i) ; 
                k_pmx(i, index2);
            }
        }
    }

    private void k_pmx(int indiceGen1, int indiceGen2) {
        int p1 = rand.nextInt(numCitta - 3) + 1; //[1, numCittà-2]
        int p2 = rand.nextInt(numCitta - 1 - p1) + p1; //[p1, numCittà-1]

        int[] figlio = new int[numCitta];
        
        //inizializzo il figlio con i placeholder -1
        for(int i=0; i<figlio.length; i++) {
        	figlio[i] = -1;
        }
        //da p1 a p2 assegngo gli elementi di G1 a F1
        for (int i = p1; i <= p2; i++) {
            figlio[i] = individui.get(indiceGen1)[i]; 
        }
        
        //Applico l'algoritmo di assegnazione dei restanti elementi
        for(int i=p1; i<=p2; i++) {
        	if((find(figlio, individui.get(indiceGen2)[i], p1, p2)) == -1) {
        		int pos = returnPos(individui.get(indiceGen2), figlio[i]);
        		if(pos == -1) {
        			System.out.println("errore nella fase di crossing, elemento F[i] non presente in G2[i]");
        			System.exit(0);
        		}
        		//finchè la posizione in F1 è gia occupata cercane una libera
        		while(figlio[pos] != -1) {
        			pos = returnPos(individui.get(indiceGen2), figlio[pos]);
        		}
        		figlio[pos] = individui.get(indiceGen2)[i];
        	}	
        }
        //Gli altri elementi di F1 vengono copiati direttamente da G2
        for(int i=0; i< figlio.length; i++) {
        	if(figlio[i] == -1) {
        		figlio[i] = individui.get(indiceGen2)[i];
        	}
        }
         
        individui.set(indiceGen1, figlio);
    }


    int returnPos(int[] vett, int elem) {
    	int pos = -1;
    	for(int i=0; i<vett.length && pos == -1; i++) {
    		if(vett[i] == elem)
    			pos = i;
    	}
    	return pos;
    }
    

    //se tra start e end tab contiene el return true
    public static int find(int[] tab, int el, int start, int end) {
        for (int i = start; i <= end; i++) {
            if (tab[i] == el) {
                return i;
            }
        }
        return -1;
    }


    public void inversione(int parameter) {
        for (int i = 0; i < numIndividui; i++) {
            int random = rand.nextInt(100);
            //la probabilità di fare una inversione deve essere bassa
            if (random <= parameter) {
                int[] child = applicaInversione(individui.get(i));
                individui.set(i, child);
            }

        }
    }

    private int[] applicaInversione(int[] tab) {
        int first_cut = rand.nextInt(numCitta - 2);
        int second_cut = rand.nextInt(numCitta - first_cut) + first_cut; //il secondo taglio lo devo fare dopo first cut
        int[] tmp = new int[second_cut - first_cut + 1]; //conterrà gli elementi da first a second cut in ordine inverso

        for (int i = 0, j = second_cut; i < tmp.length; i++, j--) {
            tmp[i] = tab[j];
        }

        for (int i = first_cut, j = 0; i <= second_cut; i++, j++) {
            tab[i] = tmp[j];
        }

        return tab;
    }

    public int[][] getDistances() {
        return matriceDistanze;
    }

    public int getCityCount() {
        return numCitta;
    }

    public int getIndividualsCount() {
        return numIndividui;
    }

    public int[] getLengths() {
        return vettoreDistanze;
    }
}



