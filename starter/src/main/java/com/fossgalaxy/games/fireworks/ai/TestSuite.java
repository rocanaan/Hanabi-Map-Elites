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
	* one player is agentName and all other players are otherAgent.
	* The number of players is kept constant
	*/
	public static StatsSummary ConstantNumberPlayersTest (int numPlayers, int numGames, String agentName, String otherAgent) {
		Random random = new Random();
        StatsSummary statsSummary = new BasicStats();

        for (int i=0; i<numGames; i++) {
            GameRunner runner = new GameRunner("test-game", numPlayers);
            
            // Adds your agent to the game
            Player player = new AgentPlayer(agentName, AgentUtils.buildAgent(agentName));

            //add N-1 other agents to the game
            for (int j=0; j<numPlayers; j++) {
                // the player class keeps track of our state for us...
            		if (otherAgent != "") {
            			player = new AgentPlayer(agentName, AgentUtils.buildAgent(otherAgent));
            		}
                runner.addPlayer(player);
            }

            GameStats stats = runner.playGame(random.nextLong());
            statsSummary.add(stats.score);
            System.out.println("Game number " + i + " complete");
        }

        return statsSummary;
	}
	
	public static Vector<StatsSummary> VariableNumberPlayersTest (int maxNumPlayers, int numGames, String agentName, String otherAgent) {
		Vector<StatsSummary> pairingStats = new Vector<StatsSummary>(maxNumPlayers-1);
		
		for (int i = 2; i <= maxNumPlayers; i++)
		{
			StatsSummary stats = ConstantNumberPlayersTest(i, numGames, agentName, otherAgent);
			pairingStats.add(stats);
		}
		
		return pairingStats;
	}

}
