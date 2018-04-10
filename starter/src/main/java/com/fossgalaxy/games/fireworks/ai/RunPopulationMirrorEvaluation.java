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

public class RunPopulationMirrorEvaluation {
	public static void main( String[] args ) {
		int maxNumPlayers = 5; // Will play games of 2, 3, 4 and 5 players with a value of maxNumPlayers = 5
		int numGames = 100;
		
		String[] agentNames = { "RuleBasedIGGI", "RuleBasedInternal","RuleBasedOuter","SampleLegalRandom","RuleBasedVanDeBergh","RuleBasedFlawed","RuleBasedPiers"};
		
		Vector<AgentPlayer> population = new Vector<AgentPlayer>();
		
		for(String name: agentNames) {
			AgentPlayer newAgent = new AgentPlayer(name, AgentUtils.buildAgent(name));
			population.add(newAgent);
		}
		
		PopulationEvaluationSummary pes = TestSuite.mirrorPopulationEvaluation(population, maxNumPlayers, numGames);
		
		System.out.println(pes);
		
//		PairingSummary stats = TestSuite.VariableNumberPlayersTest(agentName, otherAgent, maxNumPlayers, numGames );
//		
//		int n = 2;
//	
//		System.out.println(stats);
				
	}
}
