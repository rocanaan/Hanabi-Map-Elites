package com.fossgalaxy.games.fireworks.ai;

import java.util.Random;
import java.util.Vector;

import com.fossgalaxy.games.fireworks.GameRunner;
import com.fossgalaxy.games.fireworks.GameStats;
import com.fossgalaxy.games.fireworks.players.Player;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;
import com.fossgalaxy.stats.BasicStats;
import com.fossgalaxy.stats.StatsSummary;

public class RunSinglePairingEvaluation {
	public static void main( String[] args ) {
		int maxNumPlayers = 5;
		int numGames = 100;
		
		String yourAgentName = "RuleBasedIGGI";
		String otherAgentName = "RuleBasedFlawed";
		
		
		
	//	Vector<AgentPlayer> otherAgents = new Vector<AgentPlayer>();
		
		AgentPlayer yourAgent = new AgentPlayer(yourAgentName, AgentUtils.buildAgent(yourAgentName));
//		for (int i = 2; i<=maxNumPlayers; i++) {
//			otherAgents.add(new AgentPlayer(otherAgentName, AgentUtils.buildAgent(otherAgentName)));
//		}
		
		AgentPlayer otherAgent = new AgentPlayer(otherAgentName, AgentUtils.buildAgent(otherAgentName));
		
		PairingSummary stats = TestSuite.VariableNumberPlayersTest( yourAgent,  otherAgent,  maxNumPlayers,  numGames, new Random());

		System.out.println(stats);
				
	}
}
