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
		int maxNumPlayers = 5;
		int numGames = 1000;
		boolean includeBaselineAgents = true;
		Rulebase rb = new Rulebase(false);
		int[][] agentChromossomes = {
				{46,8,2,13,25,38,1,36,27,26,12,17,14,11,48,19,15,3,10,33,30,34,44,29,49,6,28,47,37,39,9,43,45,42,21,22,24,23,35,20,4,40,41,7,32,0,5,31,16,18},
				{62,8,40,54,68,52,33,36,14,53,23,21,15,57,43,63,22,49,69,30,10,65,35,20,29,25,12,31,0,56,70,27,11,1,18,39,45,42,17,66,48,67,50,32,13,5,60,7,58,38,28,41,47,9,34,19,6,51,71,55,44,26,4,24,16,3,37,46,2,59,61,64},
				{61,64,8,40,62,68,52,33,36,14,53,23,21,15,57,43,63,22,49,69,30,10,65,35,20,29,25,12,31,0,56,70,27,11,1,54,18,39,45,42,17,3,26,67,50,6,13,5,60,7,58,28,38,41,47,9,34,19,32,51,71,55,44,48,4,24,16,66,37,46,2,59}
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
			HistogramAgent agent = Rulebase.makeAgent(rules);
			population.add(new AgentPlayer ("Agent " + i, agent));
		}
		
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
