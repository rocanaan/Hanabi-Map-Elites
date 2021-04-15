package PostSimulationAnalyses;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.fossgalaxy.games.fireworks.ai.Agent;

import MapElites.ActionDatabaseEntry;

public class ChromosomeSimilarityAnalyser {
	
	public static void main(String[] args) {
//		String path = "/Users/rodrigocanaan/Dev/HanabiResults/HammingDistances";
//		String identifier = "HammingDistances";
//		
//		ArrayList<int[]> chromosomes = getChromosomesFromFile();
		
//		String chromosomeFileName = "/Users/rodrigocanaan/Dev/HanabiResults/Fixed/ChromosomesRun1M";
		String chromosomeFileName = "Pop2C";
		String chromosomeFileName2 = "Pop3C";

		int sizeDim1 = 20;
		int sizeDim2 = 20;

		System.out.println("Getting valid mask");
		int[][] validMask = GetStateActionArchiveFromFile.getValidMaskFromFile(chromosomeFileName,sizeDim1,sizeDim2);
		ArrayList<int[]> chromosomes = getChromosomesFromFile(chromosomeFileName);
		
		
		//Intra
		System.out.println("Intra");

		int[][][][] pairwiseHamming = getPairwiseHammingDistances (chromosomes, validMask, sizeDim1, sizeDim2);
		CalculateActionSimilarity.printHistogram(pairwiseHamming, validMask, sizeDim1, sizeDim2, 1);
		
		//Cross
		System.out.println("Cross");
		int[][] validMask2 = GetStateActionArchiveFromFile.getValidMaskFromFile(chromosomeFileName2,sizeDim1,sizeDim2);
		ArrayList<int[]> chromosomes2 = getChromosomesFromFile(chromosomeFileName2);
		getCrossHammingDistance(chromosomes,validMask,chromosomes2,validMask2,sizeDim1,sizeDim2);
	}
	
	public static int[][] getCrossHammingDistance (ArrayList<int[]> chromosomes1, int[][] validMask1,
			ArrayList<int[]> chromosomes2, int[][] validMask2, 
			int sizeDim1, int sizeDim2){
		int[][] crossHamming = new int[sizeDim1][sizeDim2];
		
		for(int i = 0; i< sizeDim1; i++) {
			for(int j = 0; j< sizeDim2; j++) {
				if (validMask1[i][j]==1 && validMask2[i][j]==1) {
					int[] chromosome1 = chromosomes1.get(j+sizeDim1*i);
					int[] chromosome2 = chromosomes2.get(j+sizeDim1*i);
					crossHamming[i][j] = calculateHammingDistance(chromosome1,chromosome2);
				}
				else {
					crossHamming[i][j] = 99;
				}
				System.out.print(crossHamming[i][j] + " ");
			}
			System.out.println("");
		}
		return crossHamming;
	}
	
	
	
	public static int[][][][] getPairwiseHammingDistances (ArrayList<int[]> chromosomes, int[][] validMask, int sizeDim1, int sizeDim2){
		int[][][][] pairwiseHammingDistances = new int[sizeDim1][sizeDim2][sizeDim1][sizeDim2];
		

		for(int i = 0; i< sizeDim1; i++) {
			for(int j = 0; j< sizeDim2; j++) {
				int[] chromosome1 = chromosomes.get(j+sizeDim1*i);
				for(int m = 0; m< sizeDim1; m++) {
					for(int n = 0; n< sizeDim2; n++) {
						int[] chromosome2 = chromosomes.get(n+sizeDim1*m);
						pairwiseHammingDistances[i][j][m][n] = calculateHammingDistance(chromosome1,chromosome2);
					}
				}
			}
		}
		return pairwiseHammingDistances;
	}

	public static int calculateHammingDistance(int[] c1, int[]c2) {
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
//		System.out.println(distance);
		return distance;
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
	
//	public int[][] getHammingDistance(ArrayList<int[]> chromosomes){
//		int size=chromosomes.size();
//		int[][] pairwiseHammingDistances = new int[size][size];
//		
//		for (int i = 0; i<size; i++) {
//			for(int j = 0; j<size;j++) {
//				int[] c1 = chromosomes.get(i);
//				int[] c2 = chromosomes.get(j);
//				int distance = 0;
//				int maxLength = Math.max(c1.length, c2.length);
//				int minLength = Math.min(c1.length, c2.length);
//				for (int geneIndex = 0; geneIndex< maxLength; geneIndex++) {
//					if (geneIndex >= minLength) {
//						distance+=1;
//					}
//					else {
//						if (c1[geneIndex]!=c2[geneIndex]) {
//							distance+=1;
//						}
//					}
//				}
//				pairwiseHammingDistances[i][j] = distance;
//			}
//		}
//		return pairwiseHammingDistances;
//	}
	
	public static ArrayList<int[]> getChromosomesFromFile(String fileName){
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
