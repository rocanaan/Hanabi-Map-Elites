package MapElites;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
import com.fossgalaxy.games.fireworks.ai.HistogramAgent;
import com.fossgalaxy.games.fireworks.ai.PopulationEvaluationSummary;
import com.fossgalaxy.games.fireworks.ai.TestSuite;
import com.fossgalaxy.games.fireworks.ai.rule.Rule;

import Evolution.GeneticAlgorithm;
import Evolution.GeneticAlgorithm2;
import Evolution.Individual;
import Evolution.Population;
import Evolution.Rulebase;
import javassist.compiler.ast.Pair;

/**
 * Main, executive class for the Traveling Salesman Problem.
 * 
 * We don't have a real list of cities, so we randomly generate a number of them
 * on a 100x100 map.
 * 
 * The TSP requires that each city is visited once and only once, so we have to
 * be careful when initializing a random Individual and also when applying
 * crossover and mutation. Check out the GeneticAlgorithm class for
 * implementations of crossover and mutation for this problem.
 * 
 * @author bkanber
 *
 */
public class RunMapElites {
	public static int G = 10000; //number of random individuals
	public static int I = 1000000 - G; // number of mutations after initial random individuals
	public static int d1 = 20; // number of niches in the first dimension
	public static int d2 = 20; // number of niches in the second dimension
	public static double mutationRate = 0.1;
	public static double crossoverRate = 0.5;
	public static int numGames = 100; // Number of games per agent per game size. There are 4 different game sizes, so this number is actually 4 times higher
	public static boolean mirror = true; // If true, will run in mirror mode. If false, will run in mixed mode, which is takes around 7 times as long
	static int minNumPlayers = 2;
	static int maxNumPlayers = 2;
	static boolean rulebaseStandard = false;
	static int chromosomeLength = 15;
	static Vector<AgentPlayer> testPool = null;
	static double[][] map = new double[d1][d2];
	static int[][][] population = new int[d1][d2][chromosomeLength];

	static String filePath = "/Users/rodrigocanaan/Dev/HanabiResults/Evolution";
	static String directory = "/20190513/";
	
	public static void printParameters() {
		System.out.println("Parameters for this run: ");

		System.out.println("G = "  + G);
		System.out.println("I = " + I);
		System.out.println("d1 = " + d1);
		System.out.println("d2 = " + d2);
		System.out.println("Mutation rate = " + mutationRate);
		System.out.println("Crossover rate =  " + crossoverRate);
		System.out.println("numGames  = " + numGames);
		System.out.println("Mirror =  " + mirror);
		System.out.println("min / max num players = " + minNumPlayers + " / " + maxNumPlayers);
		System.out.println("rulebaseStandard = " + rulebaseStandard);
		System.out.println("Chromosome Length =  " + chromosomeLength);
	}


	
	public static ArrayList<Integer> getNiche(double feature1, double feature2){
		ArrayList<Integer> coords = new ArrayList<Integer>();
		
		int c1 = (int) Math.floor(feature1 / (1.0/(double)d1));
		if (c1 == d1) {
			c1 --;
		}
		
		int c2 = (int) Math.floor(feature2 / (1.0/d2));
		if (c2 == d2) {
			c2 --;
		}
//		System.out.println("Calculating second niche");
//		System.out.println(feature2);
//		System.out.println(1/d2);
//		System.out.println(c2);
		
		coords.add(c1);
		coords.add(c2);
		return coords;
	}
	
	public static void updateMap(double fitness, int niche1, int niche2, int[] candidateChromosome) {
		boolean reevaluate = true; //TODO: This should be an external parameter
		double eliteFitness = map[niche1][niche2];
		if (reevaluate) {
			Rulebase rb = new Rulebase(rulebaseStandard);
			int[] eliteChromosome = population[niche1][niche2];
			Rule[] agentRules = new Rule[chromosomeLength];
			for (int geneIndex = 0; geneIndex<chromosomeLength; geneIndex++) {
				agentRules[geneIndex] = rb.ruleMapping(eliteChromosome[geneIndex]);
			}
			HistogramAgent histo;
	        histo = rb.makeAgent(agentRules);
	        ReportAgent agent = new ReportAgent(histo);
	        
	        Vector<AgentPlayer> agentPlayers = new Vector<AgentPlayer>();
	        Vector<ReportAgent> agents = new Vector<ReportAgent>();
	        
	        agentPlayers.add(new AgentPlayer("elite report agent ", agent));
	
	        PopulationEvaluationSummary pes = null;
	        if (testPool == null) {
	            if (mirror) {
	                pes = TestSuite.mirrorPopulationEvaluation(agentPlayers, minNumPlayers, maxNumPlayers, numGames);
	            } //TODO: create a class that returns the testpool
	            else {
	                Vector<AgentPlayer> baselinePool;
	                baselinePool = Rulebase.GetBaselineAgentPlayers();
	                pes = TestSuite.mixedPopulationEvaluation(agentPlayers, baselinePool, minNumPlayers, maxNumPlayers, numGames);
	            }
	        } else {
	            pes = TestSuite.mixedPopulationEvaluation(agentPlayers, testPool, minNumPlayers, maxNumPlayers, numGames);
	        }
	        
	        eliteFitness = pes.getScoreIndividualAgent(0);
		}
		if (fitness > eliteFitness) {
//			System.out.println("Fitness is greater than current elite");
			map[niche1][niche2] = fitness;
			population[niche1][niche2] = candidateChromosome;
		}
	}
	
	public static void printMap() {
		double max = 0;
		int imax = 0;
		int jmax = 0;
		for (int i = 0; i<d1; i++) {
			for(int j = 0; j<d2; j++) {
				System.out.print(map[i][j] + "  ");
				if (map[i][j] > max) {
					max = map[i][j];
					imax = i;
					jmax = j;
				}
			}
			System.out.println("");
		}
		System.out.print("Max from this generation is [" + imax +"," + jmax+ "] with fitness " + max + " with chromosome: {");
		for (int k=0; k< chromosomeLength;k++) {
			if (k != (chromosomeLength-1)) {
				System.out.print(population[imax][jmax][k] + ",");
			}
			else {
				System.out.print(population[imax][jmax][k] + "}");
			}
		}
		System.out.println("");
	}
	
	
	
	public static void printChromosomes() {
		for (int i = 0; i<d1; i++) {
			for(int j = 0; j<d2; j++) {
				System.out.print("Chromosome in niche " + i + "," + j +  " : {");
				for (int k=0; k< chromosomeLength;k++) {
					if (k != (chromosomeLength-1)) {
						System.out.print(population[i][j][k] + ",");
					}
					else {
						System.out.print(population[i][j][k]);
					}
				}
				System.out.println("} with fitness " + map[i][j]);
			}

		}
	}
	
	public static boolean sanityCheck(double fitness) { //TODO: Threshold should be a parameter
		double threshold = 3;
		double max = 0;
		int imax = 0;
		int jmax = 0;
		for (int i = 0; i<d1; i++) {
			for(int j = 0; j<d2; j++) {
				if (map[i][j] > max) {
					max = map[i][j];
					imax = i;
					jmax = j;
				}
			}
		} //TODO Thiw whole "make agent from chromosome, add to population of agent players, call pes etc should be encapsulated"
		Rulebase rb = new Rulebase(rulebaseStandard);
		int [] elite = population[imax][jmax];
		Rule[] rulesMapElites = new Rule[elite.length+1]; //TODO check if the +1 is needed
		for (int i = 0; i < elite.length; i++) {
			rulesMapElites[i] = rb.ruleMapping(elite[i]); // TODO making an agent player out of a chromosome should be extracted
		}
		
		HistogramAgent histo;
        histo = rb.makeAgent(rulesMapElites);
        ReportAgent agent = new ReportAgent(histo);
		
		Vector<AgentPlayer> agentPlayers = new Vector<AgentPlayer>();
        
        agentPlayers.add(new AgentPlayer("elite report agent ", agent));
		
		
		PopulationEvaluationSummary pes = null;
		if (mirror) {
            pes = TestSuite.mirrorPopulationEvaluation(agentPlayers, minNumPlayers, maxNumPlayers, numGames);
        } //TODO: create a class that returns the testpool
        else {
            Vector<AgentPlayer> baselinePool;
            baselinePool = Rulebase.GetBaselineAgentPlayers();
            pes = TestSuite.mixedPopulationEvaluation(agentPlayers, baselinePool, minNumPlayers, maxNumPlayers, numGames);
        }
		
		double sanityFitness = pes.getScoreIndividualAgent(0);
		if (Math.abs(sanityFitness-fitness) < threshold) {
			return true;
		}
		return false;
		
	}
	
	private static class AvgMaxNumGood{
		public double avg;
		public double max;
		public int numGood;
		
		public AvgMaxNumGood(double avg, double max, int numGood) {
			this.avg = avg;
			this.max = max;
			this.numGood = numGood;
			
		}
		
	}
	
	public static AvgMaxNumGood getStats(double threshold) {
		double avg = 0;
		int count = 0;
		double max = 0;
		int numGood = 0;
		for (int i = 0; i<d1; i++) {
			for(int j = 0; j<d2; j++) {
				avg += map[i][j];
				count +=1;
				if (map[i][j] > max) {
					max = map[i][j];
				}
				if (map[i][j] > threshold) {
					numGood ++;
				}
			}
		}
		if (count != 0) {
			avg = avg/count;
		}
		return new AvgMaxNumGood(avg,max,numGood);
		
	}
	
	public static void  printStats(ArrayList<AvgMaxNumGood> stats) {
		System.out.print("Printing avgs: ");
		for(AvgMaxNumGood s: stats) {
			System.out.print(String.format("%.02f", s.avg) + " ");
		}
		System.out.println("");
		System.out.print("Printing max: ");
		for(AvgMaxNumGood s: stats) {
			System.out.print(s.max + " ");
		}
		System.out.println("");
		System.out.print("Printing numGood: ");
		for(AvgMaxNumGood s: stats) {
			System.out.print(s.numGood + " ");
		}
		System.out.println("");
	}
	
	
	//TODO: Map is not correctly serialized
	public static void serialize(double[][] map, String mapFileName, int[][][] population, String populationFileName) {
		//map
		try {
			FileOutputStream file = new FileOutputStream(mapFileName); // TODO: There should bbe a class just to serialize, another to gather the data
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(map);
			out.close();
			file.close();
            System.out.println("Map has been serialized"); 

		}
	    catch(IOException ex) 
        { 
            System.err.println("Failed to serialize map"); 
            System.err.println(ex); 

        } 
		//population
		try {
			FileOutputStream file = new FileOutputStream(populationFileName); // TODO: There should bbe a class just to serialize, another to gather the data
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(population);
			out.close();
			file.close();
            System.out.println("Population has been serialized"); 

		}
	    catch(IOException ex) 
        { 
            System.err.println("Failed to serialize population"); 
        } 
	}
//		
//		
//		
//		
//		
//	}
	
	public static void main(String[] args) {
		Rulebase rb = new Rulebase(rulebaseStandard);
		// Create cities
		int numRules = rb.getRuleset().length;
		
		ArrayList<AvgMaxNumGood> stats = new ArrayList<AvgMaxNumGood>(); 
		
		int numFailsSanityCheck = 0;
		
		double[][] map = new double[d1][d2];
		
		// Evaluate G random individuals
		for (int individual = 0; individual<G; individual++) {
			// Make agent from random chromosome
			int[] chromosome = rb.getRandomChromossome(chromosomeLength);
			Rule[] agentRules = new Rule[chromosomeLength];
			for (int geneIndex = 0; geneIndex<chromosomeLength; geneIndex++) {
				agentRules[geneIndex] = rb.ruleMapping(chromosome[geneIndex]);
			}
			HistogramAgent histo;
            histo = rb.makeAgent(agentRules);
            ReportAgent agent = new ReportAgent(histo);
            
            Vector<AgentPlayer> agentPlayers = new Vector<AgentPlayer>();
	        Vector<ReportAgent> agents = new Vector<ReportAgent>();
	        
	        agentPlayers.add(new AgentPlayer("report agent " + individual, agent));

	        PopulationEvaluationSummary pes = null;
	        if (testPool == null) {
	            if (mirror) {
	                pes = TestSuite.mirrorPopulationEvaluation(agentPlayers, minNumPlayers, maxNumPlayers, numGames);
	            } //TODO: create a class that returns the testpool
	            else {
	                Vector<AgentPlayer> baselinePool;
	                baselinePool = Rulebase.GetBaselineAgentPlayers();
	                pes = TestSuite.mixedPopulationEvaluation(agentPlayers, baselinePool, minNumPlayers, maxNumPlayers, numGames);
	            }
	        } else {
	            pes = TestSuite.mixedPopulationEvaluation(agentPlayers, testPool, minNumPlayers, maxNumPlayers, numGames);
	        }
	        // This whole block up to here should be a function that returns a pair of pes and agent
	        
	        
	        double fitness = pes.getScoreIndividualAgent(0);
	        double dim1 = (double)agent.hintsGiven/(double)agent.possibleHints;
    			double dim2 = agent.totalPlayability/(double)agent.countPlays;
//    			System.out.println("Agent " + individual + " has fitness " + fitness + " dim 1 " + dim1 + " dim 2 " + dim2);
    			
    			ArrayList<Integer> niches = getNiche(dim1, dim2);
//    			System.out.println(dim1);
//    			System.out.println(dim2);
//    			System.out.println(niches.get(0));
//    			System.out.println(niches.get(1));


    			updateMap(fitness,  niches.get(0), niches.get(1), chromosome);

    			if (individual % 1000 == 0) {
        			System.out.println("Printing Map for initial iteration " + individual);
    				printParameters();
        			printMap();
     			printChromosomes();
        			AvgMaxNumGood s = getStats(12);
        			stats.add(s);
        			printStats(stats);
         		System.out.println("");
        			if (!sanityCheck(s.max)) {
        				numFailsSanityCheck +=1;
        			}
           		System.out.println("Number of batches that failed sanity check =  " + numFailsSanityCheck );
           		
           		String mapFileName = filePath+directory+"map"+individual;
           		String populationFileName = filePath+directory+"population"+individual; 		
           		serialize(map, mapFileName,population,populationFileName);
           		
           		
           		
    			}

	        
		}
		
		for (int individual = 0; individual < I; individual++) {
			Random r = new Random();
			int i = r.nextInt(d1);
			int j = r.nextInt(d2); //TODO: what happens if this niche is empty
			
			int[] chromosome = Arrays.copyOf(population[i][j],chromosomeLength);
			
			if (crossoverRate > Math.random()) {
				int x = r.nextInt(d1);
				int y = r.nextInt(d2);
				int[] c2 = population [x][y];
				
				for (int k = 0; k<chromosomeLength; k++) {
					if (0.5 > Math.random()) {
						chromosome[k] = c2[k];
					}
				}

			}
			for (int k = 0; k<chromosomeLength; k++) {
				if (mutationRate > Math.random()) {
					chromosome[k] = r.nextInt(rb.getRuleset().length);
				}
			} 	 		
			
			// From here on out it's the same, should refactor
			Rule[] agentRules = new Rule[chromosomeLength];
			for (int geneIndex = 0; geneIndex<chromosomeLength; geneIndex++) {
				agentRules[geneIndex] = rb.ruleMapping(chromosome[geneIndex]);
			}
			HistogramAgent histo;
            histo = rb.makeAgent(agentRules);
            ReportAgent agent = new ReportAgent(histo);
            
            Vector<AgentPlayer> agentPlayers = new Vector<AgentPlayer>();
	        Vector<ReportAgent> agents = new Vector<ReportAgent>();
	        
	        agentPlayers.add(new AgentPlayer("report agent " + individual, agent));

	        PopulationEvaluationSummary pes = null;
	        if (testPool == null) {
	            if (mirror) {
	                pes = TestSuite.mirrorPopulationEvaluation(agentPlayers, minNumPlayers, maxNumPlayers, numGames);
	            } //TODO: create a class that returns the testpool
	            else {
	                Vector<AgentPlayer> baselinePool;
	                baselinePool = Rulebase.GetBaselineAgentPlayers();
	                pes = TestSuite.mixedPopulationEvaluation(agentPlayers, baselinePool, minNumPlayers, maxNumPlayers, numGames);
	            }
	        } else {
	            pes = TestSuite.mixedPopulationEvaluation(agentPlayers, testPool, minNumPlayers, maxNumPlayers, numGames);
	        }
	        
	        double fitness = pes.getScoreIndividualAgent(0);
	        double dim1 = (double)agent.hintsGiven/(double)agent.possibleHints;
    			double dim2 = agent.totalPlayability/(double)agent.countPlays;
//    			System.out.println("Agent " + individual + " has fitness " + fitness + " dim 1 " + dim1 + " dim 2 " + dim2);
    			
    			ArrayList<Integer> niches = getNiche(dim1, dim2);
//    			System.out.println(dim1);
//    			System.out.println(dim2);
//    			System.out.println(niches.get(0));
//    			System.out.println(niches.get(1));


    			updateMap(fitness,  niches.get(0), niches.get(1), chromosome);


    			if (individual % 5000 == 0 || individual == (I-1)) {
        			System.out.println("Printing Map for mutation iteration " + individual);
    				printParameters();
        			printMap();
     			printChromosomes();
        			AvgMaxNumGood s = getStats(12);
        			stats.add(s);
        			printStats(stats);
         		System.out.println("");
        			if (!sanityCheck(s.max)) {
        				numFailsSanityCheck +=1;
        			}
           		System.out.println("Number of batches that failed sanity check =  " + numFailsSanityCheck );
         		int ind = individual+G;
         		String mapFileName = filePath+directory+"map"+ind;
           		String populationFileName = filePath+directory+"population"+ind; 		
           		serialize(map, mapFileName,population,populationFileName);

    			}
//    			if ( (individual % 10000) == 0) {
//    				printChromosomes();
//    			}
		}
		
		System.out.println("Final iteration results:  ");

		printMap();
		printChromosomes();
		printParameters();

	}
////		City cities[] = new City[numRules];
////		
////		// Loop to create random cities
////		for (int cityIndex = 0; cityIndex < numRules; cityIndex++) {
////			// Generate x,y position
////			int xPos = (int) (100 * Math.random());
////			int yPos = (int) (100 * Math.random());
////			
////			// Add city
////			cities[cityIndex] = new City(xPos, yPos);
////		}
//
//		// Initialize the population
//		
//		
//		// Initial GA
//		GeneticAlgorithm2 ga = new GeneticAlgorithm2(populationSize, mutationRate, crossoverRate, elitismCount, tournamentSize);
//
//		// Initialize population
//		Population population = ga.initPopulation(chromossomeLength, numRules);
//
//		// Evaluate population
//		ga.evalPopulation(population, numRules, mirror, null, minNumPlayers, maxNumPlayers, rulebaseStandard);
//
//		//Route startRoute = new Route(population.getFittest(0), cities);
//		//System.out.println("Start Distance: " + startRoute.getDistance());
//
//		// Keep track of current generation
//		int generation = 1;
//		// Start evolution loop
//		while (ga.isTerminationConditionMet(generation, maxGenerations) == false) {
//			// Print fittest individual from population
//			//Route route = new Route(population.getFittest(0), cities);
//			//System.out.println("G"+generation+" Best distance: " + route.getDistance());
//
//			// Apply crossover
//			population = ga.crossoverPopulation(population);
//
//			// Apply mutation
//			population = ga.mutatePopulation(population);
//
//			// Evaluate population
//			System.out.println("Evaluating fitness after generation " + generation);
//			ga.evalPopulation(population, numRules, mirror, null, minNumPlayers, maxNumPlayers, rulebaseStandard);
//			ga.printFittestPerGeneration();
//			
//
//			// Increment the current generation
//			generation++;
//		}
//		
//		//System.out.println("Stopped after " + maxGenerations + " generations.");
//		//Route route = new Route(population.getFittest(0));
//		//System.out.println("Best distance: " + route.getDistance());
//
//	}

}
