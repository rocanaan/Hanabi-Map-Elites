package com.fossgalaxy.games.fireworks.ai;

import java.util.Random;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;

public class RunSinglePairingEvaluation {
	public static void main( String[] args ) {
		int minNumPlayers = 2;
		int maxNumPlayers = 2;
		int numGames = 1000;
		
		String yourAgentName = "RuleBasedPiers";
		String otherAgentName = "RuleBasedPiers";
		
		
		
	//	Vector<AgentPlayer> otherAgents = new Vector<AgentPlayer>();
		
		AgentPlayer yourAgent = new AgentPlayer(yourAgentName, AgentUtils.buildAgent(yourAgentName));
//		for (int i = 2; i<=maxNumPlayers; i++) {
//			otherAgents.add(new AgentPlayer(otherAgentName, AgentUtils.buildAgent(otherAgentName)));
//		}
		
		AgentPlayer otherAgent = new AgentPlayer(otherAgentName, AgentUtils.buildAgent(otherAgentName));
		
		PairingSummary stats = TestSuite.VariableNumberPlayersTest( yourAgent,  otherAgent, minNumPlayers, maxNumPlayers,  numGames, new Random());

		System.out.println(stats);
				
	}
}
