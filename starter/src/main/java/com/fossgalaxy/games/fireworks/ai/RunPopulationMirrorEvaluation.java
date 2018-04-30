package com.fossgalaxy.games.fireworks.ai;

import java.util.Vector;

import com.fossgalaxy.games.fireworks.ai.rule.Rule;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;

import Evolution.Rulebase;

/*
 * This class evaluates an entire population in mirror tests
 */

public class RunPopulationMirrorEvaluation {
	public static void main( String[] args ) {
		int minNumPlayers = 2;
		int maxNumPlayers = 5; // Will play games of 2, 3, 4 and 5 players with a value of maxNumPlayers = 5
		int numGames = 20;
		Rulebase rb = new Rulebase(false);
                
		String[] agentNames = { "RuleBasedIGGI", "RuleBasedInternal","RuleBasedOuter","SampleLegalRandom","RuleBasedVanDeBergh","RuleBasedFlawed","RuleBasedPiers"};
		
		Vector<AgentPlayer> population = new Vector<AgentPlayer>();
		
		for(String name: agentNames) {
			AgentPlayer newAgent = new AgentPlayer(name, AgentUtils.buildAgent(name));
			population.add(newAgent);
		}
		
		// Best agent on the GA
		int[] chromossome1 = {8,13,28,6,33,35,20,38,37,23,30,26,9,0,14,17,10,21,25,4,15,16,11,31,3,5,29,36,7,27,34,12,2,39,19,40,18,24,1,32,22};
		Rule[] rules1 = new Rule[chromossome1.length];
		for (int i = 0; i < chromossome1.length; i++) {
			rules1[i] = rb.ruleMapping(chromossome1[i]);
		}
		HistogramAgent evolvedHAgent = rb.makeAgent(rules1);
		population.add(new AgentPlayer("evolvedAgent", evolvedHAgent));
		
		// Variation putting Osawa discard (38) before oldestNoInfo (28)
		int[] chromossome2 = {8,13,38,28,6,33,35,20,37,23,30,26,9,0,14,17,10,21,25,4,15,16,11,31,3,5,29,36,7,27,34,12,2,39,19,40,18,24,1,32,22};
		Rule[] rules2 = new Rule[chromossome2.length];
		for (int i = 0; i < chromossome2.length; i++) {
			rules2[i] = rb.ruleMapping(chromossome2[i]);
		}
		HistogramAgent variantHAgent = rb.makeAgent(rules2);
		population.add(new AgentPlayer("variantAgent", variantHAgent));
		
		// Variation putting Osawa discard (38) before oldestNoInfo (28) and add hailMary
		int[] chromossome3 = {42,8,13,38,28,6,33,35,20,37,23,30,26,9,0,14,17,10,21,25,4,15,16,11,31,3,5,29,36,7,27,34,12,2,39,19,40,18,24,1,32,22};
		Rule[] rules3 = new Rule[chromossome2.length+1];
		for (int i = 0; i < chromossome3.length; i++) {
			rules3[i] = rb.ruleMapping(chromossome3[i]);
		}
		HistogramAgent HailMaryHAgent = rb.makeAgent(rules1);
		population.add(new AgentPlayer("Hail Mary", HailMaryHAgent));
		
		PopulationEvaluationSummary pes = TestSuite.mirrorPopulationEvaluation(population, minNumPlayers, maxNumPlayers, numGames);
	
		
		System.out.println(pes);
		
		evolvedHAgent.printHistogram();
		variantHAgent.printHistogram();
		HailMaryHAgent.printHistogram();
		
//		PairingSummary stats = TestSuite.VariableNumberPlayersTest(agentName, otherAgent, maxNumPlayers, numGames );
//		
//		int n = 2;
//	
//		System.out.println(stats);
				
	}
}
