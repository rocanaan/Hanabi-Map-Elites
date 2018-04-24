package com.fossgalaxy.games.fireworks.ai;

import com.fossgalaxy.games.fireworks.ai.osawa.rules.OsawaDiscard;
import com.fossgalaxy.games.fireworks.ai.rule.DiscardOldestFirst;
import com.fossgalaxy.games.fireworks.ai.rule.PlaySafeCard;
import com.fossgalaxy.games.fireworks.ai.rule.ProductionRuleAgent;
import com.fossgalaxy.games.fireworks.ai.rule.TellAnyoneAboutUsefulCard;
import com.fossgalaxy.games.fireworks.ai.rule.random.PlayProbablySafeCard;
import com.fossgalaxy.games.fireworks.ai.rule.simple.PlayIfCertain;

import java.util.Vector;

import com.fossgalaxy.games.fireworks.GameRunner;
import com.fossgalaxy.games.fireworks.GameStats;
import com.fossgalaxy.games.fireworks.players.Player;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;
import com.fossgalaxy.stats.BasicStats;
import com.fossgalaxy.stats.StatsSummary;

public class EvolutionaryAlgorithm {
	public static void main( String[] args ) {
		int maxNumPlayers = 5;
		int numGames = 100;
		
		ProductionRuleAgent pra = new ProductionRuleAgent();
		
        pra.addRule(new PlayIfCertain());
//        pra.addRule(new PlaySafeCard());
//        pra.addRule(new TellAnyoneAboutUsefulCard());
//        pra.addRule(new OsawaDiscard());
//        pra.addRule(new DiscardOldestFirst());

		AgentPlayer agent = new AgentPlayer("test IGGI", pra);
		
		Vector<AgentPlayer> agentList = new Vector<AgentPlayer>();
		
		agentList.add(agent);
		
	//	Vector<AgentPlayer> otherAgents = new Vector<AgentPlayer>();
		
		//AgentPlayer yourAgent = new AgentPlayer(yourAgentName, AgentUtils.buildAgent(yourAgentName));
//		for (int i = 2; i<=maxNumPlayers; i++) {
//			otherAgents.add(new AgentPlayer(otherAgentName, AgentUtils.buildAgent(otherAgentName)));
//		}
		
		//AgentPlayer otherAgent = new AgentPlayer("other test IGGI", pra);
		
		Vector<AgentPlayer> testPool = new Vector<AgentPlayer>();
		
		
		String[] testPoolNames = {"RuleBasedIGGI", "RuleBasedInternal","RuleBasedOuter","SampleLegalRandom","RuleBasedVanDeBergh","RuleBasedFlawed","RuleBasedPiers"};
		
		for(String other: testPoolNames) {
			AgentPlayer otherAgent = new AgentPlayer(other, AgentUtils.buildAgent(other));
			testPool.add(otherAgent);
		}
		
		PopulationEvaluationSummary stats = TestSuite.mixedPopulationEvaluation(agentList, testPool, maxNumPlayers, numGames);

		System.out.println(stats);
				
	}
}
