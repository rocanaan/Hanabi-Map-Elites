package com.fossgalaxy.games.fireworks.ai;

import java.util.Map;
import java.util.Vector;

import com.fossgalaxy.games.fireworks.ai.rule.Rule;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;

import Evolution.Rulebase;

/*
 * This class evaluates an entire population in mirror tests
 */

public class RunMirrorPrintFullResultsSpecialized {
	public static void main( String[] args ) {
		int minNumPlayers = 2;
		int maxNumPlayers = 5; // Will play games of 2, 3, 4 and 5 players with a value of maxNumPlayers = 5
		int numGames = 2000;
		boolean includeBaselineAgents = false;
		Rulebase rb = new Rulebase(false);
		
		int[][] agentChromossomes = {
				{68,7,63,8,67,66,52,35,36,51,33,13,54,16,48,37,69,19,4,5,44,61,27,49,34,39,18,46,0,59,65,29,3,23,70,24,58,38,42,32,6,56,9,14,41,30,11,45,43,21,2,15,1,50,26,62,28,57,22,64,20,10,31,55,40,47,60,71,17,53,12,25},
				{63,41,50,7,67,29,32,0,39,35,54,38,13,45,16,40,55,19,46,12,58,25,24,14,56,49,61,28,44,2,53,20,60,27,43,66,68,33,15,23,1,3,71,30,21,69,52,59,6,36,31,51,9,11,37,17,10,5,70,62,42,22,8,48,18,34,65,4,47,26,64,57},
                            };
                
		String[] agentNames = { "RuleBasedIGGI", "RuleBasedInternal","RuleBasedOuter","SampleLegalRandom","RuleBasedVanDeBergh","RuleBasedFlawed","RuleBasedPiers"};
		
		Vector<AgentPlayer> population = new Vector<AgentPlayer>();
		
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
		
//		// Best agent on the GA
//		int[] chromossome1 = {8,13,28,6,33,35,20,38,37,23,30,26,9,0,14,17,10,21,25,4,15,16,11,31,3,5,29,36,7,27,34,12,2,39,19,40,18,24,1,32,22};
//		Rule[] rules1 = new Rule[chromossome1.length];
//		for (int i = 0; i < chromossome1.length; i++) {
//			rules1[i] = rb.ruleMapping(chromossome1[i]);
//		}
//		HistogramAgent evolvedHAgent = rb.makeAgent(rules1);
//		population.add(new AgentPlayer("evolvedAgent", evolvedHAgent));
//		
//		// Variation putting Osawa discard (38) before oldestNoInfo (28)
//		int[] chromossome2 = {8,13,38,28,6,33,35,20,37,23,30,26,9,0,14,17,10,21,25,4,15,16,11,31,3,5,29,36,7,27,34,12,2,39,19,40,18,24,1,32,22};
//		Rule[] rules2 = new Rule[chromossome2.length];
//		for (int i = 0; i < chromossome2.length; i++) {
//			rules2[i] = rb.ruleMapping(chromossome2[i]);
//		}
//		HistogramAgent variantHAgent = rb.makeAgent(rules2);
//		population.add(new AgentPlayer("variantAgent", variantHAgent));
//		
//		// Variation putting Osawa discard (38) before oldestNoInfo (28) and add hailMary
//		int[] chromossome3 = {42,8,13,38,28,6,33,35,20,37,23,30,26,9,0,14,17,10,21,25,4,15,16,11,31,3,5,29,36,7,27,34,12,2,39,19,40,18,24,1,32,22};
//		Rule[] rules3 = new Rule[chromossome2.length+1];
//		for (int i = 0; i < chromossome3.length; i++) {
//			rules3[i] = rb.ruleMapping(chromossome3[i]);
//		}
//		HistogramAgent HailMaryHAgent = rb.makeAgent(rules1);
//		population.add(new AgentPlayer("Hail Mary", HailMaryHAgent));
		
		Vector<Map<Integer, Vector<Double>>> populationResults = NewTestSuite.mirrorPopulationEvaluation(population, minNumPlayers, maxNumPlayers, numGames);
	
		System.out.println("Printing full match list in mirror mode:");
		for (int playerID = 0; playerID < populationResults.size(); playerID++) {
			Map<Integer, Vector<Double>> playerResults = populationResults.get(playerID);
			String playerName = population.get(playerID).toString();
			for (int gameSize = minNumPlayers; gameSize <= maxNumPlayers; gameSize++) {
				Vector<Double> matchResults = playerResults.get(gameSize);
				for (double result : matchResults) {
					System.out.println(playerID + ",self," + gameSize + "," + result);
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
				Vector<Double> individualResults = new Vector<Double>();
				Map<Integer, Vector<Double>> playerResults = populationResults.get(playerID);
				String playerName = population.get(playerID).toString();
				for (int gameSize = minNumPlayers; gameSize <= maxNumPlayers; gameSize++) {
					double sum = 0;
					int n = 0;
					Vector<Double> matchResults = playerResults.get(gameSize);
					for (double result : matchResults) {
						individualResults.add(result);
						sum+=result;
						n++;
					}
					if(n!=0) {
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
