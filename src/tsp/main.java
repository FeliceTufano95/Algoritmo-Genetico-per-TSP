package tsp;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);

        //PROVA CLONE
        System.out.println("inserisci il nome del file di input");
        String name = scan.next();
        System.out.println("inserisci il massimo numero di iterazioni");
        int end = scan.nextInt();
        
        String nameOpt = "Opt_"+name;//scan.next();
        
        
        //empiricamente tra 2 e 3
        int parametroK = 3;
        int[][] matriceDistanze = LetturaFile.readFileToArray("resources/"+name+".txt");
		int[] percorsoOttimo = LetturaFile.readOpt("resources/"+nameOpt+".txt");
        if(percorsoOttimo.length==0) {
        	System.out.println("\n***File di ottimo non presente*** \n\n");
        }
        
        //se troppo bassa l'algoritmo ha pochi modi per genereare individui con buona fitness, se troppo ci saranno troppi geni con cattivo valore per produrre buoni risultati
        //1.5, 2 volte più grande
        int dimensionePopolazione =  matriceDistanze.length+(matriceDistanze.length/2);//popolazione = 1.5 dati
		int opt = matriceDistanze.length+1;
		if(percorsoOttimo.length!=0) {
			opt = distanzaOttima(matriceDistanze, percorsoOttimo, matriceDistanze.length);
        }
        int minDistance;
        int[] percorsoMigliore, percorsoIniziale;
        
        Popolazione popolazione = new Popolazione(matriceDistanze.length, dimensionePopolazione, matriceDistanze);
        popolazione.creaPopolazione();

        //calcolo soluzione iniziale
        minDistance = getMin(popolazione.calcolaLunghezze(), popolazione);
        int soluzioneIniziale = minDistance;
        percorsoMigliore = popolazione.getIndividui().get(popolazione.indiceDistanzaMin);
        percorsoIniziale = percorsoMigliore;
        
        int maxIter = 0; //numero massimo di iterazioni oltre il quale l'algoritmo si ferma
        while (maxIter < end && minDistance>opt) { 
            popolazione.tournament_select(parametroK);
            popolazione.pmxCrossing(60);
            popolazione.inversione(5);

            int actualMinDist = getMin(popolazione.calcolaLunghezze(), popolazione);
            if (actualMinDist < minDistance) {
                minDistance = actualMinDist;
                percorsoMigliore = popolazione.getIndividui().get(popolazione.indiceDistanzaMin);
                System.out.println("miglior risultato corrente: " + minDistance+" Opt: "+opt+" iterazioni: "+maxIter);
                maxIter = 0;
            }else{
            	maxIter++;
            }
        }

        System.out.println("---RISULTATI: ---\n");
        System.out.println("\n\nSoluzione iniziale: " + soluzioneIniziale);
        System.out.println("Soluzione migliore: "+minDistance);
        if(percorsoOttimo.length!=0) { 
        	System.out.println("Soluzione ottima: "+opt);
        	System.out.println("GAP: "+((float)Math.abs(minDistance-opt)/Math.abs(opt))*100+"%");
        }
        PrintWriter writer = new PrintWriter("resources/bestResult.txt", StandardCharsets.UTF_8);
        writer.print("Miglior risultato per "+name+": \n");
        writer.print("Percorso: \n");
        for (int i = 0; i < percorsoMigliore.length; i++) {
            writer.print(percorsoMigliore[i]);
            if (i < percorsoMigliore.length - 1) {
                writer.print("-");
            }
        }
        
        
        writer.print("\n\nvalore: " + minDistance);
        writer.close();

    }

    //setta l'indice dell'elemento con valore di distanza minima e restituisce il corrispondente valore minimo
    public static int getMin(int[] array, Popolazione population) {
        int minindex = 0;
        int minValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < minValue) {
                minValue = array[i];
                minindex = i;
            }
        }
        population.indiceDistanzaMin = minindex;
        return minValue;
    }
    
    public static int distanzaOttima(int[][] distances, int vett[], int numCitta) {
    	int distanzaOttima = 0;
        for (int i = 0; i < numCitta-1; i++) {
            distanzaOttima += distances[vett[i]][vett[i + 1]]; //distanza tra città i e i+1
        }
        distanzaOttima += distances[vett[numCitta-1]][vett[0]];
        return distanzaOttima;
    }
}
