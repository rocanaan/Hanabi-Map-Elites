package com.fossgalaxy.games.fireworks.ai;

import java.util.Map;
import java.util.Vector;

import com.fossgalaxy.games.fireworks.ai.rule.Rule;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;

import Evolution.Rulebase;

/*
 * This class evaluates an entire population in mirror tests
 */

public class RunMixedPrintFullResults {
	public static void main( String[] args ) {
		int minNumPlayers = 2;
		int maxNumPlayers = 2;
		int numGames = 20;
		boolean includeBaselineAgents = true;
		Rulebase rb = new Rulebase(false);
		int[][] agentChromossomes = {
				//Best specialized 2p mirror agent
				{68,7,63,8,67,66,52,35,36,51,33,13,54,16,48,37,69,19,4,5,44,61,27,49,34,39,18,46,0,59,65,29,3,23,70,24,58,38,42,32,6,56,9,14,41,30,11,45,43,21,2,15,1,50,26,62,28,57,22,64,20,10,31,55,40,47,60,71,17,53,12,25},
				//Best Map Elites agent
				{67,21,8,52,33,53,36,93,96,34,27,6,19,62,21} ,
				// Best specialized 2p mixed agent
				{68,7,63,8,67,66,52,35,36,51,33,13,54,16,48,37,69,19,4,5,44,61,27,49,34,39,18,46,0,59,65,29,3,23,70,24,58,38,42,32,6,56,9,14,41,30,11,45,43,21,2,15,1,50,26,62,28,57,22,64,20,10,31,55,40,47,60,71,17,53,12,25},

				//Agents from some run of evolution
//				{47,46,13,10,8,32,36,27,15,22,31,41,21,18,35,28,9,49,14,39,24,5,0,7,1,29,23,20,37,43,17,42,30,12,2,40,4,6,26,33,11,45,3,16,19,38,44,25,34,48},
//				{10,7,21,2,38,46,13,8,47,36,27,15,43,31,32,25,30,29,3,11,42,49,44,4,35,48,45,37,6,20,19,28,33,12,34,9,22,39,24,17,0,1,16,23,40,26,14,41,18,5,},
//				{25,46,13,47,8,15,36,27,32,22,31,41,21,18,35,28,9,49,14,39,24,5,0,7,1,26,23,20,37,43,17,42,30,12,2,40,4,6,29,33,11,45,3,16,19,38,44,34,48,10},
//				{46,13,10,8,32,36,27,43,15,22,45,25,29,7,17,19,18,3,49,24,31,20,9,48,21,4,41,14,2,42,37,47,34,33,12,40,38,0,28,39,5,23,30,11,44,16,26,35,1,6},
//				{46,13,8,25,37,47,36,27,15,43,32,31,30,10,3,45,42,49,44,4,35,48,11,18,6,20,19,26,33,12,34,9,22,39,24,17,0,1,16,23,40,28,14,41,5,29,7,21,2,38},
//				{10,37,46,13,7,33,36,28,22,45,11,44,3,41,21,19,23,6,27,12,26,39,8,0,49,38,42,43,17,25,30,34,32,2,48,5,4,29,47,20,14,15,18,24,16,1,40,31,35,9},
//				{1,40,8,47,13,46,10,25,28,37,22,11,16,27,19,0,31,18,5,9,20,14,15,21,12,39,38,42,43,17,30,34,32,2,48,4,7,29,35,41,3,36,26,44,23,49,33,6,24,45},
//				{37,8,13,46,1,25,28,10,11,27,19,18,5,9,14,15,21,12,39,42,17,30,32,34,48,4,35,41,3,36,40,0,23,7,20,24,29,6,22,31,45,47,2,43,38,33,16,26,44,49},
//				{8,35,47,13,46,41,21,37,10,36,28,22,12,11,44,3,23,49,33,31,6,27,24,45,26,39,19,0,16,38,42,43,17,25,30,34,32,2,48,5,4,7,9,20,1,14,15,29,40,18},
//				{2,46,13,10,8,32,36,27,43,15,38,25,41,21,42,28,40,49,14,0,47,48,12,29,16,3,30,45,44,7,31,17,35,19,4,22,20,23,33,26,18,39,24,5,1,11,37,9,34,6}
		};
		
		// This is the population of agents we are evaluating
		String[] agentNames = {"RuleBasedIGGI", "RuleBasedInternal","RuleBasedOuter","SampleLegalRandom","RuleBasedVanDeBergh","RuleBasedFlawed","RuleBasedPiers"};
		
		// This is the set of agents we are testing AGAINST
		// Note: This is the same testing pool used by Walton-Rivers
		String[] testPoolNames = {"RuleBasedIGGI", "RuleBasedInternal","RuleBasedOuter","SampleLegalRandom","RuleBasedVanDeBergh","RuleBasedFlawed","RuleBasedPiers"};
		
		Vector<AgentPlayer> population = new Vector<AgentPlayer>();
		Vector<AgentPlayer> testPool = new Vector<AgentPlayer>();
		
		
		if (includeBaselineAgents) {
			for(String name: agentNames) {
				AgentPlayer newAgent = new AgentPlayer(name, AgentUtils.buildAgent(name));
				population.add(newAgent);
			}
		}
		
		// Read agents from agentChromossomes and add them to the population
		for (int i = 0; i < agentChromossomes.length; i++){
			int[] chromossome = agentChromossomes[i];
			Rule[] rules = new Rule [chromossome.length];
			for (int j= 0; j< chromossome.length; j++) {
				rules[j] = rb.ruleMapping(chromossome[j]);
			}
			HistogramAgent agent = rb.makeAgent(rules);
			population.add(new AgentPlayer ("Agent " + i, agent));
		}
		
		//Mixed
		int [] twoPlayerChromosomeMixed = {67, 69, 62, 57, 46, 8, 33, 36 ,19, 44, 54, 26}; 
		int [] threePlusPlayerChromosomeMixed = {46, 10, 7, 67, 45, 29, 4, 71, 17, 43, 19 };
		int [][] specializedChromosomeMixed = {twoPlayerChromosomeMixed,threePlusPlayerChromosomeMixed};
		SpecializedAgent specializedMixed = new SpecializedAgent(specializedChromosomeMixed, rb);
		


		//Mirror
		int [] twoPlayerChromosomeMirror = {68,7,63,8,67,66,52,35,36,51,33,13,54,16,48,37,69,19,4,5,44,61,27,49,34,39,18,46,0,59,65,29,3,23,70,24,58,38,42,32,6,56,9,14,41,30,11,45,43,21,2,15,1,50,26,62,28,57,22,64,20,10,31,55,40,47,60,71,17,53,12,25}; 
		int [] threePlusPlayerChromosomeMirror = {63,41,50,7,67,29,32,0,39,35,54,38,13,45,16,40,55,19,46,12,58,25,24,14,56,49,61,28,44,2,53,20,60,27,43,66,68,33,15,23,1,3,71,30,21,69,52,59,6,36,31,51,9,11,37,17,10,5,70,62,42,22,8,48,18,34,65,4,47,26,64,57};
		int [][] specializedChromosomeMirror = {twoPlayerChromosomeMirror,threePlusPlayerChromosomeMirror};
		SpecializedAgent specializedMirror = new SpecializedAgent(specializedChromosomeMirror, rb);
		
		population.add(new AgentPlayer ("Specialized Mixed", specializedMixed));
		population.add(new AgentPlayer ("Specialized Mirror", specializedMirror));

		
		
		for(String other: testPoolNames) {
			AgentPlayer otherAgent = new AgentPlayer(other, AgentUtils.buildAgent(other));
			testPool.add(otherAgent);
		}

		
		Vector<Vector<Map<Integer, Vector<Double>>>> populationResults = NewTestSuite.mixedPopulationEvaluation(population, testPool, minNumPlayers, maxNumPlayers, numGames);
		
		System.out.println("Printing full match list in mirror mode:");
		for (int playerID = 0; playerID < populationResults.size(); playerID++) {
			double sum = 0;
			int numMatches = 0;
			String playerName = population.get(playerID).toString();
			Vector<Map<Integer, Vector<Double>>> playerResults = populationResults.get(playerID);
			
			for (int partnerID = 0; partnerID < playerResults.size(); partnerID++) {
				Map<Integer, Vector<Double>> pairingResults = playerResults.get(partnerID);
				for (int gameSize = minNumPlayers; gameSize <= maxNumPlayers; gameSize++) {
					Vector<Double> matchResults = pairingResults.get(gameSize);
					for (double result : matchResults) {
						System.out.println(playerID + "," +partnerID + "," + gameSize + "," + result);
						sum+=result;
						numMatches++;
					}
				}
			}

		}
		
		boolean printSummary = true;
		if(printSummary) {
			System.out.println("");
			System.out.println("Summary of results by player:");
			
			double maxScore = -1;
			int bestPlayer = -1;
			
			for (int playerID = 0; playerID < populationResults.size(); playerID++) {
				System.out.println("Summary for player " + playerID);
				String playerName = population.get(playerID).toString();
				Vector<Map<Integer, Vector<Double>>> playerResults = populationResults.get(playerID);
				Vector<Double> individualResults = new Vector<Double>();

				
				double[] sumByGameSize = new double[6];
				int[] countByGameSize = new int[6];
				
				for (int partnerID = 0; partnerID < playerResults.size(); partnerID++) {
					Map<Integer, Vector<Double>> pairingResults = playerResults.get(partnerID);
					for (int gameSize = minNumPlayers; gameSize <= maxNumPlayers; gameSize++) {
						Vector<Double> matchResults = pairingResults.get(gameSize);
						for (double result : matchResults) {
							sumByGameSize[gameSize] += result;
							countByGameSize[gameSize] +=1;
							individualResults.add(result);
						}
					}
				}
				
				for (int gameSize = minNumPlayers; gameSize <= maxNumPlayers; gameSize++) {
					int  n = countByGameSize[gameSize];
					if (n !=0) {
						double sum  = sumByGameSize[gameSize];
						System.out.println("   Player " + playerID + " has a mean score of " + sum/n + " in games of size " + gameSize);

					}
				}
				
				double mean = Utils.Utils.getMean(individualResults);
				System.out.println("Player " + playerID + "'s mean score: " + mean );
				double SD = Utils.Utils.getStandardDeviation(individualResults);
				System.out.println("SD: " + SD + " SEM: " + SD/Math.sqrt(individualResults.size()));
				
				if (mean > maxScore) {
					maxScore = mean;
					bestPlayer = playerID;
				}	
				System.out.println("");

			}
			System.out.println("Best overall player is " + bestPlayer + " with a mean score of " + maxScore + " over all game sizes");
		}
		
		
		
//		PairingSummary stats = TestSuite.VariableNumberPlayersTest(agentName, otherAgent, maxNumPlayers, numGames );
//		
//		int n = 2;
//	
//		System.out.println(stats);
				
	}
}
