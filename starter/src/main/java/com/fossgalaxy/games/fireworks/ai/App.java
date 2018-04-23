package com.fossgalaxy.games.fireworks.ai;

import com.fossgalaxy.games.fireworks.GameRunner;
import com.fossgalaxy.games.fireworks.GameStats;
import com.fossgalaxy.games.fireworks.ai.AgentPlayer;
import com.fossgalaxy.games.fireworks.ai.mcts.MCTSPredictor;
import com.fossgalaxy.games.fireworks.players.Player;
import com.fossgalaxy.games.fireworks.utils.AgentUtils;
import com.fossgalaxy.stats.BasicStats;
import com.fossgalaxy.stats.StatsSummary;

import java.util.Random;

/**
 * Game runner for testing.
 *
 * This will run a bunch of games with your agent so you can see how it does.
 */
public class App 
{
    public static void main( String[] args )
    {
        int numPlayers = 5;
        int numGames = 1;
        //String agentName = "SampleAgent";
        String agentName = "HumanControlledStateEvaluator";
        String otherAgentName = "pmctsND";
        String modelName = "RuleBasedPiers";

        Random random = new Random();
        StatsSummary statsSummary = new BasicStats();
        
        GameRunner runner = new GameRunner("test-game", numPlayers);
        
        
        
        Agent a1 = AgentUtils.buildAgent(agentName);
        Player player = new AgentPlayer(agentName, AgentUtils.buildAgent(agentName));
        runner.addPlayer(player);
        
        Agent a2 = AgentUtils.buildAgent(modelName);

        for (int i=0; i<numGames; i++) {
            //add your agents to the game
            Agent[] agents = new Agent[numPlayers];
            for (int j = 0; j<numPlayers; j++) {
            		agents[j]=a2;
            }
            for (int j=1; j<numPlayers; j++) {
                // the player class keeps track of our state for us...
                
                Agent mctsAgent = new MCTSPredictor(agents);
                
                player = new AgentPlayer(otherAgentName, mctsAgent);
                runner.addPlayer(player);
            }

            GameStats stats = runner.playGame(random.nextLong());
            statsSummary.add(stats.score);
            System.out.println("Game number " + i + " complete");
        }

        //print out the stats
        System.out.println("Stats for agent " +  agentName);
        System.out.println(String.format("Our agent: Avg: %f, min: %f, max: %f",
                statsSummary.getMean(),
                statsSummary.getMin(),
                statsSummary.getMax()));
    }
}
