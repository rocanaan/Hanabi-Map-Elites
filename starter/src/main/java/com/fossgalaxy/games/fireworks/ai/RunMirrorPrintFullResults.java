package com.fossgalaxy.games.fireworks.ai;

import java.util.Map;
import java.util.Vector;

import com.fossgalaxy.games.fireworks.ai.rule.Rule;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;

import Evolution.Rulebase;

/*
 * This class evaluates an entire population in mirror tests
 */

public class RunMirrorPrintFullResults {
	public static void main( String[] args ) {
		int minNumPlayers = 2;
		int maxNumPlayers = 5; // Will play games of 2, 3, 4 and 5 players with a value of maxNumPlayers = 5
		int numGames = 2000;
		boolean includeBaselineAgents = true;
		Rulebase rb = new Rulebase(false);
		
		int[][] agentChromossomes = {
				{46,8,2,13,25,38,1,36,27,26,12,17,14,11,48,19,15,3,10,33,30,34,44,29,49,6,28,47,37,39,9,43,45,42,21,22,24,23,35,20,4,40,41,7,32,0,5,31,16,18},
				{7,46,1,8,47,13,33,36,12,45,41,40,15,27,24,11,26,23,42,31,49,28,6,43,0,22,20,38,21,9,37,32,29,16,39,4,19,5,34,3,30,48,25,35,10,2,18,14,17,44},
				{8,46,2,13,1,38,25,36,26,12,5,11,19,15,3,10,33,30,34,29,44,6,28,37,39,43,21,27,24,23,35,20,4,41,7,32,14,16,47,17,48,9,0,22,45,18,42,40,49,31},
				{46,37,41,8,47,2,13,35,36,18,14,17,5,49,39,42,27,45,19,48,7,25,11,1,32,33,20,3,23,22,4,6,16,21,15,31,12,43,40,10,44,24,0,34,9,38,29,26,30,28},
				{41,46,8,47,35,10,13,2,36,18,14,17,33,44,7,27,24,12,11,26,23,42,25,9,1,49,3,6,43,0,22,20,38,21,15,31,45,40,37,32,29,16,39,4,19,5,34,28,30,48},
				{1,8,47,13,33,36,29,21,17,34,28,12,26,19,41,32,3,2,20,15,45,48,31,42,39,27,46,6,16,38,14,5,37,24,40,18,7,23,44,10,0,30,49,35,11,22,43,9,4,25},  
				{41,8,47,13,35,28,18,24,11,26,23,36,25,1,6,32,37,48,10,45,22,46,34,20,19,17,39,42,40,38,14,9,4,3,29,21,5,2,7,44,0,15,16,12,43,33,49,30,27,31},
				{41,8,47,13,35,28,24,11,23,36,25,34,46,6,45,17,26,21,1,10,29,37,18,22,20,19,39,42,40,38,9,4,3,32,12,14,30,16,31,48,44,5,2,0,7,15,49,33,43,27},
				{46,37,8,47,35,10,13,2,36,18,14,17,24,20,16,33,41,30,7,27,5,12,11,6,23,42,25,19,1,32,49,3,31,43,0,22,34,9,29,38,21,15,26,40,48,39,28,45,44,4},
				{8,47,2,13,35,36,37,15,45,18,23,39,28,41,33,0,26,1,16,10,44,42,22,38,6,48,14,27,31,34,25,40,46,19,9,7,43,49,20,3,32,12,4,24,30,11,29,17,21,5}
		};
                
		String[] agentNames = { "RuleBasedIGGI", "RuleBasedInternal","RuleBasedOuter","SampleLegalRandom","RuleBasedVanDeBergh","RuleBasedFlawed","RuleBasedPiers"};
		
		Vector<AgentPlayer> population = new Vector<AgentPlayer>();
		
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
