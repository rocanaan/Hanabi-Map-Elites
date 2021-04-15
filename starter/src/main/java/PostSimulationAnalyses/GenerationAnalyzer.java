package PostSimulationAnalyses;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
import com.fossgalaxy.games.fireworks.ai.HistogramAgent;
import com.fossgalaxy.games.fireworks.ai.PopulationEvaluationSummary;
import com.fossgalaxy.games.fireworks.ai.TestSuite;
import com.fossgalaxy.games.fireworks.ai.rule.Rule;

import Evolution.Rulebase;
import MapElites.ReportAgent;
import Utils.Utils;

// Methods to analyse the results of "generation" of map elites

public class GenerationAnalyzer {
	
	public static class GenerationSummary implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		int generation;
		int countNonZero;
		int countMismatches;
		double maxScore;
		double averageScore;
		double averageNonzeroScore;
		double[][] map;
		
		public GenerationSummary(int generation, int countNonZero, int countMismatches, double maxScore,
				double averageScore, double averageNonzeroScore, double[][] map) {
			super();
			this.generation = generation;
			this.countNonZero = countNonZero;
			this.countMismatches = countMismatches;
			this.maxScore = maxScore;
			this.averageScore = averageScore;
			this.averageNonzeroScore = averageNonzeroScore;
			this.map = map;
		}

//		@Override
//		public int compare(Object o1, Object o2) {
//			GenerationSummary s1 = (GenerationSummary)o1;
//			GenerationSummary s2 = (GenerationSummary)o2;
//			if (s1.generation < s2.generation) {
//				return -1;
//			}
//			else if (s1.generation>s2.generation) {
//				return 1;
//			}
//			return 0;
//		}

	@Override
	public String toString() {
		return (generation + " " + countNonZero + " " + countMismatches + " " + maxScore + " " + averageScore + " " + averageNonzeroScore);
	}
		
		

	}

	public static void main(String[] args) {
		int sizeDim1 =20;
		int sizeDim2 = 20;
		int minNumPlayers =2;
		int maxNumPlayers =2;
		int numGames = 100;

		ArrayList<GenerationSummary> generationArchive = new ArrayList<GenerationSummary>();
		
		boolean rulebaseStandard = false;
		Rulebase rb = new Rulebase(rulebaseStandard);

		// TODO Auto-generated method stub
		final File folder = new File("/Users/rodrigocanaan/Dev/HanabiResults/Evolution/20190507");
		
		Vector<String> filenames = getFileNamesFromFolder (folder);
		Map<Integer,int[][][]> populations = getPopulationsFromFolder(filenames);
		for (int generation: populations.keySet()) {
			int countNonZero = 0;
			int countMismatches = 0;
			int[][][] population = populations.get(generation);
			double[][] map = new double[sizeDim1][sizeDim2];
				
			
			double maxScore = 0;
			double averageScore = 0;
            
            	// Makes agents and counts nonzeros
			for (int i =0; i<sizeDim1; i++) {
				for(int j =0; j<sizeDim2; j++) {
					boolean nonzero = false;

					int[] chromosome = population[i][j];
					for (int k = 0; k<chromosome.length;k++) {
						if ( chromosome[k]!=0) {
							countNonZero += 1;
							nonzero = true;
							break;
						}
					}
					
					if (nonzero) {
			            Vector<AgentPlayer> agentPlayers = new Vector<AgentPlayer>();

						Rule[] agentRules = new Rule[chromosome.length];
						for (int geneIndex = 0; geneIndex<chromosome.length; geneIndex++) {
							agentRules[geneIndex] = rb.ruleMapping(chromosome[geneIndex]);
						}
						HistogramAgent histo;
			            histo = rb.makeAgent(agentRules);
			            ReportAgent agent = new ReportAgent(histo);				        
				        agentPlayers.add(new AgentPlayer("report agent " + i + "," + j, agent));
				        PopulationEvaluationSummary pes = TestSuite.mirrorPopulationEvaluation(agentPlayers, minNumPlayers, maxNumPlayers, numGames);
				        
				        double fitness = pes.getScoreIndividualAgent(0);
				        double dim1 = (double)agent.hintsGiven/(double)agent.possibleHints;
			    			double dim2 = agent.totalPlayability/(double)agent.countPlays;
//			    			System.out.println("Agent " + individual + " has fitness " + fitness + " dim 1 " + dim1 + " dim 2 " + dim2);
			    			
			    			ArrayList<Integer> niches = MapElites.RunMapElites.getNiche(dim1, dim2);
			    			if (niches.get(0)!=i || niches.get(1)!=j) {
			    				countMismatches +=1; //TODO: This doesn't seem to be working, should be incorporated in the base MapElites
			    			}
			    			
			    			if (fitness > maxScore) {
			    				maxScore = fitness;
			    			}
			    			averageScore+= fitness;
			    			map[i][j] = fitness;
					}
				}
			}
			double averageNonzeroScore =0; 
			if(countNonZero!=0) {
				averageNonzeroScore = averageScore/countNonZero;
			}
			averageScore = averageScore/400;

			System.out.println(generation + " " + countNonZero + " " + countMismatches + " " + maxScore + " " + averageScore + " " + averageNonzeroScore);
			for (int i =0; i<sizeDim1; i++) {
				for(int j =0; j<sizeDim2; j++) {
					System.out.print(map[i][j] + " ");
				}
				System.out.println("");
			}
			
			GenerationSummary summary = new GenerationSummary(generation,countNonZero,countMismatches,maxScore,averageScore,averageNonzeroScore,map);
			generationArchive.add(summary);
			for (GenerationSummary s: generationArchive) {
				System.out.println(s);
			}
			String outputFileName = "/Users/rodrigocanaan/Dev/HanabiResults/Evolution/20190507/Summary/" + Utils.getDateTimeString();
			ObjectSerializer.serializeObject(generationArchive, outputFileName, "Summaries");

		}
		
		

		
		
		
		//getMapsFromFolder(filenames);

	}
	
	
	public static Vector<String> getFileNamesFromFolder(final File folder) {
		Vector<String> vectorNames = new Vector<String>();
		int i = 0;
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            getFileNamesFromFolder(fileEntry);
	        } else {
	        		vectorNames.add(folder+"/"+fileEntry.getName());
	        }
	    }
	    return vectorNames;
	}
	
	public static Map<Integer, Double[][]> getMapsFromFolder(Vector<String> filenames){
		Map<Integer, Double[][]> result = new HashMap<Integer,Double[][]>();
		
		for (String name: filenames) {
			if (name.contains("map")){
				int generation = Integer.parseInt(name.split("map")[1]);
				System.out.println(generation);
				double[][] map = readMapFromFile(name);
				for (int i=0 ; i<20;i++) {
					for (int j = 0; j<20; j++) {
						System.out.println(map[i][j]);
					}
				}
			}
		}
		return result;
		
	}
	
	public static Map<Integer, int[][][]> getPopulationsFromFolder(Vector<String> filenames){
		Map<Integer, int[][][]> result = new HashMap<Integer,int[][][]>();
		
		for (String name: filenames) {
			if (name.contains("population")){
				int generation = Integer.parseInt(name.split("population")[1]);
				System.out.println(generation);
				int[][][] population = readPopulationsFromFile(name);
				result.put(generation,population);
			}
		}
		return result;
		
	}
	
	public static double[][] readMapFromFile (String fileName){
		
		try
        {    
			System.out.println("Deserializing " + fileName);
            // Reading the object from a file 
            FileInputStream file = new FileInputStream(fileName); 
            ObjectInputStream in = new ObjectInputStream(file); 
              
            // Method for deserialization of object 
            double[][] map = (double[][])in.readObject(); 
              
            in.close(); 
            file.close(); 
              
            System.out.println("Map has been deserialized "); 
            return map;

        } 
          
        catch(IOException ex) 
        { 
            System.err.println("IOException is caught"); 
            System.err.println(ex);
        } 
          
        catch(ClassNotFoundException ex) 
        { 
            System.err.println("ClassNotFoundException is caught"); 
        } 
		return null;
	}
	
	public static int[][][] readPopulationsFromFile (String fileName){
		
		try
        {    
			System.out.println("Deserializing " + fileName);
            // Reading the object from a file 
            FileInputStream file = new FileInputStream(fileName); 
            ObjectInputStream in = new ObjectInputStream(file); 
              
            // Method for deserialization of object 
            int[][][] population = (int[][][])in.readObject(); 
              
            in.close(); 
            file.close(); 
              
            System.out.println("Population has been deserialized "); 
            return population;

        } 
          
        catch(IOException ex) 
        { 
            System.err.println("IOException is caught"); 
            System.err.println(ex);
        } 
          
        catch(ClassNotFoundException ex) 
        { 
            System.err.println("ClassNotFoundException is caught"); 
        } 
		return null;
	}





}
