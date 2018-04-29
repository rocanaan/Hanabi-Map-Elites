package com.fossgalaxy.games.fireworks.ai;

import java.util.Random;
import java.util.Vector;

import com.fossgalaxy.games.fireworks.GameRunner;
import com.fossgalaxy.games.fireworks.GameStats;
import com.fossgalaxy.games.fireworks.players.Player;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;
import com.fossgalaxy.stats.BasicStats;
import com.fossgalaxy.stats.StatsSummary;

public class TestSuite {
	
	/* This method performs a number of simulation of a game where
	* one player is yourAgent and all other players are otherAgent.
	* The number of players is kept constant
	*/
//	public static StatsSummary ConstantNumberPlayersTest (int numPlayers, int numGames, AgentPlayer yourAgent, Vector<AgentPlayer> otherAgents) {
//		Random random = new Random();
//        StatsSummary statsSummary = new BasicStats();
//
//        for (int i=0; i<numGames; i++) {
//            GameRunner runner = new GameRunner("test-game", numPlayers);
//            
//            // Adds your agent to the game
//            runner.addPlayer(yourAgent);
//            
//            //add N-1 copies of other agent to the game
//            for (int j = 2; j<=numPlayers;j++)
//            {
//            		runner.addPlayer(otherAgents.get(j-2));
//            }
//            
////            // Adds your agent to the game
////            Player player = new AgentPlayer(yourAgentName, AgentUtils.buildAgent(yourAgentName));
////
////            //add N-1 other agents to the game
////            for (int j=0; j<numPlayers; j++) {
////                // the player class keeps track of our state for us...
////            		if (otherAgentName != "") {
////            			player = new AgentPlayer(yourAgentName, AgentUtils.buildAgent(otherAgentName));
////            		}
////                runner.addPlayer(player);
////            }
//
//            GameStats stats = runner.playGame(random.nextLong());
//            statsSummary.add(stats.score);
//            System.out.println("Game number " + i + " complete");
//        }
//
//        return statsSummary;
//	}
	
	public static StatsSummary ConstantNumberPlayersTest (int numPlayers, int numGames, AgentPlayer yourAgent, AgentPlayer otherAgent, Random random) {
        StatsSummary statsSummary = new BasicStats();
        
        boolean randomizePosition = true;

        for (int i=0; i<numGames; i++) {
            GameRunner runner = new GameRunner("test-game", numPlayers);
            
            int playerPosition = 0;
            if (randomizePosition) {
            		playerPosition = random.nextInt(numPlayers);
            }
            
            for (int pos = 0; pos<numPlayers; pos++ ) {
            		if (pos == playerPosition) {
            			// Adds your agent to the game
                     runner.addPlayer(yourAgent);
            		}
	            else {
	            	// Adds the other 
	            		runner.addPlayer(new AgentPlayer(otherAgent.getName(),otherAgent.policy));
	            }
            }
            // Adds your agent to the game
            
//            // Adds your agent to the game
//            Player player = new AgentPlayer(yourAgentName, AgentUtils.buildAgent(yourAgentName));
//
//            //add N-1 other agents to the game
//            for (int j=0; j<numPlayers; j++) {
//                // the player class keeps track of our state for us...
//            		if (otherAgentName != "") {
//            			player = new AgentPlayer(yourAgentName, AgentUtils.buildAgent(otherAgentName));
//            		}
//                runner.addPlayer(player);
//            }


            GameStats stats = runner.playGame(random.nextLong());
            statsSummary.add(stats.score);
           // System.out.println("Game number " + i + " complete");
        }

        return statsSummary;
	}
	
//	
//	public static PairingSummary VariableNumberPlayersTest (AgentPlayer yourAgent, String yourAgentName, Vector<AgentPlayer> otherAgents, String otherAgentName, int maxNumPlayers, int numGames) {
//		PairingSummary pairingStats = new PairingSummary(yourAgentName, otherAgentName, maxNumPlayers, numGames);
//		
//		for (int i = 2; i <= maxNumPlayers; i++)
//		{
//			StatsSummary stats = ConstantNumberPlayersTest(i, numGames, yourAgent, otherAgents);
//			pairingStats.results.add(stats);
//		}
//		
//		return pairingStats;
//	}
	
	public static PairingSummary VariableNumberPlayersTest (AgentPlayer yourAgent, AgentPlayer otherAgent, int maxNumPlayers, int numGames, Random random) {
	PairingSummary pairingStats = new PairingSummary(yourAgent, otherAgent, maxNumPlayers, numGames);
	
	for (int i = 2; i <= maxNumPlayers; i++)
	{
		StatsSummary stats = ConstantNumberPlayersTest(i, numGames, yourAgent, otherAgent, random);
		pairingStats.results.add(stats);
	}
	
	return pairingStats;
}

	
	
	public static PopulationEvaluationSummary mirrorPopulationEvaluation(Vector<AgentPlayer> population, int maxNumPlayers, int numGames) {
		PopulationEvaluationSummary pes = new PopulationEvaluationSummary(true, population, null);
		
		Random random = new Random();
		Long seed = random.nextLong();
		
		
		for (AgentPlayer agent: population) {
			random.setSeed(seed);
			PairingSummary summary = VariableNumberPlayersTest(agent, agent, maxNumPlayers, numGames,random);
			
			AgentMultiPairingSummary amps = new AgentMultiPairingSummary(true, agent, null);
			amps.pairings.addElement(summary);
			pes.pairings.add(amps);

		}
		
		return pes;
		
	}
	
	
	public static PopulationEvaluationSummary mixedPopulationEvaluation(Vector<AgentPlayer> population, Vector<AgentPlayer> testPool, int maxNumPlayers, int numGames) {
		PopulationEvaluationSummary pes = new PopulationEvaluationSummary(true, population, null);
		
		Random random = new Random();
		Long seed = random.nextLong();
		
		for (AgentPlayer agent: population) {
			random.setSeed(seed);
			AgentMultiPairingSummary amps = new AgentMultiPairingSummary(false, agent, testPool);

			for (AgentPlayer other: testPool) {
				PairingSummary summary = VariableNumberPlayersTest(agent, other, maxNumPlayers, numGames, random);
				amps.pairings.addElement(summary);
			}
			pes.pairings.add(amps);


		}
		
		return pes;
		
	}
	
	
	


}
