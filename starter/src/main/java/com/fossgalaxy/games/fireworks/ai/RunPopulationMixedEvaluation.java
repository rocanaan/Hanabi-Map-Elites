package com.fossgalaxy.games.fireworks.ai;

import java.util.Vector;

import com.fossgalaxy.games.fireworks.GameRunner;
import com.fossgalaxy.games.fireworks.GameStats;
import com.fossgalaxy.games.fireworks.players.Player;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;
import com.fossgalaxy.stats.BasicStats;
import com.fossgalaxy.stats.StatsSummary;

/*
 * This class evaluates an entire population in mirror tests
 */

public class RunPopulationMixedEvaluation {
	public static void main( String[] args ) {
		int maxNumPlayers = 5;
		int numGames = 1000;
		
		// This is the population of agents we are evaluating
		String[] agentNames = {"RuleBasedIGGI", "RuleBasedInternal","RuleBasedOuter","SampleLegalRandom","RuleBasedVanDeBergh","RuleBasedFlawed","RuleBasedPiers", "PiersNoDispensableTell", "PiersDispensableAfterOsawa"};
		
		// This is the set of agents we are testing AGAINST
		// Note: This is the same testing pool used by Walton-Rivers
		String[] testPoolNames = {"RuleBasedIGGI", "RuleBasedInternal","RuleBasedOuter","SampleLegalRandom","RuleBasedVanDeBergh","RuleBasedFlawed","RuleBasedPiers"};
		
		Vector<AgentPlayer> population = new Vector<AgentPlayer>();
		Vector<AgentPlayer> testPool = new Vector<AgentPlayer>();
		
		for(String name: agentNames) {
			AgentPlayer newAgent = new AgentPlayer(name, AgentUtils.buildAgent(name));
			population.add(newAgent);
		}
		
		for(String other: testPoolNames) {
			AgentPlayer otherAgent = new AgentPlayer(other, AgentUtils.buildAgent(other));
			testPool.add(otherAgent);
		}

		
		PopulationEvaluationSummary pes = TestSuite.mixedPopulationEvaluation(population, testPool, maxNumPlayers, numGames);
		
		System.out.println(pes);
		
//		PairingSummary stats = TestSuite.VariableNumberPlayersTest(agentName, otherAgent, maxNumPlayers, numGames );
//		
//		int n = 2;
//	
//		System.out.println(stats);
				
	}
}
