package tsp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class LetturaFile {

    public static int[][] readFileToArray(String fileName) throws IOException{
        int[][] distances = new int[][]{};
        int lineCounter = 0; //indice di riga
        int wordCounter = 0; //indice di colonna

        File f = new File(fileName);
        if (f.exists() && f.isFile()) {
            BufferedReader bfReader = null;
            try {
                bfReader = new BufferedReader(new java.io.FileReader(fileName));

                //prima riga contiene la dimensione delle distanze
                String line = bfReader.readLine().trim(); //elimino spazi all'inizio e alla fine della riga
                distances = new int[Integer.parseInt(line)][Integer.parseInt(line)];

                line = bfReader.readLine();
                while (line != null) {
                    String[] words = line.split("\\s+"); //divido la sequenza in base ai caratteri vuoti

                    for (String word : words) {
                        distances[lineCounter][wordCounter] = (int) Double.parseDouble(words[wordCounter]);
                        distances[wordCounter][lineCounter] = (int) Double.parseDouble(words[wordCounter]);
                        wordCounter++;
                    }

                    line = bfReader.readLine();
                    lineCounter++;
                    wordCounter = 0;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } finally {
                if (bfReader != null) {
                    bfReader.close();
                }
            }

        }else {
        	System.out.println("File non trovato!");
        }
        return distances;
    }
    
    //leggo il file ottimo
    public static int[] readOpt(String fileName){
    	int vett[] = new int[] {};
		String inputFileName = fileName;
        String line = null;
        int i = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(inputFileName));
            
            line = in.readLine();
            vett = new int[Integer.parseInt(line)];
            line = in.readLine();

            while(line!=null){     
                int numero = Integer.parseInt(line);
                vett[i] = numero-1; //lo rendo 0-based: città 1 diventa la città 0
                i++;
                line = in.readLine();

            }
            i--;
            in.close();
            
        } catch(FileNotFoundException e) {
            System.out.println(inputFileName+" FileNotFound");
        } catch(NumberFormatException e) {
            System.out.println(" linea non corretta: -> "+line+" <-");
        } catch(IOException e) {
            System.out.println(" IOException  "+e);
        }
        return vett;
	}

    	
}
