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
		int numGames = 1000;
		boolean includeBaselineAgents = false;
		Rulebase rb = new Rulebase(false);
		
		int[][] agentChromossomes = {
				{46,8,2,13,25,38,1,36,27,26,12,17,14,11,48,19,15,3,10,33,30,34,44,29,49,6,28,47,37,39,9,43,45,42,21,22,24,23,35,20,4,40,41,7,32,0,5,31,16,18},
				{62,8,40,54,68,52,33,36,14,53,23,21,15,57,43,63,22,49,69,30,10,65,35,20,29,25,12,31,0,56,70,27,11,1,18,39,45,42,17,66,48,67,50,32,13,5,60,7,58,38,28,41,47,9,34,19,6,51,71,55,44,26,4,24,16,3,37,46,2,59,61,64},
				{61,64,8,40,62,68,52,33,36,14,53,23,21,15,57,43,63,22,49,69,30,10,65,35,20,29,25,12,31,0,56,70,27,11,1,54,18,39,45,42,17,3,26,67,50,6,13,5,60,7,58,28,38,41,47,9,34,19,32,51,71,55,44,48,4,24,16,66,37,46,2,59}
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
			HistogramAgent agent = Rulebase.makeAgent(rules);
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
