package com.fossgalaxy.games.fireworks.ai;

import java.util.Map;
import java.util.Vector;

import com.fossgalaxy.games.fireworks.ai.rule.Rule;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;

import Evolution.Rulebase;

/*
 * This class evaluates an entire population in mirror tests
 */

public class RunMixedPrintFullResultsSpecialized {
	public static void main( String[] args ) {
		int minNumPlayers = 2;
		int maxNumPlayers = 2;
		int numGames = 2000;
		boolean includeBaselineAgents = false;
		Rulebase rb = new Rulebase(false);
		int[][] agentChromossomes = {
				{56,13,8,33,36,17,45,14,34,53,1,7,2,58,23,12,22,25,24,64,15,41,55,26,9,31,3,71,69,40,20,11,65,47,42,51,66,35,32,18,54,62,27,10,44,29,39,49,59,4,60,48,50,46,21,5,38,30,28,6,52,63,19,57,37,68,70,43,67,16,0,61},
				{67,69,62,57,46,21,16,8,33,36,71,19,2,23,44,58,54,47,26,7,25,14,60,22,5,70,39,20,49,63,29,64,38,56,51,12,32,66,17,45,0,11,30,34,28,15,13,42,65,52,4,43,35,37,1,3,10,48,24,53,68,50,59,6,61,41,31,9,18,27,55,40},
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
		
                Agent[] agents = new Agent[2];
                for (int i = 0; i < agentChromossomes.length; i++){
			int[] chromossome = agentChromossomes[i];
			Rule[] rules = new Rule [chromossome.length];
			for (int j= 0; j< chromossome.length; j++) {
				rules[j] = rb.ruleMapping(chromossome[j]);
			}
			HistogramAgent agent = rb.makeAgent(rules);
			agents[i]=agent;
		}
                SpecializedAgent sa = new SpecializedAgent(agents);
                population.add(new AgentPlayer ("Specialized Agent", sa));
                
		// Read agents from agentChromossomes and add them to the population
//		for (int i = 0; i < agentChromossomes.length; i++){
//			int[] chromossome = agentChromossomes[i];
//			Rule[] rules = new Rule [chromossome.length];
//			for (int j= 0; j< chromossome.length; j++) {
//				rules[j] = rb.ruleMapping(chromossome[j]);
//			}
//			HistogramAgent agent = rb.makeAgent(rules);
//			population.add(new AgentPlayer ("Agent " + i, agent));
//		}
		
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
