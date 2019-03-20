package MapElites;

import java.util.ArrayList;
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
	public static int I = 1000000; // number of mutations after initial random individuals
	public static int d1 = 20; // numer of niches in the first dimension
	public static int d2 = 20; // numer of niches in the second dimension
	public static double mutationRate = 0.1;
	public static double crossoverRate = 0.5;
	public static int numGames = 20; // Number of games per agent per game size. There are 4 different game sizes, so this number is actually 4 times higher
	public static boolean mirror = true; // If true, will run in mirror mode. If false, will run in mixed mode, which is takes around 7 times as long
	static int minNumPlayers = 2;
	static int maxNumPlayers = 2;
	static boolean rulebaseStandard = false;
	static int chromosomeLength = 15;
	static Vector<AgentPlayer> testPool = null;
	static double[][] map = new double[d1][d2];
	static int[][][] population = new int[d1][d2][chromosomeLength];
	
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
        
        double eliteFitness = pes.getScoreIndividualAgent(0);
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
		System.out.println("Max from this generation is [" + imax +"," + jmax+ "] with fitness " + max);
	}
	
	
	
	public static void printChromosomes() {
		for (int i = 0; i<d1; i++) {
			for(int j = 0; j<d2; j++) {
				System.out.print("Chromosome in niche " + i + "," + j +  " : [");
				for (int k=0; k< chromosomeLength;k++) {
					System.out.print(population[i][j][k] + " ");
				}
				System.out.println("] with fitness " + map[i][j]);
			}

		}
	}
	
	public static void main(String[] args) {
		Rulebase rb = new Rulebase(rulebaseStandard);
		// Create cities
		int numRules = rb.getRuleset().length;
		
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

    			if (individual % 100 == 0) {
        			System.out.println("Printing Map for initial iteration " + individual);
        			printMap();
    			}
    			if ( (individual % 1000) == 0) {
    				printChromosomes();
    				printParameters();
    			}
	        
		}
		
		for (int individual = 0; individual < I; individual++) {
			Random r = new Random();
			int i = r.nextInt(d1);
			int j = r.nextInt(d2);
			
			int[] chromosome = population[i][j];
			
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

    			if (individual % 100 == 0) {
        			System.out.println("Printing Map for mutation iteration " + individual);
        			printMap();
    			}
    			if ( (individual % 10000) == 0) {
    				printChromosomes();
    				printParameters();
    			}
		}
		
		System.out.println("Final iteration results:  ");

		printMap();
		printChromosomes();
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
