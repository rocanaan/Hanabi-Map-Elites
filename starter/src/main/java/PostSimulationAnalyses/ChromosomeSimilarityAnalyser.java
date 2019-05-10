package PostSimulationAnalyses;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ChromosomeSimilarityAnalyser {
	
	public static void main(String[] args) {
		String path = "/Users/rodrigocanaan/Dev/HanabiResults/HammingDistances";
		String identifier = "HammingDistances";
		
		ArrayList<int[]> chromosomes = getChromosomesFromFile();
	}
	
	
	
	
	
	public void serializeLog(String log, String logFileName) {
		
	}
	
	public void serializeHammingDistances(int[][] hammingDistances, String outputFileName) { //TODO this should be a method to serialize objects in general
		try {
			FileOutputStream file = new FileOutputStream(outputFileName);
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(hammingDistances);
			out.close();
			file.close();
            System.out.println("Hamming Distances have been serialized"); 

		}
	    catch(IOException ex) 
        { 
            System.err.println("Failed to serialize hamming distances"); 
        } 
	}
	
	public int[][] getHammingDistance(ArrayList<int[]> chromosomes){
		int size=chromosomes.size();
		int[][] pairwiseHammingDistances = new int[size][size];
		
		for (int i = 0; i<size; i++) {
			for(int j = 0; j<size;j++) {
				int[] c1 = chromosomes.get(i);
				int[] c2 = chromosomes.get(j);
				int distance = 0;
				int maxLength = Math.max(c1.length, c2.length);
				int minLength = Math.min(c1.length, c2.length);
				for (int geneIndex = 0; geneIndex< maxLength; geneIndex++) {
					if (geneIndex >= minLength) {
						distance+=1;
					}
					else {
						if (c1[geneIndex]!=c2[geneIndex]) {
							distance+=1;
						}
					}
				}
				pairwiseHammingDistances[i][j] = distance;
			}
		}
		return pairwiseHammingDistances;
	}
	
	public ArrayList<int[]> getChromosomesFromFile(String fileName){
		ArrayList<int[]> chromosomes = new ArrayList<int[]>();
		try {
			String thisLine;
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            while ((thisLine = br.readLine()) != null) {
                //thisLine = thisLine.substring(1, thisLine.length() - 2);
            		thisLine = thisLine.replaceAll(" ", "");
            		thisLine = thisLine.replaceAll("\t", "");
            		String[] c = thisLine.split(",");
            		int[] chromossome = new int[c.length];
                for (int i = 0; i < c.length; i++) {
                    chromossome[i] = Integer.parseInt(c[i]);    
                }
                chromosomes.add(chromossome);
            }
		 }catch (Exception e) {
		  	 System.err.println("Failed to load chromosomes from file");
			 System.err.println(e);
		 }
		return chromosomes;

	 } 
}
