package com.fossgalaxy.games.fireworks.ai;

import com.fossgalaxy.games.fireworks.GameRunner;
import com.fossgalaxy.games.fireworks.GameStats;
import com.fossgalaxy.games.fireworks.ai.username.*;
import com.fossgalaxy.games.fireworks.ai.mcts.MCTSPredictor;
import com.fossgalaxy.games.fireworks.ai.rule.ProductionRuleAgent;
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
public class HistogramResults 
{
    public static void main( String[] args )
    {
        int numPlayers = 2;
        int numGames = 10;

        Random random = new Random();
        StatsSummary statsSummary = new BasicStats();
        
        // build ProductionRuleAgent
        // make sure that the return type of buildRuleBased() is ProductionRuleAgent
        ProductionRuleAgent a1 = RuleBasedPiers.buildRuleBased();
        // build HistogramAgent based on the ProductionRuleAgent
        HistogramAgent agent1 = new HistogramAgent(a1);
        
        ProductionRuleAgent a2 = RuleBasedVanDeBergh.buildRuleBased();
        HistogramAgent agent2 = new HistogramAgent(a2);
        
        HistogramAgent[] myAgents = new HistogramAgent[]{ agent1, agent2 };
        
        for (int i=0; i<numGames; i++) {
            GameRunner runner = new GameRunner("test-game", numPlayers);
            for (int j=0; j<numPlayers; j++) {
                runner.addPlayer(new AgentPlayer("agent"+j, myAgents[j]));
            }
            GameStats stats = runner.playGame(random.nextLong());
            statsSummary.add(stats.score);
            System.out.println("Game number " + i + " complete");
        }

        //print out the stats
        System.out.println("Stats:");
        System.out.println(String.format("Our agent: Avg: %f, min: %f, max: %f",
                statsSummary.getMean(),
                statsSummary.getMin(),
                statsSummary.getMax()));
        
        for (HistogramAgent h : myAgents){
            h.printHistogram();
        }
    }
}
