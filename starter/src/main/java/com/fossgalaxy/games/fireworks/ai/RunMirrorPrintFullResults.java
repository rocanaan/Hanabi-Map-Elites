package com.fossgalaxy.games.fireworks.ai;

import java.util.Map;
import java.util.Vector;

import com.fossgalaxy.games.fireworks.ai.Agent;
import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
import com.fossgalaxy.games.fireworks.ai.rule.ProductionRuleAgent;
import com.fossgalaxy.games.fireworks.ai.rule.Rule;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;

import Evolution.Rulebase;

/*
 * This class evaluates an entire population in mirror tests
 */

public class RunMirrorPrintFullResults {
	public static void main( String[] args ) {
		int minNumPlayers = 2;
		int maxNumPlayers = 2; // Will play games of 2, 3, 4 and 5 players with a value of maxNumPlayers = 5
		int numGames = 20;
		boolean includeBaselineAgents = false;
		Rulebase rb = new Rulebase(false);
		
		int[][] agentChromossomes = {
				{8,10,76,38,28,7,73,45,68,22,70,11,72,65,42,92,69,89,90,96,80,54,34,58,46,84,98,75,18,57,59,55,71,82,83,85,52,25,81,53,40,79,2,5,63,9,30,37,64,27,60,56,15,67,86,47,78,35,12,17,41,0,48,23,31,19,36,62,6,94,97,66,77,26,20,93,33,100,50,102,43,91,51,3,95,99,88,4,101,1,13,103,61,32,21,14,16,87,24,49,44,104,29,39,74},
				{21,57,47,10,32,61,36,19,18,54,27,1,87,48,2,85,74,79,84,82,34,43,35,83,17,75,11,5,89,8,69,33,40,88,25,14,55,45,51,42,52,23,81,72,98,80,20,0,92,28,13,24,12,66,67,104,65,93,58,50,41,71,97,63,86,70,62,77,91,102,9,99,53,96,59,56,46,60,103,76,95,3,7,78,16,29,49,22,6,39,94,68,37,101,73,15,64,100,30,4,26,90,31,38,44},
				{16,2,70,77,97,75,102,33,47,65,42,62,101,74,76,36,35,7,27,78,58,17,92,64,94,43,12,49,53,69,5,55,68,14,86,3,83,19,61,38,57,90,91,84,31,99,45,48,52,98,89,34,0,21,73,60,96,15,13,28,41,88,81,56,79,104,23,9,40,29,80,67,20,100,10,51,8,26,11,103,1,39,87,72,24,50,63,85,95,30,66,6,32,18,46,54,59,4,93,44,82,22,25,71,37},
				{6,81,27,59,61,38,23,45,94,93,47,89,100,1,92,14,68,104,40,58,30,83,48,75,32,102,67,90,96,3,36,70,64,12,63,85,41,4,73,2,54,25,20,33,55,101,15,103,21,95,44,10,39,13,66,99,7,24,57,79,69,88,98,51,49,16,18,17,46,87,5,65,8,76,34,84,26,72,62,77,19,52,9,50,28,35,42,80,37,78,29,97,91,74,31,53,0,11,22,71,56,43,86,82,60},
				{1,8,67,72,98,74,42,16,33,61,21,96,36,26,71,3,78,22,15,54,69,87,104,52,27,40,34,89,17,94,66,6,44,73,35,64,51,25,100,62,56,102,80,48,77,55,60,7,63,93,5,19,39,101,18,99,70,45,11,49,31,75,24,84,41,82,92,79,90,46,91,86,88,95,10,43,30,76,57,59,47,28,13,29,83,58,14,50,9,4,12,37,85,81,0,23,65,32,53,2,20,97,103,68,38},
				{2,71,79,74,53,56,92,89,20,17,98,85,1,81,99,32,7,62,34,18,94,76,42,37,41,66,14,8,88,104,26,90,40,60,82,77,68,58,11,47,35,54,84,44,50,16,72,39,96,12,24,36,49,21,83,61,59,63,5,55,86,30,87,91,4,15,65,78,100,6,27,48,28,69,73,19,57,103,75,13,52,29,101,46,45,102,31,25,22,10,97,0,33,93,95,23,64,3,67,80,70,43,38,51,9},
				{71,8,102,67,62,53,1,28,33,61,98,15,23,56,5,32,48,50,45,55,40,101,99,34,74,75,49,9,104,13,57,30,89,93,95,103,72,19,70,79,86,12,84,92,68,44,11,91,100,73,46,97,10,59,24,22,82,90,51,63,87,43,21,6,4,78,65,7,25,35,76,36,58,60,14,29,64,3,27,39,69,2,94,96,37,81,83,66,80,0,77,26,38,85,52,88,31,20,18,16,17,42,41,54,47}
//				{68,7,63,8,67,66,52,35,36,51,33,13,54,16,48,37,69,19,4,5,44,61,27,49,34,39,18,46,0,59,65,29,3,23,70,24,58,38,42,32,6,56,9,14,41,30,11,45,43,21,2,15,1,50,26,62,28,57,22,64,20,10,31,55,40,47,60,71,17,53,12,25},
//				//Best Map Elites agent
//				{67,21,8,52,33,53,36,93,96,34,27,6,19,62,21} 
				//Individual (0.45,8) of run 1M
//				{67,21,8,52,33,53,36,93,96,34,27,6,19,62,21},
				//Individual (0.5,8) of run 1M
//				{67,21,8,52,33,53,36,93,96,51,27,6,19,62,21}
				//Agents from some evolution run - unsure
//				{46,8,2,13,25,38,1,36,27,26,12,17,14,11,48,19,15,3,10,33,30,34,44,29,49,6,28,47,37,39,9,43,45,42,21,22,24,23,35,20,4,40,41,7,32,0,5,31,16,18},
//				{7,46,1,8,47,13,33,36,12,45,41,40,15,27,24,11,26,23,42,31,49,28,6,43,0,22,20,38,21,9,37,32,29,16,39,4,19,5,34,3,30,48,25,35,10,2,18,14,17,44},
//				{8,46,2,13,1,38,25,36,26,12,5,11,19,15,3,10,33,30,34,29,44,6,28,37,39,43,21,27,24,23,35,20,4,41,7,32,14,16,47,17,48,9,0,22,45,18,42,40,49,31},
//				{46,37,41,8,47,2,13,35,36,18,14,17,5,49,39,42,27,45,19,48,7,25,11,1,32,33,20,3,23,22,4,6,16,21,15,31,12,43,40,10,44,24,0,34,9,38,29,26,30,28},
//				{41,46,8,47,35,10,13,2,36,18,14,17,33,44,7,27,24,12,11,26,23,42,25,9,1,49,3,6,43,0,22,20,38,21,15,31,45,40,37,32,29,16,39,4,19,5,34,28,30,48},
//				{1,8,47,13,33,36,29,21,17,34,28,12,26,19,41,32,3,2,20,15,45,48,31,42,39,27,46,6,16,38,14,5,37,24,40,18,7,23,44,10,0,30,49,35,11,22,43,9,4,25},  
//				{41,8,47,13,35,28,18,24,11,26,23,36,25,1,6,32,37,48,10,45,22,46,34,20,19,17,39,42,40,38,14,9,4,3,29,21,5,2,7,44,0,15,16,12,43,33,49,30,27,31},
//				{41,8,47,13,35,28,24,11,23,36,25,34,46,6,45,17,26,21,1,10,29,37,18,22,20,19,39,42,40,38,9,4,3,32,12,14,30,16,31,48,44,5,2,0,7,15,49,33,43,27},
//				{46,37,8,47,35,10,13,2,36,18,14,17,24,20,16,33,41,30,7,27,5,12,11,6,23,42,25,19,1,32,49,3,31,43,0,22,34,9,29,38,21,15,26,40,48,39,28,45,44,4},
//				{8,47,2,13,35,36,37,15,45,18,23,39,28,41,33,0,26,1,16,10,44,42,22,38,6,48,14,27,31,34,25,40,46,19,9,7,43,49,20,3,32,12,4,24,30,11,29,17,21,5}
		};
                
		String[] agentNames = { "RuleBasedIGGI", "RuleBasedInternal","RuleBasedOuter","RuleBasedLegalRandom","RuleBasedVanDeBergh","RuleBasedFlawed","RuleBasedPiers"};
		
		Vector<AgentPlayer> population = new Vector<AgentPlayer>();
		
		if (includeBaselineAgents) {
			for(String name: agentNames) {
				AgentPlayer newAgent = (new AgentPlayer(name, new HistogramAgent((ProductionRuleAgent) AgentUtils.buildAgent(name))));
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
		
//		int [] twoPlayerChromosomeMirror = {68,7,63,8,67,66,52,35,36,51,33,13,54,16,48,37,69,19,4,5,44,61,27,49,34,39,18,46,0,59,65,29,3,23,70,24,58,38,42,32,6,56,9,14,41,30,11,45,43,21,2,15,1,50,26,62,28,57,22,64,20,10,31,55,40,47,60,71,17,53,12,25}; 
//		int [] threePlusPlayerChromosomeMirror = {63,41,50,7,67,29,32,0,39,35,54,38,13,45,16,40,55,19,46,12,58,25,24,14,56,49,61,28,44,2,53,20,60,27,43,66,68,33,15,23,1,3,71,30,21,69,52,59,6,36,31,51,9,11,37,17,10,5,70,62,42,22,8,48,18,34,65,4,47,26,64,57};
//		int [][] specializedChromosomeMirror = {twoPlayerChromosomeMirror,threePlusPlayerChromosomeMirror};
//		SpecializedAgent specializedMirror = new SpecializedAgent(specializedChromosomeMirror, rb);
//		population.add(new AgentPlayer("Specialized Mirror",specializedMirror));
		
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

			for (AgentPlayer agent : population) {
				Agent a = agent.policy;
				if (a instanceof HistogramAgent) {
					HistogramAgent h = (HistogramAgent)a;
					h.printHistogram();
				}
			}
		}
		
		

		
		
//		PairingSummary stats = TestSuite.VariableNumberPlayersTest(agentName, otherAgent, maxNumPlayers, numGames );
//		
//		int n = 2;
//	
//		System.out.println(stats);
				
	}
}
